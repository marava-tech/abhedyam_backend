package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Razorpay configuration for client-side integration")
public class RazorpayConfigResponse {
    
    @Schema(description = "Razorpay key ID for client-side integration", example = "rzp_live_xxxxx")
    private String razorpayKey;
    
    @Schema(description = "Custom Razorpay payment page URL", example = "https://rzp.io/rzp/yMlz0H3")
    private String customPaymentPageUrl;
}

