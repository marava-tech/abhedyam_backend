package com.abhedyam.service;

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
public class Fast2SmsService {
    
    private final RestTemplate restTemplate;
    
    @Value("${app.fast2sms.api-key:}")
    private String apiKey;
    
    private static final String FAST2SMS_URL = "https://www.fast2sms.com/dev/bulkV2";
    
    public void sendOtp(String phone, String otp) {
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("Fast2SMS API key not configured. Skipping SMS send. OTP: {}", otp);
            return;
        }
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("authorization", apiKey);
            
            Map<String, Object> body = new HashMap<>();
            body.put("route", "otp");
            body.put("variables_values", otp);
            body.put("numbers", phone.replace("+", ""));
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                FAST2SMS_URL,
                HttpMethod.POST,
                request,
                String.class
            );
            
            log.info("Fast2SMS response for phone {}: {}", phone, response.getBody());
        } catch (Exception e) {
            log.error("Error sending OTP via Fast2SMS for phone: {}", phone, e);
            throw new RuntimeException("Failed to send OTP", e);
        }
    }
}

