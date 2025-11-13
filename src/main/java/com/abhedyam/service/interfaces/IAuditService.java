package com.abhedyam.service.interfaces;

import com.abhedyam.model.Audit;

import java.util.List;
import java.util.UUID;

public interface IAuditService {
    Audit create(Audit audit);
    Audit getById(UUID id);
    List<Audit> getAll();
    List<Audit> getByOwnerId(UUID ownerId);
    Audit update(UUID id, Audit auditDetails);
    void delete(UUID id);
}

