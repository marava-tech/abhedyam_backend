package com.abhedyam.config;

import com.abhedyam.annotation.RateLimited;
import com.abhedyam.exception.BusinessException;
import com.abhedyam.util.RateLimiter;
import com.abhedyam.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitAspect {
    
    private final RateLimiter rateLimiter;
    
    @Around("@annotation(rateLimited)")
    public Object checkRateLimit(ProceedingJoinPoint joinPoint, RateLimited rateLimited) throws Throwable {
        String key = buildKey(rateLimited);
        
        if (!rateLimiter.isAllowed(key, rateLimited.maxRequests(), rateLimited.windowSeconds())) {
            throw new BusinessException("RATE_LIMIT_EXCEEDED", 
                "Rate limit exceeded. Please try again later.");
        }
        
        return joinPoint.proceed();
    }
    
    private String buildKey(RateLimited rateLimited) {
        String prefix = rateLimited.keyPrefix();
        if (prefix.isEmpty()) {
            prefix = "default";
        }
        
        try {
            String userId = SecurityUtil.getCurrentUserId().toString();
            return prefix + ":" + userId;
        } catch (Exception e) {
            return prefix + ":anonymous";
        }
    }
}

