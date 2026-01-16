package com.abhedyam.repository;

import com.abhedyam.model.Reminder;
import com.abhedyam.model.enums.ReminderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, UUID> {
    List<Reminder> findByOwnerId(UUID ownerId);
    List<Reminder> findByCustomerId(UUID customerId);
    List<Reminder> findByCustomerIdAndOwnerId(UUID customerId, UUID ownerId);
    
    @Query("SELECT r FROM Reminder r WHERE r.status = :status " +
           "AND r.time <= :maxTime " +
           "AND r.isActive = true")
    List<Reminder> findDueReminders(@Param("status") ReminderStatus status, 
                                    @Param("maxTime") Instant maxTime);
    
    @Query("SELECT COUNT(r) FROM Reminder r WHERE r.customerId = :customerId AND r.ownerId = :ownerId")
    long countByCustomerIdAndOwnerId(@Param("customerId") UUID customerId, @Param("ownerId") UUID ownerId);
    
    @Query("SELECT COUNT(r) FROM Reminder r WHERE r.customerId = :customerId AND r.ownerId = :ownerId " +
           "AND r.status = :status")
    long countByCustomerIdAndOwnerIdAndStatus(@Param("customerId") UUID customerId, 
                                               @Param("ownerId") UUID ownerId,
                                               @Param("status") ReminderStatus status);
}

