package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Schema(description = "Sale item details")
public class SaleItemRequest {
    @NotNull(message = "Product ID is required")
    @Schema(description = "UUID of the product", example = "123e4567-e89b-12d3-a456-426614174001", required = true)
    private UUID productId;
    
    @Positive(message = "Price must be positive")
    @Schema(description = "Price per unit (if not provided, product price will be used)", example = "1000.00")
    private BigDecimal price;
}

