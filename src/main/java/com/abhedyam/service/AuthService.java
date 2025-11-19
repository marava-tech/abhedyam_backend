package com.abhedyam.service;

import com.abhedyam.dto.AuthResponse;
import com.abhedyam.dto.GoogleLoginRequest;
import com.abhedyam.service.GoogleOAuthService.GoogleUserInfo;
import com.abhedyam.model.User;
import com.abhedyam.model.enums.UserType;
import com.abhedyam.repository.UserRepository;
import com.abhedyam.util.EmailUtil;
import com.abhedyam.util.JwtUtil;
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
    private final UserRepository userRepository;
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
            throw new com.abhedyam.exception.BusinessException("OWNER_NOT_FOUND", 
                "Owner account not found. Please contact support to create an account.");
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
            UserType.BUSINESS
        );
    }
}
