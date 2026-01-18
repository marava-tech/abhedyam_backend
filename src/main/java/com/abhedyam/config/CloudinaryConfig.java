package com.abhedyam.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Value("${app.cloudinary.cloud-name:dhssmiyoc}")
    private String cloudName;

    @Value("${app.cloudinary.api-key:REDACTED_CLOUDINARY_KEY}")
    private String apiKey;

    @Value("${app.cloudinary.api-secret:REDACTED_CLOUDINARY_SECRET}")
    private String apiSecret;

    @Bean
    Cloudinary getCloudinary() {
        String cloudinaryUrl = System.getenv("CLOUDINARY_URL");
        if (cloudinaryUrl != null && !cloudinaryUrl.isEmpty()) {
            return new Cloudinary(cloudinaryUrl);
        }
        
        Map<String, String> map = new HashMap<>();
        map.put("cloud_name", cloudName);
        map.put("api_key", apiKey);
        map.put("api_secret", apiSecret);
        map.put("secure", "true");
        map.put("timeout", "300000");
        map.put("connection_timeout", "300000");
        return new Cloudinary(map);
    }
}

