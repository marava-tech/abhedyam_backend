package com.abhedyam.dto;

import com.abhedyam.model.enums.CallDirection;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
public class CallLogCreateRequest {
    @NotNull(message = "Customer ID is required")
    private UUID customerId;
    
    @NotNull(message = "Direction is required")
    private CallDirection direction;
    
    @NotNull(message = "Start time is required")
    @JsonDeserialize(using = FlexibleInstantDeserializer.class)
    private Instant startTime;
    
    @JsonDeserialize(using = FlexibleInstantDeserializer.class)
    private Instant endTime;
    
    private static class FlexibleInstantDeserializer extends JsonDeserializer<Instant> {
        @Override
        public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String value = p.getText();
            if (value == null || value.isEmpty()) {
                return null;
            }
            
            try {
                if (value.endsWith("Z") || value.contains("+") || value.contains("-") && value.length() > 19) {
                    return Instant.parse(value);
                } else {
                    LocalDateTime localDateTime = LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    return localDateTime.atZone(ZoneId.of("Asia/Kolkata")).toInstant();
                }
            } catch (Exception e) {
                throw new IOException("Failed to parse Instant: " + value, e);
            }
        }
    }
    
    private Integer durationSeconds;
    
    @NotNull(message = "Phone number is required")
    private String phone;
    
    private String key;
}

