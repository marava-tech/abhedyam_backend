package com.abhedyam.service.interfaces;

import com.abhedyam.dto.OwnerPublicResponse;
import com.abhedyam.dto.OwnerResponse;
import com.abhedyam.dto.OwnerSummaryResponse;
import com.abhedyam.dto.OwnerUpdateRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface IOwnerService {
    OwnerResponse getById(UUID id);
    OwnerResponse updateCurrentOwner(OwnerUpdateRequest request);
    OwnerResponse updateOwnerForOwner(UUID ownerId, OwnerUpdateRequest request);
    List<OwnerPublicResponse> getAllPublic(BigDecimal latitude, BigDecimal longitude);
    OwnerSummaryResponse getOwnerSummary(UUID ownerId);
}

