package com.abhedyam.service;

import com.abhedyam.service.interfaces.ISmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class Fast2SmsService implements ISmsService {
    
    private final RestTemplate restTemplate;
    
    @Value("${app.fast2sms.api-key:}")
    private String apiKey;
    
    private static final String FAST2SMS_URL = "https://www.fast2sms.com/dev/bulkV2";
    
    public void sendOtp(String phone, String otp) {
        sendSms(phone, "Your OTP is: " + otp);
    }
    
    @Override
    public void sendSms(String phone, String message) {
        sendSmsWithRetry(phone, message, 1);
    }
    
    @Override
    public boolean sendSmsWithRetry(String phone, String message, int maxRetries) {
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("Fast2SMS API key not configured. Skipping SMS send. Message: {}", message);
            return false;
        }
        
        int attempt = 0;
        Exception lastException = null;
        
        while (attempt < maxRetries) {
            attempt++;
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("authorization", apiKey);
                
                Map<String, Object> body = new HashMap<>();
                body.put("route", "q");
                body.put("message", message);
                body.put("numbers", phone.replace("+", ""));
                
                HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
                
                ResponseEntity<String> response = restTemplate.exchange(
                    FAST2SMS_URL,
                    HttpMethod.POST,
                    request,
                    String.class
                );
                
                log.info("Fast2SMS response for phone {} (attempt {}/{}): {}", phone, attempt, maxRetries, response.getBody());
                return true;
            } catch (Exception e) {
                lastException = e;
                log.warn("Error sending SMS via Fast2SMS for phone {} (attempt {}/{}): {}", phone, attempt, maxRetries, e.getMessage());
                
                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(1000 * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error("Interrupted during retry delay", ie);
                        return false;
                    }
                }
            }
        }
        
        log.error("Failed to send SMS via Fast2SMS after {} attempts for phone: {}", maxRetries, phone, lastException);
        return false;
    }
}

