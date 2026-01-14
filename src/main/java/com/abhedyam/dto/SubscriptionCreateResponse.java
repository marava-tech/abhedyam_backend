package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Response after creating a payment order")
public class SubscriptionCreateResponse {
    
    @Schema(description = "Razorpay order ID to be used for checkout", example = "order_XXXXXXXXXXXX")
    private String orderId;
    
    @Schema(description = "Payment amount in paise", example = "100000")
    private Long amount;
}

