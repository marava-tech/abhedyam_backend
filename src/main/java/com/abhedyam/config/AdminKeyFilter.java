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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import com.abhedyam.security.UserPrincipal;
import java.util.ArrayList;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminKeyFilter extends OncePerRequestFilter {

    @Value("${app.admin.key-prefix:Madhu7814}")
    private String keyPrefix;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String adminKey = request.getHeader("X-Admin-Key");
        String path = request.getRequestURI();

        // Skip if already authenticated (e.g. by JWT)
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (adminKey != null && !adminKey.trim().isEmpty()) {
            LocalDate now = LocalDate.now();
            String expectedDate = now.format(DateTimeFormatter.ofPattern("dd"));
            String expectedMonth = now.format(DateTimeFormatter.ofPattern("MM"));
            String expectedKey = keyPrefix + expectedDate + expectedMonth;

            if (expectedKey.equals(adminKey)) {
                log.debug("Valid Admin Key detected, setting admin authentication for path: {}", path);
                // Set system admin context
                UserPrincipal adminPrincipal = new UserPrincipal(
                        UUID.fromString("00000000-0000-0000-0000-000000000000"), "ADMIN");
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        adminPrincipal, null, new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else if (path.startsWith("/api/v1/admin")) {
                // Invalid key on an admin route
                log.warn("Invalid admin key provided for admin route: {}", path);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"FORBIDDEN\",\"message\":\"Invalid admin key\"}");
                return;
            }
        } else if (path.startsWith("/api/v1/admin")) {
            // Missing key on an admin route
            log.warn("Admin key missing for admin route: {}", path);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"UNAUTHORIZED\",\"message\":\"Admin key is required\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
