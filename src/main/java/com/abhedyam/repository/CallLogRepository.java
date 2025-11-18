package com.abhedyam.repository;

import com.abhedyam.model.CallLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CallLogRepository extends JpaRepository<CallLog, UUID> {
    List<CallLog> findByOwnerId(UUID ownerId);
    List<CallLog> findByCustomerId(UUID customerId);
    boolean existsByKey(String key);
    Optional<CallLog> findByKey(String key);
    
    @Query("SELECT cl FROM CallLog cl WHERE cl.customerId = :customerId " +
           "AND cl.ownerId = :ownerId " +
           "AND (cl.isActive IS NULL OR cl.isActive = true) " +
           "ORDER BY cl.startTime DESC")
    Page<CallLog> findByCustomerIdAndOwnerId(@Param("customerId") UUID customerId,
                                              @Param("ownerId") UUID ownerId,
                                              Pageable pageable);
}

