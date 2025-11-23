package com.abhedyam.service;

import com.abhedyam.dto.PageResponse;
import com.abhedyam.dto.PaymentCreateRequest;
import com.abhedyam.dto.PaymentResponse;
import com.abhedyam.dto.PaymentStatusUpdateRequest;
import com.abhedyam.exception.BusinessException;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Customer;
import com.abhedyam.model.Payment;
import com.abhedyam.model.Product;
import com.abhedyam.model.SaleItem;
import com.abhedyam.model.User;
import com.abhedyam.model.enums.PaymentStatus;
import com.abhedyam.model.enums.SaleItemStatus;
import com.abhedyam.model.enums.UserType;
import com.abhedyam.repository.CustomerRepository;
import com.abhedyam.repository.PaymentRepository;
import com.abhedyam.repository.ProductRepository;
import com.abhedyam.repository.SaleItemRepository;
import com.abhedyam.repository.UserRepository;
import com.abhedyam.service.interfaces.IAuditService;
import com.abhedyam.service.interfaces.IPaymentService;
import com.abhedyam.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService implements IPaymentService {
    
    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final SaleItemRepository saleItemRepository;
    private final UserRepository userRepository;
    private final IAuditService auditService;
    
    @Override
    public Payment create(Payment payment) {
        return paymentRepository.save(payment);
    }
    
    @Override
    @Transactional
    public PaymentResponse createManualPayment(PaymentCreateRequest request) {
        UUID currentUserId = SecurityUtil.getCurrentUserId();
        
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("INVALID_AMOUNT", "Payment amount must be greater than zero");
        }
        
        SaleItem saleItem = saleItemRepository.findById(request.getSaleItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Sale item not found"));
        
        UUID saleItemOwnerId = saleItem.getOwnerId();
        UUID saleItemCustomerId = saleItem.getCustomerId();
        
        // Allow access if:
        // 1. Current user is the customer of the sale item
        // 2. Current user is the owner of the sale item
        boolean hasAccess = false;
        UUID paymentOwnerId = saleItemOwnerId;
        
        if (currentUserId.equals(saleItemCustomerId)) {
            hasAccess = true;
            paymentOwnerId = saleItemOwnerId;
        } else if (saleItemOwnerId != null && saleItemOwnerId.equals(currentUserId)) {
            User currentUser = userRepository.findById(currentUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
            if (currentUser.getType() == UserType.BUSINESS) {
                hasAccess = true;
                paymentOwnerId = currentUserId;
            }
        }
        
        if (!hasAccess) {
            throw new BusinessException("UNAUTHORIZED", "You don't have access to this sale item");
        }
        
        BigDecimal remainingAmount = saleItem.getRemainingAmount();
        if (remainingAmount == null) {
            BigDecimal totalAmount = saleItem.getPrice().multiply(
                saleItem.getQuantity() != null ? saleItem.getQuantity() : BigDecimal.ONE
            );
            remainingAmount = totalAmount;
        }
        
        if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("NO_DUE_AMOUNT", "No due amount exists for this sale item. Payment cannot be processed.");
        }
        
        if (request.getAmount().compareTo(remainingAmount) > 0) {
            throw new BusinessException("PAYMENT_EXCEEDS_DUE", 
                String.format("Payment amount (₹%s) cannot exceed the due amount (₹%s). You can pay maximum ₹%s.", 
                    request.getAmount(), remainingAmount, remainingAmount));
        }
        
        Customer customer = customerRepository.findById(saleItem.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        Product product = productRepository.findById(saleItem.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        Payment payment = new Payment();
        payment.setCustomerId(saleItem.getCustomerId());
        payment.setOwnerId(paymentOwnerId);
        payment.setSaleItemId(request.getSaleItemId());
        payment.setAmount(request.getAmount());
        payment.setMedium(request.getMedium());
        payment.setTimestamp(Instant.now());
        payment.setReference(request.getReference());
        payment.setStatus(request.getStatus() != null ? request.getStatus() : PaymentStatus.SUCCESS);
        
        Payment savedPayment = paymentRepository.save(payment);
        
        if (savedPayment.getStatus() == PaymentStatus.SUCCESS) {
            updateSaleItemBalance(saleItem, savedPayment.getAmount(), true);
            auditService.logPaymentSuccess(
                savedPayment.getId(),
                paymentOwnerId,
                customer.getId(),
                customer.getName(),
                request.getSaleItemId(),
                product.getName(),
                savedPayment.getAmount(),
                savedPayment.getReference()
            );
        }
        
        return new PaymentResponse(
                savedPayment.getId(),
                savedPayment.getCustomerId(),
                customer.getName(),
                savedPayment.getOwnerId(),
                savedPayment.getSaleItemId(),
                product.getName(),
                savedPayment.getAmount(),
                savedPayment.getMedium(),
                savedPayment.getTimestamp(),
                savedPayment.getReference(),
                savedPayment.getStatus(),
                savedPayment.getCreatedAt(),
                savedPayment.getUpdatedAt()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public Payment getById(UUID id) {
        UUID currentUserId = SecurityUtil.getCurrentUserId();
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        
        UUID paymentOwnerId = payment.getOwnerId();
        UUID paymentCustomerId = payment.getCustomerId();
        
        // Allow access if:
        // 1. Current user is the customer of the payment
        // 2. Current user is the owner of the payment
        boolean hasAccess = false;
        
        if (currentUserId.equals(paymentCustomerId)) {
            hasAccess = true;
        } else if (paymentOwnerId != null && paymentOwnerId.equals(currentUserId)) {
            User currentUser = userRepository.findById(currentUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
            if (currentUser.getType() == UserType.BUSINESS) {
                hasAccess = true;
            }
        }
        
        if (!hasAccess) {
            throw new BusinessException("UNAUTHORIZED", "You don't have access to this payment");
        }
        
        return payment;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Payment> getByOwnerId(UUID ownerId) {
        UUID currentOwnerId = SecurityUtil.getCurrentUserId();
        UUID targetOwnerId = ownerId != null ? ownerId : currentOwnerId;
        if (!currentOwnerId.equals(targetOwnerId)) {
            throw new BusinessException("UNAUTHORIZED", "You can only view your own payments");
        }
        return paymentRepository.findByOwnerId(targetOwnerId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PageResponse<PaymentResponse> getMyPayments(Integer page, Integer size) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        
        if (page == null || page < 0) {
            page = 0;
        }
        if (size == null || size < 1) {
            size = 10;
        }
        
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Payment> paymentPage = paymentRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId, pageable);
        
        List<PaymentResponse> responses = paymentPage.getContent().stream()
                .map(payment -> {
                    Customer customer = customerRepository.findById(payment.getCustomerId())
                            .orElse(null);
                    String customerName = customer != null ? customer.getName() : "Unknown";
                    
                    String productName = "Unknown";
                    if (payment.getSaleItemId() != null) {
                        SaleItem saleItem = saleItemRepository.findById(payment.getSaleItemId()).orElse(null);
                        if (saleItem != null) {
                            Product product = productRepository.findById(saleItem.getProductId()).orElse(null);
                            if (product != null) {
                                productName = product.getName();
                            }
                        }
                    }
                    
                    return new PaymentResponse(
                            payment.getId(),
                            payment.getCustomerId(),
                            customerName,
                            payment.getOwnerId(),
                            payment.getSaleItemId(),
                            productName,
                            payment.getAmount(),
                            payment.getMedium(),
                            payment.getTimestamp(),
                            payment.getReference(),
                            payment.getStatus(),
                            payment.getCreatedAt(),
                            payment.getUpdatedAt()
                    );
                })
                .toList();
        
        return new PageResponse<>(
            responses,
            paymentPage.getNumber(),
            paymentPage.getSize(),
            paymentPage.getTotalElements(),
            paymentPage.getTotalPages(),
            paymentPage.hasNext(),
            paymentPage.hasPrevious()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> filterPayments(String searchText) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        boolean isNumeric = false;
        BigDecimal amount = null;
        
        if (searchText != null && !searchText.trim().isEmpty()) {
            try {
                amount = new BigDecimal(searchText.trim());
                isNumeric = true;
            } catch (NumberFormatException e) {
                isNumeric = false;
            }
        }
        
        List<Payment> payments = paymentRepository.filterPayments(
            ownerId,
            searchText != null && !searchText.trim().isEmpty() ? searchText.trim() : null,
            isNumeric,
            amount
        );
        
        return payments.stream()
                .sorted((p1, p2) -> {
                    if (p1.getCreatedAt() == null && p2.getCreatedAt() == null) return 0;
                    if (p1.getCreatedAt() == null) return 1;
                    if (p2.getCreatedAt() == null) return -1;
                    return p2.getCreatedAt().compareTo(p1.getCreatedAt());
                })
                .limit(15)
                .map(payment -> {
                    Customer customer = customerRepository.findById(payment.getCustomerId())
                            .orElse(null);
                    String customerName = customer != null ? customer.getName() : "Unknown";
                    
                    String productName = "Unknown";
                    if (payment.getSaleItemId() != null) {
                        SaleItem saleItem = saleItemRepository.findById(payment.getSaleItemId()).orElse(null);
                        if (saleItem != null) {
                            Product product = productRepository.findById(saleItem.getProductId()).orElse(null);
                            if (product != null) {
                                productName = product.getName();
                            }
                        }
                    }
                    
                    return new PaymentResponse(
                            payment.getId(),
                            payment.getCustomerId(),
                            customerName,
                            payment.getOwnerId(),
                            payment.getSaleItemId(),
                            productName,
                            payment.getAmount(),
                            payment.getMedium(),
                            payment.getTimestamp(),
                            payment.getReference(),
                            payment.getStatus(),
                            payment.getCreatedAt(),
                            payment.getUpdatedAt()
                    );
                })
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Payment> getByCustomerId(UUID customerId) {
        UUID currentUserId = SecurityUtil.getCurrentUserId();
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        
        UUID ownerId = customer.getOwnerId();
        
        // Allow access if:
        // 1. Current user is the customer themselves
        // 2. Current user is the owner of this customer
        boolean hasAccess = false;
        
        if (currentUserId.equals(customerId)) {
            hasAccess = true;
        } else if (ownerId != null) {
            User currentUser = userRepository.findById(currentUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
            
            if (currentUser.getType() == UserType.BUSINESS && ownerId.equals(currentUserId)) {
                hasAccess = true;
            }
        }
        
        if (!hasAccess) {
            throw new BusinessException("UNAUTHORIZED", "You don't have access to this customer's payments");
        }
        
        // If customer is accessing, use their ownerId; if owner is accessing, use currentUserId
        UUID filterOwnerId = currentUserId.equals(customerId) ? ownerId : currentUserId;
        
        List<Payment> payments = paymentRepository.findByCustomerId(customerId);
        return payments.stream()
            .filter(p -> filterOwnerId != null && p.getOwnerId().equals(filterOwnerId))
            .sorted((p1, p2) -> {
                if (p1.getCreatedAt() == null && p2.getCreatedAt() == null) return 0;
                if (p1.getCreatedAt() == null) return 1;
                if (p2.getCreatedAt() == null) return -1;
                return p2.getCreatedAt().compareTo(p1.getCreatedAt());
            })
            .toList();
    }
    
    @Override
    @Transactional
    public Payment updateStatus(UUID id, PaymentStatusUpdateRequest request) {
        Payment payment = getById(id);
        PaymentStatus oldStatus = payment.getStatus();
        
        if (payment.getSaleItemId() != null) {
            SaleItem saleItem = saleItemRepository.findById(payment.getSaleItemId()).orElse(null);
            if (saleItem != null) {
                if (oldStatus == PaymentStatus.SUCCESS && request.getStatus() != PaymentStatus.SUCCESS) {
                    updateSaleItemBalance(saleItem, payment.getAmount(), false);
                } else if (oldStatus != PaymentStatus.SUCCESS && request.getStatus() == PaymentStatus.SUCCESS) {
                    BigDecimal remainingAmount = saleItem.getRemainingAmount();
                    if (remainingAmount == null) {
                        BigDecimal totalAmount = saleItem.getPrice().multiply(
                            saleItem.getQuantity() != null ? saleItem.getQuantity() : BigDecimal.ONE
                        );
                        remainingAmount = totalAmount;
                    }
                    
                    if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new BusinessException("NO_DUE_AMOUNT", "No due amount exists for this sale item. Payment cannot be processed.");
                    }
                    
                    if (payment.getAmount().compareTo(remainingAmount) > 0) {
                        throw new BusinessException("PAYMENT_EXCEEDS_DUE", 
                            String.format("Payment amount (₹%s) cannot exceed the due amount (₹%s). You can pay maximum ₹%s.", 
                                payment.getAmount(), remainingAmount, remainingAmount));
                    }
                    
                    updateSaleItemBalance(saleItem, payment.getAmount(), true);
                }
            }
        }
        
        payment.setStatus(request.getStatus());
        if (request.getReference() != null) {
            payment.setReference(request.getReference());
        }
        Payment savedPayment = paymentRepository.save(payment);
        
        if (oldStatus != PaymentStatus.SUCCESS && request.getStatus() == PaymentStatus.SUCCESS) {
            Customer customer = customerRepository.findById(payment.getCustomerId())
                    .orElse(null);
            String customerName = customer != null ? customer.getName() : "Unknown";
            
            String productName = "Unknown";
            if (payment.getSaleItemId() != null) {
                SaleItem saleItem = saleItemRepository.findById(payment.getSaleItemId()).orElse(null);
                if (saleItem != null) {
                    Product product = productRepository.findById(saleItem.getProductId()).orElse(null);
                    if (product != null) {
                        productName = product.getName();
                    }
                }
            }
            
            auditService.logPaymentSuccess(
                savedPayment.getId(),
                payment.getOwnerId(),
                payment.getCustomerId(),
                customerName,
                payment.getSaleItemId(),
                productName,
                payment.getAmount(),
                payment.getReference()
            );
        }
        
        return savedPayment;
    }
    
    @Override
    @Transactional
    public Payment update(UUID id, Payment paymentDetails) {
        Payment payment = getById(id);
        PaymentStatus oldStatus = payment.getStatus();
        BigDecimal oldAmount = payment.getAmount();
        
        if (paymentDetails.getAmount() != null) {
            if (paymentDetails.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("INVALID_AMOUNT", "Payment amount must be greater than zero");
            }
        }
        
        if (payment.getSaleItemId() != null) {
            SaleItem saleItem = saleItemRepository.findById(payment.getSaleItemId()).orElse(null);
            if (saleItem != null) {
                boolean statusChanged = paymentDetails.getStatus() != null && !paymentDetails.getStatus().equals(oldStatus);
                boolean amountChanged = paymentDetails.getAmount() != null && paymentDetails.getAmount().compareTo(oldAmount) != 0;
                
                if (statusChanged) {
                    if (oldStatus == PaymentStatus.SUCCESS && paymentDetails.getStatus() != PaymentStatus.SUCCESS) {
                        updateSaleItemBalance(saleItem, oldAmount, false);
                    } else if (oldStatus != PaymentStatus.SUCCESS && paymentDetails.getStatus() == PaymentStatus.SUCCESS) {
                        BigDecimal amountToProcess = paymentDetails.getAmount() != null ? paymentDetails.getAmount() : oldAmount;
                        
                        BigDecimal remainingAmount = saleItem.getRemainingAmount();
                        if (remainingAmount == null) {
                            BigDecimal totalAmount = saleItem.getPrice().multiply(
                                saleItem.getQuantity() != null ? saleItem.getQuantity() : BigDecimal.ONE
                            );
                            remainingAmount = totalAmount;
                        }
                        
                        if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
                            throw new BusinessException("NO_DUE_AMOUNT", "No due amount exists for this sale item. Payment cannot be processed.");
                        }
                        
                        if (amountToProcess.compareTo(remainingAmount) > 0) {
                            throw new BusinessException("PAYMENT_EXCEEDS_DUE", 
                                String.format("Payment amount (₹%s) cannot exceed the due amount (₹%s). You can pay maximum ₹%s.", 
                                    amountToProcess, remainingAmount, remainingAmount));
                        }
                        
                        updateSaleItemBalance(saleItem, amountToProcess, true);
                    }
                } else if (amountChanged && oldStatus == PaymentStatus.SUCCESS) {
                    BigDecimal newAmount = paymentDetails.getAmount();
                    
                    BigDecimal remainingAmount = saleItem.getRemainingAmount();
                    if (remainingAmount == null) {
                        BigDecimal totalAmount = saleItem.getPrice().multiply(
                            saleItem.getQuantity() != null ? saleItem.getQuantity() : BigDecimal.ONE
                        );
                        remainingAmount = totalAmount;
                    }
                    
                    BigDecimal availableDueAmount = remainingAmount.add(oldAmount);
                    
                    if (availableDueAmount.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new BusinessException("NO_DUE_AMOUNT", "No due amount exists for this sale item. Payment cannot be updated.");
                    }
                    
                    if (newAmount.compareTo(availableDueAmount) > 0) {
                        throw new BusinessException("PAYMENT_EXCEEDS_DUE", 
                            String.format("Updated payment amount (₹%s) cannot exceed the available due amount (₹%s). You can pay maximum ₹%s.", 
                                newAmount, availableDueAmount, availableDueAmount));
                    }
                    
                    updateSaleItemBalance(saleItem, oldAmount, false);
                    updateSaleItemBalance(saleItem, newAmount, true);
                }
            }
        }
        
        if (paymentDetails.getAmount() != null) payment.setAmount(paymentDetails.getAmount());
        if (paymentDetails.getMedium() != null) payment.setMedium(paymentDetails.getMedium());
        if (paymentDetails.getReference() != null) payment.setReference(paymentDetails.getReference());
        if (paymentDetails.getStatus() != null) payment.setStatus(paymentDetails.getStatus());
        Payment savedPayment = paymentRepository.save(payment);
        
        if (oldStatus != PaymentStatus.SUCCESS && paymentDetails.getStatus() == PaymentStatus.SUCCESS) {
            Customer customer = customerRepository.findById(payment.getCustomerId())
                    .orElse(null);
            String customerName = customer != null ? customer.getName() : "Unknown";
            
            String productName = "Unknown";
            if (payment.getSaleItemId() != null) {
                SaleItem saleItem = saleItemRepository.findById(payment.getSaleItemId()).orElse(null);
                if (saleItem != null) {
                    Product product = productRepository.findById(saleItem.getProductId()).orElse(null);
                    if (product != null) {
                        productName = product.getName();
                    }
                }
            }
            
            auditService.logPaymentSuccess(
                savedPayment.getId(),
                payment.getOwnerId(),
                payment.getCustomerId(),
                customerName,
                payment.getSaleItemId(),
                productName,
                savedPayment.getAmount(),
                savedPayment.getReference()
            );
        }
        
        return savedPayment;
    }
    
    private void updateSaleItemBalance(SaleItem saleItem, BigDecimal paymentAmount, boolean isPaymentSuccess) {
        if (saleItem.getRemainingAmount() == null) {
            BigDecimal totalAmount = saleItem.getPrice().multiply(
                saleItem.getQuantity() != null ? saleItem.getQuantity() : BigDecimal.ONE
            );
            saleItem.setRemainingAmount(totalAmount);
        }
        
        if (isPaymentSuccess) {
            BigDecimal newRemainingAmount = saleItem.getRemainingAmount().subtract(paymentAmount);
            if (newRemainingAmount.compareTo(BigDecimal.ZERO) < 0) {
                newRemainingAmount = BigDecimal.ZERO;
            }
            saleItem.setRemainingAmount(newRemainingAmount);
        } else {
            BigDecimal newRemainingAmount = saleItem.getRemainingAmount().add(paymentAmount);
            BigDecimal totalAmount = saleItem.getPrice().multiply(
                saleItem.getQuantity() != null ? saleItem.getQuantity() : BigDecimal.ONE
            );
            if (newRemainingAmount.compareTo(totalAmount) > 0) {
                newRemainingAmount = totalAmount;
            }
            saleItem.setRemainingAmount(newRemainingAmount);
        }
        
        updateSaleItemStatus(saleItem);
        saleItemRepository.save(saleItem);
    }
    
    private void updateSaleItemStatus(SaleItem saleItem) {
        if (saleItem.getRemainingAmount() == null) {
            saleItem.setStatus(SaleItemStatus.NOT_PAID);
            return;
        }
        
        BigDecimal totalAmount = saleItem.getPrice().multiply(
            saleItem.getQuantity() != null ? saleItem.getQuantity() : BigDecimal.ONE
        );
        
        if (saleItem.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0) {
            saleItem.setStatus(SaleItemStatus.FULLY_PAID);
        } else if (saleItem.getRemainingAmount().compareTo(totalAmount) < 0) {
            saleItem.setStatus(SaleItemStatus.PARTIALLY_PAID);
        } else {
            saleItem.setStatus(SaleItemStatus.NOT_PAID);
        }
    }
}

