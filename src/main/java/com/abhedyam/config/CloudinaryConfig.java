package com.abhedyam.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    Cloudinary getCloudinary() {
        Map<String, String> map = new HashMap<>();
        map.put("cloud_name", "dohsebpd1");
        map.put("api_key", "421394291995232");
        map.put("api_secret", "ChXQSdHuz9jeycoA0XsW2BFwkcs");
        map.put("secure", "true");
        map.put("timeout", "300000");
        map.put("connection_timeout", "300000");
        return new Cloudinary(map);
    }
}

