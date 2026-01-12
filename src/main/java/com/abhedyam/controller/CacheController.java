package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.exception.BusinessException;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.repository.UserRepository;
import com.abhedyam.util.EmailUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cache")
@RequiredArgsConstructor
@Slf4j
public class CacheController {
    
    private static final String SECURITY_KEY = "Madhu7814";
    
    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;
    
    private static final String CUSTOMERS_CACHE_PREFIX = "customers:my:";
    private static final String CUSTOMER_SUMMARY_CACHE_PREFIX = "customers:summary:";
    private static final String CUSTOMER_MY_SUMMARY_CACHE_PREFIX = "customers:my-summary:";
    private static final String PRODUCTS_CACHE_PREFIX = "products:owner:";
    private static final String PRODUCTS_WITH_STOCK_CACHE_PREFIX = "products:with-stock:";
    private static final String PAYMENTS_MY_CACHE_PREFIX = "payments:my:";
    private static final String DASHBOARD_STATS_CACHE_PREFIX = "stats:dashboard:";
    private static final String STATS_CACHE_PREFIX = "stats:daily:";
    private static final String VILLAGES_CACHE_PREFIX = "villages:";
    private static final String INVENTORY_CACHE_PREFIX = "inventory:owner:";
    
    @PostMapping("/invalidate")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<String> invalidateCacheByOwnerEmail(
            @RequestParam("email") String email,
            @RequestParam("key") String key) {
        
        if (key == null || !key.equals(SECURITY_KEY)) {
            throw new BusinessException("UNAUTHORIZED", "Invalid or missing key");
        }
        
        String normalizedEmail = EmailUtil.normalizeEmail(email);
        if (!EmailUtil.isValidEmail(normalizedEmail)) {
            throw new BusinessException("INVALID_EMAIL", "Invalid email format");
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
            CUSTOMERS_CACHE_PREFIX + ownerId + ":*",
            CUSTOMER_SUMMARY_CACHE_PREFIX + ownerId + ":*",
            PRODUCTS_CACHE_PREFIX + ownerId,
            PRODUCTS_WITH_STOCK_CACHE_PREFIX + ownerId,
            PAYMENTS_MY_CACHE_PREFIX + ownerId + ":*",
            DASHBOARD_STATS_CACHE_PREFIX + ownerId,
            STATS_CACHE_PREFIX + ownerId + ":*",
            VILLAGES_CACHE_PREFIX + ownerId + ":*",
            INVENTORY_CACHE_PREFIX + ownerId
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
            Set<String> customerSummaryKeys = redisTemplate.keys(CUSTOMER_MY_SUMMARY_CACHE_PREFIX + "*");
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

