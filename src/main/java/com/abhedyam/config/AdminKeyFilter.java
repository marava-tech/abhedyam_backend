package com.abhedyam.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminKeyFilter extends OncePerRequestFilter {

    @Value("${app.admin.key-prefix:Madhu7814}")
    private String keyPrefix;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String path = request.getRequestURI();
        
        if (path.startsWith("/api/v1/admin")) {
            String adminKey = request.getHeader("X-Admin-Key");
            
            if (adminKey == null || adminKey.trim().isEmpty()) {
                log.warn("Admin API access attempted without key - path: {}", path);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"UNAUTHORIZED\",\"message\":\"Admin key is required\"}");
                return;
            }
            
            LocalDate now = LocalDate.now();
            String expectedDate = now.format(DateTimeFormatter.ofPattern("dd"));
            String expectedMonth = now.format(DateTimeFormatter.ofPattern("MM"));
            String expectedKey = keyPrefix + expectedDate + expectedMonth;
            
            if (!expectedKey.equals(adminKey)) {
                log.warn("Invalid admin key attempted - path: {}, provided: {}", path, adminKey);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"FORBIDDEN\",\"message\":\"Invalid admin key\"}");
                return;
            }
            
            log.debug("Admin key validated successfully - path: {}", path);
        }
        
        filterChain.doFilter(request, response);
    }
}

