package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.dto.CustomerLocationRequest;
import com.abhedyam.dto.CustomerLocationResponse;
import com.abhedyam.dto.LocationDetailsResponse;
import com.abhedyam.dto.LocationDetailsUpdateRequest;
import com.abhedyam.dto.PageResponse;
import com.abhedyam.service.interfaces.ILocationDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/location-details")
@RequiredArgsConstructor
public class LocationDetailsController {

    private final ILocationDetailsService locationDetailsService;

    @GetMapping("/customers/{customerId}")
    public ApiResponse<LocationDetailsResponse> getCustomerLocation(@PathVariable UUID customerId) {
        LocationDetailsResponse location = locationDetailsService.getCustomerLocation(customerId);
        if (location == null) {
            throw new com.abhedyam.exception.ResourceNotFoundException("LocationDetails not found for customer");
        }
        return ApiResponse.success(location);
    }

    @GetMapping("/users/{userId}")
    public ApiResponse<LocationDetailsResponse> getLocationByUserId(@PathVariable UUID userId) {
        return ApiResponse.success(locationDetailsService.getLocationByUserId(userId));
    }

    @PatchMapping("/users/{userId}")
    public ApiResponse<LocationDetailsResponse> update(@PathVariable UUID userId,
            @Valid @RequestBody LocationDetailsUpdateRequest request) {
        return ApiResponse.success(locationDetailsService.updateLocationForUser(userId, request));
    }

    @PatchMapping("/customers/{customerId}")
    public ApiResponse<LocationDetailsResponse> updateCustomerLocation(
            @PathVariable UUID customerId,
            @Valid @RequestBody LocationDetailsUpdateRequest request) {
        return ApiResponse.success(locationDetailsService.updateCustomerLocation(customerId, request));
    }

    @GetMapping("/villages")
    public ApiResponse<PageResponse<com.abhedyam.dto.VillageResponse>> getVillages(
            @RequestParam(value = "name", required = false, defaultValue = "") String name,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "20") Integer size) {
        return ApiResponse.success(locationDetailsService.getVillagesPaginated(name, page, size));
    }

    @PostMapping("/customers/locations")
    public ApiResponse<List<CustomerLocationResponse>> getCustomerLocations(
            @Valid @RequestBody CustomerLocationRequest request) {
        return ApiResponse.success(locationDetailsService.getCustomerLocations(request));
    }
}
