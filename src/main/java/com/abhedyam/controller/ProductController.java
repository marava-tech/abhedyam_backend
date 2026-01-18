package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.dto.ProductCreateRequest;
import com.abhedyam.model.Product;
import com.abhedyam.service.interfaces.IProductService;
import jakarta.validation.Valid;
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
    public ApiResponse<Product> create(@Valid @RequestBody ProductCreateRequest request) {
        return ApiResponse.success(productService.create(request));
    }
    
    @GetMapping("/{id}")
    public ApiResponse<Product> getById(@PathVariable UUID id) {
        return ApiResponse.success(productService.getById(id));
    }
    
    @GetMapping("/owner/{ownerId}/with-stock")
    public ApiResponse<List<com.abhedyam.dto.ProductWithStockResponse>> getProductsWithStockByOwnerId(@PathVariable UUID ownerId) {
        return ApiResponse.success(productService.getProductsWithStockByOwnerId(ownerId));
    }
}

