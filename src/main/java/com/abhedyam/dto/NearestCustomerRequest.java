package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to find nearest customer")
public class NearestCustomerRequest {
    @NotNull(message = "Latitude is required")
    @Schema(description = "Current latitude", example = "12.9716")
    private BigDecimal latitude;
    
    @NotNull(message = "Longitude is required")
    @Schema(description = "Current longitude", example = "77.5946")
    private BigDecimal longitude;
}

