package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Schema(description = "Request to update a product")
public class ProductUpdateRequest {
    @NotNull(message = "Product ID is required")
    @Schema(description = "Product ID", example = "123e4567-e89b-12d3-a456-426614174001", required = true)
    private UUID id;
    
    @Schema(description = "Product code/SKU", example = "PROD001")
    private String code;
    
    @Schema(description = "Product name", example = "Sample Product")
    private String name;
    
    @Schema(description = "Product price", example = "500.00")
    private BigDecimal price;
    
    @Schema(description = "Product image URL", example = "https://example.com/image.jpg")
    private String imageUrl;
}

