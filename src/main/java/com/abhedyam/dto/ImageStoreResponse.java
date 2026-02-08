package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Image store entry response")
public class ImageStoreResponse {

    @Schema(description = "Unique identifier")
    private UUID id;

    @Schema(description = "Display name")
    private String name;

    @Schema(description = "Tags for search")
    private List<String> tags;

    @Schema(description = "Image URL")
    private String imageUrl;

    @Schema(description = "Optional description")
    private String description;

    @Schema(description = "Created at")
    private Instant createdAt;

    @Schema(description = "Updated at")
    private Instant updatedAt;
}
