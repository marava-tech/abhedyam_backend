package com.abhedyam.service.interfaces;

import com.abhedyam.dto.AuthResponse;
import com.abhedyam.dto.OtpVerifyRequest;

public interface IAuthService {
    void sendOtp(String phone);
    AuthResponse verifyOtp(OtpVerifyRequest request);
}

