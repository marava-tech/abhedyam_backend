package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.dto.DocumentCreateRequest;
import com.abhedyam.dto.DocumentOrderUpdateRequest;
import com.abhedyam.dto.DocumentResponse;
import com.abhedyam.service.interfaces.IDocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Tag(name = "Documents", description = "API for managing documents")
public class DocumentController {
    
    private final IDocumentService documentService;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a document", description = "Upload a new document. Only owners can create documents.")
    public ApiResponse<DocumentResponse> create(@Valid @RequestBody DocumentCreateRequest request) {
        return ApiResponse.success(documentService.create(request));
    }
    
    @GetMapping
    @Operation(summary = "Get documents by owner ID", description = "Get all active documents for an owner. If ownerId is not provided, returns documents for the current user. Customers can view owner documents.")
    public ApiResponse<List<DocumentResponse>> getByOwnerId(@RequestParam(value = "ownerId", required = false) UUID ownerId) {
        return ApiResponse.success(documentService.getByOwnerId(ownerId));
    }
    
    @PutMapping("/order")
    @Operation(summary = "Update document order indexes", description = "Bulk update order indexes of multiple documents for reordering. Only the document owner can update.")
    public ApiResponse<List<DocumentResponse>> updateOrderIndexes(
            @Valid @RequestBody DocumentOrderUpdateRequest request) {
        return ApiResponse.success(documentService.updateOrderIndexes(request));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a document", description = "Soft delete a document by setting isActive to false. Only the document owner can delete.")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        documentService.delete(id);
        return ApiResponse.success(null);
    }
}

