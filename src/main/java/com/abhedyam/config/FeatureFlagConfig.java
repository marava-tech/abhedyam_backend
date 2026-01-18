package com.abhedyam.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "app.features")
@Data
public class FeatureFlagConfig {
    
    private Map<String, Boolean> global = Map.of(
        "dailyQuote", true
    );
    
    public boolean isEnabled(String featureName) {
        return global.getOrDefault(featureName, false);
    }
}

