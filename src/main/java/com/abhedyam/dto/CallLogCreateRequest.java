package com.abhedyam.dto;

import com.abhedyam.model.enums.CallDirection;
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
    private Instant startTime;
    
    private Instant endTime;
    
    private Integer durationSeconds;
    
    @NotNull(message = "Phone number is required")
    private String phone;
}

