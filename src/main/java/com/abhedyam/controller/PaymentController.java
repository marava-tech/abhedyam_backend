package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.dto.PaymentCreateRequest;
import com.abhedyam.dto.PaymentResponse;
import com.abhedyam.dto.PaymentStatusUpdateRequest;
import com.abhedyam.model.Payment;
import com.abhedyam.service.interfaces.IPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    
    private final IPaymentService paymentService;
    
    @GetMapping("/{id}")
    public ApiResponse<Payment> getById(@PathVariable UUID id) {
        return ApiResponse.success(paymentService.getById(id));
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PaymentResponse> createManualPayment(@Valid @RequestBody PaymentCreateRequest request) {
        return ApiResponse.success(paymentService.createManualPayment(request));
    }
    
    @GetMapping("/customer/{customerId}")
    public ApiResponse<List<Payment>> getByCustomerId(@PathVariable UUID customerId) {
        return ApiResponse.success(paymentService.getByCustomerId(customerId));
    }
    
    @PatchMapping("/{id}/status")
    public ApiResponse<Payment> updateStatus(@PathVariable UUID id, @Valid @RequestBody PaymentStatusUpdateRequest request) {
        return ApiResponse.success(paymentService.updateStatus(id, request));
    }
    
    @PutMapping("/{id}")
    public ApiResponse<Payment> update(@PathVariable UUID id, @RequestBody Payment payment) {
        return ApiResponse.success(paymentService.update(id, payment));
    }
}


