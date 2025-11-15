package com.abhedyam.dto;

import com.abhedyam.model.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Schema(description = "Request to update a payment")
public class PaymentUpdateRequest {
    @NotNull(message = "Payment ID is required")
    @Schema(description = "Payment ID", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
    private UUID id;
    
    @Schema(description = "Payment amount", example = "1000.00")
    private BigDecimal amount;
    
    @Schema(description = "Payment status", example = "SUCCESS")
    private PaymentStatus status;
    
    @Schema(description = "Payment reference/transaction ID", example = "TXN123456789")
    private String reference;
    
    @Schema(description = "Payment timestamp", example = "2024-12-31T10:00:00Z")
    private Instant paidAt;
}

