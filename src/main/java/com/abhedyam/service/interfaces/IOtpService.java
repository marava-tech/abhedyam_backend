package com.abhedyam.service.interfaces;

public interface IOtpService {
    void sendOtp(String identifier);
    boolean verifyOtp(String identifier, String otp);
}

