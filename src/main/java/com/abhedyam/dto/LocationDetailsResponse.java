package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Schema(description = "Location details response")
public class LocationDetailsResponse {
    @Schema(description = "Location details ID", example = "3595381f-d038-4d6b-8fe0-dc76ebb7dde2")
    private UUID id;
    
    @Schema(description = "Latitude coordinate", example = "12.9309220")
    private BigDecimal latitude;
    
    @Schema(description = "Longitude coordinate", example = "77.6089082")
    private BigDecimal longitude;
    
    @Schema(description = "Village name", example = "Koramangala")
    private String village;
    
    @Schema(description = "Creation timestamp")
    private Instant createdAt;
    
    @Schema(description = "Last update timestamp")
    private Instant updatedAt;
}

