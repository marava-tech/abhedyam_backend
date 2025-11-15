package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(description = "Request to update a note")
public class NoteUpdateRequest {
    @NotNull(message = "Note ID is required")
    @Schema(description = "Note ID", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
    private UUID id;
    
    @NotBlank(message = "Note text is required")
    @Schema(description = "Note content", example = "Customer prefers morning calls", required = true)
    private String text;
}

