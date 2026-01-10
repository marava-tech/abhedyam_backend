package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.dto.CustomerLocationRequest;
import com.abhedyam.dto.CustomerLocationResponse;
import com.abhedyam.dto.LocationDetailsCreateRequest;
import com.abhedyam.dto.LocationDetailsResponse;
import com.abhedyam.dto.LocationDetailsUpdateRequest;
import com.abhedyam.dto.VillageSearchResult;
import com.abhedyam.service.interfaces.ILocationDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/location-details")
@RequiredArgsConstructor
public class LocationDetailsController {
    
    private final ILocationDetailsService locationDetailsService;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<LocationDetailsResponse> create(@Valid @RequestBody LocationDetailsCreateRequest request) {
        return ApiResponse.success(locationDetailsService.create(request));
    }
    
    @GetMapping("/me")
    public ApiResponse<LocationDetailsResponse> getCurrentUserLocation() {
        return ApiResponse.success(locationDetailsService.getCurrentUserLocation());
    }
    
    @GetMapping("/customers/{customerId}")
    public ApiResponse<LocationDetailsResponse> getCustomerLocation(@PathVariable UUID customerId) {
        return ApiResponse.success(locationDetailsService.getCustomerLocation(customerId));
    }
    
    @GetMapping("/users/{userId}")
    public ApiResponse<LocationDetailsResponse> getLocationByUserId(@PathVariable UUID userId) {
        return ApiResponse.success(locationDetailsService.getLocationByUserId(userId));
    }
    
    @GetMapping
    public ApiResponse<List<LocationDetailsResponse>> getAll() {
        return ApiResponse.success(locationDetailsService.getAll());
    }
    
    @PatchMapping("/me")
    public ApiResponse<LocationDetailsResponse> update(@Valid @RequestBody LocationDetailsUpdateRequest request) {
        return ApiResponse.success(locationDetailsService.updateCurrentUserLocation(request));
    }
    
    @PatchMapping("/customers/{customerId}")
    public ApiResponse<LocationDetailsResponse> updateCustomerLocation(
            @PathVariable UUID customerId,
            @Valid @RequestBody LocationDetailsUpdateRequest request) {
        return ApiResponse.success(locationDetailsService.updateCustomerLocation(customerId, request));
    }
    
    @GetMapping("/search-villages")
    public ApiResponse<List<VillageSearchResult>> searchVillages(@RequestParam("name") String name) {
        return ApiResponse.success(locationDetailsService.searchVillagesByName(name));
    }
    
    @GetMapping("/villages")
    public ApiResponse<List<com.abhedyam.dto.VillageResponse>> getVillages(
            @RequestParam(value = "name", required = false, defaultValue = "") String name) {
        return ApiResponse.success(locationDetailsService.searchVillagesByNameWithCount(name));
    }
    
    @PostMapping("/customers/locations")
    public ApiResponse<List<CustomerLocationResponse>> getCustomerLocations(@Valid @RequestBody CustomerLocationRequest request) {
        return ApiResponse.success(locationDetailsService.getCustomerLocations(request));
    }
}

