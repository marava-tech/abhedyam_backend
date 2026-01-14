package com.abhedyam.repository;

import com.abhedyam.model.Subscription;
import com.abhedyam.model.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    
    Optional<Subscription> findByRazorpayOrderId(String razorpayOrderId);
    
    Optional<Subscription> findByOwnerId(UUID ownerId);
    
    List<Subscription> findAllByOwnerIdOrderByCreatedAtDesc(UUID ownerId);
    
    Optional<Subscription> findFirstByOwnerIdAndStatusOrderByCreatedAtDesc(UUID ownerId, SubscriptionStatus status);
}

