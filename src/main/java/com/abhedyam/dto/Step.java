package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Step in a guide item")
public class Step {
    @Schema(description = "Step order", example = "1")
    private Integer order;
    
    @Schema(description = "Step title", example = "Navigate to Profile")
    private String title;
    
    @Schema(description = "Detailed step description (supports \\n for line breaks)", 
            example = "Tap on the profile icon in the bottom navigation or settings menu")
    private String description;
    
    @Schema(description = "Material icon name", example = "person")
    private String icon;
}

