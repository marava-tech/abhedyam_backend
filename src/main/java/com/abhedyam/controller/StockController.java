package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.dto.StockAdjustmentRequest;
import com.abhedyam.model.InventoryLedger;
import com.abhedyam.model.Product;
import com.abhedyam.service.interfaces.IStockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stock")
@RequiredArgsConstructor
public class StockController {
    
    private final IStockService stockService;
    
    @PostMapping("/purchase-in")
    public ApiResponse<InventoryLedger> recordPurchaseIn(
            @RequestParam UUID productId,
            @RequestParam BigDecimal quantity,
            @RequestParam(required = false) String note) {
        return ApiResponse.success(stockService.recordPurchaseIn(productId, quantity, note));
    }
    
    @PostMapping("/sale-out")
    public ApiResponse<InventoryLedger> recordSaleOut(
            @RequestParam UUID productId,
            @RequestParam BigDecimal quantity,
            @RequestParam(required = false) UUID saleItemId,
            @RequestParam(required = false) String note) {
        return ApiResponse.success(stockService.recordSaleOut(productId, quantity, saleItemId, note));
    }
    
    @PostMapping("/adjust")
    public ApiResponse<InventoryLedger> adjustStock(@Valid @RequestBody StockAdjustmentRequest request) {
        return ApiResponse.success(stockService.recordManualAdjustment(request));
    }
    
    @GetMapping("/{productId}/current")
    public ApiResponse<BigDecimal> getCurrentStock(@PathVariable UUID productId) {
        return ApiResponse.success(stockService.getCurrentStock(productId));
    }
    
    @PostMapping("/{productId}/sync")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> syncStockFromLedger(@PathVariable UUID productId) {
        stockService.syncStockFromLedger(productId);
        return ApiResponse.success(null);
    }
    
    @GetMapping("/low-stock")
    public ApiResponse<List<Product>> getLowStockProducts(@RequestParam(defaultValue = "0") BigDecimal threshold) {
        return ApiResponse.success(stockService.getLowStockProducts(threshold));
    }
}

