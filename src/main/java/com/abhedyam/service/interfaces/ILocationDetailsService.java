package com.abhedyam.service.interfaces;

import com.abhedyam.dto.LocationDetailsCreateRequest;
import com.abhedyam.dto.LocationDetailsResponse;
import com.abhedyam.dto.LocationDetailsUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface ILocationDetailsService {
    LocationDetailsResponse create(LocationDetailsCreateRequest request);
    LocationDetailsResponse getById(UUID id);
    LocationDetailsResponse getCurrentUserLocation();
    List<LocationDetailsResponse> getAll();
    LocationDetailsResponse update(UUID id, LocationDetailsUpdateRequest request);
    LocationDetailsResponse updateCurrentUserLocation(LocationDetailsUpdateRequest request);
    void delete(UUID id);
    void deleteCurrentUserLocation();
}

