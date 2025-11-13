package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.dto.UpiAccountCreateRequest;
import com.abhedyam.model.UPIAccount;
import com.abhedyam.service.interfaces.IUpiAccountManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/upi-accounts")
@RequiredArgsConstructor
public class UpiAccountController {
    
    private final IUpiAccountManagementService upiAccountManagementService;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UPIAccount> createUpiAccount(@Valid @RequestBody UpiAccountCreateRequest request) {
        return ApiResponse.success(upiAccountManagementService.createUpiAccount(request));
    }
    
    @GetMapping
    public ApiResponse<List<UPIAccount>> getOwnerUpiAccounts() {
        return ApiResponse.success(upiAccountManagementService.getOwnerUpiAccounts());
    }
    
    @GetMapping("/{id}")
    public ApiResponse<UPIAccount> getUpiAccountById(@PathVariable UUID id) {
        return ApiResponse.success(upiAccountManagementService.getUpiAccountById(id));
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deleteUpiAccount(@PathVariable UUID id) {
        upiAccountManagementService.deleteUpiAccount(id);
        return ApiResponse.success(null);
    }
    
    @PutMapping("/{id}/primary")
    public ApiResponse<UPIAccount> setPrimaryUpiAccount(@PathVariable UUID id) {
        return ApiResponse.success(upiAccountManagementService.setPrimaryUpiAccount(id));
    }
}

