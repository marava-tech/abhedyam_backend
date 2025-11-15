package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.model.Inventory;
import com.abhedyam.service.interfaces.IInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventories")
@RequiredArgsConstructor
public class InventoryController {
    
    private final IInventoryService inventoryService;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Inventory> create(@RequestBody Inventory inventory) {
        return ApiResponse.success(inventoryService.create(inventory));
    }
    
    @GetMapping("/{id}")
    public ApiResponse<Inventory> getById(@PathVariable UUID id) {
        return ApiResponse.success(inventoryService.getById(id));
    }
    
    @GetMapping
    public ApiResponse<List<Inventory>> getAll() {
        return ApiResponse.success(inventoryService.getAll());
    }
    
    @GetMapping("/owner/{ownerId}")
    public ApiResponse<List<Inventory>> getByOwnerId(@PathVariable UUID ownerId) {
        return ApiResponse.success(inventoryService.getByOwnerId(ownerId));
    }
    
    @PutMapping("/{id}")
    public ApiResponse<Inventory> update(@PathVariable UUID id, @RequestBody Inventory inventory) {
        return ApiResponse.success(inventoryService.update(id, inventory));
    }
}

