package com.abhedyam.service;

import com.abhedyam.dto.AuthResponse;
import com.abhedyam.dto.FirebaseLoginRequest;
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
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements IAuthService {
    
    private final IOtpService otpService;
    private final FirebaseService firebaseService;
    private final UserRepository userRepository;
    private final OwnerRepository ownerRepository;
    private final JwtUtil jwtUtil;
    
    @Override
    public void sendOtp(String identifier) {
        if (!EmailUtil.isEmail(identifier)) {
            throw new com.abhedyam.exception.BusinessException("INVALID_EMAIL", "OTP can only be sent to email addresses");
        }
        otpService.sendOtp(identifier);
    }
    
    @Override
    @Transactional
    public AuthResponse login(OtpVerifyRequest request) {
        String email = request.getEmail();
        
        if (!EmailUtil.isEmail(email)) {
            throw new com.abhedyam.exception.BusinessException("INVALID_EMAIL", "Invalid email format");
        }
        
        String normalizedEmail = EmailUtil.normalizeEmail(email);
        if (!EmailUtil.isValidEmail(normalizedEmail)) {
            throw new com.abhedyam.exception.BusinessException("INVALID_EMAIL", "Invalid email format");
        }
        
        if (!otpService.verifyOtp(normalizedEmail, request.getOtp())) {
            throw new com.abhedyam.exception.BusinessException("INVALID_OTP", "Invalid or expired OTP. Please request a new OTP.");
        }
        
        Optional<User> existingUser = userRepository.findByEmail(normalizedEmail);
        boolean isNewUser = existingUser.isEmpty();
        
        User user;
        if (isNewUser) {
            user = createNewUserWithEmail(normalizedEmail);
            log.info("New user created with email: {}", normalizedEmail);
        } else {
            user = existingUser.get();
            log.info("User logged in with email: {}", normalizedEmail);
        }
        
        String phoneForToken = user.getPhoneNormalized() != null ? user.getPhoneNormalized() : normalizedEmail;
        String token = jwtUtil.generateToken(user.getId(), phoneForToken);
        
        return new AuthResponse(
            token,
            user.getId().toString(),
            phoneForToken,
            user.getName(),
            isNewUser
        );
    }
    
    @Transactional
    public AuthResponse loginWithFirebase(FirebaseLoginRequest request) {
        String normalizedPhone = PhoneUtil.normalizePhone(request.getPhone());
        if (!PhoneUtil.isValidPhone(normalizedPhone)) {
            throw new com.abhedyam.exception.BusinessException("INVALID_PHONE", "Invalid phone number format");
        }
        
        if (request.getFirebaseToken() != null && !request.getFirebaseToken().trim().isEmpty()) {
            FirebaseToken firebaseToken = firebaseService.verifyIdToken(request.getFirebaseToken());
            String tokenPhone = firebaseService.getPhoneNumberFromToken(firebaseToken);
            String normalizedTokenPhone = PhoneUtil.normalizePhone(tokenPhone);
            
            if (!normalizedPhone.equals(normalizedTokenPhone)) {
                throw new com.abhedyam.exception.BusinessException("PHONE_MISMATCH", "Phone number in request does not match Firebase token");
            }
            log.info("Firebase token validated for phone: {}", normalizedPhone);
        } else {
            log.warn("Firebase token validation skipped for testing - phone: {}", normalizedPhone);
        }
        
        Optional<User> existingUser = userRepository.findByPhoneNormalized(normalizedPhone);
        boolean isNewUser = existingUser.isEmpty();
        
        User user;
        if (isNewUser) {
            user = createNewUserWithFirebase(normalizedPhone, request.getName(), request.getEmail());
            log.info("New user created with phone: {}", normalizedPhone);
        } else {
            user = existingUser.get();
            if (request.getEmail() != null && !request.getEmail().trim().isEmpty() && user.getEmail() == null) {
                user.setEmail(EmailUtil.normalizeEmail(request.getEmail()));
                userRepository.save(user);
            }
            log.info("User logged in with phone: {}", normalizedPhone);
        }
        
        String phoneForToken = user.getPhoneNormalized() != null ? user.getPhoneNormalized() : normalizedPhone;
        String token = jwtUtil.generateToken(user.getId(), phoneForToken);
        
        return new AuthResponse(
            token,
            user.getId().toString(),
            phoneForToken,
            user.getName(),
            isNewUser
        );
    }
    
    private User createNewUserWithEmail(String normalizedEmail) {
        Owner owner = new Owner();
        owner.setName("User");
        owner.setType(UserType.BUSINESS);
        owner.setBusinessName("My Business");
        owner.setSubscription(com.abhedyam.model.enums.Subscription.GO);
        owner.setIsVerified(false);
        owner.setEmail(normalizedEmail);
        
        Owner savedOwner = ownerRepository.save(owner);
        log.info("Created new user and owner with email, ID: {}", savedOwner.getId());
        return savedOwner;
    }
    
    private User createNewUserWithFirebase(String normalizedPhone, String name, String email) {
        Owner owner = new Owner();
        owner.setName(name != null && !name.trim().isEmpty() ? name : "User");
        owner.setType(UserType.BUSINESS);
        owner.setBusinessName("My Business");
        owner.setSubscription(com.abhedyam.model.enums.Subscription.GO);
        owner.setIsVerified(false);
        
        owner.setPhone(PhoneUtil.extractPhoneWithoutCountryCode(normalizedPhone));
        owner.setPhoneNormalized(normalizedPhone);
        
        if (email != null && !email.trim().isEmpty()) {
            String normalizedEmail = EmailUtil.normalizeEmail(email);
            if (EmailUtil.isValidEmail(normalizedEmail)) {
                owner.setEmail(normalizedEmail);
            }
        }
        
        Owner savedOwner = ownerRepository.save(owner);
        log.info("Created new user and owner with Firebase, ID: {}", savedOwner.getId());
        return savedOwner;
    }
}

