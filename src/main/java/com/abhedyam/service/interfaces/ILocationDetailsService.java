package com.abhedyam.service.interfaces;

import com.abhedyam.dto.CustomerLocationRequest;
import com.abhedyam.dto.CustomerLocationResponse;
import com.abhedyam.dto.LocationDetailsCreateRequest;
import com.abhedyam.dto.LocationDetailsResponse;
import com.abhedyam.dto.LocationDetailsUpdateRequest;
import com.abhedyam.dto.VillageSearchResult;

import java.util.List;
import java.util.UUID;

public interface ILocationDetailsService {
    LocationDetailsResponse create(LocationDetailsCreateRequest request);
    LocationDetailsResponse getById(UUID id);
    LocationDetailsResponse getCurrentUserLocation();
    LocationDetailsResponse getCustomerLocation(UUID customerId);
    List<LocationDetailsResponse> getAll();
    List<VillageSearchResult> searchVillagesByName(String name);
    LocationDetailsResponse update(UUID id, LocationDetailsUpdateRequest request);
    LocationDetailsResponse updateCurrentUserLocation(LocationDetailsUpdateRequest request);
    LocationDetailsResponse updateCustomerLocation(UUID customerId, LocationDetailsUpdateRequest request);
    List<CustomerLocationResponse> getCustomerLocations(CustomerLocationRequest request);
}

