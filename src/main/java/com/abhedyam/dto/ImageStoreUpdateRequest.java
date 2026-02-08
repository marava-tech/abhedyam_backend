package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to update an image store entry")
public class ImageStoreUpdateRequest {

    @Schema(description = "Display name", example = "Rice bag")
    private String name;

    @Schema(description = "Tags for search", example = "[\"rice\", \"grocery\"]")
    private List<String> tags;

    @Schema(description = "Image URL")
    private String imageUrl;

    @Schema(description = "Optional description")
    private String description;
}
