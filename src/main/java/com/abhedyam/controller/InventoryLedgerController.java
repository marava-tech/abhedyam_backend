package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.model.InventoryLedger;
import com.abhedyam.service.interfaces.IInventoryLedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory-ledgers")
@RequiredArgsConstructor
public class InventoryLedgerController {
    
    private final IInventoryLedgerService inventoryLedgerService;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<InventoryLedger> create(@RequestBody InventoryLedger inventoryLedger) {
        return ApiResponse.success(inventoryLedgerService.create(inventoryLedger));
    }
    
    @GetMapping("/{id}")
    public ApiResponse<InventoryLedger> getById(@PathVariable UUID id) {
        return ApiResponse.success(inventoryLedgerService.getById(id));
    }
    
    @GetMapping
    public ApiResponse<List<InventoryLedger>> getAll() {
        return ApiResponse.success(inventoryLedgerService.getAll());
    }
    
    @GetMapping("/owner/{ownerId}")
    public ApiResponse<List<InventoryLedger>> getByOwnerId(@PathVariable UUID ownerId) {
        return ApiResponse.success(inventoryLedgerService.getByOwnerId(ownerId));
    }
    
    @GetMapping("/product/{productId}")
    public ApiResponse<List<InventoryLedger>> getByProductId(@PathVariable UUID productId) {
        return ApiResponse.success(inventoryLedgerService.getByProductId(productId));
    }
    
    @PutMapping("/{id}")
    public ApiResponse<InventoryLedger> update(@PathVariable UUID id, @RequestBody InventoryLedger inventoryLedger) {
        return ApiResponse.success(inventoryLedgerService.update(id, inventoryLedger));
    }
}

