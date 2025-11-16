package com.abhedyam.service.interfaces;

import com.abhedyam.dto.DocumentCreateRequest;
import com.abhedyam.dto.DocumentOrderUpdateRequest;
import com.abhedyam.dto.DocumentResponse;

import java.util.List;
import java.util.UUID;

public interface IDocumentService {
    DocumentResponse create(DocumentCreateRequest request);
    List<DocumentResponse> getByOwnerId(UUID ownerId);
    List<DocumentResponse> updateOrderIndexes(DocumentOrderUpdateRequest request);
    void delete(UUID documentId);
}

