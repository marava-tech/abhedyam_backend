package com.abhedyam.service;

import com.abhedyam.dto.PageResponse;
import com.abhedyam.dto.SaleCreateRequest;
import com.abhedyam.dto.SaleDetailResponse;
import com.abhedyam.dto.SaleItemRequest;
import com.abhedyam.dto.SaleSearchRequest;
import com.abhedyam.exception.BusinessException;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Customer;
import com.abhedyam.model.Payment;
import com.abhedyam.model.Product;
import com.abhedyam.model.SaleItem;
import com.abhedyam.model.enums.PaymentStatus;
import com.abhedyam.repository.CustomerRepository;
import com.abhedyam.repository.PaymentRepository;
import com.abhedyam.repository.ProductRepository;
import com.abhedyam.repository.SaleItemRepository;
import com.abhedyam.service.interfaces.ISaleService;
import com.abhedyam.service.interfaces.IStockService;
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
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class SaleService implements ISaleService {
    
    private final SaleItemRepository saleItemRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;
    private final IStockService stockService;
    private final RedisTemplate<String, String> redisTemplate;
    private final com.abhedyam.service.interfaces.IAuditService auditService;
    
    private static final String IDEMPOTENCY_PREFIX = "sale:idempotency:";
    private static final int IDEMPOTENCY_TTL_HOURS = 24;
    
    @Override
    @Transactional
    public SaleDetailResponse createSale(SaleCreateRequest request) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        
        if (request.getIdempotencyKey() != null) {
            String idempotencyKey = IDEMPOTENCY_PREFIX + request.getIdempotencyKey();
            String existingTransactionId = redisTemplate.opsForValue().get(idempotencyKey);
            if (existingTransactionId != null) {
                log.info("Idempotent sale creation detected. Returning existing sale: {}", existingTransactionId);
                return getSaleByTransactionId(existingTransactionId);
            }
        }
        
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        if (customer.getOwnerId() != null && !customer.getOwnerId().equals(ownerId)) {
            throw new BusinessException("UNAUTHORIZED", "You don't have access to this customer");
        }
        
        String transactionId = UUID.randomUUID().toString();
        List<SaleItem> saleItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (SaleItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + itemRequest.getProductId()));
            
            if (!product.getOwnerId().equals(ownerId)) {
                throw new BusinessException("UNAUTHORIZED", "You don't have access to this product");
            }
            
            BigDecimal quantity = itemRequest.getQuantity() != null ? itemRequest.getQuantity() : BigDecimal.ONE;
            BigDecimal currentStock = stockService.getCurrentStock(itemRequest.getProductId());
            
            if (currentStock.compareTo(quantity) < 0) {
                throw new BusinessException("INSUFFICIENT_STOCK", 
                    "Insufficient stock for product: " + product.getName() + 
                    ". Available: " + currentStock + ", Required: " + quantity);
            }
            
            BigDecimal itemTotal = itemRequest.getPrice().multiply(quantity);
            totalAmount = totalAmount.add(itemTotal);
            
            SaleItem saleItem = new SaleItem();
            saleItem.setProductId(itemRequest.getProductId());
            saleItem.setCustomerId(request.getCustomerId());
            saleItem.setOwnerId(ownerId);
            saleItem.setPrice(itemRequest.getPrice());
            saleItem.setQuantity(quantity);
            saleItem.setTransactionId(transactionId);
            saleItem.setDueDate(request.getDueDate());
            
            SaleItem savedItem = saleItemRepository.save(saleItem);
            saleItems.add(savedItem);
            
            stockService.recordSaleOut(itemRequest.getProductId(), quantity, savedItem.getId(), 
                "Sale transaction: " + transactionId);
        }
        
        if (request.getIdempotencyKey() != null) {
            String idempotencyKey = IDEMPOTENCY_PREFIX + request.getIdempotencyKey();
            redisTemplate.opsForValue().set(idempotencyKey, transactionId, IDEMPOTENCY_TTL_HOURS, TimeUnit.HOURS);
        }
        
        log.info("Sale created: Transaction ID {}, Total Amount: {}", transactionId, totalAmount);
        
        UUID firstSaleItemId = saleItems.isEmpty() ? null : saleItems.get(0).getId();
        auditService.logSaleCreation(firstSaleItemId != null ? firstSaleItemId : UUID.randomUUID(), 
            ownerId, request.getCustomerId(), totalAmount, transactionId);
        
        return buildSaleDetailResponse(transactionId, customer, saleItems, totalAmount);
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
            ownerId, firstItem.getCustomerId(), totalAmount, transactionId);
        
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

