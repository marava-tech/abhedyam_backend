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
@Schema(description = "Search result for video recommendations")
public class VideoStoreSearchResult {
    private UUID id;
    private String name;
    private List<String> tags;
    private String videoUrl;
}
