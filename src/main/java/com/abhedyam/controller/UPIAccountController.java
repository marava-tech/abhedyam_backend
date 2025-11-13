package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.model.UPIAccount;
import com.abhedyam.service.interfaces.IUPIAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/upi-accounts")
@RequiredArgsConstructor
public class UPIAccountController {
    
    private final IUPIAccountService upiAccountService;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UPIAccount> create(@RequestBody UPIAccount upiAccount) {
        return ApiResponse.success(upiAccountService.create(upiAccount));
    }
    
    @GetMapping("/{id}")
    public ApiResponse<UPIAccount> getById(@PathVariable UUID id) {
        return ApiResponse.success(upiAccountService.getById(id));
    }
    
    @GetMapping
    public ApiResponse<List<UPIAccount>> getAll() {
        return ApiResponse.success(upiAccountService.getAll());
    }
    
    @PutMapping("/{id}")
    public ApiResponse<UPIAccount> update(@PathVariable UUID id, @RequestBody UPIAccount upiAccount) {
        return ApiResponse.success(upiAccountService.update(id, upiAccount));
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        upiAccountService.delete(id);
        return ApiResponse.success(null);
    }
}

