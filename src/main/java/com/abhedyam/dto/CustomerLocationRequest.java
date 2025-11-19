package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Schema(description = "Request to get customer locations by IDs")
public class CustomerLocationRequest {
    @NotEmpty(message = "At least one customer ID is required")
    @Schema(description = "List of customer IDs", required = true, example = "[\"123e4567-e89b-12d3-a456-426614174000\"]")
    private List<UUID> customerIds;
    
    @Schema(description = "Current latitude for distance calculation and sorting", example = "12.9352")
    private BigDecimal currentLat;
    
    @Schema(description = "Current longitude for distance calculation and sorting", example = "77.6245")
    private BigDecimal currentLng;
}

