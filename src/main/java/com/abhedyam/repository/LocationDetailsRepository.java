package com.abhedyam.repository;

import com.abhedyam.model.LocationDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LocationDetailsRepository extends JpaRepository<LocationDetails, UUID> {
    Optional<LocationDetails> findByUserId(UUID userId);
    
    @Query("SELECT DISTINCT ld.village FROM LocationDetails ld " +
           "INNER JOIN Customer c ON c.id = ld.userId " +
           "WHERE c.ownerId = :ownerId " +
           "AND ld.village IS NOT NULL " +
           "AND LOWER(ld.village) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<String> findDistinctVillagesByNameContainingIgnoreCaseAndOwnerId(@Param("name") String name,
                                                                           @Param("ownerId") UUID ownerId);
}

