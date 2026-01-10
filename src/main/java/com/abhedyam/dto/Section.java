package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Section in the app usage guide")
public class Section {
    @Schema(description = "Unique identifier", example = "profile-management")
    private String id;
    
    @Schema(description = "Section title", example = "Profile Management")
    private String title;
    
    @Schema(description = "Material icon name", example = "account_circle")
    private String icon;
    
    @Schema(description = "Brief section description", example = "Manage your business profile and personal information")
    private String description;
    
    @Schema(description = "List of guide items in this section")
    private List<GuideItem> items;
}

