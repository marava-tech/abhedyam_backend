package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Enhanced description overrides for app guide items")
public class AppGuideEnhancedDescriptions {
    @Schema(description = "Note about enhanced descriptions")
    private String note;
    
    @Schema(description = "Enhanced description map by context")
    private Map<String, Map<String, String>> enhancedDescriptions;
    
    @Schema(description = "Usage guidance for enhanced descriptions")
    private String usage;
}


