package com.abhedyam.repository;

import com.abhedyam.model.OwnerOnboardingRequest;
import com.abhedyam.model.enums.OnboardingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OwnerOnboardingRequestRepository
        extends JpaRepository<OwnerOnboardingRequest, UUID>, JpaSpecificationExecutor<OwnerOnboardingRequest> {

    List<OwnerOnboardingRequest> findByOwnerId(UUID ownerId);

    List<OwnerOnboardingRequest> findByStatus(OnboardingStatus status);

    List<OwnerOnboardingRequest> findByOwnerIdAndStatus(UUID ownerId, OnboardingStatus status);
}
