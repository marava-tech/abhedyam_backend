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
@Schema(description = "Video store entry details")
public class VideoStoreResponse {
    private UUID id;
    private String name;
    private List<String> tags;
    private String videoUrl;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
}
