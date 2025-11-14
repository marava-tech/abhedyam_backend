package com.abhedyam.service.interfaces;

import com.abhedyam.model.Document;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface IDocumentUploadService {
    Document uploadDocument(MultipartFile file, String name, Integer orderIndex, Boolean visibleToCustomers);
    List<Document> getOwnerDocuments();
    Document getDocumentById(UUID id);
    Document updateDocument(UUID id, String name, Integer orderIndex, Boolean visibleToCustomers);
    void deleteDocument(UUID id);
}

