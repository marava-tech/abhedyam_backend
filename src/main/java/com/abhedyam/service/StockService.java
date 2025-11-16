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
        UUID ownerId = getCurrentOwnerId();
        validateProductAccess(productId);
        
        BigDecimal currentStock = getCurrentStock(productId);
        BigDecimal newStock = currentStock.add(quantity);
        
        InventoryLedger ledger = createLedgerEntry(
            ownerId, productId, quantity, newStock,
            InventoryLedgerSourceType.STOCK_ENTRY, null, note
        );
        
        updateStockCache(productId, newStock);
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        auditService.logStockChange(productId, ownerId, product.getName(), currentStock, newStock, "PURCHASE_IN", note);
        
        log.info("Purchase In recorded: Product {}, Quantity {}, New Stock {}", productId, quantity, newStock);
        return ledger;
    }
    
    @Override
    @Transactional
    public InventoryLedger recordSaleOut(UUID productId, BigDecimal quantity, UUID saleItemId, String note) {
        UUID ownerId = getCurrentOwnerId();
        validateProductAccess(productId);
        
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
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        auditService.logStockChange(productId, ownerId, product.getName(), currentStock, newStock, "SALE_OUT", note);
        
        log.info("Sale Out recorded: Product {}, Quantity {}, New Stock {}", productId, quantity, newStock);
        return ledger;
    }
    
    @Override
    @Transactional
    public InventoryLedger recordManualAdjustment(StockAdjustmentRequest request) {
        UUID ownerId = getCurrentOwnerId();
        validateProductAccess(request.getProductId());
        
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
        
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        auditService.logStockChange(request.getProductId(), ownerId, product.getName(), currentStock, newStock, 
            "MANUAL_ADJUSTMENT", request.getNote());
        
        log.info("Manual adjustment recorded: Product {}, Change {}, New Stock {}", 
            request.getProductId(), request.getChangeQty(), newStock);
        return ledger;
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getCurrentStock(UUID productId) {
        validateProductAccess(productId);
        
        BigDecimal inventoryStock = getStockFromInventory(productId);
        if (inventoryStock != null) {
            return inventoryStock;
        }
        
        return computeStockFromLedger(productId);
    }
    
    private BigDecimal getStockFromInventory(UUID productId) {
        UUID ownerId = getCurrentOwnerId();
        return inventoryRepository.findByOwnerIdAndProductId(ownerId, productId)
            .map(Inventory::getStock)
            .orElse(null);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal computeStockFromLedger(UUID productId) {
        UUID ownerId = getCurrentOwnerId();
        List<InventoryLedger> ledgers = inventoryLedgerRepository.findByOwnerIdAndProductId(ownerId, productId);
        
        return ledgers.stream()
            .map(InventoryLedger::getChangeQty)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .max(BigDecimal.ZERO);
    }
    
    @Override
    @Transactional
    public void syncStockFromLedger(UUID productId) {
        BigDecimal computedStock = computeStockFromLedger(productId);
        validateProductAccess(productId);
        
        BigDecimal oldStock = getStockFromInventory(productId);
        if (oldStock == null) {
            oldStock = BigDecimal.ZERO;
        }
        
        updateStockCache(productId, computedStock);
        
        if (!oldStock.equals(computedStock)) {
            UUID ownerId = getCurrentOwnerId();
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            auditService.logStockChange(productId, ownerId, product.getName(), oldStock, computedStock, 
                "SYNC_FROM_LEDGER", "Stock synced from ledger");
        }
        
        log.info("Stock synced from ledger: Product {}, Stock {}", productId, computedStock);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Product> getLowStockProducts(BigDecimal threshold) {
        UUID ownerId = getCurrentOwnerId();
        List<Product> products = productRepository.findByOwnerId(ownerId).stream()
            .filter(p -> p.getIsActive() != null && p.getIsActive())
            .toList();
        
        return products.stream()
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
        UUID ownerId = getCurrentOwnerId();
        Inventory inventory = inventoryRepository.findByOwnerIdAndProductId(ownerId, productId)
            .orElseGet(() -> {
                Inventory inv = new Inventory();
                inv.setProductId(productId);
                inv.setOwnerId(ownerId);
                return inv;
            });
        
        inventory.setStock(stock);
        inventoryRepository.save(inventory);
    }
    
    private Product validateProductAccess(UUID productId) {
        UUID ownerId = getCurrentOwnerId();
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        if (!product.getOwnerId().equals(ownerId)) {
            throw new BusinessException("UNAUTHORIZED", "You don't have access to this product");
        }
        
        return product;
    }
    
    private UUID getCurrentOwnerId() {
        return SecurityUtil.getCurrentUserId();
    }
}

