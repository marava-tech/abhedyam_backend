package com.abhedyam.dto;

import com.abhedyam.model.enums.CallDirection;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;
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
    
    private Integer durationSeconds;
    
    @NotNull(message = "Phone number is required")
    private String phone;
    
    private String key;
}

