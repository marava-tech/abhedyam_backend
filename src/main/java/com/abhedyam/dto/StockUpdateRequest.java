package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Schema(description = "Request to update stock to a specific value")
public class StockUpdateRequest {
    @NotNull(message = "Product ID is required")
    @Schema(description = "UUID of the product", example = "123e4567-e89b-12d3-a456-426614174001", required = true)
    private UUID productId;
    
    @NotNull(message = "Stock value is required")
    @Schema(description = "New stock value (absolute value, must be >= 0)", example = "100", required = true)
    private BigDecimal stock;
    
    @Schema(description = "Note explaining the update", example = "Physical stock count")
    private String note;
}

