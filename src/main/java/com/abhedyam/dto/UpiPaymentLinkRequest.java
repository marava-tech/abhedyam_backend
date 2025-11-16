package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Schema(description = "Request to generate UPI payment link")
public class UpiPaymentLinkRequest {
    @NotNull(message = "Sale Item ID is required")
    @Schema(description = "UUID of the sale item", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
    private UUID saleItemId;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @Schema(description = "Payment amount", example = "1000.00", required = true)
    private BigDecimal amount;
}
