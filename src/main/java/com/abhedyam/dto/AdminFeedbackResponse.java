package com.abhedyam.dto;

import com.abhedyam.model.enums.FeedbackCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Schema(description = "Feedback response for admin")
public class AdminFeedbackResponse {
    @Schema(description = "Feedback ID", example = "3595381f-d038-4d6b-8fe0-dc76ebb7dde2")
    private UUID id;
    
    @Schema(description = "User ID who submitted feedback", example = "3595381f-d038-4d6b-8fe0-dc76ebb7dde2")
    private UUID userId;
    
    @Schema(description = "User name", example = "John Doe")
    private String userName;
    
    @Schema(description = "User email", example = "john@example.com")
    private String userEmail;
    
    @Schema(description = "Feedback category", example = "BUG")
    private FeedbackCategory category;
    
    @Schema(description = "Issue description", example = "App crashes when opening the sales tab")
    private String issueDescription;
    
    @Schema(description = "Image URL", example = "https://cdn.example.com/screenshots/issue.png")
    private String imageUrl;
    
    @Schema(description = "Creation timestamp", example = "2025-11-15T10:51:15.325Z")
    private Instant createdAt;
}

