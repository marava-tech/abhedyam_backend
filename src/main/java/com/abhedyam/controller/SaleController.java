package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.dto.SaleCreateRequest;
import com.abhedyam.dto.SaleDetailResponse;
import com.abhedyam.service.interfaces.ISaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sales")
@RequiredArgsConstructor
public class SaleController {
    
    private final ISaleService saleService;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SaleDetailResponse> createSale(@Valid @RequestBody SaleCreateRequest request) {
        return ApiResponse.success(saleService.createSale(request));
    }
    
    @GetMapping("/transaction/{transactionId}")
    public ApiResponse<SaleDetailResponse> getSaleByTransactionId(@PathVariable String transactionId) {
        return ApiResponse.success(saleService.getSaleByTransactionId(transactionId));
    }
    
    @PostMapping("/transaction/{transactionId}/cancel")
    public ApiResponse<SaleDetailResponse> cancelSale(@PathVariable String transactionId) {
        return ApiResponse.success(saleService.cancelSale(transactionId));
    }
}

