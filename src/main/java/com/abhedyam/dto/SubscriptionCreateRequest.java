package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request to create a payment order")
public class SubscriptionCreateRequest {
    
    @Schema(description = "Payment amount in paise", example = "100000")
    private Long amount;
}

