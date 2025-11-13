package com.abhedyam.service.interfaces;

import com.abhedyam.dto.StockAdjustmentRequest;
import com.abhedyam.model.InventoryLedger;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface IStockService {
    InventoryLedger recordPurchaseIn(UUID productId, BigDecimal quantity, String note);
    InventoryLedger recordSaleOut(UUID productId, BigDecimal quantity, UUID saleItemId, String note);
    InventoryLedger recordManualAdjustment(StockAdjustmentRequest request);
    BigDecimal getCurrentStock(UUID productId);
    BigDecimal computeStockFromLedger(UUID productId);
    void syncStockFromLedger(UUID productId);
    List<com.abhedyam.model.Product> getLowStockProducts(BigDecimal threshold);
}

