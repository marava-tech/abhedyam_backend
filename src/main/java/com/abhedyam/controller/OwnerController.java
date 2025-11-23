package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.dto.OwnerCreateRequest;
import com.abhedyam.dto.OwnerDetailsResponse;
import com.abhedyam.dto.OwnerPublicResponse;
import com.abhedyam.dto.OwnerResponse;
import com.abhedyam.dto.OwnerUpdateRequest;
import com.abhedyam.service.interfaces.IOwnerService;
import com.abhedyam.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/owners")
@RequiredArgsConstructor
public class OwnerController {
    
    private final IOwnerService ownerService;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OwnerResponse> create(@Valid @RequestBody OwnerCreateRequest request) {
        return ApiResponse.success(ownerService.create(request));
    }
    
    @GetMapping("/{id}")
    public ApiResponse<OwnerResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(ownerService.getById(id));
    }
    
    @GetMapping("/{id}/details")
    public ApiResponse<OwnerDetailsResponse> getOwnerDetails(@PathVariable UUID id) {
        return ApiResponse.success(ownerService.getOwnerDetails(id));
    }
    
    @GetMapping("/me/details")
    public ApiResponse<OwnerDetailsResponse> getCurrentOwnerDetails() {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        return ApiResponse.success(ownerService.getOwnerDetails(ownerId));
    }
    
    @GetMapping
    public ApiResponse<List<OwnerResponse>> getAll() {
        return ApiResponse.success(ownerService.getAll());
    }
    
    @GetMapping("/public")
    public ApiResponse<List<OwnerPublicResponse>> getAllPublic(
            @Parameter(description = "Latitude for distance calculation (optional)", example = "12.9716")
            @RequestParam(required = false) BigDecimal latitude,
            @Parameter(description = "Longitude for distance calculation (optional)", example = "77.5946")
            @RequestParam(required = false) BigDecimal longitude) {
        return ApiResponse.success(ownerService.getAllPublic(latitude, longitude));
    }
    
    @PatchMapping("/me")
    public ApiResponse<OwnerResponse> updateCurrentOwner(@Valid @RequestBody OwnerUpdateRequest request) {
        return ApiResponse.success(ownerService.updateCurrentOwner(request));
    }
}

