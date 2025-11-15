package com.abhedyam.service;

import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Document;
import com.abhedyam.repository.DocumentRepository;
import com.abhedyam.service.interfaces.IDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService implements IDocumentService {
    
    private final DocumentRepository documentRepository;
    
    public Document create(Document document) {
        return documentRepository.save(document);
    }
    
    public Document getById(UUID id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + id));
    }
    
    public List<Document> getAll() {
        return documentRepository.findAll();
    }
    
    public List<Document> getByOwnerId(UUID ownerId) {
        return documentRepository.findByOwnerId(ownerId);
    }
    
    @Transactional
    public Document update(UUID id, Document documentDetails) {
        Document document = getById(id);
        if (documentDetails.getName() != null) document.setName(documentDetails.getName());
        if (documentDetails.getMimeType() != null) document.setMimeType(documentDetails.getMimeType());
        if (documentDetails.getUploadedUrl() != null) document.setUploadedUrl(documentDetails.getUploadedUrl());
        if (documentDetails.getOrderIndex() != null) document.setOrderIndex(documentDetails.getOrderIndex());
        if (documentDetails.getVisibleToCustomers() != null) document.setVisibleToCustomers(documentDetails.getVisibleToCustomers());
        return documentRepository.save(document);
    }
}

