package com.abhedyam.config;

import com.abhedyam.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorrelationIdFilter correlationIdFilter;
    private final LoggingFilter loggingFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    boolean isDev = "dev".equals(activeProfile) || "local".equals(activeProfile) || activeProfile.isEmpty();
                    
                    if (isDev) {
                        auth.requestMatchers(
                                "/api/v1/auth/**",
                                "/api/v1/products/owner/*/with-stock",
                                "/api/v1/owners/public",
                                "/api/v1/inventories/owner/*",
                                "/api/v1/app-usage-guide",
                                "/api/v1/cache/invalidate",
                                "/api/v1/webhook/razorpay",
                                "/api/v1/health",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-ui/index.html",
                                "/api-docs/**",
                                "/v3/api-docs/**",
                                "/error"
                        ).permitAll();
                    } else {
                        auth.requestMatchers(
                                "/api/v1/auth/**",
                                "/api/v1/products/owner/*/with-stock",
                                "/api/v1/owners/public",
                                "/api/v1/inventories/owner/*",
                                "/api/v1/app-usage-guide",
                                "/api/v1/cache/invalidate",
                                "/api/v1/webhook/razorpay",
                                "/api/v1/health",
                                "/error"
                        ).permitAll();
                    }
                    
                    auth.requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers("/actuator/**").authenticated()
                        .anyRequest().authenticated();
                })
                .addFilterBefore(correlationIdFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(loggingFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

