package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.dto.LocationDetailsCreateRequest;
import com.abhedyam.dto.LocationDetailsResponse;
import com.abhedyam.dto.LocationDetailsUpdateRequest;
import com.abhedyam.service.interfaces.ILocationDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    
    @GetMapping
    public ApiResponse<List<LocationDetailsResponse>> getAll() {
        return ApiResponse.success(locationDetailsService.getAll());
    }
    
    @PatchMapping("/me")
    public ApiResponse<LocationDetailsResponse> update(@Valid @RequestBody LocationDetailsUpdateRequest request) {
        return ApiResponse.success(locationDetailsService.updateCurrentUserLocation(request));
    }
    
    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> delete() {
        locationDetailsService.deleteCurrentUserLocation();
        return ApiResponse.success(null);
    }
}

