package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "App usage guide response")
public class AppUsageGuideResponse {
    @Schema(description = "App version this guide is for", example = "1.2.0")
    private String version;
    
    @Schema(description = "ISO 8601 timestamp of last update", example = "2024-01-15T10:30:00Z")
    private Instant lastUpdated;
    
    @Schema(description = "List of guide sections")
    private List<Section> sections;
}

