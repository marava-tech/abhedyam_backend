package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Product search result")
public class ProductSearchResult {
    @Schema(description = "Product ID", example = "123e4567-e89b-12d3-a456-426614174001")
    private UUID id;
    
    @Schema(description = "Product name", example = "Sample Product")
    private String name;
}

