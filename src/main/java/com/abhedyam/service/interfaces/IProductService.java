package com.abhedyam.service.interfaces;

import com.abhedyam.dto.PageResponse;
import com.abhedyam.dto.ProductCreateRequest;
import com.abhedyam.dto.ProductSearchRequest;
import com.abhedyam.dto.ProductUpdateRequest;
import com.abhedyam.model.Product;

import java.util.List;
import java.util.UUID;

public interface IProductService {
    Product create(ProductCreateRequest request);
    Product getById(UUID id);
    List<Product> getByOwnerId(UUID ownerId);
    PageResponse<Product> searchProducts(ProductSearchRequest request);
    Product updateProduct(ProductUpdateRequest request);
    Product toggleActive(UUID id);
}

