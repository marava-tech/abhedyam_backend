package com.abhedyam.repository;

import com.abhedyam.model.Customer;
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
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    List<Customer> findByOwnerId(UUID ownerId);
    
    Optional<Customer> findByPhoneNormalized(String phoneNormalized);
    
    @Query("SELECT c FROM Customer c WHERE c.ownerId = :ownerId " +
           "AND (:searchTerm IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(c.phone) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Customer> searchCustomers(@Param("ownerId") UUID ownerId,
                                   @Param("searchTerm") String searchTerm,
                                   Pageable pageable);
    
    @Query("SELECT c FROM Customer c WHERE c.ownerId = :ownerId " +
           "AND LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Customer> findByNameContainingIgnoreCaseAndOwnerId(@Param("name") String name, 
                                                              @Param("ownerId") UUID ownerId);
}

