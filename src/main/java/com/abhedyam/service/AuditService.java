package com.abhedyam.service;

import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Audit;
import com.abhedyam.model.enums.AuditAction;
import com.abhedyam.model.enums.AuditType;
import com.abhedyam.repository.AuditRepository;
import com.abhedyam.service.interfaces.IAuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService implements IAuditService {
    
    private final AuditRepository auditRepository;
    
    @Override
    public Audit create(Audit audit) {
        if (audit.getTimestamp() == null) {
            audit.setTimestamp(Instant.now());
        }
        return auditRepository.save(audit);
    }
    
    public Audit getById(UUID id) {
        return auditRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Audit not found with id: " + id));
    }
    
    public List<Audit> getAll() {
        return auditRepository.findAll();
    }
    
    public List<Audit> getByOwnerId(UUID ownerId) {
        return auditRepository.findByOwnerId(ownerId);
    }
    
    @Override
    @Async("virtualThreadExecutor")
    @Transactional
    public void logFinancialOperation(AuditType type, AuditAction action, UUID entityId, 
                                      UUID ownerId, BigDecimal amount, String details) {
        try {
            Audit audit = new Audit();
            audit.setOwnerId(ownerId);
            audit.setType(type);
            audit.setAction(action);
            audit.setEntityId(entityId);
            audit.setAmount(amount);
            audit.setHeadline(String.format("%s %s", type.name(), action.name()));
            audit.setDescription(details);
            audit.setTimestamp(Instant.now());
            
            auditRepository.save(audit);
            log.info("Financial operation logged: type={}, action={}, entityId={}, amount={}", 
                type, action, entityId, amount);
        } catch (Exception e) {
            log.error("Failed to log financial operation: type={}, action={}, entityId={}", 
                type, action, entityId, e);
        }
    }
    
    @Override
    @Async("virtualThreadExecutor")
    @Transactional
    public void logStockChange(UUID productId, UUID ownerId, String productName, BigDecimal oldStock, 
                               BigDecimal newStock, String source, String details) {
        try {
            Audit audit = new Audit();
            audit.setOwnerId(ownerId);
            audit.setType(AuditType.STOCK);
            audit.setAction(AuditAction.UPDATE);
            audit.setEntityId(productId);
            audit.setAmount(newStock.subtract(oldStock));
            
            BigDecimal change = newStock.subtract(oldStock);
            String changeText = change.compareTo(BigDecimal.ZERO) > 0 ? "increased" : "decreased";
            String sourceText = getReadableSourceText(source);
            
            audit.setHeadline(String.format("%s stock %s from %s to %s", productName, changeText, oldStock, newStock));
            
            String description = String.format("Stock %s for %s: %s → %s (%s)", 
                changeText, productName, oldStock, newStock, sourceText);
            if (details != null && !details.trim().isEmpty()) {
                description += String.format(". %s", details);
            }
            audit.setDescription(description);
            audit.setTimestamp(Instant.now());
            
            auditRepository.save(audit);
            log.info("Stock change logged: productId={}, productName={}, oldStock={}, newStock={}, source={}", 
                productId, productName, oldStock, newStock, source);
        } catch (Exception e) {
            log.error("Failed to log stock change: productId={}, productName={}, oldStock={}, newStock={}", 
                productId, productName, oldStock, newStock, e);
        }
    }
    
    private String getReadableSourceText(String source) {
        if (source == null) return "Unknown";
        return switch (source) {
            case "PURCHASE_IN" -> "Purchase received";
            case "SALE_OUT" -> "Sale transaction";
            case "MANUAL_ADJUSTMENT" -> "Manual adjustment";
            case "SYNC_FROM_LEDGER" -> "Stock sync";
            default -> source;
        };
    }
    
    @Override
    @Async("virtualThreadExecutor")
    @Transactional
    public void logSaleCreation(UUID saleId, UUID ownerId, UUID customerId, String customerName, BigDecimal amount, String transactionId) {
        try {
            Audit audit = new Audit();
            audit.setOwnerId(ownerId);
            audit.setType(AuditType.SALE);
            audit.setAction(AuditAction.CREATE);
            audit.setEntityId(saleId);
            audit.setAmount(amount);
            audit.setHeadline(String.format("Sale created for %s", customerName));
            audit.setDescription(String.format("New sale transaction for %s with total amount ₹%s. Transaction ID: %s", 
                customerName, amount, transactionId));
            audit.setTimestamp(Instant.now());
            
            auditRepository.save(audit);
            log.info("Sale creation logged: saleId={}, customerId={}, customerName={}, amount={}", 
                saleId, customerId, customerName, amount);
        } catch (Exception e) {
            log.error("Failed to log sale creation: saleId={}, customerId={}, customerName={}", 
                saleId, customerId, customerName, e);
        }
    }
    
    @Override
    @Async("virtualThreadExecutor")
    @Transactional
    public void logSaleCancellation(UUID saleId, UUID ownerId, UUID customerId, String customerName, BigDecimal amount, String transactionId) {
        try {
            Audit audit = new Audit();
            audit.setOwnerId(ownerId);
            audit.setType(AuditType.SALE);
            audit.setAction(AuditAction.DELETE);
            audit.setEntityId(saleId);
            audit.setAmount(amount.negate());
            audit.setHeadline(String.format("Sale cancelled for %s", customerName));
            audit.setDescription(String.format("Sale transaction cancelled for %s. Refunded amount: ₹%s. Transaction ID: %s", 
                customerName, amount, transactionId));
            audit.setTimestamp(Instant.now());
            
            auditRepository.save(audit);
            log.info("Sale cancellation logged: saleId={}, customerId={}, customerName={}, amount={}", 
                saleId, customerId, customerName, amount);
        } catch (Exception e) {
            log.error("Failed to log sale cancellation: saleId={}, customerId={}, customerName={}", 
                saleId, customerId, customerName, e);
        }
    }
    
    @Override
    @Async("virtualThreadExecutor")
    @Transactional
    public void logProductCreation(UUID productId, UUID ownerId, String productName, String productCode) {
        try {
            Audit audit = new Audit();
            audit.setOwnerId(ownerId);
            audit.setType(AuditType.PRODUCT);
            audit.setAction(AuditAction.PRODUCT_CREATED);
            audit.setEntityId(productId);
            audit.setAmount(BigDecimal.ZERO);
            audit.setHeadline(String.format("%s product created", productName));
            audit.setDescription(String.format("New product '%s' added to inventory with product code: %s", 
                productName, productCode));
            audit.setTimestamp(Instant.now());
            
            auditRepository.save(audit);
            log.info("Product creation logged: productId={}, name={}, code={}", productId, productName, productCode);
        } catch (Exception e) {
            log.error("Failed to log product creation: productId={}, name={}, code={}", 
                productId, productName, productCode, e);
        }
    }
    
    @Override
    @Async("virtualThreadExecutor")
    @Transactional
    public void logReminderCreation(UUID reminderId, UUID ownerId, UUID customerId, String customerName, String reminderText) {
        try {
            Audit audit = new Audit();
            audit.setOwnerId(ownerId);
            audit.setType(AuditType.REMINDER);
            audit.setAction(AuditAction.REMINDER_CREATED);
            audit.setEntityId(reminderId);
            audit.setAmount(BigDecimal.ZERO);
            audit.setHeadline(String.format("Reminder created for %s", customerName));
            
            String reminderPreview = reminderText != null && reminderText.length() > 100 
                ? reminderText.substring(0, 100) + "..." 
                : (reminderText != null ? reminderText : "No text provided");
            audit.setDescription(String.format("Reminder scheduled for customer %s. Message: %s", 
                customerName, reminderPreview));
            audit.setTimestamp(Instant.now());
            
            auditRepository.save(audit);
            log.info("Reminder creation logged: reminderId={}, customerId={}, customerName={}", reminderId, customerId, customerName);
        } catch (Exception e) {
            log.error("Failed to log reminder creation: reminderId={}, customerId={}, customerName={}", 
                reminderId, customerId, customerName, e);
        }
    }
    
    @Override
    @Async("virtualThreadExecutor")
    @Transactional
    public void logPaymentSuccess(UUID paymentId, UUID ownerId, UUID customerId, String customerName, UUID saleItemId, String productName, BigDecimal amount, String reference) {
        try {
            Audit audit = new Audit();
            audit.setOwnerId(ownerId);
            audit.setType(AuditType.PAYMENT);
            audit.setAction(AuditAction.UPDATE);
            audit.setEntityId(paymentId);
            audit.setAmount(amount);
            audit.setHeadline(String.format("Payment received from %s", customerName));
            
            String description = String.format("Payment of ₹%s received from %s for product '%s'", 
                amount, customerName, productName);
            if (reference != null && !reference.isEmpty()) {
                description += String.format(". Payment reference: %s", reference);
            }
            audit.setDescription(description);
            audit.setTimestamp(Instant.now());
            
            auditRepository.save(audit);
            log.info("Payment success logged: paymentId={}, customerId={}, customerName={}, amount={}", 
                paymentId, customerId, customerName, amount);
        } catch (Exception e) {
            log.error("Failed to log payment success: paymentId={}, customerId={}, customerName={}", 
                paymentId, customerId, customerName, e);
        }
    }
    
    @Transactional
    public Audit update(UUID id, Audit auditDetails) {
        Audit audit = getById(id);
        if (auditDetails.getHeadline() != null) audit.setHeadline(auditDetails.getHeadline());
        if (auditDetails.getDescription() != null) audit.setDescription(auditDetails.getDescription());
        return auditRepository.save(audit);
    }
}
