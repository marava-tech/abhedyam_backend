package com.abhedyam.service;

import com.abhedyam.exception.BusinessException;
import com.abhedyam.service.interfaces.IEmailService;
import com.abhedyam.service.interfaces.IOtpService;
import com.abhedyam.util.EmailUtil;
import com.abhedyam.util.PhoneUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService implements IOtpService {
    
    private final RedisTemplate<String, String> redisTemplate;
    private final IEmailService emailService;
    private final SmsService smsService;
    
    private static final String OTP_PREFIX = "otp:";
    private static final int OTP_EXPIRY_MINUTES = 10;
    
    @Override
    public void sendOtp(String identifier) {
        String normalizedIdentifier;
        boolean isEmail = EmailUtil.isEmail(identifier);
        
        if (isEmail) {
            normalizedIdentifier = EmailUtil.normalizeEmail(identifier);
            if (!EmailUtil.isValidEmail(normalizedIdentifier)) {
                throw new BusinessException("INVALID_EMAIL", "Invalid email format");
            }
        } else {
            normalizedIdentifier = PhoneUtil.normalizePhone(identifier);
            if (!PhoneUtil.isValidPhone(normalizedIdentifier)) {
                throw new BusinessException("INVALID_PHONE", "Invalid phone number format");
            }
        }
        
        String otp = PhoneUtil.generateOTP();
        String otpKey = OTP_PREFIX + normalizedIdentifier;
        
        redisTemplate.opsForValue().set(otpKey, otp, OTP_EXPIRY_MINUTES, TimeUnit.MINUTES);
        
        if (isEmail) {
            emailService.sendOtpEmail(normalizedIdentifier, otp);
            log.info("OTP sent via email to: {}", normalizedIdentifier);
        } else {
            smsService.sendOtp(normalizedIdentifier, otp);
            log.info("OTP sent via SMS to phone: {}", normalizedIdentifier);
        }
    }
    
    @Override
    public boolean verifyOtp(String identifier, String otp) {
        String normalizedIdentifier;
        boolean isEmail = EmailUtil.isEmail(identifier);
        
        if (isEmail) {
            normalizedIdentifier = EmailUtil.normalizeEmail(identifier);
        } else {
            normalizedIdentifier = PhoneUtil.normalizePhone(identifier);
        }
        
        String otpKey = OTP_PREFIX + normalizedIdentifier;
        String storedOtp = redisTemplate.opsForValue().get(otpKey);
        
        if (storedOtp == null) {
            log.warn("OTP not found or expired for: {}", normalizedIdentifier);
            return false;
        }
        
        if (storedOtp.equals(otp)) {
            redisTemplate.delete(otpKey);
            log.info("OTP verified successfully for: {}", normalizedIdentifier);
            return true;
        }
        
        log.warn("Invalid OTP provided for: {}", normalizedIdentifier);
        return false;
    }
}

