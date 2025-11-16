package com.abhedyam.service.interfaces;

import com.abhedyam.dto.FileUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface IFileUploadService {
    FileUploadResponse uploadFile(MultipartFile file);
}

