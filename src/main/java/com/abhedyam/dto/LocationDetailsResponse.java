package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Schema(description = "Location details response")
public class LocationDetailsResponse {
    @Schema(description = "Location details ID (read-only, system-generated)", example = "3595381f-d038-4d6b-8fe0-dc76ebb7dde2", accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;
    
    @Schema(description = "Latitude coordinate", example = "12.9309220")
    private BigDecimal latitude;
    
    @Schema(description = "Longitude coordinate", example = "77.6089082")
    private BigDecimal longitude;
    
    @Schema(description = "Village name", example = "Koramangala")
    private String village;
    
    @Schema(description = "Full address text", example = "123 Main Street, Koramangala, Bangalore 560095")
    private String addressText;
    
    @Schema(description = "Creation timestamp (read-only, system-generated)", example = "2025-11-15T10:51:15.325Z", accessMode = Schema.AccessMode.READ_ONLY)
    private Instant createdAt;
    
    @Schema(description = "Last update timestamp (read-only, system-generated)", example = "2025-11-15T10:51:15.325Z", accessMode = Schema.AccessMode.READ_ONLY)
    private Instant updatedAt;
}

