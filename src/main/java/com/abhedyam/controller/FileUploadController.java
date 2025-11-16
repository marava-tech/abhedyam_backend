package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.dto.FileUploadResponse;
import com.abhedyam.service.interfaces.IFileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Tag(name = "File Upload", description = "API for uploading files to Cloudinary")
public class FileUploadController {
    
    private final IFileUploadService fileUploadService;
    
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Upload a file",
        description = "Uploads a file (image, document, etc.) to Cloudinary and returns the public URL. All files are stored in the 'abhedyam' folder.",
        requestBody = @RequestBody(
            description = "File to upload",
            required = true,
            content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
        )
    )
    public ApiResponse<FileUploadResponse> uploadFile(
            @Schema(description = "File to upload", type = "string", format = "binary")
            @RequestParam("file") MultipartFile file) {
        return ApiResponse.success(fileUploadService.uploadFile(file));
    }
}

