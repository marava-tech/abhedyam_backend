package com.abhedyam.service.interfaces;

import com.abhedyam.dto.StockAdjustmentRequest;
import com.abhedyam.dto.StockUpdateRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface IStockService {
    void recordPurchaseIn(UUID productId, BigDecimal quantity, String note);
    void recordSaleOut(UUID productId, BigDecimal quantity, UUID saleItemId, String note);
    void recordManualAdjustment(StockAdjustmentRequest request);
    void updateStock(StockUpdateRequest request);
    BigDecimal getCurrentStock(UUID productId);
    List<com.abhedyam.model.Product> getLowStockProducts(BigDecimal threshold);
}

