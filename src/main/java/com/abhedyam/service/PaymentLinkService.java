package com.abhedyam.service;

import com.abhedyam.dto.UpiPaymentLinkRequest;
import com.abhedyam.dto.UpiPaymentLinkResponse;
import com.abhedyam.exception.BusinessException;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Customer;
import com.abhedyam.model.Payment;
import com.abhedyam.model.Product;
import com.abhedyam.model.SaleItem;
import com.abhedyam.model.enums.PaymentMedium;
import com.abhedyam.model.enums.PaymentStatus;
import java.math.BigDecimal;
import com.abhedyam.repository.CustomerRepository;
import com.abhedyam.repository.PaymentRepository;
import com.abhedyam.repository.ProductRepository;
import com.abhedyam.repository.SaleItemRepository;
import com.abhedyam.service.interfaces.IAuditService;
import com.abhedyam.service.interfaces.ICustomerService;
import com.abhedyam.service.interfaces.IPaymentLinkService;
import com.abhedyam.util.SecurityUtil;
import com.abhedyam.constants.ErrorCodes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentLinkService implements IPaymentLinkService {
    
    private final SaleItemRepository saleItemRepository;
    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final IAuditService auditService;
    private final ICustomerService customerService;
    private final CustomerService customerServiceConcrete;
    private final PaymentService paymentService;
    private final StatsService statsService;
    
    @Override
    @Transactional
    public UpiPaymentLinkResponse generateUpiPaymentLink(UpiPaymentLinkRequest request) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCodes.INVALID_AMOUNT, "Payment amount must be greater than zero");
        }
        
        SaleItem saleItem = saleItemRepository.findById(request.getSaleItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Sale item not found"));
        
        if (!saleItem.getOwnerId().equals(ownerId)) {
            throw new BusinessException(ErrorCodes.UNAUTHORIZED, "You don't have permission to access this sale item");
        }
        
        Product product = productRepository.findById(saleItem.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        Customer customer = customerRepository.findById(saleItem.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        long existingPaymentCount = paymentRepository.countBySaleItemId(request.getSaleItemId());
        long paymentNumber = existingPaymentCount + 1;
        String ordinalNumber = getOrdinalNumber(paymentNumber);
        
        String description = String.format("%s payment for %s by %s", 
                ordinalNumber, product.getName(), customer.getName());
        
        String orderId = "ORDER_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
        
        Payment payment = new Payment();
        payment.setCustomerId(saleItem.getCustomerId());
        payment.setOwnerId(ownerId);
        payment.setSaleItemId(request.getSaleItemId());
        payment.setAmount(request.getAmount());
        payment.setMedium(PaymentMedium.UPI);
        payment.setTimestamp(Instant.now());
        payment.setReference(orderId);
        payment.setStatus(PaymentStatus.PENDING);
        
        Payment savedPayment = paymentRepository.save(payment);
        String paymentId = savedPayment.getId().toString();
        
        auditService.logPaymentCreation(
            savedPayment.getId(),
            ownerId,
            customer.getId(),
            customer.getName(),
            request.getSaleItemId(),
            product.getName(),
            savedPayment.getAmount(),
            savedPayment.getReference(),
            savedPayment.getStatus().toString(),
            savedPayment.getMedium().toString()
        );
        
        try {
            paymentService.invalidateOwnerCaches(ownerId);
            customerService.invalidateCustomerSummaryCache(ownerId, customer.getId());
            customerServiceConcrete.invalidateOwnerCaches(ownerId);
            statsService.invalidateOwnerCaches(ownerId);
        } catch (Exception e) {
            log.warn("Error invalidating caches on payment link creation: {}", e.getMessage());
        }
        
        String paymentLink = generatePaymentLink(orderId, request.getAmount(), description);
        
        log.info("UPI payment link generated: Order ID {}, Payment ID {}, Sale Item ID {}", orderId, paymentId, request.getSaleItemId());
        
        return new UpiPaymentLinkResponse(paymentLink, paymentId, orderId);
    }
    
    private String getOrdinalNumber(long number) {
        if (number % 100 >= 11 && number % 100 <= 13) {
            return number + "th";
        }
        switch ((int) (number % 10)) {
            case 1:
                return number + "st";
            case 2:
                return number + "nd";
            case 3:
                return number + "rd";
            default:
                return number + "th";
        }
    }
    
    private String generatePaymentLink(String orderId, java.math.BigDecimal amount, String description) {
        return "https://payments.example.com/pay?orderId=" + orderId + "&amount=" + amount;
    }
}

