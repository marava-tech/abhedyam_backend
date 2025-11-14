package com.abhedyam.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class LoggingFilter extends OncePerRequestFilter {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
        
        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            
            Map<String, Object> logData = new HashMap<>();
            logData.put("method", request.getMethod());
            logData.put("uri", request.getRequestURI());
            logData.put("queryString", request.getQueryString());
            logData.put("status", wrappedResponse.getStatus());
            logData.put("duration", duration);
            logData.put("correlationId", MDC.get("correlationId"));
            
            if (wrappedResponse.getStatus() >= 400) {
                log.error("Request failed: {}", objectMapper.writeValueAsString(logData));
            } else {
                log.info("Request completed: {}", objectMapper.writeValueAsString(logData));
            }
            
            wrappedResponse.copyBodyToResponse();
        }
    }
}

