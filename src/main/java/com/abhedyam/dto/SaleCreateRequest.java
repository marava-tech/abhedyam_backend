package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Schema(description = "Request to create a sale with multiple items", example = SwaggerExamples.SALE_CREATE_REQUEST)
public class SaleCreateRequest {
    @NotNull(message = "Customer ID is required")
    @Schema(description = "UUID of the customer", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
    private UUID customerId;
    
    @NotEmpty(message = "At least one sale item is required")
    @Schema(description = "List of sale items", required = true)
    private List<SaleItemRequest> items;
    
    @Schema(description = "Due date for payment", example = "2024-12-31T23:59:59Z")
    private Instant dueDate;
    
    @Schema(description = "Idempotency key to prevent duplicate sales", example = "unique-key-123")
    private String idempotencyKey;
}

