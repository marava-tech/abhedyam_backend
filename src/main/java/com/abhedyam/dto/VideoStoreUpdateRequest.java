package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to update a video store entry")
public class VideoStoreUpdateRequest {
    private String name;
    private List<String> tags;
    private String videoUrl;
    private String description;
}
