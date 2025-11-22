package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Product response with stock information")
public class ProductWithStockResponse {
    @Schema(description = "Product ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;
    
    @Schema(description = "Product code", example = "RICE001")
    private String code;
    
    @Schema(description = "Product name", example = "Rice")
    private String name;
    
    @Schema(description = "Product price", example = "50.00")
    private BigDecimal price;
    
    @Schema(description = "Owner ID", example = "223e4567-e89b-12d3-a456-426614174001")
    private UUID ownerId;
    
    @Schema(description = "Active status", example = "true")
    private Boolean isActive;
    
    @Schema(description = "Current stock available", example = "100.00")
    private BigDecimal stock;
    
    @Schema(description = "Creation timestamp")
    private Instant createdAt;
    
    @Schema(description = "Update timestamp")
    private Instant updatedAt;
}

