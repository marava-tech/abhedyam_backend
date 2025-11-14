package com.abhedyam.service.interfaces;

public interface IEmailService {
    void sendEmail(String to, String subject, String body);
    boolean sendEmailWithRetry(String to, String subject, String body, int maxRetries);
    void sendOtpEmail(String to, String otp);
}

