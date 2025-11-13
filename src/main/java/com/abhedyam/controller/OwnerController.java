package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.model.Owner;
import com.abhedyam.service.interfaces.IOwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/owners")
@RequiredArgsConstructor
public class OwnerController {
    
    private final IOwnerService ownerService;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Owner> create(@RequestBody Owner owner) {
        return ApiResponse.success(ownerService.create(owner));
    }
    
    @GetMapping("/{id}")
    public ApiResponse<Owner> getById(@PathVariable UUID id) {
        return ApiResponse.success(ownerService.getById(id));
    }
    
    @GetMapping
    public ApiResponse<List<Owner>> getAll() {
        return ApiResponse.success(ownerService.getAll());
    }
    
    @PutMapping("/{id}")
    public ApiResponse<Owner> update(@PathVariable UUID id, @RequestBody Owner owner) {
        return ApiResponse.success(ownerService.update(id, owner));
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        ownerService.delete(id);
        return ApiResponse.success(null);
    }
}

