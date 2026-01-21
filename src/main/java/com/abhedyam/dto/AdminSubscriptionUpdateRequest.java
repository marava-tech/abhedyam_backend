package com.abhedyam.dto;

import com.abhedyam.model.enums.Subscription;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request to update owner subscription")
public class AdminSubscriptionUpdateRequest {
    @NotNull(message = "Subscription is required")
    @Schema(description = "New subscription plan", example = "PRO", requiredMode = Schema.RequiredMode.REQUIRED)
    private Subscription subscription;
}
