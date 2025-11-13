package com.abhedyam.service;

import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Audit;
import com.abhedyam.repository.AuditRepository;
import com.abhedyam.service.interfaces.IAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditService implements IAuditService {
    
    private final AuditRepository auditRepository;
    
    public Audit create(Audit audit) {
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
    
    @Transactional
    public Audit update(UUID id, Audit auditDetails) {
        Audit audit = getById(id);
        if (auditDetails.getHeadline() != null) audit.setHeadline(auditDetails.getHeadline());
        if (auditDetails.getDescription() != null) audit.setDescription(auditDetails.getDescription());
        return auditRepository.save(audit);
    }
    
    @Transactional
    public void delete(UUID id) {
        Audit audit = getById(id);
        audit.setDeletedAt(Instant.now());
        audit.setIsActive(false);
        auditRepository.save(audit);
    }
}

