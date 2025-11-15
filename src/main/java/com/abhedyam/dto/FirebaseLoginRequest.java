package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Login request with phone number (Firebase token validation disabled for testing)", example = SwaggerExamples.FIREBASE_LOGIN_REQUEST)
public class FirebaseLoginRequest {
    @Schema(description = "Firebase ID token (optional for testing, will be validated in future)", example = "eyJhbGciOiJSUzI1NiIsImtpZCI6Ij...")
    private String firebaseToken;
    
    @NotBlank(message = "Phone number is required")
    @Schema(description = "Phone number in E.164 format", example = "+919876543210", required = true)
    private String phone;
    
    @Schema(description = "User name (optional, for new users)", example = "John Doe")
    private String name;
    
    @Schema(description = "Email address (optional, for notifications)", example = "user@example.com")
    private String email;
}

