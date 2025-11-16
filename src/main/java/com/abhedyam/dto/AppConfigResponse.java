package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "App configuration response")
public class AppConfigResponse {
    @Schema(description = "Daily quote feature enabled", example = "true")
    private Boolean dailyQuoteEnabled;
    
    @Schema(description = "Call log sync feature enabled", example = "true")
    private Boolean callLogSyncEnabled;
    
    @Schema(description = "Dark mode enabled", example = "false")
    private Boolean isDarkModeEnabled;
    
    @Schema(description = "Other feature flags and configuration")
    private Map<String, Object> otherFlags;
}

