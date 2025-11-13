package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.model.Document;
import com.abhedyam.service.interfaces.IDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {
    
    private final IDocumentService documentService;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Document> create(@RequestBody Document document) {
        return ApiResponse.success(documentService.create(document));
    }
    
    @GetMapping("/{id}")
    public ApiResponse<Document> getById(@PathVariable UUID id) {
        return ApiResponse.success(documentService.getById(id));
    }
    
    @GetMapping
    public ApiResponse<List<Document>> getAll() {
        return ApiResponse.success(documentService.getAll());
    }
    
    @GetMapping("/owner/{ownerId}")
    public ApiResponse<List<Document>> getByOwnerId(@PathVariable UUID ownerId) {
        return ApiResponse.success(documentService.getByOwnerId(ownerId));
    }
    
    @PutMapping("/{id}")
    public ApiResponse<Document> update(@PathVariable UUID id, @RequestBody Document document) {
        return ApiResponse.success(documentService.update(id, document));
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        documentService.delete(id);
        return ApiResponse.success(null);
    }
}

