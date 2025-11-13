package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(description = "Request to create a customer", example = SwaggerExamples.CUSTOMER_CREATE_REQUEST)
public class CustomerCreateRequest {
    @NotBlank(message = "Customer name is required")
    @Schema(description = "Customer name", example = "John Doe", required = true)
    private String name;
    
    @NotBlank(message = "Phone number is required")
    @Schema(description = "Phone number", example = "+919876543210", required = true)
    private String phone;
    
    @Schema(description = "Customer image URL", example = "https://example.com/image.jpg")
    private String imageUrl;
    
    @Schema(description = "Location details ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID locationDetailsId;
}
