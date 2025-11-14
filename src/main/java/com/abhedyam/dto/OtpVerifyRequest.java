package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Login request with email/phone and OTP", example = SwaggerExamples.OTP_VERIFY_REQUEST)
public class OtpVerifyRequest {
    @NotBlank(message = "Email or phone number is required")
    @Schema(description = "Email address or phone number in E.164 format", example = "user@example.com or +919876543210", required = true)
    private String identifier;
    
    @NotBlank(message = "OTP is required")
    @Schema(description = "OTP code received via email/SMS", example = "123456", required = true)
    private String otp;
}
