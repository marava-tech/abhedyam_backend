package com.abhedyam.service.interfaces;

import com.abhedyam.dto.PaymentVerifyRequest;
import com.abhedyam.dto.SubscriptionCreateRequest;
import com.abhedyam.dto.SubscriptionCreateResponse;
import com.abhedyam.dto.SubscriptionDetailsResponse;
import com.abhedyam.dto.SubscriptionStatusResponse;

import java.util.UUID;

public interface ISubscriptionService {
    SubscriptionCreateResponse createSubscription(SubscriptionCreateRequest request);
    void verifyPayment(PaymentVerifyRequest request);
    void handleWebhook(String payload, String signature);
    SubscriptionStatusResponse getSubscriptionStatus();
    SubscriptionDetailsResponse getSubscriptionDetailsByOwnerId(UUID ownerId);
    void ensureProSubscription(UUID ownerId);
    void activateProPlanForTesting(UUID ownerId);
    void downgradeToGoForTesting(UUID ownerId);
}

