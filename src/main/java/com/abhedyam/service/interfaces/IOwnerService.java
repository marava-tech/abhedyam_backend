package com.abhedyam.service.interfaces;

import com.abhedyam.dto.OwnerCreateRequest;
import com.abhedyam.dto.OwnerDetailsResponse;
import com.abhedyam.dto.OwnerResponse;
import com.abhedyam.dto.OwnerUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface IOwnerService {
    OwnerResponse create(OwnerCreateRequest request);
    OwnerResponse getById(UUID id);
    OwnerDetailsResponse getOwnerDetails(UUID id);
    List<OwnerResponse> getAll();
    OwnerResponse updateCurrentOwner(OwnerUpdateRequest request);
}

