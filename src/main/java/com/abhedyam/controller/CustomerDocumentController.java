package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.model.Document;
import com.abhedyam.service.interfaces.ICustomerDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers/{customerId}/documents")
@RequiredArgsConstructor
public class CustomerDocumentController {
    
    private final ICustomerDocumentService customerDocumentService;
    
    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Document> uploadDocument(
            @PathVariable UUID customerId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "orderIndex", required = false) Integer orderIndex) {
        return ApiResponse.success(customerDocumentService.uploadCustomerDocument(customerId, file, name, orderIndex));
    }
    
    @GetMapping
    public ApiResponse<List<Document>> getCustomerDocuments(@PathVariable UUID customerId) {
        return ApiResponse.success(customerDocumentService.getCustomerDocuments(customerId));
    }
    
    @GetMapping("/{id}")
    public ApiResponse<Document> getDocumentById(@PathVariable UUID id) {
        return ApiResponse.success(customerDocumentService.getDocumentById(id));
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deleteDocument(@PathVariable UUID id) {
        customerDocumentService.deleteDocument(id);
        return ApiResponse.success(null);
    }
}

