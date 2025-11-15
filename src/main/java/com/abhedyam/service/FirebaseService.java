package com.abhedyam.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FirebaseService {
    
    public FirebaseToken verifyIdToken(String idToken) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            log.info("Firebase token verified for UID: {}", decodedToken.getUid());
            return decodedToken;
        } catch (FirebaseAuthException e) {
            log.error("Firebase token verification failed: {}", e.getMessage());
            throw new com.abhedyam.exception.BusinessException("INVALID_FIREBASE_TOKEN", "Invalid or expired Firebase token");
        }
    }
    
    public String getPhoneNumberFromToken(FirebaseToken token) {
        Object phoneClaim = token.getClaims().get("phone_number");
        if (phoneClaim == null) {
            phoneClaim = token.getClaims().get("phone");
        }
        
        if (phoneClaim == null) {
            throw new com.abhedyam.exception.BusinessException("MISSING_PHONE", "Phone number not found in Firebase token");
        }
        
        return phoneClaim.toString();
    }
    
    public String getEmailFromToken(FirebaseToken token) {
        return token.getEmail();
    }
}

