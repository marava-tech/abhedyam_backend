package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
@Schema(description = "Owner settings response")
public class OwnerSettingsResponse {
    @Schema(description = "Settings ID", example = "3595381f-d038-4d6b-8fe0-dc76ebb7dde2")
    private UUID id;
    
    @Schema(description = "Daily quote enabled", example = "true")
    private Boolean dailyQuoteEnabled;
    
    @Schema(description = "Call log sync enabled", example = "true")
    private Boolean callLogSyncEnabled;
    
    @Schema(description = "Other feature flags")
    private Map<String, Object> otherFlags;
    
    @Schema(description = "Creation timestamp")
    private Instant createdAt;
    
    @Schema(description = "Last update timestamp")
    private Instant updatedAt;
}

