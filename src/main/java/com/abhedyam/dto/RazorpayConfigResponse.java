package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "Razorpay configuration for client-side integration")
public class RazorpayConfigResponse {
    
    @Schema(description = "Razorpay key ID for client-side integration", example = "rzp_test_xxxxx")
    private String razorpayKey;
    
    @Schema(description = "Map of subscription plan types to Razorpay plan IDs", 
            example = "{\"PRO\": \"plan_xxxxx\", \"PLUS\": \"plan_yyyyy\"}")
    private Map<String, String> razorpayPlanId;
    
    @Schema(description = "Custom Razorpay payment page URL", example = "https://rzp.io/rzp/yMlz0H3")
    private String customPaymentPageUrl;
}

