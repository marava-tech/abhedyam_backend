package com.abhedyam.service;

import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Audit;
import com.abhedyam.model.User;
import com.abhedyam.model.enums.AuditAction;
import com.abhedyam.model.enums.AuditType;
import com.abhedyam.repository.AuditRepository;
import com.abhedyam.repository.UserRepository;
import com.abhedyam.service.interfaces.IAuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService implements IAuditService {
    
    private final AuditRepository auditRepository;
    private final UserRepository userRepository;
    
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
            String headline = formatFinancialOperationHeadline(type, action, amount);
            audit.setHeadline(headline);
            audit.setDescription(formatFinancialOperationDescription(type, action, amount, details));
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
            
            String description = String.format("Stock %s for product '%s' from %s to %s. Reason: %s", 
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
            audit.setHeadline(String.format("Sale of ₹%s created for %s", amount, customerName));
            audit.setDescription(String.format("Created a new sale transaction for customer %s with total amount ₹%s. Transaction ID: %s", 
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
            audit.setHeadline(String.format("Sale of ₹%s cancelled for %s", amount, customerName));
            audit.setDescription(String.format("Sale transaction cancelled for customer %s. Refunded amount: ₹%s. Transaction ID: %s", 
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
            audit.setHeadline(String.format("Product '%s' added to inventory", productName));
            audit.setDescription(String.format("Added new product '%s' to inventory with product code: %s", 
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
    public void logReminderCreation(UUID reminderId, UUID ownerId, UUID customerId, String customerName, 
                                   String reminderName, String reminderText, Instant reminderTime) {
        try {
            Audit audit = new Audit();
            audit.setOwnerId(ownerId);
            audit.setType(AuditType.REMINDER);
            audit.setAction(AuditAction.REMINDER_CREATED);
            audit.setEntityId(reminderId);
            audit.setAmount(BigDecimal.ZERO);
            
            String headline;
            String description;
            
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a", Locale.ENGLISH)
                .withZone(ZoneId.systemDefault());
            String formattedTime = reminderTime != null ? timeFormatter.format(reminderTime) : "scheduled time";
            
            String reminderNameDisplay = reminderName != null && !reminderName.trim().isEmpty() 
                ? reminderName 
                : "Reminder";
            
            String reminderPreview = reminderText != null && reminderText.length() > 150 
                ? reminderText.substring(0, 150) + "..." 
                : (reminderText != null && !reminderText.trim().isEmpty() ? reminderText : "No message provided");
            
            if (customerId != null && customerName != null) {
                headline = String.format("Reminder '%s' created for %s", reminderNameDisplay, customerName);
                description = String.format("A reminder has been scheduled for customer %s on %s. " +
                    "The reminder message: \"%s\"", customerName, formattedTime, reminderPreview);
            } else if (ownerId != null) {
                String ownerName = "You";
                try {
                    User owner = userRepository.findById(ownerId).orElse(null);
                    if (owner != null && owner.getName() != null && !owner.getName().trim().isEmpty()) {
                        ownerName = owner.getName();
                    }
                } catch (Exception e) {
                    log.warn("Could not fetch owner name for reminder audit: {}", e.getMessage());
                }
                headline = String.format("Personal reminder '%s' created", reminderNameDisplay);
                String verb = "You".equals(ownerName) ? "have" : "has";
                description = String.format("%s %s created a personal reminder scheduled for %s. " +
                    "Reminder message: \"%s\"", ownerName, verb, formattedTime, reminderPreview);
            } else {
                headline = String.format("Broadcast reminder '%s' created", reminderNameDisplay);
                description = String.format("A broadcast reminder has been scheduled for all users on %s. " +
                    "Message: \"%s\"", formattedTime, reminderPreview);
            }
            
            audit.setHeadline(headline);
            audit.setDescription(description);
            audit.setTimestamp(Instant.now());
            
            auditRepository.save(audit);
            log.info("Reminder creation logged: reminderId={}, name={}, customerId={}, ownerId={}", 
                reminderId, reminderName, customerId, ownerId);
        } catch (Exception e) {
            log.error("Failed to log reminder creation: reminderId={}, customerId={}, ownerId={}", 
                reminderId, customerId, ownerId, e);
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
            audit.setHeadline(String.format("Payment of ₹%s received from %s", amount, customerName));
            
            String description = String.format("Received payment of ₹%s from %s for product '%s'", 
                amount, customerName, productName);
            
            if (reference != null && !reference.trim().isEmpty()) {
                String referenceText = formatPaymentReference(reference);
                description += String.format(". %s", referenceText);
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
    
    private String formatPaymentReference(String reference) {
        if (reference == null || reference.trim().isEmpty()) {
            return "";
        }
        
        String trimmedRef = reference.trim();
        
        if (isImageUrl(trimmedRef)) {
            return "Payment receipt image uploaded";
        }
        
        if (isTransactionId(trimmedRef)) {
            return String.format("Transaction ID: %s", trimmedRef);
        }
        
        if (trimmedRef.length() > 50) {
            return String.format("Reference: %s...", trimmedRef.substring(0, 50));
        }
        
        return String.format("Reference: %s", trimmedRef);
    }
    
    private boolean isImageUrl(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }
        String lowerText = text.toLowerCase();
        return lowerText.startsWith("http://") || lowerText.startsWith("https://") ||
               lowerText.contains(".jpg") || lowerText.contains(".jpeg") || 
               lowerText.contains(".png") || lowerText.contains(".gif") ||
               lowerText.contains("cloudinary") || lowerText.contains("image") ||
               lowerText.contains("/upload/");
    }
    
    private boolean isTransactionId(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }
        String upperText = text.toUpperCase();
        return upperText.startsWith("TXN") || upperText.startsWith("UPI") ||
               upperText.startsWith("PAY") || (text.length() >= 10 && text.matches(".*\\d{10,}.*"));
    }
    
    private String formatFinancialOperationHeadline(AuditType type, AuditAction action, BigDecimal amount) {
        String typeText = type.name().toLowerCase().replace("_", " ");
        String actionText = action.name().toLowerCase().replace("_", " ");
        
        if (amount != null && amount.compareTo(BigDecimal.ZERO) != 0) {
            return String.format("%s %s - ₹%s", capitalizeFirst(typeText), actionText, amount);
        }
        return String.format("%s %s", capitalizeFirst(typeText), actionText);
    }
    
    private String formatFinancialOperationDescription(AuditType type, AuditAction action, BigDecimal amount, String details) {
        String typeText = type.name().toLowerCase().replace("_", " ");
        String actionText = action.name().toLowerCase().replace("_", " ");
        
        StringBuilder description = new StringBuilder();
        description.append(String.format("%s operation: %s", capitalizeFirst(typeText), actionText));
        
        if (amount != null && amount.compareTo(BigDecimal.ZERO) != 0) {
            description.append(String.format(" with amount ₹%s", amount));
        }
        
        if (details != null && !details.trim().isEmpty()) {
            description.append(String.format(". %s", details));
        }
        
        return description.toString();
    }
    
    private String capitalizeFirst(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
    
    @Transactional
    public Audit update(UUID id, Audit auditDetails) {
        Audit audit = getById(id);
        if (auditDetails.getHeadline() != null) audit.setHeadline(auditDetails.getHeadline());
        if (auditDetails.getDescription() != null) audit.setDescription(auditDetails.getDescription());
        return auditRepository.save(audit);
    }
}
