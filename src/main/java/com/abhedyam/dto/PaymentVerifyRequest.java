package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request to verify payment signature")
public class PaymentVerifyRequest {
    
    @NotBlank(message = "Order ID is required")
    @Schema(description = "Razorpay order ID", example = "order_XXXXXXXXXXXX", required = true)
    private String orderId;
    
    @NotBlank(message = "Payment ID is required")
    @Schema(description = "Razorpay payment ID", example = "pay_XXXXXXXXXXXX", required = true)
    private String paymentId;
    
    @NotBlank(message = "Razorpay signature is required")
    @Schema(description = "Razorpay payment signature for verification", example = "abc123...", required = true)
    private String razorpaySignature;
}

