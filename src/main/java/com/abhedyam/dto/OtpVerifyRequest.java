package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Login request with email and OTP")
public class OtpVerifyRequest {
    @NotBlank(message = "Email is required")
    @Schema(description = "Email address", example = "user@example.com", required = true)
    private String email;
    
    @NotBlank(message = "OTP is required")
    @Schema(description = "OTP code received via email", example = "123456", required = true)
    private String otp;
}
