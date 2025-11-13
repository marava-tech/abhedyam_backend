package com.abhedyam.service;

import com.abhedyam.dto.UpiPaymentLinkRequest;
import com.abhedyam.dto.UpiPaymentLinkResponse;
import com.abhedyam.exception.BusinessException;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Customer;
import com.abhedyam.model.Payment;
import com.abhedyam.model.enums.PaymentMedium;
import com.abhedyam.model.enums.PaymentStatus;
import com.abhedyam.repository.CustomerRepository;
import com.abhedyam.repository.PaymentRepository;
import com.abhedyam.service.interfaces.IPaymentLinkService;
import com.abhedyam.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentLinkService implements IPaymentLinkService {
    
    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;
    
    @Value("${app.cashfree.client-id:}")
    private String cashfreeClientId;
    
    @Value("${app.cashfree.client-secret:}")
    private String cashfreeClientSecret;
    
    @Override
    @Transactional
    public UpiPaymentLinkResponse generateUpiPaymentLink(UpiPaymentLinkRequest request) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        if (customer.getOwnerId() != null && !customer.getOwnerId().equals(ownerId)) {
            throw new BusinessException("UNAUTHORIZED", "You don't have access to this customer");
        }
        
        String orderId = "ORDER_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
        String paymentId = UUID.randomUUID().toString();
        
        Payment payment = new Payment();
        payment.setCustomerId(request.getCustomerId());
        payment.setOwnerId(ownerId);
        payment.setAmount(request.getAmount());
        payment.setMedium(PaymentMedium.UPI);
        payment.setTimestamp(Instant.now());
        payment.setReference(orderId);
        payment.setStatus(PaymentStatus.PENDING);
        
        paymentRepository.save(payment);
        
        String paymentLink = generatePaymentLink(orderId, request.getAmount(), request.getDescription());
        
        log.info("UPI payment link generated: Order ID {}, Payment ID {}", orderId, paymentId);
        
        return new UpiPaymentLinkResponse(paymentLink, paymentId, orderId);
    }
    
    private String generatePaymentLink(String orderId, java.math.BigDecimal amount, String description) {
        if (cashfreeClientId == null || cashfreeClientId.isEmpty()) {
            log.warn("Cashfree credentials not configured. Returning stub payment link.");
            return "https://payments.example.com/pay?orderId=" + orderId + "&amount=" + amount;
        }
        
        return "https://payments.cashfree.com/pay?orderId=" + orderId + "&amount=" + amount;
    }
}

