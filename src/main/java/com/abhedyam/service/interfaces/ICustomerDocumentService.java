package com.abhedyam.service.interfaces;

import com.abhedyam.model.Document;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ICustomerDocumentService {
    Document uploadCustomerDocument(UUID customerId, MultipartFile file, String name, Integer orderIndex);
    List<Document> getCustomerDocuments(UUID customerId);
    Document getDocumentById(UUID id);
    void deleteDocument(UUID id);
}

