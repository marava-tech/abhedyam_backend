package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request to verify OTP and login", example = SwaggerExamples.OTP_VERIFY_REQUEST)
public class OtpVerifyRequest {
    @NotBlank(message = "Phone number is required")
    @Schema(description = "Phone number in E.164 format", example = "+919876543210", required = true)
    private String phone;
    
    @NotBlank(message = "OTP is required")
    @Schema(description = "OTP code received via SMS", example = "123456", required = true)
    private String otp;
}
