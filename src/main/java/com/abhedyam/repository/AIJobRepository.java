package com.abhedyam.repository;

import com.abhedyam.model.AIJob;
import com.abhedyam.model.enums.AIJobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AIJobRepository extends JpaRepository<AIJob, UUID> {
    List<AIJob> findByOwnerId(UUID ownerId);
    List<AIJob> findByOwnerIdAndStatus(UUID ownerId, AIJobStatus status);
    List<AIJob> findByStatus(AIJobStatus status);
}

