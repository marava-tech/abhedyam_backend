package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(description = "Request to update a customer")
public class CustomerUpdateRequest {
    @NotNull(message = "Customer ID is required")
    @Schema(description = "Customer ID", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
    private UUID id;
    
    @Schema(description = "Customer name", example = "John Doe")
    private String name;
    
    @Schema(description = "Phone number", example = "+919876543210")
    private String phone;
    
    @Schema(description = "Customer image URL", example = "https://example.com/image.jpg")
    private String imageUrl;
    
    @Schema(description = "Location details ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID locationDetailsId;
}

