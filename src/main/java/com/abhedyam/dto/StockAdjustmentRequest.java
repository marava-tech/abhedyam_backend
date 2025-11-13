package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Schema(description = "Request to adjust stock manually", example = SwaggerExamples.STOCK_ADJUSTMENT_REQUEST)
public class StockAdjustmentRequest {
    @NotNull(message = "Product ID is required")
    @Schema(description = "UUID of the product", example = "123e4567-e89b-12d3-a456-426614174001", required = true)
    private UUID productId;
    
    @NotNull(message = "Change quantity is required")
    @Schema(description = "Quantity change (positive for increase, negative for decrease)", example = "10", required = true)
    private BigDecimal changeQty;
    
    @Schema(description = "Note explaining the adjustment", example = "Stock correction")
    private String note;
}
