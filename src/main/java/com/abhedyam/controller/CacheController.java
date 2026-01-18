package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.exception.BusinessException;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.repository.UserRepository;
import com.abhedyam.util.EmailUtil;
import com.abhedyam.constants.CacheKeys;
import com.abhedyam.constants.ErrorCodes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cache")
@RequiredArgsConstructor
@Slf4j
public class CacheController {
    
    @Value("${app.cache.invalidate.key:}")
    private String securityKey;
    
    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;
    
    
    @PostMapping("/invalidate")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<String> invalidateCacheByOwnerEmail(
            @RequestParam("email") String email,
            @RequestParam("key") String key) {
        
        if (key == null || securityKey == null || securityKey.isEmpty() || !key.equals(securityKey)) {
            throw new BusinessException(ErrorCodes.UNAUTHORIZED, "Invalid or missing key");
        }
        
        String normalizedEmail = EmailUtil.normalizeEmail(email);
        if (!EmailUtil.isValidEmail(normalizedEmail)) {
            throw new BusinessException(ErrorCodes.INVALID_EMAIL, "Invalid email format");
        }
        
        UUID ownerId = userRepository.findByEmail(normalizedEmail)
                .map(user -> user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        
        int invalidatedCount = invalidateAllCacheForOwner(ownerId);
        
        log.info("Cache invalidated for owner {} (email: {}). Total keys invalidated: {}", 
            ownerId, normalizedEmail, invalidatedCount);
        
        return ApiResponse.success(
            String.format("Cache invalidated successfully for owner %s. Total keys invalidated: %d", 
                ownerId, invalidatedCount)
        );
    }
    
    private int invalidateAllCacheForOwner(UUID ownerId) {
        int totalInvalidated = 0;
        
        String[] cachePrefixes = {
            CacheKeys.CUSTOMERS_MY_PREFIX + ownerId + ":*",
            CacheKeys.CUSTOMER_SUMMARY_PREFIX + ownerId + ":*",
            CacheKeys.PRODUCTS_OWNER_PREFIX + ownerId,
            CacheKeys.PRODUCTS_WITH_STOCK_PREFIX + ownerId,
            CacheKeys.PAYMENTS_MY_PREFIX + ownerId + ":*",
            CacheKeys.DASHBOARD_STATS_PREFIX + ownerId,
            CacheKeys.STATS_DAILY_PREFIX + ownerId + ":*",
            CacheKeys.VILLAGES_PREFIX + ownerId + ":*",
            CacheKeys.INVENTORY_OWNER_PREFIX + ownerId
        };
        
        for (String pattern : cachePrefixes) {
            try {
                Set<String> keys = redisTemplate.keys(pattern);
                if (keys != null && !keys.isEmpty()) {
                    redisTemplate.delete(keys);
                    totalInvalidated += keys.size();
                    log.debug("Invalidated {} keys for pattern: {}", keys.size(), pattern);
                }
            } catch (Exception e) {
                log.warn("Error invalidating cache for pattern {}: {}", pattern, e.getMessage());
            }
        }
        
        try {
            Set<String> customerSummaryKeys = redisTemplate.keys(CacheKeys.CUSTOMER_MY_SUMMARY_PREFIX + "*");
            if (customerSummaryKeys != null && !customerSummaryKeys.isEmpty()) {
                redisTemplate.delete(customerSummaryKeys);
                totalInvalidated += customerSummaryKeys.size();
            }
        } catch (Exception e) {
            log.warn("Error invalidating customer my summary cache: {}", e.getMessage());
        }
        
        return totalInvalidated;
    }
}

