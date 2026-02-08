package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Image store search result for product recommendations")
public class ImageStoreSearchResult {

    @Schema(description = "Unique identifier")
    private UUID id;

    @Schema(description = "Display name")
    private String name;

    @Schema(description = "Tags")
    private List<String> tags;

    @Schema(description = "Image URL")
    private String imageUrl;
}
