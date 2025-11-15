package com.abhedyam.service;

import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Audit;
import com.abhedyam.model.enums.AuditAction;
import com.abhedyam.model.enums.AuditType;
import com.abhedyam.repository.AuditRepository;
import com.abhedyam.service.interfaces.IAuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    @Transactional
    public void logFinancialOperation(AuditType type, AuditAction action, UUID entityId, 
                                      UUID ownerId, BigDecimal amount, String details) {
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
    }
    
    @Override
    @Transactional
    public void logStockChange(UUID productId, UUID ownerId, BigDecimal oldStock, 
                               BigDecimal newStock, String source, String details) {
        Audit audit = new Audit();
        audit.setOwnerId(ownerId);
        audit.setType(AuditType.STOCK);
        audit.setAction(AuditAction.UPDATE);
        audit.setEntityId(productId);
        audit.setAmount(newStock.subtract(oldStock));
        audit.setHeadline(String.format("Stock change: %s -> %s", oldStock, newStock));
        audit.setDescription(String.format("Source: %s. %s", source, details));
        audit.setTimestamp(Instant.now());
        
        auditRepository.save(audit);
        log.info("Stock change logged: productId={}, oldStock={}, newStock={}, source={}", 
            productId, oldStock, newStock, source);
    }
    
    @Override
    @Transactional
    public void logSaleCreation(UUID saleId, UUID ownerId, UUID customerId, BigDecimal amount, String transactionId) {
        logFinancialOperation(
            AuditType.SALE,
            AuditAction.CREATE,
            saleId,
            ownerId,
            amount,
            String.format("Sale created: transactionId=%s, customerId=%s", transactionId, customerId)
        );
    }
    
    @Override
    @Transactional
    public void logSaleCancellation(UUID saleId, UUID ownerId, UUID customerId, BigDecimal amount, String transactionId) {
        logFinancialOperation(
            AuditType.SALE,
            AuditAction.DELETE,
            saleId,
            ownerId,
            amount.negate(),
            String.format("Sale cancelled: transactionId=%s, customerId=%s", transactionId, customerId)
        );
    }
    
    @Transactional
    public Audit update(UUID id, Audit auditDetails) {
        Audit audit = getById(id);
        if (auditDetails.getHeadline() != null) audit.setHeadline(auditDetails.getHeadline());
        if (auditDetails.getDescription() != null) audit.setDescription(auditDetails.getDescription());
        return auditRepository.save(audit);
    }
}
