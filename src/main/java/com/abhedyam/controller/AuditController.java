package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.model.Audit;
import com.abhedyam.service.interfaces.IAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audits")
@RequiredArgsConstructor
public class AuditController {
    
    private final IAuditService auditService;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Audit> create(@RequestBody Audit audit) {
        return ApiResponse.success(auditService.create(audit));
    }
    
    @GetMapping("/{id}")
    public ApiResponse<Audit> getById(@PathVariable UUID id) {
        return ApiResponse.success(auditService.getById(id));
    }
    
    @GetMapping
    public ApiResponse<List<Audit>> getAll() {
        return ApiResponse.success(auditService.getAll());
    }
    
    @GetMapping("/owner/{ownerId}")
    public ApiResponse<List<Audit>> getByOwnerId(@PathVariable UUID ownerId) {
        return ApiResponse.success(auditService.getByOwnerId(ownerId));
    }
    
    @PutMapping("/{id}")
    public ApiResponse<Audit> update(@PathVariable UUID id, @RequestBody Audit audit) {
        return ApiResponse.success(auditService.update(id, audit));
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        auditService.delete(id);
        return ApiResponse.success(null);
    }
}

