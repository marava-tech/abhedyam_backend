package com.abhedyam.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class FlexibleInstantDeserializer extends JsonDeserializer<Instant> {
    private static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");
    
    @Override
    public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText();
        if (value == null || value.isEmpty()) {
            return null;
        }
        
        try {
            if (value.endsWith("Z") || value.contains("+") || value.contains("-")) {
                return Instant.parse(value);
            } else {
                LocalDateTime localDateTime = LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                Instant instant = localDateTime.atZone(IST_ZONE).toInstant();
                return instant;
            }
        } catch (Exception e) {
            throw new IOException("Failed to parse Instant: " + value, e);
        }
    }
}

