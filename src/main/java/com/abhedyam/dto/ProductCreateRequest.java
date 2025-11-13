package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "Request to create a product", example = SwaggerExamples.PRODUCT_CREATE_REQUEST)
public class ProductCreateRequest {
    @NotBlank(message = "Product code/SKU is required")
    @Schema(description = "Product code/SKU", example = "PROD001", required = true)
    private String code;
    
    @NotBlank(message = "Product name is required")
    @Schema(description = "Product name", example = "Sample Product", required = true)
    private String name;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    @Schema(description = "Product price", example = "500.00", required = true)
    private BigDecimal price;
    
    @Schema(description = "Main product image URL", example = "https://example.com/image.jpg")
    private String imageUrl;
    
    @Schema(description = "List of product image URLs", example = "[\"https://example.com/image1.jpg\", \"https://example.com/image2.jpg\"]")
    private List<String> images;
    
    @Schema(description = "Initial stock quantity", example = "100")
    private BigDecimal stock;
}
