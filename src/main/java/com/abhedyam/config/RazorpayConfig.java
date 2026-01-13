package com.abhedyam.config;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Getter
public class RazorpayConfig {
    
    @Value("${app.razorpay.key-id:}")
    private String keyId;
    
    @Value("${app.razorpay.key-secret:}")
    private String keySecret;
    
    @Value("${app.razorpay.plan-id.pro:${app.razorpay.plan-id:}}")
    private String proPlanId;
    
    @Value("${app.razorpay.plan-id.plus:}")
    private String plusPlanId;
    
    @Value("${app.razorpay.custom-payment-page-url:}")
    private String customPaymentPageUrl;
    
    @Bean
    public RazorpayClient razorpayClient() throws RazorpayException {
        if (keyId == null || keyId.isEmpty() || keySecret == null || keySecret.isEmpty()) {
            throw new IllegalStateException("Razorpay key-id and key-secret must be configured");
        }
        return new RazorpayClient(keyId, keySecret);
    }
    
    public Map<String, String> getPlanIds() {
        Map<String, String> planIds = new HashMap<>();
        if (proPlanId != null && !proPlanId.trim().isEmpty()) {
            planIds.put("PRO", proPlanId.trim());
        }
        if (plusPlanId != null && !plusPlanId.trim().isEmpty()) {
            planIds.put("PLUS", plusPlanId.trim());
        }
        return planIds;
    }
    
    public boolean isTestMode() {
        return keyId != null && keyId.startsWith("rzp_test_");
    }
    
    public String getMode() {
        return isTestMode() ? "TEST" : "LIVE";
    }
}

