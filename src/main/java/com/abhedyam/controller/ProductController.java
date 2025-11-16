package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.dto.PageResponse;
import com.abhedyam.dto.ProductCreateRequest;
import com.abhedyam.dto.ProductSearchRequest;
import com.abhedyam.dto.ProductSearchResult;
import com.abhedyam.dto.ProductUpdateRequest;
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
    
    @GetMapping("/search")
    public ApiResponse<PageResponse<Product>> searchProducts(@ModelAttribute ProductSearchRequest request) {
        return ApiResponse.success(productService.searchProducts(request));
    }
    
    @GetMapping("/search-by-name")
    public ApiResponse<List<ProductSearchResult>> searchByName(@RequestParam("name") String name) {
        return ApiResponse.success(productService.searchByName(name));
    }
    
    @GetMapping("/my-products")
    public ApiResponse<List<Product>> getMyProducts() {
        return ApiResponse.success(productService.getByOwnerId(null));
    }
    
    @PatchMapping("/me")
    public ApiResponse<Product> updateProduct(@Valid @RequestBody ProductUpdateRequest request) {
        return ApiResponse.success(productService.updateProduct(request));
    }
    
    @PatchMapping("/me/toggle-active")
    public ApiResponse<Product> toggleActive(@RequestParam("id") UUID id) {
        return ApiResponse.success(productService.toggleActive(id));
    }
}

