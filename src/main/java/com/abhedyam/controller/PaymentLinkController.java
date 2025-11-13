package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.dto.UpiPaymentLinkRequest;
import com.abhedyam.dto.UpiPaymentLinkResponse;
import com.abhedyam.service.interfaces.IPaymentLinkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments/upi-link")
@RequiredArgsConstructor
public class PaymentLinkController {
    
    private final IPaymentLinkService paymentLinkService;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UpiPaymentLinkResponse> generateUpiPaymentLink(@Valid @RequestBody UpiPaymentLinkRequest request) {
        return ApiResponse.success(paymentLinkService.generateUpiPaymentLink(request));
    }
}

