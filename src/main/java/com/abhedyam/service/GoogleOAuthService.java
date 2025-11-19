package com.abhedyam.service;

import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoogleOAuthService {
    
    private final FirebaseService firebaseService;
    
    public GoogleUserInfo verifyToken(String idToken) {
        try {
            FirebaseToken firebaseToken = firebaseService.verifyIdToken(idToken);
            
            String firebaseUid = firebaseService.getUidFromToken(firebaseToken);
            String email = firebaseService.getEmailFromToken(firebaseToken);
            if (email == null || email.trim().isEmpty()) {
                throw new com.abhedyam.exception.BusinessException("MISSING_EMAIL", 
                    "Email is required but not provided by Google");
            }
            
            String name = firebaseService.getNameFromToken(firebaseToken);
            String picture = firebaseService.getPictureFromToken(firebaseToken);
            Boolean emailVerified = firebaseService.isEmailVerifiedFromToken(firebaseToken);
            
            log.info("Google login verified for Firebase UID: {}, email: {}", firebaseUid, email);
            
            return new GoogleUserInfo(firebaseUid, email, name, picture, emailVerified);
        } catch (com.abhedyam.exception.BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error verifying Google token via Firebase", e);
            throw new com.abhedyam.exception.BusinessException("GOOGLE_TOKEN_VERIFICATION_FAILED", 
                "Failed to verify Google ID token: " + e.getMessage());
        }
    }
    
    public static class GoogleUserInfo {
        private final String firebaseUid;
        private final String email;
        private final String name;
        private final String picture;
        private final Boolean emailVerified;
        
        public GoogleUserInfo(String firebaseUid, String email, String name, String picture, Boolean emailVerified) {
            this.firebaseUid = firebaseUid;
            this.email = email;
            this.name = name;
            this.picture = picture;
            this.emailVerified = emailVerified;
        }
        
        public String getFirebaseUid() {
            return firebaseUid;
        }
        
        public String getEmail() {
            return email;
        }
        
        public String getName() {
            return name;
        }
        
        public String getPicture() {
            return picture;
        }
        
        public Boolean getEmailVerified() {
            return emailVerified;
        }
    }
}

