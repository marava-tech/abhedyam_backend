package com.abhedyam.config;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class RazorpayConfig {
    
    @Value("${app.razorpay.key-id:}")
    private String keyId;
    
    @Value("${app.razorpay.key-secret:}")
    private String keySecret;
    
    @Value("${app.razorpay.custom-payment-page-url:}")
    private String customPaymentPageUrl;
    
    @Bean
    public RazorpayClient razorpayClient() throws RazorpayException {
        if (keyId == null || keyId.isEmpty() || keySecret == null || keySecret.isEmpty()) {
            throw new IllegalStateException("Razorpay key-id and key-secret must be configured");
        }
        return new RazorpayClient(keyId, keySecret);
    }
    
    public boolean isTestMode() {
        return keyId != null && keyId.startsWith("rzp_test_");
    }
    
    public String getMode() {
        return isTestMode() ? "TEST" : "LIVE";
    }
}

