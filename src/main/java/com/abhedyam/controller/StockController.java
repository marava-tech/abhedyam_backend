package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.dto.StockAdjustmentRequest;
import com.abhedyam.dto.StockUpdateRequest;
import com.abhedyam.model.Product;
import com.abhedyam.service.interfaces.IStockService;
import com.abhedyam.util.StockFormatUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ApiResponse<Void> recordPurchaseIn(
            @RequestParam UUID productId,
            @RequestParam BigDecimal quantity,
            @RequestParam(required = false) String note) {
        stockService.recordPurchaseIn(productId, quantity, note);
        return ApiResponse.success(null);
    }
    
    @PostMapping("/sale-out")
    public ApiResponse<Void> recordSaleOut(
            @RequestParam UUID productId,
            @RequestParam BigDecimal quantity,
            @RequestParam(required = false) UUID saleItemId,
            @RequestParam(required = false) String note) {
        stockService.recordSaleOut(productId, quantity, saleItemId, note);
        return ApiResponse.success(null);
    }
    
    @PostMapping("/adjust")
    public ApiResponse<Void> adjustStock(@Valid @RequestBody StockAdjustmentRequest request) {
        stockService.recordManualAdjustment(request);
        return ApiResponse.success(null);
    }
    
    @PutMapping("/update")
    public ApiResponse<Void> updateStock(@Valid @RequestBody StockUpdateRequest request) {
        stockService.updateStock(request);
        return ApiResponse.success(null);
    }
    
    @GetMapping("/{productId}/current")
    public ApiResponse<Number> getCurrentStock(@PathVariable UUID productId) {
        BigDecimal stock = stockService.getCurrentStock(productId);
        return ApiResponse.success(StockFormatUtil.formatStock(stock));
    }
    
    @GetMapping("/low-stock")
    public ApiResponse<List<Product>> getLowStockProducts(@RequestParam(defaultValue = "0") BigDecimal threshold) {
        return ApiResponse.success(stockService.getLowStockProducts(threshold));
    }
}

