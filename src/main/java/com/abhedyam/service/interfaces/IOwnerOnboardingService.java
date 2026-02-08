package com.abhedyam.service.interfaces;

import com.abhedyam.dto.OwnerOnboardingCreateRequest;
import com.abhedyam.dto.OwnerOnboardingResponse;
import com.abhedyam.dto.OwnerOnboardingStatusUpdateRequest;
import com.abhedyam.model.enums.OnboardingStatus;

import java.util.List;
import java.util.UUID;

public interface IOwnerOnboardingService {

    OwnerOnboardingResponse createRequest(OwnerOnboardingCreateRequest request);

    OwnerOnboardingResponse updateStatus(UUID requestId, OwnerOnboardingStatusUpdateRequest request);

    OwnerOnboardingResponse getRequest(UUID requestId);

    List<OwnerOnboardingResponse> getRequestsByOwner(UUID ownerId);

    List<OwnerOnboardingResponse> getAllRequests(OnboardingStatus status);
}
