package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.dto.AppConfigResponse;
import com.abhedyam.service.interfaces.IAppConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/app-config")
@RequiredArgsConstructor
@Tag(name = "App Configuration", description = "APIs for fetching app configuration")
public class AppConfigController {
    
    private final IAppConfigService appConfigService;
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get app configuration by user ID", 
               description = "Fetches app configuration settings for a specific user. Returns default values if user is not an owner or has no settings.")
    public ApiResponse<AppConfigResponse> getAppConfigByUserId(@PathVariable UUID userId) {
        return ApiResponse.success(appConfigService.getAppConfigByUserId(userId));
    }
}

