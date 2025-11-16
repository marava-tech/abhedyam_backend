package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Request to update document order indexes in bulk")
public class DocumentOrderUpdateRequest {
    @NotEmpty(message = "At least one document order item is required")
    @Valid
    @Schema(description = "List of document IDs with their new order indexes", required = true)
    private List<DocumentOrderItem> documents;
}

