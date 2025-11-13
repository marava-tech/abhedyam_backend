package com.abhedyam.service.interfaces;

public interface IOtpService {
    void sendOtp(String phone);
    boolean verifyOtp(String phone, String otp);
    boolean isRateLimited(String phone);
}

