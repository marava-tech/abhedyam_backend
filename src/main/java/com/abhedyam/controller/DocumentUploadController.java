package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.model.Document;
import com.abhedyam.service.interfaces.IDocumentUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentUploadController {
    
    private final IDocumentUploadService documentUploadService;
    
    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Document> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "orderIndex", required = false) Integer orderIndex,
            @RequestParam(value = "visibleToCustomers", required = false) Boolean visibleToCustomers) {
        return ApiResponse.success(documentUploadService.uploadDocument(file, name, orderIndex, visibleToCustomers));
    }
    
    @GetMapping
    public ApiResponse<List<Document>> getOwnerDocuments() {
        return ApiResponse.success(documentUploadService.getOwnerDocuments());
    }
    
    @GetMapping("/{id}")
    public ApiResponse<Document> getDocumentById(@PathVariable UUID id) {
        return ApiResponse.success(documentUploadService.getDocumentById(id));
    }
    
    @PutMapping("/{id}")
    public ApiResponse<Document> updateDocument(
            @PathVariable UUID id,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "orderIndex", required = false) Integer orderIndex,
            @RequestParam(value = "visibleToCustomers", required = false) Boolean visibleToCustomers) {
        return ApiResponse.success(documentUploadService.updateDocument(id, name, orderIndex, visibleToCustomers));
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deleteDocument(@PathVariable UUID id) {
        documentUploadService.deleteDocument(id);
        return ApiResponse.success(null);
    }
}

