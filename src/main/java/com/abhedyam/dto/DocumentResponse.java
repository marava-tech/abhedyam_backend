package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Document response")
public class DocumentResponse {
    @Schema(description = "Document ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;
    
    @Schema(description = "Document name", example = "Terms and Conditions")
    private String name;
    
    @Schema(description = "MIME type", example = "application/pdf")
    private String mimeType;
    
    @Schema(description = "Upload URL", example = "https://res.cloudinary.com/...")
    private String uploadedUrl;
    
    @Schema(description = "Owner ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID ownerId;
    
    @Schema(description = "Order index for sorting", example = "0")
    private Integer orderIndex;
    
    @Schema(description = "Active status", example = "true")
    private Boolean isActive;
    
    @Schema(description = "Visible to customers", example = "true")
    private Boolean visibleToCustomers;
    
    @Schema(description = "Created timestamp")
    private Instant createdAt;
    
    @Schema(description = "Updated timestamp")
    private Instant updatedAt;
}

