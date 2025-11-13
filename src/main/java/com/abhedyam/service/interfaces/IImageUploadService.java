package com.abhedyam.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface IImageUploadService {
    String uploadImage(MultipartFile file);
}

