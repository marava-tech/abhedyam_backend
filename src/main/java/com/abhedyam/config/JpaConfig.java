package com.abhedyam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "istDateTimeProvider")
public class JpaConfig {
    
    @Bean
    public DateTimeProvider istDateTimeProvider() {
        return new IstAuditingHandler();
    }
}

