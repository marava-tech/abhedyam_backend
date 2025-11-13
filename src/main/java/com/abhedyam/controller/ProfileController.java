package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.dto.ProfileUpdateRequest;
import com.abhedyam.model.Owner;
import com.abhedyam.model.User;
import com.abhedyam.service.interfaces.IProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {
    
    private final IProfileService profileService;
    
    @GetMapping("/me")
    public ApiResponse<User> getCurrentUserProfile() {
        return ApiResponse.success(profileService.getCurrentUserProfile());
    }
    
    @GetMapping("/owner")
    public ApiResponse<Owner> getCurrentOwnerProfile() {
        return ApiResponse.success(profileService.getCurrentOwnerProfile());
    }
    
    @PutMapping("/me")
    public ApiResponse<User> updateProfile(@RequestBody ProfileUpdateRequest request) {
        return ApiResponse.success(profileService.updateProfile(request));
    }
}

