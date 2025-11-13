package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.dto.AuthResponse;
import com.abhedyam.dto.OtpSendRequest;
import com.abhedyam.dto.OtpVerifyRequest;
import com.abhedyam.service.interfaces.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final IAuthService authService;
    
    @PostMapping("/otp/send")
    @ResponseStatus(HttpStatus.OK)
    @com.abhedyam.annotation.RateLimited(maxRequests = 3, windowSeconds = 60, keyPrefix = "otp.send")
    public ApiResponse<Void> sendOtp(@Valid @RequestBody OtpSendRequest request) {
        authService.sendOtp(request.getPhone());
        return ApiResponse.success(null);
    }
    
    @PostMapping("/otp/verify")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<AuthResponse> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        AuthResponse response = authService.verifyOtp(request);
        return ApiResponse.success(response);
    }
}

