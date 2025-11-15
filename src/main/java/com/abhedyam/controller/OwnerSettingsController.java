package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.dto.OwnerSettingsResponse;
import com.abhedyam.dto.OwnerSettingsUpdateRequest;
import com.abhedyam.service.interfaces.IOwnerSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/owner-settings")
@RequiredArgsConstructor
public class OwnerSettingsController {
    
    private final IOwnerSettingsService ownerSettingsService;
    
    @GetMapping("/me")
    public ApiResponse<OwnerSettingsResponse> getCurrentOwnerSettings() {
        return ApiResponse.success(ownerSettingsService.getCurrentOwnerSettings());
    }
    
    @PatchMapping("/me")
    public ApiResponse<OwnerSettingsResponse> updateCurrentOwnerSettings(@RequestBody OwnerSettingsUpdateRequest request) {
        return ApiResponse.success(ownerSettingsService.updateCurrentOwnerSettings(request));
    }
}

