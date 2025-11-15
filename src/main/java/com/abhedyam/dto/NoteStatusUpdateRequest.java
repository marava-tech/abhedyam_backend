package com.abhedyam.dto;

import com.abhedyam.model.enums.NoteStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(description = "Request to update note status")
public class NoteStatusUpdateRequest {
    @NotNull(message = "Note ID is required")
    @Schema(description = "Note ID", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
    private UUID id;
    
    @NotNull(message = "Note status is required")
    @Schema(description = "New note status", example = "ACTIVE", required = true)
    private NoteStatus status;
}

