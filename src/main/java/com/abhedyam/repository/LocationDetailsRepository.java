package com.abhedyam.repository;

import com.abhedyam.model.LocationDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LocationDetailsRepository extends JpaRepository<LocationDetails, UUID> {
    Optional<LocationDetails> findByUserId(UUID userId);
}

