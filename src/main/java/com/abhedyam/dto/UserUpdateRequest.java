package com.abhedyam.dto;

import com.abhedyam.model.enums.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request to update user details")
public class UserUpdateRequest {
    @Schema(description = "User name", example = "John Doe")
    private String name;
    
    @Schema(description = "Phone number", example = "+919876543210")
    private String phone;
    
    @Schema(description = "Email address", example = "john@example.com")
    private String email;
    
    @Schema(description = "Profile image URL", example = "https://example.com/image.jpg")
    private String imageUrl;
    
    @Schema(description = "User type", example = "BUSINESS", allowableValues = {"CUSTOMER", "BUSINESS"})
    private UserType type;
}

