package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a video store entry")
public class VideoStoreCreateRequest {

    @NotBlank
    @Schema(description = "Display name", example = "Product Demo", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Tags for search", example = "[\"demo\", \"tutorial\"]")
    private List<String> tags;

    @NotBlank
    @Schema(description = "Video URL", requiredMode = Schema.RequiredMode.REQUIRED)
    private String videoUrl;

    @Schema(description = "Optional description")
    private String description;
}
