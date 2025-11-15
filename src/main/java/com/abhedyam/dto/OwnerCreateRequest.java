package com.abhedyam.dto;

import com.abhedyam.model.enums.Subscription;
import com.abhedyam.model.enums.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request to create an owner")
public class OwnerCreateRequest {
    @NotBlank(message = "Name is required")
    @Schema(description = "Owner name", example = "John Doe", required = true)
    private String name;
    
    @NotBlank(message = "Business name is required")
    @Schema(description = "Business name", example = "My Business", required = true)
    private String businessName;
    
    @Schema(description = "Phone number", example = "+919876543210")
    private String phone;
    
    @Schema(description = "Email address", example = "john@example.com")
    private String email;
    
    @Schema(description = "User type", example = "BUSINESS")
    private UserType type;
    
    @Schema(description = "Profile image URL", example = "https://example.com/image.jpg")
    private String imageUrl;
    
    @Schema(description = "Subscription plan", example = "GO")
    private Subscription subscription;
    
    @Schema(description = "Verification status", example = "false")
    private Boolean isVerified;
}

