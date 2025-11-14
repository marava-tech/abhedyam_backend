package com.abhedyam.service;

import com.abhedyam.dto.AuthResponse;
import com.abhedyam.dto.OtpVerifyRequest;
import com.abhedyam.model.Owner;
import com.abhedyam.model.User;
import com.abhedyam.model.enums.UserType;
import com.abhedyam.repository.OwnerRepository;
import com.abhedyam.repository.UserRepository;
import com.abhedyam.service.interfaces.IAuthService;
import com.abhedyam.service.interfaces.IOtpService;
import com.abhedyam.util.EmailUtil;
import com.abhedyam.util.JwtUtil;
import com.abhedyam.util.PhoneUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements IAuthService {
    
    private final IOtpService otpService;
    private final UserRepository userRepository;
    private final OwnerRepository ownerRepository;
    private final JwtUtil jwtUtil;
    
    @Override
    public void sendOtp(String identifier) {
        otpService.sendOtp(identifier);
    }
    
    @Override
    @Transactional
    public AuthResponse login(OtpVerifyRequest request) {
        String identifier = request.getIdentifier();
        boolean isEmail = EmailUtil.isEmail(identifier);
        
        String normalizedIdentifier;
        if (isEmail) {
            normalizedIdentifier = EmailUtil.normalizeEmail(identifier);
            if (!EmailUtil.isValidEmail(normalizedIdentifier)) {
                throw new com.abhedyam.exception.BusinessException("INVALID_EMAIL", "Invalid email format");
            }
        } else {
            normalizedIdentifier = PhoneUtil.normalizePhone(identifier);
            if (!PhoneUtil.isValidPhone(normalizedIdentifier)) {
                throw new com.abhedyam.exception.BusinessException("INVALID_PHONE", "Invalid phone number format");
            }
        }
        
        if (!otpService.verifyOtp(normalizedIdentifier, request.getOtp())) {
            throw new com.abhedyam.exception.BusinessException("INVALID_OTP", "Invalid or expired OTP. Please request a new OTP.");
        }
        
        Optional<User> existingUser;
        if (isEmail) {
            existingUser = userRepository.findByEmail(normalizedIdentifier);
        } else {
            existingUser = userRepository.findByPhoneNormalized(normalizedIdentifier);
        }
        
        boolean isNewUser = existingUser.isEmpty();
        
        User user;
        if (isNewUser) {
            user = createNewUser(normalizedIdentifier, isEmail);
            log.info("New user created and logged in: {}", normalizedIdentifier);
        } else {
            user = existingUser.get();
            log.info("User logged in: {}", normalizedIdentifier);
        }
        
        String token = jwtUtil.generateToken(user.getId(), normalizedIdentifier);
        
        return new AuthResponse(
            token,
            user.getId().toString(),
            isEmail ? user.getEmail() : user.getPhoneNormalized(),
            user.getName(),
            isNewUser
        );
    }
    
    private User createNewUser(String normalizedIdentifier, boolean isEmail) {
        User user = new User();
        user.setName("User");
        user.setType(UserType.BUSINESS);
        
        if (isEmail) {
            user.setEmail(normalizedIdentifier);
        } else {
            user.setPhone(normalizedIdentifier.replace("+", ""));
            user.setPhoneNormalized(normalizedIdentifier);
        }
        
        User savedUser = userRepository.save(user);
        
        Owner owner = new Owner();
        owner.setId(savedUser.getId());
        if (isEmail) {
            owner.setEmail(savedUser.getEmail());
        } else {
            owner.setPhone(savedUser.getPhone());
            owner.setPhoneNormalized(savedUser.getPhoneNormalized());
        }
        owner.setName(savedUser.getName());
        owner.setType(savedUser.getType());
        owner.setBusinessName("My Business");
        owner.setSubscription(com.abhedyam.model.enums.Subscription.GO);
        owner.setIsVerified(false);
        ownerRepository.save(owner);
        
        log.info("Created new user and owner with ID: {}", savedUser.getId());
        return savedUser;
    }
}

