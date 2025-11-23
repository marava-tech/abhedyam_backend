package com.abhedyam.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Converter(autoApply = true)
public class IstInstantConverter implements AttributeConverter<Instant, LocalDateTime> {
    
    private static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");
    
    @Override
    public LocalDateTime convertToDatabaseColumn(Instant instant) {
        if (instant == null) {
            return null;
        }
        return instant.atZone(IST_ZONE).toLocalDateTime();
    }
    
    @Override
    public Instant convertToEntityAttribute(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(IST_ZONE).toInstant();
    }
}

