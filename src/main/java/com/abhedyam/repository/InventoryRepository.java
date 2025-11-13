package com.abhedyam.repository;

import com.abhedyam.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
    List<Inventory> findByOwnerId(UUID ownerId);
    Optional<Inventory> findByOwnerIdAndProductId(UUID ownerId, UUID productId);
}

