package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.model.Product;
import com.abhedyam.service.interfaces.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final IProductService productService;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Product> create(@RequestBody Product product) {
        return ApiResponse.success(productService.create(product));
    }
    
    @GetMapping("/{id}")
    public ApiResponse<Product> getById(@PathVariable UUID id) {
        return ApiResponse.success(productService.getById(id));
    }
    
    @GetMapping
    public ApiResponse<List<Product>> getAll() {
        return ApiResponse.success(productService.getAll());
    }
    
    @GetMapping("/owner/{ownerId}")
    public ApiResponse<List<Product>> getByOwnerId(@PathVariable UUID ownerId) {
        return ApiResponse.success(productService.getByOwnerId(ownerId));
    }
    
    @PutMapping("/{id}")
    public ApiResponse<Product> update(@PathVariable UUID id, @RequestBody Product product) {
        return ApiResponse.success(productService.update(id, product));
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        productService.delete(id);
        return ApiResponse.success(null);
    }
}

