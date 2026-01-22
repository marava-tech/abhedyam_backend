package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.dto.SaleItemResponse;
import com.abhedyam.model.SaleItem;
import com.abhedyam.service.interfaces.ISaleItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sale-items")
@RequiredArgsConstructor
@Tag(name = "Sale Items", description = "Sale items management APIs")
public class SaleItemController {
    
    private final ISaleItemService saleItemService;
    
    @GetMapping("/{id}")
    public ApiResponse<SaleItem> getById(@PathVariable UUID id) {
        return ApiResponse.success(saleItemService.getById(id));
    }
    
    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get sale items by customer ID", description = "Get all sale items for a customer. Accessible by the customer themselves or their owner. Returns sale items with product names.")
    public ApiResponse<List<SaleItemResponse>> getByCustomerId(@PathVariable UUID customerId) {
        return ApiResponse.success(saleItemService.getByCustomerId(customerId));
    }
    
    @GetMapping("/transaction/{transactionId}")
    public ApiResponse<List<SaleItem>> getByTransactionId(@PathVariable String transactionId) {
        return ApiResponse.success(saleItemService.getByTransactionId(transactionId));
    }
}

