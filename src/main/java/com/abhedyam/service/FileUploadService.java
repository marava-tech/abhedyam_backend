package com.abhedyam.service;

import com.abhedyam.dto.FileUploadResponse;
import com.abhedyam.exception.BusinessException;
import com.abhedyam.service.interfaces.IFileUploadService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadService implements IFileUploadService {
    
    private final Cloudinary cloudinary;
    private static final String FOLDER_NAME = "abhedyam";
    
    @Override
    public FileUploadResponse uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("INVALID_FILE", "File is required");
        }
        
        try {
            Map<String, Object> uploadParams = ObjectUtils.asMap(
                "folder", FOLDER_NAME,
                "resource_type", "auto"
            );
            
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                uploadParams
            );
            
            String url = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");
            
            log.info("File uploaded successfully: publicId={}, url={}", publicId, url);
            
            return new FileUploadResponse(url, publicId);
        } catch (IOException e) {
            log.error("Error uploading file to Cloudinary", e);
            throw new BusinessException("UPLOAD_FAILED", "Failed to upload file: " + e.getMessage());
        }
    }
}

