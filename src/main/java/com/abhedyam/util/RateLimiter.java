package com.abhedyam.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimiter {
    
    private final RedisTemplate<String, String> redisTemplate;
    
    public boolean isAllowed(String key, int maxRequests, int windowSeconds) {
        String redisKey = "rate_limit:" + key;
        
        String countStr = redisTemplate.opsForValue().get(redisKey);
        int currentCount = countStr != null ? Integer.parseInt(countStr) : 0;
        
        if (currentCount >= maxRequests) {
            Long ttl = redisTemplate.getExpire(redisKey);
            log.warn("Rate limit exceeded for key: {}, current: {}, max: {}, ttl: {}", 
                key, currentCount, maxRequests, ttl);
            return false;
        }
        
        Long newCount = redisTemplate.opsForValue().increment(redisKey);
        if (newCount == 1) {
            redisTemplate.expire(redisKey, windowSeconds, TimeUnit.SECONDS);
        }
        
        return true;
    }
    
    public long getRemainingRequests(String key, int maxRequests) {
        String redisKey = "rate_limit:" + key;
        String countStr = redisTemplate.opsForValue().get(redisKey);
        int currentCount = countStr != null ? Integer.parseInt(countStr) : 0;
        return Math.max(0, maxRequests - currentCount);
    }
}

