package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.model.SaleItem;
import com.abhedyam.service.interfaces.ISaleItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sale-items")
@RequiredArgsConstructor
public class SaleItemController {
    
    private final ISaleItemService saleItemService;
    
    @GetMapping("/{id}")
    public ApiResponse<SaleItem> getById(@PathVariable UUID id) {
        return ApiResponse.success(saleItemService.getById(id));
    }
    
    @GetMapping("/transaction/{transactionId}")
    public ApiResponse<List<SaleItem>> getByTransactionId(@PathVariable String transactionId) {
        return ApiResponse.success(saleItemService.getByTransactionId(transactionId));
    }
}

