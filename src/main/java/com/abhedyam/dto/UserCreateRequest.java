package com.abhedyam.dto;

import com.abhedyam.model.enums.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request to create a user")
public class UserCreateRequest {
    @NotBlank(message = "Name is required")
    @Schema(description = "User name", example = "John Doe", required = true)
    private String name;
    
    @Schema(description = "Phone number", example = "+919876543210")
    private String phone;
    
    @Schema(description = "Email address", example = "john@example.com")
    private String email;
    
    @Schema(description = "User type", example = "BUSINESS")
    private UserType type;
    
    @Schema(description = "Profile image URL", example = "https://example.com/image.jpg")
    private String imageUrl;
}

