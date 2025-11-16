package com.abhedyam.service;

import com.abhedyam.dto.PaymentStatusUpdateRequest;
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
import com.abhedyam.service.interfaces.IAuditService;
import com.abhedyam.service.interfaces.IPaymentService;
import com.abhedyam.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService implements IPaymentService {
    
    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final SaleItemRepository saleItemRepository;
    private final IAuditService auditService;
    
    @Override
    public Payment create(Payment payment) {
        return paymentRepository.save(payment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Payment getById(UUID id) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        
        if (!payment.getOwnerId().equals(ownerId)) {
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
    public List<Payment> getByCustomerId(UUID customerId) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        List<Payment> payments = paymentRepository.findByCustomerId(customerId);
        return payments.stream()
            .filter(p -> p.getOwnerId().equals(ownerId))
            .toList();
    }
    
    @Override
    @Transactional
    public Payment updateStatus(UUID id, PaymentStatusUpdateRequest request) {
        Payment payment = getById(id);
        PaymentStatus oldStatus = payment.getStatus();
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
                payment.getAmount(),
                payment.getReference()
            );
        }
        
        return savedPayment;
    }
}

