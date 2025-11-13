package com.abhedyam.repository;

import com.abhedyam.model.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, UUID> {
    List<SaleItem> findByOwnerId(UUID ownerId);
    List<SaleItem> findByCustomerId(UUID customerId);
    List<SaleItem> findByTransactionId(String transactionId);
}

