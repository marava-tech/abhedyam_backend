package com.abhedyam.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface IStorageService {
    String saveFile(MultipartFile file, String subfolder);
    void deleteFile(String filePath);
    String getFileUrl(String filePath);
}

