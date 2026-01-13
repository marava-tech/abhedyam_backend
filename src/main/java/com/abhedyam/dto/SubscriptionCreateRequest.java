package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request to create a subscription")
public class SubscriptionCreateRequest {
    
    @Schema(description = "Razorpay plan ID (optional - recurring will be controlled manually)", example = "plan_XXXXXXXXXXXX")
    private String planId;
}

