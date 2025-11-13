package com.abhedyam.service;

import com.abhedyam.exception.BusinessException;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Document;
import com.abhedyam.repository.DocumentRepository;
import com.abhedyam.service.interfaces.IDocumentUploadService;
import com.abhedyam.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentUploadService implements IDocumentUploadService {
    
    private final DocumentRepository documentRepository;
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    
    @Override
    @Transactional
    public Document uploadDocument(MultipartFile file, String name, Integer orderIndex, Boolean visibleToCustomers) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        
        if (file.isEmpty()) {
            throw new BusinessException("EMPTY_FILE", "File cannot be empty");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("FILE_TOO_LARGE", "File size exceeds 10MB limit");
        }
        
        String fileName = name != null ? name : file.getOriginalFilename();
        String mimeType = file.getContentType();
        String uploadedUrl = uploadToStorage(file);
        
        Document document = new Document();
        document.setName(fileName);
        document.setMimeType(mimeType != null ? mimeType : "application/octet-stream");
        document.setUploadedUrl(uploadedUrl);
        document.setOrderIndex(orderIndex != null ? orderIndex : 0);
        document.setVisibleToCustomers(visibleToCustomers != null ? visibleToCustomers : false);
        document.setOwnerId(ownerId);
        
        return documentRepository.save(document);
    }
    
    private String uploadToStorage(MultipartFile file) {
        log.info("Uploading file: {} (size: {} bytes)", file.getOriginalFilename(), file.getSize());
        return "https://storage.example.com/documents/" + UUID.randomUUID() + "/" + file.getOriginalFilename();
    }
    
    @Override
    public List<Document> getOwnerDocuments() {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        return documentRepository.findByOwnerId(ownerId);
    }
    
    @Override
    public Document getDocumentById(UUID id) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));
        
        if (!document.getOwnerId().equals(ownerId)) {
            throw new BusinessException("UNAUTHORIZED", "You don't have access to this document");
        }
        
        return document;
    }
    
    @Override
    @Transactional
    public void deleteDocument(UUID id) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        Document document = getDocumentById(id);
        
        document.setDeletedAt(Instant.now());
        document.setIsActive(false);
        documentRepository.save(document);
    }
}

