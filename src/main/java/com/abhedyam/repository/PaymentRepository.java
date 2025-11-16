package com.abhedyam.repository;

import com.abhedyam.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByOwnerId(UUID ownerId);
    List<Payment> findByCustomerId(UUID customerId);
    long countBySaleItemId(UUID saleItemId);
}

