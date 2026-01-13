package com.abhedyam.service;

import com.abhedyam.config.RazorpayConfig;
import com.abhedyam.dto.PaymentVerifyRequest;
import com.abhedyam.dto.SubscriptionCreateRequest;
import com.abhedyam.dto.SubscriptionCreateResponse;
import com.abhedyam.dto.SubscriptionDetailsResponse;
import com.abhedyam.dto.SubscriptionStatusResponse;
import com.abhedyam.exception.BusinessException;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Owner;
import com.abhedyam.model.Subscription;
import com.abhedyam.model.enums.SubscriptionStatus;
import com.abhedyam.repository.OwnerRepository;
import com.abhedyam.repository.SubscriptionRepository;
import com.abhedyam.service.interfaces.ISubscriptionService;
import com.abhedyam.util.SecurityUtil;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService implements ISubscriptionService {
    
    private final RazorpayClient razorpayClient;
    private final RazorpayConfig razorpayConfig;
    private final OwnerRepository ownerRepository;
    private final SubscriptionRepository subscriptionRepository;
    
    @Override
    @Transactional
    public SubscriptionCreateResponse createSubscription(SubscriptionCreateRequest request) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        String requestedPlanId = request.getPlanId();
        
        String razorpayMode = razorpayConfig.getMode();
        String razorpayKeyId = razorpayConfig.getKeyId();
        
        log.info("Creating subscription without plan_id (manual recurring control) - ownerId: {}, requestedPlanId: {}, razorpayMode: {}, razorpayKeyId: {}", 
                ownerId, requestedPlanId, razorpayMode, razorpayKeyId);
        
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> {
                    log.error("Owner not found for ownerId: {}", ownerId);
                    return new ResourceNotFoundException("Owner not found");
                });
        
        Optional<Subscription> existingPendingSubscription = subscriptionRepository
                .findFirstByOwnerIdAndStatusOrderByCreatedAtDesc(ownerId, SubscriptionStatus.PENDING);
        
        if (existingPendingSubscription.isPresent()) {
            Subscription pendingSubscription = existingPendingSubscription.get();
            String existingSubscriptionId = pendingSubscription.getRazorpaySubscriptionId();
            
            log.info("Reusing existing pending subscription - ownerId: {}, razorpaySubscriptionId: {}, planId: {}", 
                    ownerId, existingSubscriptionId, pendingSubscription.getRazorpayPlanId());
            
            SubscriptionCreateResponse response = new SubscriptionCreateResponse();
            response.setSubscriptionId(existingSubscriptionId);
            return response;
        }
        
        try {
            JSONObject subscriptionRequest = new JSONObject();
            subscriptionRequest.put("total_count", 1);
            subscriptionRequest.put("quantity", 1);
            
            JSONObject customerRequest = new JSONObject();
            customerRequest.put("name", owner.getName());
            if (owner.getEmail() != null) {
                customerRequest.put("email", owner.getEmail());
            }
            if (owner.getPhone() != null) {
                customerRequest.put("contact", owner.getPhone());
            }
            subscriptionRequest.put("customer_notify", 1);
            
            log.debug("Calling Razorpay API to create subscription without plan_id - ownerId: {}, ownerName: {}, razorpayMode: {}, razorpayKeyId: {}", 
                    ownerId, owner.getName(), razorpayMode, razorpayKeyId);
            
            com.razorpay.Subscription razorpaySubscription = razorpayClient.subscriptions.create(subscriptionRequest);
            String subscriptionId = razorpaySubscription.get("id");
            
            log.info("Razorpay subscription created successfully (without plan_id) - ownerId: {}, razorpaySubscriptionId: {}", 
                    ownerId, subscriptionId);
            
            Subscription subscription = new Subscription();
            subscription.setOwnerId(ownerId);
            subscription.setRazorpaySubscriptionId(subscriptionId);
            subscription.setRazorpayPlanId(requestedPlanId);
            subscription.setStatus(SubscriptionStatus.PENDING);
            
            subscriptionRepository.save(subscription);
            
            log.info("Subscription saved to database - ownerId: {}, subscriptionId: {}, razorpaySubscriptionId: {}, storedPlanId: {}", 
                    ownerId, subscription.getId(), subscriptionId, requestedPlanId);
            
            SubscriptionCreateResponse response = new SubscriptionCreateResponse();
            response.setSubscriptionId(subscriptionId);
            return response;
            
        } catch (RazorpayException e) {
            String errorMessage = e.getMessage();
            log.error("Error creating Razorpay subscription - ownerId: {}, razorpayMode: {}, razorpayKeyId: {}, error: {}", 
                    ownerId, razorpayMode, razorpayKeyId, errorMessage, e);
            throw new BusinessException("SUBSCRIPTION_CREATE_FAILED", 
                String.format("Failed to create subscription in %s mode: %s", razorpayMode, errorMessage));
        }
    }
    
    @Override
    @Transactional
    public void verifyPayment(PaymentVerifyRequest request) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        log.info("Verifying payment - ownerId: {}, subscriptionId: {}, paymentId: {}", 
                ownerId, request.getSubscriptionId(), request.getPaymentId());
        
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> {
                    log.error("Owner not found for payment verification - ownerId: {}", ownerId);
                    return new ResourceNotFoundException("Owner not found");
                });
        
        Subscription subscription = subscriptionRepository.findByRazorpaySubscriptionId(request.getSubscriptionId())
                .orElseThrow(() -> {
                    log.error("Subscription not found for payment verification - razorpaySubscriptionId: {}, ownerId: {}", 
                            request.getSubscriptionId(), ownerId);
                    return new ResourceNotFoundException("Subscription not found");
                });
        
        if (!subscription.getOwnerId().equals(ownerId)) {
            log.warn("Unauthorized payment verification attempt - subscriptionOwnerId: {}, requestOwnerId: {}, subscriptionId: {}", 
                    subscription.getOwnerId(), ownerId, request.getSubscriptionId());
            throw new BusinessException("UNAUTHORIZED", "You don't have access to this subscription");
        }
        
        String signatureData = request.getSubscriptionId() + "|" + request.getPaymentId();
        log.debug("Generating signature for payment verification - subscriptionId: {}, paymentId: {}, signatureData: {}", 
                request.getSubscriptionId(), request.getPaymentId(), signatureData);
        
        String generatedSignature = generateSignature(signatureData, razorpayConfig.getKeySecret());
        String receivedSignature = request.getRazorpaySignature();
        
        if (generatedSignature == null) {
            log.error("Generated signature is null - subscriptionId: {}, paymentId: {}, ownerId: {}", 
                    request.getSubscriptionId(), request.getPaymentId(), ownerId);
            throw new BusinessException("SIGNATURE_GENERATION_FAILED", "Failed to generate signature for payment verification");
        }
        
        log.debug("Signature comparison - generatedLength: {}, receivedLength: {}, generated: {}, received: {}", 
                generatedSignature.length(),
                receivedSignature != null ? receivedSignature.length() : 0,
                generatedSignature,
                receivedSignature);
        
        if (!generatedSignature.equals(receivedSignature)) {
            log.error("Invalid payment signature - subscriptionId: {}, paymentId: {}, ownerId: {}, generatedSignature: {}, receivedSignature: {}, signatureData: {}", 
                    request.getSubscriptionId(), request.getPaymentId(), ownerId, generatedSignature, receivedSignature, signatureData);
            throw new BusinessException("INVALID_SIGNATURE", 
                String.format("Payment signature verification failed. Ensure you are using the correct key secret and the signature is generated from '%s'", signatureData));
        }
        
        log.debug("Signature verified successfully, fetching subscription from Razorpay - subscriptionId: {}", 
                request.getSubscriptionId());
        
        try {
            com.razorpay.Subscription razorpaySubscription = razorpayClient.subscriptions.fetch(request.getSubscriptionId());
            Object statusObj = razorpaySubscription.get("status");
            String status = statusObj != null ? statusObj.toString() : null;
            
            log.info("Razorpay subscription status fetched - subscriptionId: {}, status: {}, ownerId: {}", 
                    request.getSubscriptionId(), status, ownerId);
            
            if ("active".equals(status) || "authenticated".equals(status)) {
                log.info("Subscription is active/authenticated, activating PRO plan - subscriptionId: {}, ownerId: {}", 
                        request.getSubscriptionId(), ownerId);
                activateProPlan(owner, subscription);
            } else {
                log.warn("Subscription status is not active/authenticated - subscriptionId: {}, status: {}, ownerId: {}", 
                        request.getSubscriptionId(), status, ownerId);
            }
        } catch (RazorpayException e) {
            log.error("Error fetching subscription from Razorpay - subscriptionId: {}, ownerId: {}, error: {}", 
                    request.getSubscriptionId(), ownerId, e.getMessage(), e);
            throw new BusinessException("SUBSCRIPTION_FETCH_FAILED", 
                String.format("Failed to verify subscription '%s': %s", request.getSubscriptionId(), e.getMessage()));
        }
    }
    
    @Override
    @Transactional
    public void handleWebhook(String payload, String signature) {
        log.info("Processing Razorpay webhook - payloadLength: {}", payload != null ? payload.length() : 0);
        
        String generatedSignature = generateSignature(payload, razorpayConfig.getKeySecret());
        
        if (!generatedSignature.equals(signature)) {
            log.warn("Invalid webhook signature received - payloadLength: {}", payload != null ? payload.length() : 0);
            throw new BusinessException("INVALID_WEBHOOK_SIGNATURE", "Webhook signature verification failed");
        }
        
        try {
            JSONObject event = new JSONObject(payload);
            String eventType = event.getString("event");
            JSONObject payloadData = event.getJSONObject("payload");
            JSONObject subscriptionData = payloadData.getJSONObject("subscription");
            String subscriptionId = subscriptionData.getString("id");
            
            log.info("Webhook event received - eventType: {}, subscriptionId: {}", eventType, subscriptionId);
            
            Subscription subscription = subscriptionRepository.findByRazorpaySubscriptionId(subscriptionId)
                    .orElse(null);
            
            if (subscription == null) {
                log.warn("Subscription not found for webhook - razorpaySubscriptionId: {}, eventType: {}", 
                        subscriptionId, eventType);
                return;
            }
            
            UUID ownerId = subscription.getOwnerId();
            Owner owner = ownerRepository.findById(ownerId)
                    .orElse(null);
            
            if (owner == null) {
                log.warn("Owner not found for webhook - subscriptionId: {}, ownerId: {}, eventType: {}", 
                        subscriptionId, ownerId, eventType);
                return;
            }
            
            log.info("Processing webhook event - eventType: {}, subscriptionId: {}, ownerId: {}, ownerName: {}", 
                    eventType, subscriptionId, ownerId, owner.getName());
            
            switch (eventType) {
                case "subscription.activated":
                    activateProPlan(owner, subscription);
                    break;
                case "subscription.expired":
                    expireSubscription(owner, subscription);
                    break;
                case "subscription.cancelled":
                    cancelSubscription(owner, subscription);
                    break;
                default:
                    log.info("Unhandled webhook event - eventType: {}, subscriptionId: {}, ownerId: {}", 
                            eventType, subscriptionId, ownerId);
            }
        } catch (Exception e) {
            log.error("Error processing webhook - payloadLength: {}, error: {}", 
                    payload != null ? payload.length() : 0, e.getMessage(), e);
            throw new BusinessException("WEBHOOK_PROCESSING_FAILED", 
                String.format("Failed to process webhook: %s", e.getMessage()));
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public SubscriptionStatusResponse getSubscriptionStatus() {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        log.debug("Getting subscription status - ownerId: {}", ownerId);
        
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> {
                    log.error("Owner not found for subscription status - ownerId: {}", ownerId);
                    return new ResourceNotFoundException("Owner not found");
                });
        
        SubscriptionStatusResponse response = new SubscriptionStatusResponse();
        response.setPlan(owner.getSubscription());
        response.setStatus(owner.getSubscriptionStatus());
        response.setValidTill(owner.getValidTill());
        
        log.debug("Subscription status retrieved - ownerId: {}, plan: {}, status: {}, validTill: {}", 
                ownerId, owner.getSubscription(), owner.getSubscriptionStatus(), owner.getValidTill());
        
        return response;
    }
    
    @Override
    @Transactional(readOnly = true)
    public SubscriptionDetailsResponse getSubscriptionDetailsByOwnerId(UUID ownerId) {
        log.debug("Getting subscription details - ownerId: {}", ownerId);
        
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> {
                    log.error("Owner not found for subscription details - ownerId: {}", ownerId);
                    return new ResourceNotFoundException("Owner not found");
                });
        
        SubscriptionDetailsResponse response = new SubscriptionDetailsResponse();
        response.setOwnerId(ownerId);
        response.setPlan(owner.getSubscription());
        response.setStatus(owner.getSubscriptionStatus());
        response.setValidTill(owner.getValidTill());
        
        List<Subscription> subscriptions = subscriptionRepository.findAllByOwnerIdOrderByCreatedAtDesc(ownerId);
        Subscription subscription = subscriptions.isEmpty() ? null : subscriptions.get(0);
        
        if (subscription != null) {
            response.setRazorpaySubscriptionId(subscription.getRazorpaySubscriptionId());
            response.setRazorpayPlanId(subscription.getRazorpayPlanId());
            response.setActivatedAt(subscription.getActivatedAt());
            response.setExpiredAt(subscription.getExpiredAt());
            response.setCancelledAt(subscription.getCancelledAt());
            
            log.debug("Subscription details retrieved - ownerId: {}, plan: {}, razorpaySubscriptionId: {}, razorpayPlanId: {}, totalSubscriptions: {}", 
                    ownerId, owner.getSubscription(), subscription.getRazorpaySubscriptionId(), subscription.getRazorpayPlanId(), subscriptions.size());
        } else {
            log.debug("No subscription entity found for owner - ownerId: {}, plan: {}", ownerId, owner.getSubscription());
        }
        
        return response;
    }
    
    public void ensureProSubscription(UUID ownerId) {
        log.debug("Checking PRO subscription requirement - ownerId: {}", ownerId);
        
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> {
                    log.error("Owner not found for PRO subscription check - ownerId: {}", ownerId);
                    return new ResourceNotFoundException("Owner not found");
                });
        
        boolean isValid = owner.getSubscription() == com.abhedyam.model.enums.Subscription.PRO &&
                owner.getSubscriptionStatus() == SubscriptionStatus.ACTIVE &&
                owner.getValidTill() != null &&
                !owner.getValidTill().isBefore(Instant.now());
        
        if (!isValid) {
            log.warn("PRO subscription check failed - ownerId: {}, plan: {}, status: {}, validTill: {}", 
                    ownerId, owner.getSubscription(), owner.getSubscriptionStatus(), owner.getValidTill());
            throw new BusinessException("SUBSCRIPTION_REQUIRED", 
                "PRO subscription is required for this feature. Please upgrade to PRO plan.");
        }
        
        log.debug("PRO subscription check passed - ownerId: {}, validTill: {}", ownerId, owner.getValidTill());
    }
    
    @Override
    @Transactional
    public void activateProPlanForTesting(UUID ownerId) {
        log.warn("Activating PRO plan for testing - ownerId: {}", ownerId);
        
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> {
                    log.error("Owner not found for test PRO activation - ownerId: {}", ownerId);
                    return new ResourceNotFoundException("Owner not found");
                });
        
        List<Subscription> subscriptions = subscriptionRepository.findAllByOwnerIdOrderByCreatedAtDesc(ownerId);
        Subscription subscription;
        
        if (subscriptions.isEmpty()) {
            log.info("No existing subscriptions found, creating new test subscription - ownerId: {}", ownerId);
            subscription = new Subscription();
            subscription.setOwnerId(ownerId);
            subscription.setRazorpaySubscriptionId("test_sub_" + ownerId.toString().substring(0, 8) + "_" + System.currentTimeMillis());
            subscription.setRazorpayPlanId(razorpayConfig.getProPlanId());
            subscription.setStatus(SubscriptionStatus.PENDING);
        } else {
            subscription = subscriptions.get(0);
            log.info("Using most recent subscription for testing - ownerId: {}, subscriptionId: {}, razorpaySubscriptionId: {}", 
                    ownerId, subscription.getId(), subscription.getRazorpaySubscriptionId());
        }
        
        activateProPlan(owner, subscription);
        
        log.warn("PRO plan activated for testing - ownerId: {}, validTill: {}", ownerId, owner.getValidTill());
    }
    
    private void activateProPlan(Owner owner, Subscription subscription) {
        UUID ownerId = owner.getId();
        String razorpaySubscriptionId = subscription.getRazorpaySubscriptionId();
        String razorpayPlanId = subscription.getRazorpayPlanId();
        
        log.info("Activating PRO plan - ownerId: {}, razorpaySubscriptionId: {}, razorpayPlanId: {}", 
                ownerId, razorpaySubscriptionId, razorpayPlanId);
        
        owner.setSubscription(com.abhedyam.model.enums.Subscription.PRO);
        owner.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
        owner.setSubscriptionId(razorpaySubscriptionId);
        
        Instant now = Instant.now();
        ZonedDateTime zonedDateTime = now.atZone(ZoneId.systemDefault());
        ZonedDateTime validTillZoned = zonedDateTime.plus(1, ChronoUnit.YEARS);
        Instant validTill = validTillZoned.toInstant();
        owner.setValidTill(validTill);
        
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setActivatedAt(now);
        subscription.setValidTill(validTill);
        
        ownerRepository.save(owner);
        subscriptionRepository.save(subscription);
        
        log.info("PRO plan activated successfully - ownerId: {}, razorpaySubscriptionId: {}, validTill: {}", 
                ownerId, razorpaySubscriptionId, validTill);
    }
    
    private void expireSubscription(Owner owner, Subscription subscription) {
        UUID ownerId = owner.getId();
        String razorpaySubscriptionId = subscription.getRazorpaySubscriptionId();
        
        log.info("Expiring subscription - ownerId: {}, razorpaySubscriptionId: {}", ownerId, razorpaySubscriptionId);
        
        owner.setSubscription(com.abhedyam.model.enums.Subscription.GO);
        owner.setSubscriptionStatus(SubscriptionStatus.EXPIRED);
        
        Instant now = Instant.now();
        subscription.setStatus(SubscriptionStatus.EXPIRED);
        subscription.setExpiredAt(now);
        
        ownerRepository.save(owner);
        subscriptionRepository.save(subscription);
        
        log.info("Subscription expired successfully - ownerId: {}, razorpaySubscriptionId: {}, expiredAt: {}", 
                ownerId, razorpaySubscriptionId, now);
    }
    
    private void cancelSubscription(Owner owner, Subscription subscription) {
        UUID ownerId = owner.getId();
        String razorpaySubscriptionId = subscription.getRazorpaySubscriptionId();
        
        log.info("Cancelling subscription - ownerId: {}, razorpaySubscriptionId: {}", ownerId, razorpaySubscriptionId);
        
        owner.setSubscription(com.abhedyam.model.enums.Subscription.GO);
        owner.setSubscriptionStatus(SubscriptionStatus.CANCELLED);
        
        Instant now = Instant.now();
        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscription.setCancelledAt(now);
        
        ownerRepository.save(owner);
        subscriptionRepository.save(subscription);
        
        log.info("Subscription cancelled successfully - ownerId: {}, razorpaySubscriptionId: {}, cancelledAt: {}", 
                ownerId, razorpaySubscriptionId, now);
    }
    
    private String generateSignature(String data, String secret) {
        try {
            if (data == null) {
                log.error("Cannot generate signature: data is null");
                throw new BusinessException("SIGNATURE_GENERATION_FAILED", "Data cannot be null for signature generation");
            }
            log.debug("Generating HMAC SHA256 signature - dataLength: {}", data.length());
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            log.error("Error generating signature - dataLength: {}, error: {}", 
                    data != null ? data.length() : 0, e.getMessage(), e);
            throw new BusinessException("SIGNATURE_GENERATION_FAILED", "Failed to generate signature");
        }
    }
}

