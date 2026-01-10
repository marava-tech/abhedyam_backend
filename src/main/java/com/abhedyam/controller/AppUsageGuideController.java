package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.dto.AppUsageGuideResponse;
import com.abhedyam.service.interfaces.IAppUsageGuideService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/app-usage-guide")
@RequiredArgsConstructor
@Tag(name = "App Usage Guide", description = "API for fetching app usage guide and suggestions")
public class AppUsageGuideController {
    
    private final IAppUsageGuideService appUsageGuideService;
    
    @GetMapping
    @Operation(
        summary = "Get app usage guide",
        description = "Retrieves comprehensive guide on how to use the application with sections, items, and step-by-step instructions"
    )
    public ApiResponse<AppUsageGuideResponse> getAppUsageGuide() {
        return ApiResponse.success(appUsageGuideService.getAppUsageGuide());
    }
}

