package com.abhedyam.service.interfaces;

import com.abhedyam.model.Audit;
import com.abhedyam.model.enums.AuditAction;
import com.abhedyam.model.enums.AuditType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface IAuditService {
    Audit create(Audit audit);
    Audit getById(UUID id);
    List<Audit> getAll();
    List<Audit> getByOwnerId(UUID ownerId);
    Audit update(UUID id, Audit auditDetails);
    void logFinancialOperation(AuditType type, AuditAction action, UUID entityId, 
                              UUID ownerId, BigDecimal amount, String details);
    void logStockChange(UUID productId, UUID ownerId, BigDecimal oldStock, 
                       BigDecimal newStock, String source, String details);
    void logSaleCreation(UUID saleId, UUID ownerId, UUID customerId, BigDecimal amount, String transactionId);
    void logSaleCancellation(UUID saleId, UUID ownerId, UUID customerId, BigDecimal amount, String transactionId);
}
