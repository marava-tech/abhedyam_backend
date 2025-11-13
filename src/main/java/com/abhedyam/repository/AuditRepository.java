package com.abhedyam.repository;

import com.abhedyam.model.Audit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditRepository extends JpaRepository<Audit, UUID> {
    List<Audit> findByOwnerId(UUID ownerId);
}

