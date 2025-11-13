package com.abhedyam.service;

import com.abhedyam.exception.BusinessException;
import com.abhedyam.service.interfaces.IImageUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@Slf4j
public class ImageUploadService implements IImageUploadService {
    
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final String[] ALLOWED_TYPES = {"image/jpeg", "image/png", "image/webp", "image/jpg"};
    
    @Override
    public String uploadImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("EMPTY_FILE", "File cannot be empty");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("FILE_TOO_LARGE", "File size exceeds 5MB limit");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !isAllowedType(contentType)) {
            throw new BusinessException("INVALID_FILE_TYPE", "Only JPEG, PNG, and WebP images are allowed");
        }
        
        String imageUrl = uploadToStorage(file);
        log.info("Image uploaded: {} (size: {} bytes)", file.getOriginalFilename(), file.getSize());
        
        return imageUrl;
    }
    
    private boolean isAllowedType(String contentType) {
        for (String allowedType : ALLOWED_TYPES) {
            if (contentType.equalsIgnoreCase(allowedType)) {
                return true;
            }
        }
        return false;
    }
    
    private String uploadToStorage(MultipartFile file) {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        return "https://storage.example.com/images/" + fileName;
    }
}

