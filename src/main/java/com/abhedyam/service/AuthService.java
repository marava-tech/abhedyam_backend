package com.abhedyam.service;

import com.abhedyam.dto.AuthResponse;
import com.abhedyam.dto.GoogleLoginRequest;
import com.abhedyam.dto.PhoneLoginRequest;
import com.abhedyam.service.GoogleOAuthService.GoogleUserInfo;
import com.abhedyam.model.Customer;
import com.abhedyam.model.Owner;
import com.abhedyam.model.User;
import com.abhedyam.model.enums.Subscription;
import com.abhedyam.model.enums.UserType;
import com.abhedyam.repository.CustomerRepository;
import com.abhedyam.repository.OwnerRepository;
import com.abhedyam.repository.UserRepository;
import com.abhedyam.util.EmailUtil;
import com.abhedyam.util.JwtUtil;
import com.abhedyam.util.PhoneUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final GoogleOAuthService googleOAuthService;
    private final FirebaseService firebaseService;
    private final UserRepository userRepository;
    private final OwnerRepository ownerRepository;
    private final CustomerRepository customerRepository;
    private final JwtUtil jwtUtil;
    
    @Transactional
    public AuthResponse loginWithGoogle(GoogleLoginRequest request) {
        GoogleUserInfo googleUser = googleOAuthService.verifyToken(request.getIdToken());
        
        String normalizedEmail = EmailUtil.normalizeEmail(googleUser.getEmail());
        if (!EmailUtil.isValidEmail(normalizedEmail)) {
            throw new com.abhedyam.exception.BusinessException("INVALID_EMAIL", 
                "Invalid email format from Google");
        }
        
        Optional<User> existingUserByUid = userRepository.findByFirebaseUid(googleUser.getFirebaseUid());
        Optional<User> existingUserByEmail = userRepository.findByEmail(normalizedEmail);
        
        User user;
        boolean isNewUser;
        
        if (existingUserByUid.isPresent()) {
            user = existingUserByUid.get();
            isNewUser = false;
            if (user.getType() != UserType.BUSINESS) {
                throw new com.abhedyam.exception.BusinessException("INVALID_USER_TYPE", 
                    "Google login is only available for business owners");
            }
            log.info("Owner logged in with Firebase UID: {}", googleUser.getFirebaseUid());
        } else if (existingUserByEmail.isPresent()) {
            user = existingUserByEmail.get();
            isNewUser = false;
            if (user.getType() != UserType.BUSINESS) {
                throw new com.abhedyam.exception.BusinessException("INVALID_USER_TYPE", 
                    "Google login is only available for business owners");
            }
            user.setFirebaseUid(googleUser.getFirebaseUid());
            log.info("Owner logged in with email, linked Firebase UID: {}", googleUser.getFirebaseUid());
        } else {
            Owner newOwner = new Owner();
            newOwner.setName(googleUser.getName() != null && !googleUser.getName().trim().isEmpty() 
                ? googleUser.getName() : "Business Owner");
            newOwner.setBusinessName(googleUser.getName() != null && !googleUser.getName().trim().isEmpty() 
                ? googleUser.getName() + "'s Business" : "My Business");
            newOwner.setEmail(normalizedEmail);
            newOwner.setFirebaseUid(googleUser.getFirebaseUid());
            newOwner.setType(UserType.BUSINESS);
            newOwner.setSubscription(Subscription.GO);
            newOwner.setIsVerified(false);
            
            if (googleUser.getPicture() != null && !googleUser.getPicture().trim().isEmpty()) {
                newOwner.setImageUrl(googleUser.getPicture());
            }
            
            user = ownerRepository.save(newOwner);
            isNewUser = true;
            log.info("Created new owner account for Google login - Firebase UID: {}, email: {}", 
                googleUser.getFirebaseUid(), normalizedEmail);
        }
        
        if (googleUser.getName() != null && !googleUser.getName().trim().isEmpty() && 
            (user.getName() == null || user.getName().trim().isEmpty() || user.getName().equals("User"))) {
            user.setName(googleUser.getName());
        }
        if (googleUser.getPicture() != null && !googleUser.getPicture().trim().isEmpty() && 
            (user.getImageUrl() == null || user.getImageUrl().trim().isEmpty())) {
            user.setImageUrl(googleUser.getPicture());
        }
        if (user.getFirebaseUid() == null || !user.getFirebaseUid().equals(googleUser.getFirebaseUid())) {
            user.setFirebaseUid(googleUser.getFirebaseUid());
        }
        userRepository.save(user);
        
        String phoneForToken = user.getPhoneNormalized() != null ? user.getPhoneNormalized() : normalizedEmail;
        String token = jwtUtil.generateToken(user.getId(), phoneForToken);
        
        return new AuthResponse(
            token,
            user.getId().toString(),
            phoneForToken,
            user.getName(),
            isNewUser,
            UserType.BUSINESS,
            null,
            user.getCreatedAt()
        );
    }
    
    @Transactional
    public AuthResponse loginWithPhone(PhoneLoginRequest request) {
        String phoneNumber = request.getPhone();
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new com.abhedyam.exception.BusinessException("MISSING_PHONE", 
                "Phone number is required");
        }
        
        String normalizedPhone = PhoneUtil.normalizePhone(phoneNumber);
        if (!PhoneUtil.isValidPhone(normalizedPhone)) {
            throw new com.abhedyam.exception.BusinessException("INVALID_PHONE", 
                "Invalid phone number format");
        }
        
        Optional<User> existingUser = userRepository.findByPhoneNormalized(normalizedPhone);
        
        Customer customer;
        boolean isNewUser;
        
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (user.getType() != UserType.CUSTOMER) {
                throw new com.abhedyam.exception.BusinessException("INVALID_USER_TYPE", 
                    "Only customer login is allowed for this app");
            }
            customer = customerRepository.findById(user.getId())
                    .orElseThrow(() -> new com.abhedyam.exception.ResourceNotFoundException("Customer record not found"));
            isNewUser = false;
            log.info("Customer logged in with phone: {}", normalizedPhone);
        } else {
            customer = new Customer();
            customer.setName("Customer");
            customer.setPhone(PhoneUtil.extractPhoneWithoutCountryCode(normalizedPhone));
            customer.setPhoneNormalized(normalizedPhone);
            customer.setType(UserType.CUSTOMER);
            
            customer = customerRepository.save(customer);
            isNewUser = true;
            log.info("Created new customer account for phone login - phone: {}", normalizedPhone);
        }
        
        String token = jwtUtil.generateCustomerToken(customer.getId(), normalizedPhone);
        
        return new AuthResponse(
            token,
            customer.getId().toString(),
            normalizedPhone,
            customer.getName(),
            isNewUser,
            UserType.CUSTOMER,
            customer.getOwnerId(),
            customer.getCreatedAt()
        );
    }
}
