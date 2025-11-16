package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Document order item for bulk update")
public class DocumentOrderItem {
    @NotNull(message = "Document ID is required")
    @Schema(description = "Document ID", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
    private UUID documentId;
    
    @NotNull(message = "Order index is required")
    @Schema(description = "Order index", example = "0", required = true)
    private Integer orderIndex;
}

