package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.dto.OwnerCreateRequest;
import com.abhedyam.dto.OwnerDetailsResponse;
import com.abhedyam.dto.OwnerPublicResponse;
import com.abhedyam.dto.OwnerResponse;
import com.abhedyam.dto.OwnerSummaryResponse;
import com.abhedyam.dto.OwnerUpdateRequest;
import com.abhedyam.service.interfaces.IOwnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/owners")
@RequiredArgsConstructor
@Tag(name = "Owners", description = "Owner management APIs")
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
    
    @PatchMapping("/{id}")
    public ApiResponse<OwnerResponse> updateOwner(@PathVariable UUID id, @Valid @RequestBody OwnerUpdateRequest request) {
        return ApiResponse.success(ownerService.updateOwnerForOwner(id, request));
    }
    
    @GetMapping("/{id}/summary")
    @Operation(summary = "Get owner summary statistics", description = "Get summary statistics for an owner including total pending amount, total customers, number of villages, and total collected amount")
    public ApiResponse<OwnerSummaryResponse> getOwnerSummary(
            @Parameter(description = "Owner ID") 
            @PathVariable UUID id) {
        return ApiResponse.success(ownerService.getOwnerSummary(id));
    }
}

