package com.abhedyam.controller;

import com.abhedyam.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/v1/files")
@Slf4j
public class FileController {
    
    @Value("${app.storage.path:./storage}")
    private String storageBasePath;
    
    @GetMapping("/**")
    public ResponseEntity<Resource> getFile(@RequestParam(required = false) String path) {
        try {
            String filePath = path != null ? path : "";
            Path fullPath = Paths.get(storageBasePath, filePath);
            
            if (!Files.exists(fullPath) || !Files.isRegularFile(fullPath)) {
                throw new ResourceNotFoundException("File not found");
            }
            
            Resource resource = new FileSystemResource(fullPath);
            String contentType = Files.probeContentType(fullPath);
            
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fullPath.getFileName() + "\"")
                .body(resource);
        } catch (Exception e) {
            log.error("Error serving file: {}", path, e);
            throw new ResourceNotFoundException("File not found");
        }
    }
}

