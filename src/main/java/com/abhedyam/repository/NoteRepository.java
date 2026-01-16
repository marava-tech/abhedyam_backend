package com.abhedyam.repository;

import com.abhedyam.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NoteRepository extends JpaRepository<Note, UUID> {
    List<Note> findByOwnerId(UUID ownerId);
    List<Note> findByCustomerId(UUID customerId);
    List<Note> findByCustomerIdAndOwnerId(UUID customerId, UUID ownerId);
    
    @Query("SELECT COUNT(n) FROM Note n WHERE n.customerId = :customerId AND n.ownerId = :ownerId")
    long countByCustomerIdAndOwnerId(@Param("customerId") UUID customerId, @Param("ownerId") UUID ownerId);
}

