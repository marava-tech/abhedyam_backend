package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(description = "Request to update subscription by admin")
public class AdminSubscriptionUpdateRequest {
    @NotNull(message = "Owner ID is required")
    @Schema(description = "Owner ID to update subscription for", example = "b1988745-e409-4070-9c52-b1f9293080f4")
    private UUID ownerId;
    
    @NotBlank(message = "Admin key is required")
    @Schema(description = "Admin key for authorization", example = "Madhu7814")
    private String adminKey;
}

