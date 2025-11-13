package com.abhedyam.service.interfaces;

import com.abhedyam.model.Product;

import java.util.List;
import java.util.UUID;

public interface IProductService {
    Product create(Product product);
    Product getById(UUID id);
    List<Product> getAll();
    List<Product> getByOwnerId(UUID ownerId);
    Product update(UUID id, Product productDetails);
    void delete(UUID id);
}

