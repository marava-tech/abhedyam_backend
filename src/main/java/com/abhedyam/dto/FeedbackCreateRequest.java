package com.abhedyam.dto;

import com.abhedyam.model.enums.FeedbackCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request to submit user feedback with optional screenshot")
public class FeedbackCreateRequest {
    
    @NotNull(message = "Category is required")
    @Schema(description = "Type of feedback", example = "BUG", requiredMode = Schema.RequiredMode.REQUIRED)
    private FeedbackCategory category;
    
    @NotBlank(message = "Issue description is required")
    @Size(max = 2000, message = "Issue description cannot exceed 2000 characters")
    @Schema(description = "Details of the issue or feedback", example = "App crashes when opening the sales tab", requiredMode = Schema.RequiredMode.REQUIRED)
    private String issueDescription;
    
    @Size(max = 2048, message = "Image URL cannot exceed 2048 characters")
    @Schema(description = "Optional screenshot URL", example = "https://cdn.example.com/screenshots/issue.png")
    private String imageUrl;
}

