package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.dto.OwnerSettingsUpdateRequest;
import com.abhedyam.model.OwnerSettings;
import com.abhedyam.service.interfaces.IOwnerSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/owner-settings")
@RequiredArgsConstructor
public class OwnerSettingsController {
    
    private final IOwnerSettingsService ownerSettingsService;
    
    @GetMapping("/me")
    public ApiResponse<OwnerSettings> getCurrentOwnerSettings() {
        return ApiResponse.success(ownerSettingsService.getCurrentOwnerSettings());
    }
    
    @PutMapping("/me")
    public ApiResponse<OwnerSettings> updateCurrentOwnerSettings(@RequestBody OwnerSettingsUpdateRequest request) {
        return ApiResponse.success(ownerSettingsService.updateCurrentOwnerSettings(request));
    }
}

