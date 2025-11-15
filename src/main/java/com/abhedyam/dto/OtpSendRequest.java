package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request to send OTP to email", example = SwaggerExamples.OTP_SEND_REQUEST)
public class OtpSendRequest {
    @NotBlank(message = "Email is required")
    @Schema(description = "Email address", example = "user@example.com", required = true)
    private String email;
}
