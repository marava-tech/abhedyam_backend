package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.model.Customer;
import com.abhedyam.service.interfaces.ICustomerService;
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
    public ApiResponse<Customer> create(@RequestBody Customer customer) {
        return ApiResponse.success(customerService.create(customer));
    }
    
    @GetMapping("/{id}")
    public ApiResponse<Customer> getById(@PathVariable UUID id) {
        return ApiResponse.success(customerService.getById(id));
    }
    
    @GetMapping
    public ApiResponse<List<Customer>> getAll() {
        return ApiResponse.success(customerService.getAll());
    }
    
    @GetMapping("/owner/{ownerId}")
    public ApiResponse<List<Customer>> getByOwnerId(@PathVariable UUID ownerId) {
        return ApiResponse.success(customerService.getByOwnerId(ownerId));
    }
    
    @PutMapping("/{id}")
    public ApiResponse<Customer> update(@PathVariable UUID id, @RequestBody Customer customer) {
        return ApiResponse.success(customerService.update(id, customer));
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        customerService.delete(id);
        return ApiResponse.success(null);
    }
}

