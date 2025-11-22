package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request to login with phone number")
public class PhoneLoginRequest {
    @NotBlank(message = "Phone number is required")
    @Schema(description = "Phone number", example = "+919876543210")
    private String phone;
}

