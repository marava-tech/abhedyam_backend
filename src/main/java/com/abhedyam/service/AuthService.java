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
    public void sendOtp(String phone) {
        otpService.sendOtp(phone);
    }
    
    @Override
    @Transactional
    public AuthResponse verifyOtp(OtpVerifyRequest request) {
        String normalizedPhone = PhoneUtil.normalizePhone(request.getPhone());
        
        if (!otpService.verifyOtp(normalizedPhone, request.getOtp())) {
            throw new com.abhedyam.exception.BusinessException("INVALID_OTP", "Invalid or expired OTP");
        }
        
        Optional<User> existingUser = userRepository.findByPhoneNormalized(normalizedPhone);
        boolean isNewUser = existingUser.isEmpty();
        
        User user;
        if (isNewUser) {
            user = createNewUser(normalizedPhone);
        } else {
            user = existingUser.get();
        }
        
        String token = jwtUtil.generateToken(user.getId(), normalizedPhone);
        
        return new AuthResponse(
            token,
            user.getId().toString(),
            normalizedPhone,
            user.getName(),
            isNewUser
        );
    }
    
    private User createNewUser(String normalizedPhone) {
        User user = new User();
        user.setPhone(normalizedPhone.replace("+", ""));
        user.setPhoneNormalized(normalizedPhone);
        user.setName("User");
        user.setType(UserType.BUSINESS);
        
        User savedUser = userRepository.save(user);
        
        Owner owner = new Owner();
        owner.setId(savedUser.getId());
        owner.setPhone(savedUser.getPhone());
        owner.setPhoneNormalized(savedUser.getPhoneNormalized());
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

