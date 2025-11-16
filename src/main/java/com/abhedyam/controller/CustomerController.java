package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.dto.CustomerCreateRequest;
import com.abhedyam.dto.CustomerProfileSummary;
import com.abhedyam.dto.CustomerResponse;
import com.abhedyam.dto.CustomerSearchRequest;
import com.abhedyam.dto.CustomerSearchResult;
import com.abhedyam.dto.CustomerUpdateRequest;
import com.abhedyam.dto.PageResponse;
import com.abhedyam.model.Customer;
import com.abhedyam.service.interfaces.ICustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {
    
    private final ICustomerService customerService;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Customer> create(@Valid @RequestBody CustomerCreateRequest request) {
        return ApiResponse.success(customerService.create(request));
    }
    
    @GetMapping("/{id}")
    public ApiResponse<Customer> getById(@PathVariable UUID id) {
        return ApiResponse.success(customerService.getById(id));
    }
    
    @GetMapping("/{id}/summary")
    public ApiResponse<CustomerProfileSummary> getProfileSummary(@PathVariable UUID id) {
        return ApiResponse.success(customerService.getCustomerProfileSummary(id));
    }
    
    @GetMapping("/search")
    public ApiResponse<PageResponse<Customer>> searchCustomers(@ModelAttribute CustomerSearchRequest request) {
        return ApiResponse.success(customerService.searchCustomers(request));
    }
    
    @GetMapping("/search-by-name")
    public ApiResponse<List<CustomerSearchResult>> searchByName(@RequestParam("name") String name) {
        return ApiResponse.success(customerService.searchByName(name));
    }
    
    @GetMapping("/my-customers")
    public ApiResponse<List<CustomerResponse>> getMyCustomers() {
        return ApiResponse.success(customerService.getMyCustomersWithVillage());
    }
    
    @PatchMapping("/me")
    public ApiResponse<Customer> updateCustomer(@Valid @RequestBody CustomerUpdateRequest request) {
        return ApiResponse.success(customerService.updateCustomer(request));
    }
}

