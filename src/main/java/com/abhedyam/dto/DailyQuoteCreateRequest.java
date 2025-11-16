package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request to create a daily quote")
public class DailyQuoteCreateRequest {
    @NotBlank(message = "Quote text is required")
    @Schema(description = "Quote text", example = "Success is not final, failure is not fatal: it is the courage to continue that counts.", required = true)
    private String text;
}

