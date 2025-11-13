package com.abhedyam.service;

import com.abhedyam.exception.BusinessException;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Document;
import com.abhedyam.repository.CustomerRepository;
import com.abhedyam.repository.DocumentRepository;
import com.abhedyam.service.interfaces.ICustomerDocumentService;
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
public class CustomerDocumentService implements ICustomerDocumentService {
    
    private final DocumentRepository documentRepository;
    private final CustomerRepository customerRepository;
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    
    @Override
    @Transactional
    public Document uploadCustomerDocument(UUID customerId, MultipartFile file, String name, Integer orderIndex) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found");
        }
        
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
        document.setVisibleToCustomers(true);
        document.setOwnerId(ownerId);
        
        return documentRepository.save(document);
    }
    
    private String uploadToStorage(MultipartFile file) {
        log.info("Uploading customer document: {} (size: {} bytes)", file.getOriginalFilename(), file.getSize());
        return "https://storage.example.com/customer-documents/" + UUID.randomUUID() + "/" + file.getOriginalFilename();
    }
    
    @Override
    public List<Document> getCustomerDocuments(UUID customerId) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found");
        }
        
        return documentRepository.findByOwnerId(ownerId).stream()
            .filter(doc -> doc.getVisibleToCustomers() != null && doc.getVisibleToCustomers())
            .toList();
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

