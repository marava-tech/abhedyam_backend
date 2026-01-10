package com.abhedyam.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Schema(description = "Request to create a sale with multiple items")
public class SaleCreateRequest {
    @Schema(description = "UUID of the customer (if not provided, customer will be created with name, phone, and village)", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID customerId;
    
    @Schema(description = "Customer name (required if customerId is not provided)", example = "John Doe")
    private String customerName;
    
    @Schema(description = "Customer phone (optional, used only if creating new customer)", example = "+919876543210")
    private String customerPhone;
    
    @Schema(description = "Customer village (optional, used only if creating new customer)", example = "Koramangala")
    private String customerVillage;
    
    @NotEmpty(message = "At least one sale item is required")
    @Schema(description = "List of sale items", required = true)
    private List<SaleItemRequest> items;
    
    @Schema(description = "Due date for payment", example = "2024-12-31T23:59:59Z")
    @JsonDeserialize(using = FlexibleInstantDeserializer.class)
    private Instant dueDate;
}

