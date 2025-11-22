package com.abhedyam.dto;

import com.abhedyam.model.enums.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authentication response")
public class AuthResponse {
    @Schema(description = "JWT token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    
    @Schema(description = "User ID", example = "3595381f-d038-4d6b-8fe0-dc76ebb7dde2")
    private String userId;
    
    @Schema(description = "Phone number or email", example = "+919876543210")
    private String phone;
    
    @Schema(description = "User name", example = "John Doe")
    private String name;
    
    @Schema(description = "Whether this is a new user", example = "false")
    private Boolean isNewUser;
    
    @Schema(description = "User type", example = "BUSINESS", allowableValues = {"CUSTOMER", "BUSINESS"})
    private UserType userType;
    
    @Schema(description = "Owner ID (for customers)", example = "3595381f-d038-4d6b-8fe0-dc76ebb7dde2")
    private UUID ownerId;
}

