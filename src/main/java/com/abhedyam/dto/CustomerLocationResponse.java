package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Customer location response")
public class CustomerLocationResponse {
    @Schema(description = "Customer ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID customerId;
    
    @Schema(description = "Customer name", example = "John Doe")
    private String name;
    
    @Schema(description = "Latitude", example = "12.9352")
    private BigDecimal lat;
    
    @Schema(description = "Longitude", example = "77.6245")
    private BigDecimal lng;
}

