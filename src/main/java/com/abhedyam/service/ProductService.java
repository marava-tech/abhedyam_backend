package com.abhedyam.service;

import com.abhedyam.dto.PageResponse;
import com.abhedyam.dto.ProductCreateRequest;
import com.abhedyam.dto.ProductSearchRequest;
import com.abhedyam.dto.ProductSearchResult;
import com.abhedyam.dto.ProductUpdateRequest;
import com.abhedyam.dto.ProductWithStockResponse;
import com.abhedyam.exception.BusinessException;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Inventory;
import com.abhedyam.model.Product;
import com.abhedyam.repository.InventoryRepository;
import com.abhedyam.repository.ProductRepository;
import com.abhedyam.service.interfaces.IAuditService;
import com.abhedyam.service.interfaces.IProductService;
import com.abhedyam.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService implements IProductService {
    
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final IAuditService auditService;
    
    @Override
    @Transactional
    public Product create(ProductCreateRequest request) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        
        if (productRepository.findByOwnerIdAndCode(ownerId, request.getCode()).isPresent()) {
            throw new BusinessException("DUPLICATE_CODE", "Product code already exists");
        }
        
        Product product = new Product();
        product.setCode(request.getCode());
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setOwnerId(ownerId);
        product.setIsActive(true);
        if (request.getImageUrl() != null) {
            product.setImageUrl(request.getImageUrl().trim().isEmpty() ? null : request.getImageUrl());
        }
        
        Product savedProduct = productRepository.save(product);
        
        Inventory inventory = new Inventory();
        inventory.setProductId(savedProduct.getId());
        inventory.setOwnerId(ownerId);
        inventory.setStock(java.math.BigDecimal.ZERO);
        inventoryRepository.save(inventory);
        
        auditService.logProductCreation(savedProduct.getId(), ownerId, savedProduct.getName(), savedProduct.getCode());
        
        decreaseStockOnProductCreationAsync(savedProduct.getId(), ownerId);
        
        return savedProduct;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Product getById(UUID id) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        if (!product.getOwnerId().equals(ownerId)) {
            throw new BusinessException("UNAUTHORIZED", "You don't have access to this product");
        }
        
        return product;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Product> getByOwnerId(UUID ownerId) {
        UUID currentOwnerId = SecurityUtil.getCurrentUserId();
        if (ownerId != null && !currentOwnerId.equals(ownerId)) {
            throw new BusinessException("UNAUTHORIZED", "You can only view your own products");
        }
        UUID targetOwnerId = ownerId != null ? ownerId : currentOwnerId;
        return productRepository.findByOwnerId(targetOwnerId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductWithStockResponse> getProductsWithStockByOwnerId(UUID ownerId) {
        if (ownerId == null) {
            throw new BusinessException("INVALID_REQUEST", "Owner ID is required");
        }
        
        List<Product> products = productRepository.findByOwnerId(ownerId).stream()
                .filter(p -> p.getIsActive() != null && p.getIsActive())
                .toList();
        
        return products.stream()
                .map(product -> {
                    BigDecimal stock = inventoryRepository.findByOwnerIdAndProductId(ownerId, product.getId())
                            .map(Inventory::getStock)
                            .orElse(BigDecimal.ZERO);
                    
                    ProductWithStockResponse response = new ProductWithStockResponse();
                    response.setId(product.getId());
                    response.setCode(product.getCode());
                    response.setName(product.getName());
                    response.setPrice(product.getPrice());
                    response.setOwnerId(product.getOwnerId());
                    response.setIsActive(product.getIsActive());
                    response.setStock(formatStock(stock));
                    response.setImageUrl(product.getImageUrl());
                    response.setCreatedAt(product.getCreatedAt());
                    response.setUpdatedAt(product.getUpdatedAt());
                    
                    return response;
                })
                .sorted((p1, p2) -> {
                    int stockCompare = p2.getStock().compareTo(p1.getStock());
                    if (stockCompare != 0) {
                        return stockCompare;
                    }
                    return p1.getPrice().compareTo(p2.getPrice());
                })
                .toList();
    }
    
    private BigDecimal formatStock(BigDecimal stock) {
        if (stock == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal stripped = stock.stripTrailingZeros();
        if (stripped.scale() <= 0) {
            return stripped.setScale(0);
        }
        return stripped;
    }
    
    @Override
    @Transactional(readOnly = true)
    public PageResponse<Product> searchProducts(ProductSearchRequest request) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        
        Sort sort = Sort.by(
            "DESC".equalsIgnoreCase(request.getSortDirection()) 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC,
            request.getSortBy()
        );
        
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
        
        Page<Product> page = productRepository.searchProducts(
            ownerId,
            request.getSearchTerm(),
            request.getIsActive(),
            pageable
        );
        
        return new PageResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.hasNext(),
            page.hasPrevious()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductSearchResult> searchByName(String name) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        List<Product> products = productRepository.findByNameContainingIgnoreCaseAndOwnerId(name, ownerId);
        return products.stream()
            .map(product -> new ProductSearchResult(product.getId(), product.getName(), product.getPrice()))
            .toList();
    }
    
    @Override
    @Transactional
    public Product updateProduct(ProductUpdateRequest request) {
        Product product = getById(request.getId());
        
        if (request.getCode() != null && !request.getCode().equals(product.getCode())) {
            UUID ownerId = SecurityUtil.getCurrentUserId();
            if (productRepository.findByOwnerIdAndCode(ownerId, request.getCode()).isPresent()) {
                throw new BusinessException("DUPLICATE_CODE", "Product code already exists");
            }
            product.setCode(request.getCode());
        }
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            product.setName(request.getName());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getImageUrl() != null) {
            if (request.getImageUrl().trim().isEmpty()) {
                product.setImageUrl(null);
            } else {
                product.setImageUrl(request.getImageUrl());
            }
        }
        
        return productRepository.save(product);
    }
    
    @Override
    @Transactional
    public Product toggleActive(UUID id) {
        Product product = getById(id);
        product.setIsActive(!product.getIsActive());
        return productRepository.save(product);
    }
    
    @Async("virtualThreadExecutor")
    @Transactional
    public void decreaseStockOnProductCreationAsync(UUID productId, UUID ownerId) {
        try {
            Inventory inventory = inventoryRepository.findByOwnerIdAndProductId(ownerId, productId)
                .orElse(null);
            
            if (inventory == null) {
                return;
            }
            
            BigDecimal currentStock = inventory.getStock();
            if (currentStock == null || currentStock.compareTo(BigDecimal.ZERO) <= 0) {
                return;
            }
            
            BigDecimal newStock = currentStock.subtract(BigDecimal.ONE);
            if (newStock.compareTo(BigDecimal.ZERO) < 0) {
                newStock = BigDecimal.ZERO;
            }
            
            inventory.setStock(newStock);
            inventoryRepository.save(inventory);
            
            Product product = productRepository.findById(productId).orElse(null);
            if (product != null) {
                auditService.logStockChange(productId, ownerId, product.getName(), currentStock, newStock, 
                    "PRODUCT_CREATION", "Stock decreased on product creation");
            }
            
            log.info("Stock decreased on product creation: Product {}, Old Stock {}, New Stock {}", 
                productId, currentStock, newStock);
        } catch (Exception e) {
            log.warn("Failed to decrease stock on product creation for product {}: {}", productId, e.getMessage());
        }
    }
}

