package com.abhedyam.controller;

import com.abhedyam.config.RazorpayConfig;
import com.abhedyam.dto.ApiResponse;
import com.abhedyam.dto.RazorpayConfigResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@Tag(name = "Payment Configuration", description = "API for payment gateway configuration")
public class PaymentConfigController {
    
    private final RazorpayConfig razorpayConfig;
    
    @GetMapping("/razorpay-config")
    @Operation(summary = "Get Razorpay configuration", description = "Returns Razorpay key ID, plan IDs, and custom payment page URL for client-side integration")
    public ApiResponse<RazorpayConfigResponse> getRazorpayConfig() {
        RazorpayConfigResponse response = new RazorpayConfigResponse();
        response.setRazorpayKey(razorpayConfig.getKeyId());
        response.setRazorpayPlanId(razorpayConfig.getPlanIds());
        String customPaymentPageUrl = razorpayConfig.getCustomPaymentPageUrl();
        if (customPaymentPageUrl != null && !customPaymentPageUrl.trim().isEmpty()) {
            response.setCustomPaymentPageUrl(customPaymentPageUrl.trim());
        }
        return ApiResponse.success(response);
    }
}

