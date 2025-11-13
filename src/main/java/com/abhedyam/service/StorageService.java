package com.abhedyam.service;

import com.abhedyam.service.interfaces.IStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class StorageService implements IStorageService {
    
    @Value("${app.storage.path:./storage}")
    private String storageBasePath;
    
    @Override
    public String saveFile(MultipartFile file, String subfolder) {
        try {
            validateFile(file);
            
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get(storageBasePath, subfolder);
            
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            String relativePath = subfolder + "/" + fileName;
            log.info("File saved: {}", relativePath);
            return relativePath;
        } catch (IOException e) {
            log.error("Error saving file", e);
            throw new RuntimeException("Failed to save file", e);
        }
    }
    
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new com.abhedyam.exception.BusinessException("INVALID_FILE", "File is empty");
        }
        
        long maxSize = 10 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new com.abhedyam.exception.BusinessException("FILE_TOO_LARGE", 
                "File size exceeds maximum limit of 10MB");
        }
        
        String contentType = file.getContentType();
        if (contentType == null) {
            throw new com.abhedyam.exception.BusinessException("INVALID_FILE_TYPE", "File type cannot be determined");
        }
        
        boolean isValidType = contentType.startsWith("image/") || 
                             contentType.equals("application/pdf") ||
                             contentType.equals("application/msword") ||
                             contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        
        if (!isValidType) {
            throw new com.abhedyam.exception.BusinessException("INVALID_FILE_TYPE", 
                "Only images, PDFs, and Word documents are allowed");
        }
    }
    
    @Override
    public void deleteFile(String filePath) {
        try {
            Path path = Paths.get(storageBasePath, filePath);
            if (Files.exists(path)) {
                Files.delete(path);
                log.info("File deleted: {}", filePath);
            }
        } catch (IOException e) {
            log.error("Error deleting file: {}", filePath, e);
        }
    }
    
    @Override
    public String getFileUrl(String filePath) {
        return "/api/v1/files?path=" + filePath;
    }
}

