package com.abhedyam.service.interfaces;

import com.abhedyam.dto.BulkImportResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface IBulkImportService {
    BulkImportResult importData(UUID ownerId, MultipartFile file);
}
