package com.abhedyam.service.interfaces;

import com.abhedyam.dto.PageResponse;
import com.abhedyam.dto.ProductCreateRequest;
import com.abhedyam.dto.ProductSearchRequest;
import com.abhedyam.model.Product;

import java.util.List;
import java.util.UUID;

public interface IProductService {
    Product create(ProductCreateRequest request);
    Product getById(UUID id);
    List<Product> getAll();
    List<Product> getByOwnerId(UUID ownerId);
    PageResponse<Product> searchProducts(ProductSearchRequest request);
    Product update(UUID id, ProductCreateRequest request);
    Product toggleActive(UUID id);
    void delete(UUID id);
}

