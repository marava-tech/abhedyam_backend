package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(description = "Request for subscription test endpoints")
public class SubscriptionTestRequest {
    @NotNull(message = "Owner ID is required")
    @Schema(description = "Owner ID", example = "3595381f-d038-4d6b-8fe0-dc76ebb7dde2", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID ownerId;
    
    @NotNull(message = "Admin key is required")
    @Schema(description = "Admin key for authorization", example = "REDACTED_ADMIN_KEY", requiredMode = Schema.RequiredMode.REQUIRED)
    private String adminKey;
}

