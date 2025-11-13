package com.abhedyam.repository;

import com.abhedyam.model.SaleItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, UUID> {
    List<SaleItem> findByOwnerId(UUID ownerId);
    List<SaleItem> findByCustomerId(UUID customerId);
    List<SaleItem> findByTransactionId(String transactionId);
    
    @Query("SELECT s FROM SaleItem s WHERE s.ownerId = :ownerId " +
           "AND (:customerId IS NULL OR s.customerId = :customerId) " +
           "AND (:transactionId IS NULL OR s.transactionId = :transactionId) " +
           "AND (:startDate IS NULL OR s.createdAt >= :startDate) " +
           "AND (:endDate IS NULL OR s.createdAt <= :endDate)")
    Page<SaleItem> searchSales(@Param("ownerId") UUID ownerId,
                               @Param("customerId") UUID customerId,
                               @Param("transactionId") String transactionId,
                               @Param("startDate") Instant startDate,
                               @Param("endDate") Instant endDate,
                               Pageable pageable);
}

