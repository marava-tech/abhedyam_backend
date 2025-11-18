package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Request to update location details")
public class LocationDetailsUpdateRequest {
    @Schema(description = "Latitude coordinate", example = "12.9309220")
    private BigDecimal latitude;
    
    @Schema(description = "Longitude coordinate", example = "77.6089082")
    private BigDecimal longitude;
    
    @Schema(description = "Village name", example = "Koramangala")
    private String village;
    
    @Schema(description = "Full address text", example = "123 Main Street, Koramangala, Bangalore 560095")
    private String addressText;
}

