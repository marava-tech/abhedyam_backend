package com.abhedyam.dto;

import com.abhedyam.model.enums.AnalyticsType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AnalyticsRequest {
    @NotNull(message = "Type is required")
    private AnalyticsType type;
    
    @NotNull(message = "Owner ID is required")
    private UUID ownerId;
}

