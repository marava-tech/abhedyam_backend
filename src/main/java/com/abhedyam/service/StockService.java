package com.abhedyam.service;

import com.abhedyam.dto.StockAdjustmentRequest;
import com.abhedyam.exception.BusinessException;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Inventory;
import com.abhedyam.model.InventoryLedger;
import com.abhedyam.model.Product;
import com.abhedyam.model.enums.InventoryLedgerSourceType;
import com.abhedyam.repository.InventoryLedgerRepository;
import com.abhedyam.repository.InventoryRepository;
import com.abhedyam.repository.ProductRepository;
import com.abhedyam.service.interfaces.IStockService;
import com.abhedyam.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockService implements IStockService {
    
    private final InventoryLedgerRepository inventoryLedgerRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final com.abhedyam.service.interfaces.IAuditService auditService;
    
    @Override
    @Transactional
    public InventoryLedger recordPurchaseIn(UUID productId, BigDecimal quantity, String note) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        Product product = validateProductAccess(productId);
        
        BigDecimal currentStock = getCurrentStock(productId);
        BigDecimal newStock = currentStock.add(quantity);
        
        InventoryLedger ledger = createLedgerEntry(
            ownerId, productId, quantity, newStock,
            InventoryLedgerSourceType.STOCK_ENTRY, null, note
        );
        
        updateStockCache(productId, newStock);
        product.setStock(newStock);
        productRepository.save(product);
        
        auditService.logStockChange(productId, ownerId, currentStock, newStock, "PURCHASE_IN", note);
        
        log.info("Purchase In recorded: Product {}, Quantity {}, New Stock {}", productId, quantity, newStock);
        return ledger;
    }
    
    @Override
    @Transactional
    public InventoryLedger recordSaleOut(UUID productId, BigDecimal quantity, UUID saleItemId, String note) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        Product product = validateProductAccess(productId);
        
        BigDecimal currentStock = getCurrentStock(productId);
        
        if (currentStock.compareTo(quantity) < 0) {
            throw new BusinessException("INSUFFICIENT_STOCK", 
                "Insufficient stock. Available: " + currentStock + ", Required: " + quantity);
        }
        
        BigDecimal newStock = currentStock.subtract(quantity);
        
        InventoryLedger ledger = createLedgerEntry(
            ownerId, productId, quantity.negate(), newStock,
            InventoryLedgerSourceType.SALE_ITEM, saleItemId, note
        );
        
        updateStockCache(productId, newStock);
        product.setStock(newStock);
        productRepository.save(product);
        
        auditService.logStockChange(productId, ownerId, currentStock, newStock, "SALE_OUT", note);
        
        log.info("Sale Out recorded: Product {}, Quantity {}, New Stock {}", productId, quantity, newStock);
        return ledger;
    }
    
    @Override
    @Transactional
    public InventoryLedger recordManualAdjustment(StockAdjustmentRequest request) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        Product product = validateProductAccess(request.getProductId());
        
        BigDecimal currentStock = getCurrentStock(request.getProductId());
        BigDecimal newStock = currentStock.add(request.getChangeQty());
        
        if (newStock.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("NEGATIVE_STOCK", "Stock cannot be negative");
        }
        
        InventoryLedger ledger = createLedgerEntry(
            ownerId, request.getProductId(), request.getChangeQty(), newStock,
            InventoryLedgerSourceType.ADJUSTMENT, null, request.getNote()
        );
        
        updateStockCache(request.getProductId(), newStock);
        product.setStock(newStock);
        productRepository.save(product);
        
        auditService.logStockChange(request.getProductId(), ownerId, currentStock, newStock, 
            "MANUAL_ADJUSTMENT", request.getNote());
        
        log.info("Manual adjustment recorded: Product {}, Change {}, New Stock {}", 
            request.getProductId(), request.getChangeQty(), newStock);
        return ledger;
    }
    
    @Override
    public BigDecimal getCurrentStock(UUID productId) {
        Product product = validateProductAccess(productId);
        
        if (product.getStock() != null) {
            return product.getStock();
        }
        
        return computeStockFromLedger(productId);
    }
    
    @Override
    public BigDecimal computeStockFromLedger(UUID productId) {
        List<InventoryLedger> ledgers = inventoryLedgerRepository.findByProductId(productId);
        
        BigDecimal total = BigDecimal.ZERO;
        for (InventoryLedger ledger : ledgers) {
            total = total.add(ledger.getChangeQty());
        }
        
        return total.max(BigDecimal.ZERO);
    }
    
    @Override
    @Transactional
    public void syncStockFromLedger(UUID productId) {
        BigDecimal computedStock = computeStockFromLedger(productId);
        Product product = validateProductAccess(productId);
        
        BigDecimal oldStock = product.getStock() != null ? product.getStock() : BigDecimal.ZERO;
        product.setStock(computedStock);
        productRepository.save(product);
        
        updateStockCache(productId, computedStock);
        
        if (!oldStock.equals(computedStock)) {
            UUID ownerId = SecurityUtil.getCurrentUserId();
            auditService.logStockChange(productId, ownerId, oldStock, computedStock, 
                "SYNC_FROM_LEDGER", "Stock synced from ledger");
        }
        
        log.info("Stock synced from ledger: Product {}, Stock {}", productId, computedStock);
    }
    
    @Override
    public List<Product> getLowStockProducts(BigDecimal threshold) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        List<Product> products = productRepository.findByOwnerId(ownerId);
        
        return products.stream()
            .filter(p -> p.getIsActive() != null && p.getIsActive())
            .filter(p -> {
                BigDecimal stock = getCurrentStock(p.getId());
                return stock.compareTo(threshold) <= 0;
            })
            .toList();
    }
    
    private InventoryLedger createLedgerEntry(UUID ownerId, UUID productId, BigDecimal changeQty,
                                             BigDecimal balanceAfter, InventoryLedgerSourceType sourceType,
                                             UUID sourceId, String note) {
        InventoryLedger ledger = new InventoryLedger();
        ledger.setOwnerId(ownerId);
        ledger.setProductId(productId);
        ledger.setChangeQty(changeQty);
        ledger.setBalanceAfter(balanceAfter);
        ledger.setSourceType(sourceType);
        ledger.setSourceId(sourceId);
        ledger.setNote(note);
        
        return inventoryLedgerRepository.save(ledger);
    }
    
    private void updateStockCache(UUID productId, BigDecimal stock) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        Inventory inventory = inventoryRepository.findByOwnerIdAndProductId(ownerId, productId)
            .orElse(new Inventory());
        
        inventory.setProductId(productId);
        inventory.setOwnerId(ownerId);
        inventory.setStock(stock);
        inventoryRepository.save(inventory);
    }
    
    private Product validateProductAccess(UUID productId) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        if (!product.getOwnerId().equals(ownerId)) {
            throw new BusinessException("UNAUTHORIZED", "You don't have access to this product");
        }
        
        return product;
    }
}

