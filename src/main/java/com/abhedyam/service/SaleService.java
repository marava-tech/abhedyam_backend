package com.abhedyam.service;

import com.abhedyam.dto.PageResponse;
import com.abhedyam.dto.SaleCreateRequest;
import com.abhedyam.dto.SaleDetailResponse;
import com.abhedyam.dto.SaleItemRequest;
import com.abhedyam.dto.SaleSearchRequest;
import com.abhedyam.exception.BusinessException;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Customer;
import com.abhedyam.model.Inventory;
import com.abhedyam.model.LocationDetails;
import com.abhedyam.model.Payment;
import com.abhedyam.model.Product;
import com.abhedyam.model.SaleItem;
import com.abhedyam.model.enums.PaymentStatus;
import com.abhedyam.model.enums.UserType;
import com.abhedyam.repository.CustomerRepository;
import com.abhedyam.repository.InventoryRepository;
import com.abhedyam.repository.LocationDetailsRepository;
import com.abhedyam.repository.PaymentRepository;
import com.abhedyam.repository.ProductRepository;
import com.abhedyam.repository.SaleItemRepository;
import com.abhedyam.service.interfaces.ISaleService;
import com.abhedyam.service.interfaces.IStockService;
import com.abhedyam.util.PhoneUtil;
import com.abhedyam.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SaleService implements ISaleService {
    
    private final SaleItemRepository saleItemRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final CustomerRepository customerRepository;
    private final LocationDetailsRepository locationDetailsRepository;
    private final PaymentRepository paymentRepository;
    private final IStockService stockService;
    private final RedisTemplate<String, String> redisTemplate;
    private final com.abhedyam.service.interfaces.IAuditService auditService;
    
    private static final String IDEMPOTENCY_PREFIX = "sale:idempotency:";
    private static final int IDEMPOTENCY_TTL_MINUTES = 1;
    
    @Override
    @Transactional
    public SaleDetailResponse createSale(SaleCreateRequest request) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        String transactionId = UUID.randomUUID().toString();
        
        try {
            Customer customer = resolveCustomer(request, ownerId, transactionId);
            
            List<UUID> productIds = new ArrayList<>();
            List<Product> products = new ArrayList<>();
            List<SaleItemRequest> processedItems = new ArrayList<>();
            
            for (SaleItemRequest itemRequest : request.getItems()) {
                Product product = resolveProduct(itemRequest, ownerId, transactionId);
                productIds.add(product.getId());
                products.add(product);
                processedItems.add(itemRequest);
            }
            
            String idempotencyKey = generateIdempotencyKey(customer.getId(), productIds);
            String redisKey = IDEMPOTENCY_PREFIX + idempotencyKey;
            
            try {
                String existingTransactionId = redisTemplate.opsForValue().get(redisKey);
                if (existingTransactionId != null) {
                    log.warn("Duplicate sale request detected with idempotencyKey: {}, returning existing transaction: {}", 
                        idempotencyKey, existingTransactionId);
                    return getSaleByTransactionId(existingTransactionId);
                }
            } catch (Exception e) {
                log.warn("Redis check failed for idempotency, proceeding with sale creation: {}", e.getMessage());
            }
            
            List<SaleItem> saleItems = new ArrayList<>();
            BigDecimal totalAmount = BigDecimal.ZERO;
            
            for (int i = 0; i < processedItems.size(); i++) {
                SaleItemRequest itemRequest = processedItems.get(i);
                Product product = products.get(i);
                UUID productId = productIds.get(i);
                
                BigDecimal quantity = BigDecimal.ONE;
                BigDecimal itemPrice = resolveItemPrice(itemRequest, product);
                
                BigDecimal itemTotal = itemPrice.multiply(quantity);
                totalAmount = totalAmount.add(itemTotal);
                
                SaleItem saleItem = createSaleItem(customer.getId(), ownerId, productId, itemPrice, 
                    quantity, itemTotal, transactionId, request.getDueDate());
                
                SaleItem savedItem = saleItemRepository.save(saleItem);
                saleItems.add(savedItem);
                
                try {
                    stockService.recordSaleOut(productId, quantity, savedItem.getId(), 
                        "Sale transaction: " + transactionId);
                } catch (Exception e) {
                    log.error("Stock update failed for product {} in transaction {}, continuing sale: {}", 
                        productId, transactionId, e.getMessage());
                }
            }
            
            try {
                redisTemplate.opsForValue().set(redisKey, transactionId, IDEMPOTENCY_TTL_MINUTES, TimeUnit.MINUTES);
            } catch (Exception e) {
                log.warn("Redis cache failed for idempotency key, sale still created: {}", e.getMessage());
            }
            
            log.info("Sale created successfully: Transaction ID {}, Total Amount: {}, Customer: {}, Items: {}", 
                transactionId, totalAmount, customer.getName(), saleItems.size());
            
            UUID firstSaleItemId = saleItems.isEmpty() ? null : saleItems.get(0).getId();
            try {
                auditService.logSaleCreation(firstSaleItemId != null ? firstSaleItemId : UUID.randomUUID(), 
                    ownerId, customer.getId(), customer.getName(), totalAmount, transactionId);
            } catch (Exception e) {
                log.warn("Audit logging failed, sale still created: {}", e.getMessage());
            }
            
            return buildSaleDetailResponse(transactionId, customer, saleItems, totalAmount);
            
        } catch (Exception e) {
            log.error("Critical error in sale creation for transaction {}: {}", transactionId, e.getMessage(), e);
            throw new BusinessException("SALE_CREATION_FAILED", 
                "Sale creation failed. Please try again or contact support if the issue persists.");
        }
    }
    
    private Customer resolveCustomer(SaleCreateRequest request, UUID ownerId, String transactionId) {
        if (request.getCustomerId() != null) {
            return customerRepository.findById(request.getCustomerId())
                .map(customer -> {
                    if (customer.getOwnerId() != null && !customer.getOwnerId().equals(ownerId)) {
                        log.warn("Customer access denied for transaction {}, creating new customer instead", transactionId);
                        return createNewCustomer(request, ownerId);
                    }
                    if (customer.getIsActive() == null || !customer.getIsActive()) {
                        log.warn("Customer {} is inactive for transaction {}, reactivating", customer.getId(), transactionId);
                        customer.setIsActive(true);
                        return customerRepository.save(customer);
                    }
                    return customer;
                })
                .orElseGet(() -> {
                    log.info("Customer {} not found for transaction {}, creating new customer", 
                        request.getCustomerId(), transactionId);
                    return createNewCustomer(request, ownerId);
                });
        } else {
            String customerName = request.getCustomerName();
            if (customerName == null || customerName.trim().isEmpty()) {
                customerName = "Customer-" + System.currentTimeMillis();
                log.warn("Customer name missing for transaction {}, using default: {}", transactionId, customerName);
            }
            request.setCustomerName(customerName);
            return createNewCustomer(request, ownerId);
        }
    }
    
    private Customer createNewCustomer(SaleCreateRequest request, UUID ownerId) {
        Customer customer = new Customer();
        customer.setName(request.getCustomerName().trim());
        customer.setType(UserType.CUSTOMER);
        customer.setOwnerId(ownerId);
        customer.setIsActive(true);
        
        if (request.getCustomerPhone() != null && !request.getCustomerPhone().trim().isEmpty()) {
            try {
                String normalizedPhone = PhoneUtil.normalizePhone(request.getCustomerPhone());
                customer.setPhone(PhoneUtil.extractPhoneWithoutCountryCode(normalizedPhone));
                customer.setPhoneNormalized(normalizedPhone);
            } catch (Exception e) {
                log.warn("Phone normalization failed, continuing without phone: {}", e.getMessage());
            }
        }
        
        customer = customerRepository.save(customer);
        
        if (request.getCustomerVillage() != null && !request.getCustomerVillage().trim().isEmpty()) {
            try {
                LocationDetails locationDetails = new LocationDetails();
                locationDetails.setUserId(customer.getId());
                locationDetails.setVillage(request.getCustomerVillage().trim());
                locationDetails.setLatitude(BigDecimal.ZERO);
                locationDetails.setLongitude(BigDecimal.ZERO);
                locationDetailsRepository.save(locationDetails);
            } catch (Exception e) {
                log.warn("Location details save failed for customer {}: {}", customer.getId(), e.getMessage());
            }
        }
        
        log.info("Customer created on the fly: {} ({})", customer.getName(), 
            customer.getPhone() != null ? customer.getPhone() : "no phone");
        return customer;
    }
    
    private Product resolveProduct(SaleItemRequest itemRequest, UUID ownerId, String transactionId) {
        if (itemRequest.getProductId() != null) {
            return productRepository.findById(itemRequest.getProductId())
                .map(product -> {
                    if (!product.getOwnerId().equals(ownerId)) {
                        log.warn("Product access denied for transaction {}, creating new product instead", transactionId);
                        return createNewProduct(itemRequest, ownerId);
                    }
                    if (product.getIsActive() == null || !product.getIsActive()) {
                        log.warn("Product {} is inactive for transaction {}, reactivating", product.getId(), transactionId);
                        product.setIsActive(true);
                        return productRepository.save(product);
                    }
                    if (product.getPrice() == null) {
                        log.warn("Product {} has no price for transaction {}, using provided price or default", 
                            product.getId(), transactionId);
                        if (itemRequest.getPrice() != null) {
                            product.setPrice(itemRequest.getPrice());
                            return productRepository.save(product);
                        }
                    }
                    return product;
                })
                .orElseGet(() -> {
                    log.info("Product {} not found for transaction {}, creating new product", 
                        itemRequest.getProductId(), transactionId);
                    return createNewProduct(itemRequest, ownerId);
                });
        } else {
            return createNewProduct(itemRequest, ownerId);
        }
    }
    
    private Product createNewProduct(SaleItemRequest itemRequest, UUID ownerId) {
        String productName = itemRequest.getProductName();
        if (productName == null || productName.trim().isEmpty()) {
            productName = "Product-" + System.currentTimeMillis();
            log.warn("Product name missing, using default: {}", productName);
        }
        
        BigDecimal price = itemRequest.getPrice();
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            price = BigDecimal.ZERO;
            log.warn("Product price missing or invalid, using default: {}", price);
        }
        
        String productCode = "PROD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        int attempts = 0;
        while (productRepository.findByOwnerIdAndCode(ownerId, productCode).isPresent() && attempts < 10) {
            productCode = "PROD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            attempts++;
        }
        
        Product product = new Product();
        product.setCode(productCode);
        product.setName(productName.trim());
        product.setPrice(price);
        product.setOwnerId(ownerId);
        product.setIsActive(true);
        product = productRepository.save(product);
        
        Inventory inventory = new Inventory();
        inventory.setProductId(product.getId());
        inventory.setOwnerId(ownerId);
        inventory.setStock(BigDecimal.ZERO);
        inventoryRepository.save(inventory);
        
        try {
            auditService.logProductCreation(product.getId(), ownerId, product.getName(), product.getCode());
        } catch (Exception e) {
            log.warn("Audit logging failed for product creation: {}", e.getMessage());
        }
        
        decreaseStockOnProductCreationAsync(product.getId(), ownerId);
        
        log.info("Product created on the fly: {} ({}) - Inventory created with stock 0", product.getName(), product.getCode());
        return product;
    }
    
    private BigDecimal resolveItemPrice(SaleItemRequest itemRequest, Product product) {
        if (itemRequest.getProductId() != null) {
            BigDecimal price = product.getPrice();
            if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
                if (itemRequest.getPrice() != null && itemRequest.getPrice().compareTo(BigDecimal.ZERO) > 0) {
                    log.warn("Product {} has no price, using provided price: {}", product.getId(), itemRequest.getPrice());
                    return itemRequest.getPrice();
                }
                log.warn("Product {} has no price and no provided price, using default: 0", product.getId());
                return BigDecimal.ZERO;
            }
            return price;
        } else {
            BigDecimal price = product.getPrice();
            if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
                log.warn("New product has invalid price, using default: 0");
                return BigDecimal.ZERO;
            }
            return price;
        }
    }
    
    private SaleItem createSaleItem(UUID customerId, UUID ownerId, UUID productId, BigDecimal price,
                                   BigDecimal quantity, BigDecimal itemTotal, String transactionId, Instant dueDate) {
        SaleItem saleItem = new SaleItem();
        saleItem.setProductId(productId);
        saleItem.setCustomerId(customerId);
        saleItem.setOwnerId(ownerId);
        saleItem.setPrice(price);
        saleItem.setQuantity(quantity);
        saleItem.setRemainingAmount(itemTotal);
        saleItem.setStatus(com.abhedyam.model.enums.SaleItemStatus.NOT_PAID);
        saleItem.setTransactionId(transactionId);
        saleItem.setDueDate(dueDate);
        return saleItem;
    }
    
    private String generateIdempotencyKey(UUID customerId, List<UUID> productIds) {
        try {
            List<String> sortedProductIds = productIds.stream()
                .map(UUID::toString)
                .sorted()
                .collect(Collectors.toList());
            
            String combined = customerId.toString() + ":" + String.join(",", sortedProductIds);
            
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(combined.getBytes());
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString().substring(0, 32);
        } catch (Exception e) {
            log.error("Error generating idempotency key", e);
            return customerId.toString() + "-" + productIds.stream()
                .map(UUID::toString)
                .sorted()
                .collect(Collectors.joining("-"));
        }
    }
    
    @Override
    public SaleDetailResponse getSaleByTransactionId(String transactionId) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        List<SaleItem> saleItems = saleItemRepository.findByTransactionId(transactionId);
        
        if (saleItems.isEmpty()) {
            throw new ResourceNotFoundException("Sale not found with transaction ID: " + transactionId);
        }
        
        SaleItem firstItem = saleItems.get(0);
        if (!firstItem.getOwnerId().equals(ownerId)) {
            throw new BusinessException("UNAUTHORIZED", "You don't have access to this sale");
        }
        
        Customer customer = customerRepository.findById(firstItem.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        BigDecimal totalAmount = saleItems.stream()
            .map(item -> item.getPrice().multiply(item.getQuantity() != null ? item.getQuantity() : BigDecimal.ONE))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return buildSaleDetailResponse(transactionId, customer, saleItems, totalAmount);
    }
    
    @Override
    public PageResponse<SaleItem> searchSales(SaleSearchRequest request) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        
        Sort sort = Sort.by(
            "DESC".equalsIgnoreCase(request.getSortDirection()) 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC,
            request.getSortBy()
        );
        
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
        
        Page<SaleItem> page = saleItemRepository.searchSales(
            ownerId,
            request.getCustomerId(),
            request.getTransactionId(),
            request.getStartDate(),
            request.getEndDate(),
            pageable
        );
        
        return new PageResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.hasNext(),
            page.hasPrevious()
        );
    }
    
    @Override
    public List<SaleItem> getSaleItemsByTransactionId(String transactionId) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        List<SaleItem> saleItems = saleItemRepository.findByTransactionId(transactionId);
        
        if (!saleItems.isEmpty() && !saleItems.get(0).getOwnerId().equals(ownerId)) {
            throw new BusinessException("UNAUTHORIZED", "You don't have access to this sale");
        }
        
        return saleItems;
    }
    
    @Override
    @Transactional
    public SaleDetailResponse cancelSale(String transactionId) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        List<SaleItem> saleItems = saleItemRepository.findByTransactionId(transactionId);
        
        if (saleItems.isEmpty()) {
            throw new ResourceNotFoundException("Sale not found with transaction ID: " + transactionId);
        }
        
        SaleItem firstItem = saleItems.get(0);
        if (!firstItem.getOwnerId().equals(ownerId)) {
            throw new BusinessException("UNAUTHORIZED", "You don't have access to this sale");
        }
        
        for (SaleItem saleItem : saleItems) {
            productRepository.findById(saleItem.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            
            BigDecimal quantity = saleItem.getQuantity() != null ? saleItem.getQuantity() : BigDecimal.ONE;
            
            com.abhedyam.dto.StockAdjustmentRequest adjustmentRequest = new com.abhedyam.dto.StockAdjustmentRequest();
            adjustmentRequest.setProductId(saleItem.getProductId());
            adjustmentRequest.setChangeQty(quantity);
            adjustmentRequest.setNote("Sale cancellation - Transaction: " + transactionId);
            
            stockService.recordManualAdjustment(adjustmentRequest);
            
            saleItem.setDeletedAt(Instant.now());
            saleItem.setIsActive(false);
            saleItemRepository.save(saleItem);
        }
        
        log.info("Sale cancelled: Transaction ID {}", transactionId);
        
        Customer customer = customerRepository.findById(firstItem.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        BigDecimal totalAmount = saleItems.stream()
            .map(item -> item.getPrice().multiply(item.getQuantity() != null ? item.getQuantity() : BigDecimal.ONE))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        UUID firstSaleItemId = saleItems.isEmpty() ? null : saleItems.get(0).getId();
        auditService.logSaleCancellation(firstSaleItemId != null ? firstSaleItemId : UUID.randomUUID(), 
            ownerId, customer.getId(), customer.getName(), totalAmount, transactionId);
        
        return buildSaleDetailResponse(transactionId, customer, saleItems, totalAmount);
    }
    
    private SaleDetailResponse buildSaleDetailResponse(String transactionId, Customer customer, 
                                                       List<SaleItem> saleItems, BigDecimal totalAmount) {
        List<Payment> payments = paymentRepository.findByCustomerId(customer.getId()).stream()
            .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
            .toList();
        
        BigDecimal totalPaid = payments.stream()
            .map(Payment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalDue = totalAmount.subtract(totalPaid);
        
        SaleItem firstItem = saleItems.isEmpty() ? null : saleItems.get(0);
        
        return new SaleDetailResponse(
            transactionId,
            customer.getId(),
            customer.getName(),
            firstItem != null ? firstItem.getOwnerId() : null,
            saleItems,
            totalAmount,
            firstItem != null ? firstItem.getCreatedAt() : Instant.now(),
            firstItem != null ? firstItem.getDueDate() : null,
            totalPaid,
            totalDue
        );
    }
    
    @Async("virtualThreadExecutor")
    @Transactional
    public void decreaseStockOnProductCreationAsync(UUID productId, UUID ownerId) {
        try {
            Inventory inventory = inventoryRepository.findByOwnerIdAndProductId(ownerId, productId)
                .orElse(null);
            
            if (inventory == null) {
                return;
            }
            
            BigDecimal currentStock = inventory.getStock();
            if (currentStock == null || currentStock.compareTo(BigDecimal.ZERO) <= 0) {
                return;
            }
            
            BigDecimal newStock = currentStock.subtract(BigDecimal.ONE);
            if (newStock.compareTo(BigDecimal.ZERO) < 0) {
                newStock = BigDecimal.ZERO;
            }
            
            inventory.setStock(newStock);
            inventoryRepository.save(inventory);
            
            Product product = productRepository.findById(productId).orElse(null);
            if (product != null) {
                auditService.logStockChange(productId, ownerId, product.getName(), currentStock, newStock, 
                    "PRODUCT_CREATION", "Stock decreased on product creation");
            }
            
            log.info("Stock decreased on product creation: Product {}, Old Stock {}, New Stock {}", 
                productId, currentStock, newStock);
        } catch (Exception e) {
            log.warn("Failed to decrease stock on product creation for product {}: {}", productId, e.getMessage());
        }
    }
}

