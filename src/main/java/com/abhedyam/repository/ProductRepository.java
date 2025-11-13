package com.abhedyam.repository;

import com.abhedyam.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByOwnerId(UUID ownerId);
    Optional<Product> findByOwnerIdAndCode(UUID ownerId, String code);
}

