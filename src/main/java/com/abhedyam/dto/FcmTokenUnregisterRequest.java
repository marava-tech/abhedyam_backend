package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request to unregister FCM token")
public class FcmTokenUnregisterRequest {
    @NotBlank(message = "FCM token is required")
    @Schema(description = "FCM registration token", example = "fcm_token_here", required = true)
    private String token;
    
    @NotBlank(message = "Package name is required")
    @Schema(description = "Package name", example = "tech.marava.abhedyamc", required = true)
    private String packageName;
}

