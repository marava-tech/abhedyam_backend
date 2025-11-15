package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
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
    
    @Schema(description = "Main product image URL", example = "https://example.com/image.jpg")
    private String imageUrl;
    
    @Schema(description = "List of product image URLs", example = "[\"https://example.com/image1.jpg\", \"https://example.com/image2.jpg\"]")
    private List<String> images;
    
    @Schema(description = "Stock quantity", example = "100")
    private BigDecimal stock;
}

