package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.model.SaleItem;
import com.abhedyam.service.interfaces.ISaleItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sale-items")
@RequiredArgsConstructor
public class SaleItemController {
    
    private final ISaleItemService saleItemService;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SaleItem> create(@RequestBody SaleItem saleItem) {
        return ApiResponse.success(saleItemService.create(saleItem));
    }
    
    @GetMapping("/{id}")
    public ApiResponse<SaleItem> getById(@PathVariable UUID id) {
        return ApiResponse.success(saleItemService.getById(id));
    }
    
    @GetMapping
    public ApiResponse<List<SaleItem>> getAll() {
        return ApiResponse.success(saleItemService.getAll());
    }
    
    @GetMapping("/owner/{ownerId}")
    public ApiResponse<List<SaleItem>> getByOwnerId(@PathVariable UUID ownerId) {
        return ApiResponse.success(saleItemService.getByOwnerId(ownerId));
    }
    
    @GetMapping("/customer/{customerId}")
    public ApiResponse<List<SaleItem>> getByCustomerId(@PathVariable UUID customerId) {
        return ApiResponse.success(saleItemService.getByCustomerId(customerId));
    }
    
    @PutMapping("/{id}")
    public ApiResponse<SaleItem> update(@PathVariable UUID id, @RequestBody SaleItem saleItem) {
        return ApiResponse.success(saleItemService.update(id, saleItem));
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        saleItemService.delete(id);
        return ApiResponse.success(null);
    }
}

