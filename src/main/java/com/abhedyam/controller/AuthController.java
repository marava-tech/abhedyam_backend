package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.dto.AuthResponse;
import com.abhedyam.dto.FirebaseLoginRequest;
import com.abhedyam.dto.OtpSendRequest;
import com.abhedyam.dto.OtpVerifyRequest;
import com.abhedyam.service.AuthService;
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
    private final AuthService authServiceImpl;
    
    @PostMapping("/otp/send")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> sendOtp(@Valid @RequestBody OtpSendRequest request) {
        authService.sendOtp(request.getEmail());
        return ApiResponse.success(null);
    }
    
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<AuthResponse> login(@Valid @RequestBody OtpVerifyRequest request) {
        AuthResponse response = authService.login(request);
        return ApiResponse.success(response);
    }
    
    @PostMapping("/firebase/login")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<AuthResponse> loginWithFirebase(@Valid @RequestBody FirebaseLoginRequest request) {
        AuthResponse response = authServiceImpl.loginWithFirebase(request);
        return ApiResponse.success(response);
    }
}

