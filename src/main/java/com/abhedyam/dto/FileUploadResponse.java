package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response containing the uploaded file URL")
public class FileUploadResponse {
    @Schema(description = "Public URL of the uploaded file", example = "https://res.cloudinary.com/dohsebpd1/image/upload/v1234567890/abhedyam/example.jpg")
    private String url;
    
    @Schema(description = "Public ID of the uploaded file", example = "abhedyam/example")
    private String publicId;
}

