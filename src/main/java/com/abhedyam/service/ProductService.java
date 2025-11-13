package com.abhedyam.service;

import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Product;
import com.abhedyam.repository.ProductRepository;
import com.abhedyam.service.interfaces.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    
    private final ProductRepository productRepository;
    
    public Product create(Product product) {
        return productRepository.save(product);
    }
    
    public Product getById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }
    
    public List<Product> getAll() {
        return productRepository.findAll();
    }
    
    public List<Product> getByOwnerId(UUID ownerId) {
        return productRepository.findByOwnerId(ownerId);
    }
    
    @Transactional
    public Product update(UUID id, Product productDetails) {
        Product product = getById(id);
        if (productDetails.getCode() != null) product.setCode(productDetails.getCode());
        if (productDetails.getName() != null) product.setName(productDetails.getName());
        if (productDetails.getImageUrl() != null) product.setImageUrl(productDetails.getImageUrl());
        if (productDetails.getPrice() != null) product.setPrice(productDetails.getPrice());
        return productRepository.save(product);
    }
    
    @Transactional
    public void delete(UUID id) {
        Product product = getById(id);
        product.setDeletedAt(Instant.now());
        product.setIsActive(false);
        productRepository.save(product);
    }
}

