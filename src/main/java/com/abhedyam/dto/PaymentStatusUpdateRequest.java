package com.abhedyam.dto;

import com.abhedyam.model.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request to update payment status", example = SwaggerExamples.PAYMENT_STATUS_UPDATE_REQUEST)
public class PaymentStatusUpdateRequest {
    @NotNull(message = "Payment status is required")
    @Schema(description = "New payment status", example = "SUCCESS", required = true)
    private PaymentStatus status;
    
    @Schema(description = "Payment reference/transaction ID", example = "TXN123456789")
    private String reference;
}
