package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.model.Payment;
import com.abhedyam.service.interfaces.IPaymentService;
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
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Payment> create(@RequestBody Payment payment) {
        return ApiResponse.success(paymentService.create(payment));
    }
    
    @GetMapping("/{id}")
    public ApiResponse<Payment> getById(@PathVariable UUID id) {
        return ApiResponse.success(paymentService.getById(id));
    }
    
    @GetMapping
    public ApiResponse<List<Payment>> getAll() {
        return ApiResponse.success(paymentService.getAll());
    }
    
    @GetMapping("/owner/{ownerId}")
    public ApiResponse<List<Payment>> getByOwnerId(@PathVariable UUID ownerId) {
        return ApiResponse.success(paymentService.getByOwnerId(ownerId));
    }
    
    @GetMapping("/customer/{customerId}")
    public ApiResponse<List<Payment>> getByCustomerId(@PathVariable UUID customerId) {
        return ApiResponse.success(paymentService.getByCustomerId(customerId));
    }
    
    @PutMapping("/{id}")
    public ApiResponse<Payment> update(@PathVariable UUID id, @RequestBody Payment payment) {
        return ApiResponse.success(paymentService.update(id, payment));
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        paymentService.delete(id);
        return ApiResponse.success(null);
    }
}

