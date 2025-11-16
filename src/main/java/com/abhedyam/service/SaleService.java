package com.abhedyam.service;

import com.abhedyam.dto.PageResponse;
import com.abhedyam.dto.SaleCreateRequest;
import com.abhedyam.dto.SaleDetailResponse;
import com.abhedyam.dto.SaleItemRequest;
import com.abhedyam.dto.SaleSearchRequest;
import com.abhedyam.exception.BusinessException;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Customer;
import com.abhedyam.model.LocationDetails;
import com.abhedyam.model.Payment;
import com.abhedyam.model.Product;
import com.abhedyam.model.SaleItem;
import com.abhedyam.model.enums.PaymentStatus;
import com.abhedyam.model.enums.UserType;
import com.abhedyam.repository.CustomerRepository;
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
        
        Customer customer;
        if (request.getCustomerId() != null) {
            customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
            
            if (customer.getOwnerId() != null && !customer.getOwnerId().equals(ownerId)) {
                throw new BusinessException("UNAUTHORIZED", "You don't have access to this customer");
            }
        } else {
            if (request.getCustomerName() == null || request.getCustomerName().trim().isEmpty()) {
                throw new BusinessException("CUSTOMER_NAME_REQUIRED", "Customer name is required when customerId is not provided");
            }
            if (request.getCustomerPhone() == null || request.getCustomerPhone().trim().isEmpty()) {
                throw new BusinessException("CUSTOMER_PHONE_REQUIRED", "Customer phone is required when customerId is not provided");
            }
            
            String normalizedPhone = PhoneUtil.normalizePhone(request.getCustomerPhone());
            customer = new Customer();
            customer.setName(request.getCustomerName());
            customer.setPhone(PhoneUtil.extractPhoneWithoutCountryCode(normalizedPhone));
            customer.setPhoneNormalized(normalizedPhone);
            customer.setType(UserType.CUSTOMER);
            customer.setOwnerId(ownerId);
            customer.setIsActive(true);
            customer = customerRepository.save(customer);
            
            if (request.getCustomerVillage() != null && !request.getCustomerVillage().trim().isEmpty()) {
                LocationDetails locationDetails = new LocationDetails();
                locationDetails.setUserId(customer.getId());
                locationDetails.setVillage(request.getCustomerVillage());
                locationDetails.setLatitude(BigDecimal.ZERO);
                locationDetails.setLongitude(BigDecimal.ZERO);
                locationDetailsRepository.save(locationDetails);
            }
            
            log.info("Customer created on the fly: {} ({})", customer.getName(), customer.getPhone());
        }
        
        List<UUID> productIds = new ArrayList<>();
        List<Product> products = new ArrayList<>();
        List<SaleItemRequest> processedItems = new ArrayList<>();
        
        for (SaleItemRequest itemRequest : request.getItems()) {
            Product product;
            UUID productId;
            
            if (itemRequest.getProductId() != null) {
                product = productRepository.findById(itemRequest.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + itemRequest.getProductId()));
                
                if (!product.getOwnerId().equals(ownerId)) {
                    throw new BusinessException("UNAUTHORIZED", "You don't have access to this product");
                }
                productId = itemRequest.getProductId();
            } else {
                if (itemRequest.getProductName() == null || itemRequest.getProductName().trim().isEmpty()) {
                    throw new BusinessException("PRODUCT_NAME_REQUIRED", "Product name is required when productId is not provided");
                }
                if (itemRequest.getPrice() == null) {
                    throw new BusinessException("PRICE_REQUIRED", "Price is required when productId is not provided");
                }
                
                String productCode = "PROD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                while (productRepository.findByOwnerIdAndCode(ownerId, productCode).isPresent()) {
                    productCode = "PROD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                }
                
                product = new Product();
                product.setCode(productCode);
                product.setName(itemRequest.getProductName());
                product.setPrice(itemRequest.getPrice());
                product.setOwnerId(ownerId);
                product.setIsActive(true);
                product = productRepository.save(product);
                
                stockService.recordPurchaseIn(product.getId(), BigDecimal.ONE, "Initial stock for product created on the fly");
                productId = product.getId();
                
                auditService.logProductCreation(product.getId(), ownerId, product.getName(), product.getCode());
                
                log.info("Product created on the fly: {} ({})", product.getName(), product.getCode());
            }
            
            productIds.add(productId);
            products.add(product);
            processedItems.add(itemRequest);
        }
        
        String idempotencyKey = generateIdempotencyKey(customer.getId(), productIds);
        String redisKey = IDEMPOTENCY_PREFIX + idempotencyKey;
        String existingTransactionId = redisTemplate.opsForValue().get(redisKey);
        
        if (existingTransactionId != null) {
            log.warn("Duplicate sale request detected with idempotencyKey: {}", idempotencyKey);
            throw new BusinessException("DUPLICATE_REQUEST", 
                "A sale with the same customer and products was created recently. Please wait a moment before retrying.");
        }
        
        String transactionId = UUID.randomUUID().toString();
        List<SaleItem> saleItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (int i = 0; i < processedItems.size(); i++) {
            SaleItemRequest itemRequest = processedItems.get(i);
            Product product = products.get(i);
            UUID productId = productIds.get(i);
            
            BigDecimal quantity = BigDecimal.ONE;
            BigDecimal currentStock = stockService.getCurrentStock(productId);
            
            if (currentStock.compareTo(quantity) < 0) {
                throw new BusinessException("INSUFFICIENT_STOCK", 
                    "Insufficient stock for product: " + product.getName() + 
                    ". Available: " + currentStock + ", Required: " + quantity);
            }
            
            BigDecimal itemPrice = itemRequest.getPrice() != null ? itemRequest.getPrice() : product.getPrice();
            if (itemPrice == null) {
                throw new BusinessException("PRICE_REQUIRED", "Product price is not set and no price provided in request");
            }
            
            BigDecimal itemTotal = itemPrice.multiply(quantity);
            totalAmount = totalAmount.add(itemTotal);
            
            SaleItem saleItem = new SaleItem();
            saleItem.setProductId(productId);
            saleItem.setCustomerId(customer.getId());
            saleItem.setOwnerId(ownerId);
            saleItem.setPrice(itemPrice);
            saleItem.setQuantity(quantity);
            saleItem.setRemainingAmount(itemTotal);
            saleItem.setStatus(com.abhedyam.model.enums.SaleItemStatus.NOT_PAID);
            saleItem.setTransactionId(transactionId);
            saleItem.setDueDate(request.getDueDate());
            
            SaleItem savedItem = saleItemRepository.save(saleItem);
            saleItems.add(savedItem);
            
            stockService.recordSaleOut(productId, quantity, savedItem.getId(), 
                "Sale transaction: " + transactionId);
        }
        
        redisTemplate.opsForValue().set(redisKey, transactionId, IDEMPOTENCY_TTL_MINUTES, TimeUnit.MINUTES);
        
        log.info("Sale created: Transaction ID {}, Total Amount: {}", transactionId, totalAmount);
        
        UUID firstSaleItemId = saleItems.isEmpty() ? null : saleItems.get(0).getId();
        auditService.logSaleCreation(firstSaleItemId != null ? firstSaleItemId : UUID.randomUUID(), 
            ownerId, customer.getId(), customer.getName(), totalAmount, transactionId);
        
        return buildSaleDetailResponse(transactionId, customer, saleItems, totalAmount);
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
}

