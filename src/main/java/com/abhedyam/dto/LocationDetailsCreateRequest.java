package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Request to create location details")
public class LocationDetailsCreateRequest {
    @NotNull(message = "Latitude is required")
    @Schema(description = "Latitude coordinate", example = "12.9309220", required = true)
    private BigDecimal latitude;
    
    @NotNull(message = "Longitude is required")
    @Schema(description = "Longitude coordinate", example = "77.6089082", required = true)
    private BigDecimal longitude;
    
    @Schema(description = "Village name", example = "Koramangala")
    private String village;
}

