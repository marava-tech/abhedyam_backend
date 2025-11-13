package com.abhedyam.service;

import com.abhedyam.exception.BusinessException;
import com.abhedyam.service.interfaces.IOtpService;
import com.abhedyam.util.PhoneUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService implements IOtpService {
    
    private final RedisTemplate<String, String> redisTemplate;
    private final Fast2SmsService fast2SmsService;
    
    private static final String OTP_PREFIX = "otp:";
    private static final String RATE_LIMIT_PREFIX = "rate_limit:";
    private static final int OTP_EXPIRY_MINUTES = 10;
    private static final int RATE_LIMIT_WINDOW_MINUTES = 1;
    private static final int MAX_OTP_REQUESTS_PER_WINDOW = 3;
    
    @Override
    public void sendOtp(String phone) {
        String normalizedPhone = PhoneUtil.normalizePhone(phone);
        
        if (!PhoneUtil.isValidPhone(normalizedPhone)) {
            throw new BusinessException("INVALID_PHONE", "Invalid phone number format");
        }
        
        if (isRateLimited(normalizedPhone)) {
            throw new BusinessException("RATE_LIMIT_EXCEEDED", 
                "Too many OTP requests. Please try again after " + RATE_LIMIT_WINDOW_MINUTES + " minute(s)");
        }
        
        String otp = PhoneUtil.generateOTP();
        String otpKey = OTP_PREFIX + normalizedPhone;
        String rateLimitKey = RATE_LIMIT_PREFIX + normalizedPhone;
        
        redisTemplate.opsForValue().set(otpKey, otp, OTP_EXPIRY_MINUTES, TimeUnit.MINUTES);
        
        Long requestCount = redisTemplate.opsForValue().increment(rateLimitKey);
        if (requestCount == 1) {
            redisTemplate.expire(rateLimitKey, RATE_LIMIT_WINDOW_MINUTES, TimeUnit.MINUTES);
        }
        
        fast2SmsService.sendOtp(normalizedPhone, otp);
        
        log.info("OTP sent to phone: {}", normalizedPhone);
    }
    
    @Override
    public boolean verifyOtp(String phone, String otp) {
        String normalizedPhone = PhoneUtil.normalizePhone(phone);
        String otpKey = OTP_PREFIX + normalizedPhone;
        
        String storedOtp = redisTemplate.opsForValue().get(otpKey);
        
        if (storedOtp == null) {
            log.warn("OTP not found or expired for phone: {}", normalizedPhone);
            return false;
        }
        
        if (storedOtp.equals(otp)) {
            redisTemplate.delete(otpKey);
            log.info("OTP verified successfully for phone: {}", normalizedPhone);
            return true;
        }
        
        log.warn("Invalid OTP provided for phone: {}", normalizedPhone);
        return false;
    }
    
    @Override
    public boolean isRateLimited(String phone) {
        String normalizedPhone = PhoneUtil.normalizePhone(phone);
        String rateLimitKey = RATE_LIMIT_PREFIX + normalizedPhone;
        
        String countStr = redisTemplate.opsForValue().get(rateLimitKey);
        if (countStr == null) {
            return false;
        }
        
        int count = Integer.parseInt(countStr);
        return count >= MAX_OTP_REQUESTS_PER_WINDOW;
    }
}

