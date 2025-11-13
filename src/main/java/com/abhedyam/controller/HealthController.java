package com.abhedyam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
@RequiredArgsConstructor
public class HealthController implements HealthIndicator {
    
    private final DataSource dataSource;
    private final RedisTemplate<String, String> redisTemplate;
    
    @Override
    @GetMapping
    public Health health() {
        Map<String, Object> details = new HashMap<>();
        
        try {
            try (Connection connection = dataSource.getConnection()) {
                boolean isValid = connection.isValid(2);
                details.put("database", isValid ? "UP" : "DOWN");
            }
        } catch (Exception e) {
            details.put("database", "DOWN");
            details.put("databaseError", e.getMessage());
        }
        
        try {
            redisTemplate.opsForValue().set("health:check", "ok");
            redisTemplate.delete("health:check");
            details.put("redis", "UP");
        } catch (Exception e) {
            details.put("redis", "DOWN");
            details.put("redisError", e.getMessage());
        }
        
        boolean isUp = details.get("database").equals("UP") && details.get("redis").equals("UP");
        
        return isUp ? Health.up().withDetails(details).build() : Health.down().withDetails(details).build();
    }
}

