package com.abhedyam.service;

import com.abhedyam.dto.StockAdjustmentRequest;
import com.abhedyam.dto.StockUpdateRequest;
import com.abhedyam.exception.BusinessException;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Inventory;
import com.abhedyam.model.Product;
import com.abhedyam.repository.InventoryRepository;
import com.abhedyam.repository.ProductRepository;
import com.abhedyam.service.interfaces.IStockService;
import com.abhedyam.util.SecurityUtil;
import com.abhedyam.constants.ErrorCodes;
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
    
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;
    private final com.abhedyam.service.interfaces.IAuditService auditService;
    
    @Override
    @Transactional
    public void recordPurchaseIn(UUID productId, BigDecimal quantity, String note) {
        validateProductAccess(productId);
        
        BigDecimal currentStock = getCurrentStock(productId);
        BigDecimal newStock = currentStock.add(quantity);
        
        updateStockCache(productId, newStock);
        
        log.info("Purchase In recorded: Product {}, Quantity {}, New Stock {}", productId, quantity, newStock);
    }
    
    @Override
    @Transactional
    public void recordSaleOut(UUID productId, BigDecimal quantity, UUID saleItemId, String note) {
        validateProductAccess(productId);
        
        BigDecimal currentStock = getCurrentStock(productId);
        
        if (currentStock.compareTo(quantity) < 0) {
            BigDecimal adjustmentNeeded = quantity.subtract(currentStock);
            log.warn("Insufficient stock detected for product {}. Current: {}, Required: {}. Auto-adjusting stock by {} to sync with physical inventory.", 
                productId, currentStock, quantity, adjustmentNeeded);
            
            BigDecimal adjustedStock = currentStock.add(adjustmentNeeded);
            updateStockCache(productId, adjustedStock);
            
            currentStock = adjustedStock;
            log.info("Stock auto-adjusted: Product {}, Adjustment {}, New Stock {}", productId, adjustmentNeeded, adjustedStock);
        }
        
        BigDecimal newStock = currentStock.subtract(quantity);
        
        updateStockCache(productId, newStock);
        
        log.info("Sale Out recorded: Product {}, Quantity {}, New Stock {}", productId, quantity, newStock);
    }
    
    @Override
    @Transactional
    public void recordManualAdjustment(StockAdjustmentRequest request) {
        validateProductAccess(request.getProductId());
        
        BigDecimal currentStock = getCurrentStock(request.getProductId());
        BigDecimal newStock = currentStock.add(request.getChangeQty());
        
        if (newStock.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ErrorCodes.NEGATIVE_STOCK, "Stock cannot be negative");
        }
        
        updateStockCache(request.getProductId(), newStock);
        
        log.info("Manual adjustment recorded: Product {}, Change {}, New Stock {}", 
            request.getProductId(), request.getChangeQty(), newStock);
    }
    
    @Override
    @Transactional
    public void updateStock(StockUpdateRequest request) {
        UUID ownerId = getCurrentOwnerId();
        validateProductAccess(request.getProductId());
        
        if (request.getStock().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ErrorCodes.NEGATIVE_STOCK, "Stock cannot be negative");
        }
        
        BigDecimal currentStock = getCurrentStock(request.getProductId());
        BigDecimal newStock = request.getStock();
        
        updateStockCache(request.getProductId(), newStock);
        
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product could not be found"));
        auditService.logStockChange(request.getProductId(), ownerId, product.getName(), currentStock, newStock, 
            "STOCK_UPDATE", request.getNote() != null ? request.getNote() : "Stock updated to " + newStock);
        
        log.info("Stock updated: Product {}, Old Stock {}, New Stock {}", 
            request.getProductId(), currentStock, newStock);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getCurrentStock(UUID productId) {
        validateProductAccess(productId);
        
        UUID ownerId = getCurrentOwnerId();
        return inventoryRepository.findByOwnerIdAndProductId(ownerId, productId)
            .map(Inventory::getStock)
            .orElse(BigDecimal.ZERO);
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
        
        inventoryService.invalidateOwnerCaches(ownerId);
    }
    
    private Product validateProductAccess(UUID productId) {
        UUID ownerId = getCurrentOwnerId();
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        if (!product.getOwnerId().equals(ownerId)) {
            throw new BusinessException(ErrorCodes.UNAUTHORIZED, "You don't have permission to access this product");
        }
        
        return product;
    }
    
    private UUID getCurrentOwnerId() {
        return SecurityUtil.getCurrentUserId();
    }
}

