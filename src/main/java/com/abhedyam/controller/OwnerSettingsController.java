package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.dto.OwnerSettingsResponse;
import com.abhedyam.dto.OwnerSettingsUpdateRequest;
import com.abhedyam.service.interfaces.IOwnerSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/owners/{ownerId}/settings")
@RequiredArgsConstructor
public class OwnerSettingsController {
    
    private final IOwnerSettingsService ownerSettingsService;
    
    @GetMapping
    public ApiResponse<OwnerSettingsResponse> getOwnerSettings(@PathVariable UUID ownerId) {
        return ApiResponse.success(ownerSettingsService.getOwnerSettings(ownerId));
    }
    
    @PatchMapping
    public ApiResponse<OwnerSettingsResponse> updateOwnerSettings(@PathVariable UUID ownerId, @RequestBody OwnerSettingsUpdateRequest request) {
        return ApiResponse.success(ownerSettingsService.updateOwnerSettings(ownerId, request));
    }
}

