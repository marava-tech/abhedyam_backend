package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request to send OTP", example = SwaggerExamples.OTP_SEND_REQUEST)
public class OtpSendRequest {
    @NotBlank(message = "Phone number is required")
    @Schema(description = "Phone number in E.164 format", example = "+919876543210", required = true)
    private String phone;
}
