package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Guide item within a section")
public class GuideItem {
    @Schema(description = "Unique identifier", example = "update-profile")
    private String id;
    
    @Schema(description = "Item title", example = "Update Your Profile")
    private String title;
    
    @Schema(description = "Brief description", example = "Keep your business information up to date")
    private String description;
    
    @Schema(description = "Material icon name", example = "edit")
    private String icon;
    
    @Schema(description = "Ordered list of steps")
    private List<Step> steps;
    
    @Schema(description = "Optional tips array")
    private List<String> tips;
}

