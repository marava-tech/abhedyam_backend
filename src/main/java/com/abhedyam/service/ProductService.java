package com.abhedyam.service;

import com.abhedyam.dto.PageResponse;
import com.abhedyam.dto.ProductCreateRequest;
import com.abhedyam.dto.ProductSearchRequest;
import com.abhedyam.dto.ProductUpdateRequest;
import com.abhedyam.exception.BusinessException;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Product;
import com.abhedyam.repository.ProductRepository;
import com.abhedyam.service.interfaces.IAuditService;
import com.abhedyam.service.interfaces.IProductService;
import com.abhedyam.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    
    private final ProductRepository productRepository;
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
            product.setImageUrl(request.getImageUrl());
        }
        if (request.getImages() != null) {
            product.setImages(request.getImages());
        }
        if (request.getStock() != null) {
            product.setStock(request.getStock());
        }
        
        Product savedProduct = productRepository.save(product);
        
        auditService.logProductCreation(savedProduct.getId(), ownerId, savedProduct.getName(), savedProduct.getCode());
        
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
        if (request.getImages() != null) {
            product.setImages(request.getImages());
        }
        if (request.getStock() != null) {
            product.setStock(request.getStock());
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
}

