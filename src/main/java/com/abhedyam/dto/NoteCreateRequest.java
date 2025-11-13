package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(description = "Request to create a note", example = SwaggerExamples.NOTE_CREATE_REQUEST)
public class NoteCreateRequest {
    @NotNull(message = "Customer ID is required")
    @Schema(description = "UUID of the customer", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
    private UUID customerId;
    
    @NotBlank(message = "Note text is required")
    @Schema(description = "Note content", example = "Customer prefers morning calls", required = true)
    private String text;
}
