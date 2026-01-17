package com.abhedyam.controller;

import com.abhedyam.dto.AdminSubscriptionUpdateRequest;
import com.abhedyam.dto.ApiResponse;
import com.abhedyam.dto.PaymentVerifyRequest;
import com.abhedyam.dto.SubscriptionCreateRequest;
import com.abhedyam.dto.SubscriptionCreateResponse;
import com.abhedyam.dto.SubscriptionDetailsResponse;
import com.abhedyam.dto.SubscriptionStatusResponse;
import com.abhedyam.exception.BusinessException;
import com.abhedyam.service.interfaces.ISubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Subscriptions", description = "API for managing subscription plans")
public class SubscriptionController {
    
    private final ISubscriptionService subscriptionService;
    
    @PostMapping("/subscription/create")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create payment order", description = "Creates a Razorpay order for one-time payment and returns order ID for checkout")
    public ApiResponse<SubscriptionCreateResponse> createSubscription(@Valid @RequestBody SubscriptionCreateRequest request) {
        return ApiResponse.success(subscriptionService.createSubscription(request));
    }
    
    @PostMapping("/payment/verify")
    @Operation(summary = "Verify payment", description = "Verifies Razorpay payment signature and activates PRO plan if valid")
    public ApiResponse<Void> verifyPayment(@Valid @RequestBody PaymentVerifyRequest request) {
        subscriptionService.verifyPayment(request);
        return ApiResponse.success(null);
    }
    
    @PostMapping(value = "/webhook/razorpay", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Razorpay webhook", description = "Handles Razorpay webhook events for payment lifecycle")
    public ApiResponse<Void> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature) {
        subscriptionService.handleWebhook(payload, signature);
        return ApiResponse.success(null);
    }
    
    @GetMapping("/user/subscription")
    @Operation(summary = "Get subscription status", description = "Returns current subscription plan, status, and validity")
    public ApiResponse<SubscriptionStatusResponse> getSubscriptionStatus() {
        return ApiResponse.success(subscriptionService.getSubscriptionStatus());
    }
    
    @GetMapping("/subscription/{ownerId}")
    @Operation(summary = "Get subscription details by owner ID", description = "Returns detailed subscription information for a specific owner")
    public ApiResponse<SubscriptionDetailsResponse> getSubscriptionDetailsByOwnerId(@PathVariable UUID ownerId) {
        return ApiResponse.success(subscriptionService.getSubscriptionDetailsByOwnerId(ownerId));
    }
    
    @PostMapping("/subscription/test/activate-pro")
    @Operation(summary = "Activate PRO plan for testing (Admin)", description = "Manually activates PRO subscription for a specific owner. Requires admin key.")
    public ApiResponse<Void> activateProPlanForTesting(@Valid @RequestBody AdminSubscriptionUpdateRequest request) {
        if (!"REDACTED_ADMIN_KEY".equals(request.getAdminKey())) {
            throw new BusinessException("UNAUTHORIZED", "Invalid admin key");
        }
        subscriptionService.activateProPlanForTesting(request.getOwnerId());
        return ApiResponse.success(null);
    }
    
    @PostMapping("/subscription/test/downgrade-go")
    @Operation(summary = "Downgrade to GO plan for testing (Admin)", description = "Manually downgrades subscription from PRO to GO for a specific owner. Requires admin key.")
    public ApiResponse<Void> downgradeToGoForTesting(@Valid @RequestBody AdminSubscriptionUpdateRequest request) {
        if (!"REDACTED_ADMIN_KEY".equals(request.getAdminKey())) {
            throw new BusinessException("UNAUTHORIZED", "Invalid admin key");
        }
        subscriptionService.downgradeToGoForTesting(request.getOwnerId());
        return ApiResponse.success(null);
    }
}

