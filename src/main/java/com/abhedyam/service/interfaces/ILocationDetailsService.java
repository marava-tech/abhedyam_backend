package com.abhedyam.service.interfaces;

import com.abhedyam.dto.CustomerLocationRequest;
import com.abhedyam.dto.CustomerLocationResponse;
import com.abhedyam.dto.LocationDetailsCreateRequest;
import com.abhedyam.dto.LocationDetailsResponse;
import com.abhedyam.dto.LocationDetailsUpdateRequest;
import com.abhedyam.dto.PageResponse;
import com.abhedyam.dto.VillageSearchResult;

import java.util.List;
import java.util.UUID;

public interface ILocationDetailsService {
    LocationDetailsResponse create(LocationDetailsCreateRequest request);
    LocationDetailsResponse getCustomerLocation(UUID customerId);
    LocationDetailsResponse getLocationByUserId(UUID userId);
    List<LocationDetailsResponse> getAll();
    List<VillageSearchResult> searchVillagesByName(String name);
    List<com.abhedyam.dto.VillageResponse> getAllVillages();
    List<com.abhedyam.dto.VillageResponse> searchVillagesByNameWithCount(String name);
    PageResponse<com.abhedyam.dto.VillageResponse> getVillagesPaginated(String name, Integer page, Integer size);
    LocationDetailsResponse updateCustomerLocation(UUID customerId, LocationDetailsUpdateRequest request);
    LocationDetailsResponse updateLocationForUser(UUID userId, LocationDetailsUpdateRequest request);
    List<CustomerLocationResponse> getCustomerLocations(CustomerLocationRequest request);
}

