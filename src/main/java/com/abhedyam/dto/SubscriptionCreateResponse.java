package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Response after creating a subscription")
public class SubscriptionCreateResponse {
    
    @Schema(description = "Razorpay subscription ID to be used for checkout", example = "sub_XXXXXXXXXXXX")
    private String subscriptionId;
}

