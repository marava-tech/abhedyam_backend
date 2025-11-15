package com.abhedyam.service;

import com.abhedyam.dto.LocationDetailsResponse;
import com.abhedyam.dto.OwnerCreateRequest;
import com.abhedyam.dto.OwnerDetailsResponse;
import com.abhedyam.dto.OwnerResponse;
import com.abhedyam.dto.OwnerSettingsResponse;
import com.abhedyam.dto.OwnerUpdateRequest;
import com.abhedyam.dto.UpiAccountResponse;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.LocationDetails;
import com.abhedyam.model.Owner;
import com.abhedyam.model.OwnerSettings;
import com.abhedyam.model.UPIAccount;
import com.abhedyam.model.enums.UserType;
import com.abhedyam.repository.LocationDetailsRepository;
import com.abhedyam.repository.OwnerRepository;
import com.abhedyam.repository.OwnerSettingsRepository;
import com.abhedyam.repository.UPIAccountRepository;
import com.abhedyam.service.interfaces.IOwnerService;
import com.abhedyam.util.EmailUtil;
import com.abhedyam.util.PhoneUtil;
import com.abhedyam.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OwnerService implements IOwnerService {
    
    private final OwnerRepository ownerRepository;
    private final LocationDetailsRepository locationDetailsRepository;
    private final OwnerSettingsRepository ownerSettingsRepository;
    private final UPIAccountRepository upiAccountRepository;
    
    @Transactional
    public OwnerResponse create(OwnerCreateRequest request) {
        Owner owner = new Owner();
        owner.setName(request.getName());
        owner.setBusinessName(request.getBusinessName());
        owner.setType(request.getType() != null ? request.getType() : UserType.BUSINESS);
        owner.setSubscription(request.getSubscription() != null ? request.getSubscription() : com.abhedyam.model.enums.Subscription.GO);
        owner.setIsVerified(request.getIsVerified() != null ? request.getIsVerified() : false);
        
        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            String normalizedPhone = PhoneUtil.normalizePhone(request.getPhone());
            owner.setPhone(PhoneUtil.extractPhoneWithoutCountryCode(normalizedPhone));
            owner.setPhoneNormalized(normalizedPhone);
        }
        
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            owner.setEmail(EmailUtil.normalizeEmail(request.getEmail()));
        }
        
        if (request.getImageUrl() != null) {
            owner.setImageUrl(request.getImageUrl());
        }
        
        Owner saved = ownerRepository.save(owner);
        return toResponse(saved);
    }
    
    public OwnerResponse getById(UUID id) {
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + id));
        return toResponse(owner);
    }
    
    @Transactional(readOnly = true)
    public OwnerDetailsResponse getOwnerDetails(UUID id) {
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + id));
        
        OwnerDetailsResponse response = new OwnerDetailsResponse();
        response.setOwner(toResponse(owner));
        
        LocationDetails location = locationDetailsRepository.findById(id).orElse(null);
        if (location != null) {
            response.setLocation(toLocationResponse(location));
        }
        
        OwnerSettings settings = ownerSettingsRepository.findById(id).orElse(null);
        if (settings != null) {
            response.setSettings(toSettingsResponse(settings));
        }
        
        UPIAccount upiAccount = upiAccountRepository.findById(id).orElse(null);
        if (upiAccount != null) {
            response.setUpiAccount(toUpiAccountResponse(upiAccount));
        }
        
        return response;
    }
    
    public List<OwnerResponse> getAll() {
        return ownerRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public OwnerResponse updateCurrentOwner(OwnerUpdateRequest request) {
        UUID id = SecurityUtil.getCurrentUserId();
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + id));
        
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            owner.setName(request.getName());
        }
        
        if (request.getBusinessName() != null && !request.getBusinessName().trim().isEmpty()) {
            owner.setBusinessName(request.getBusinessName());
        }
        
        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            String normalizedPhone = PhoneUtil.normalizePhone(request.getPhone());
            owner.setPhone(PhoneUtil.extractPhoneWithoutCountryCode(normalizedPhone));
            owner.setPhoneNormalized(normalizedPhone);
        }
        
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            owner.setEmail(EmailUtil.normalizeEmail(request.getEmail()));
        }
        
        if (request.getImageUrl() != null) {
            if (request.getImageUrl().trim().isEmpty()) {
                owner.setImageUrl(null);
            } else {
                owner.setImageUrl(request.getImageUrl());
            }
        }
        
        if (request.getIsVerified() != null) {
            owner.setIsVerified(request.getIsVerified());
        }
        
        if (request.getSubscription() != null) {
            owner.setSubscription(request.getSubscription());
        }
        
        Owner saved = ownerRepository.save(owner);
        return toResponse(saved);
    }
    
    private OwnerResponse toResponse(Owner owner) {
        OwnerResponse response = new OwnerResponse();
        response.setId(owner.getId());
        response.setName(owner.getName());
        response.setBusinessName(owner.getBusinessName());
        response.setPhone(owner.getPhone());
        response.setEmail(owner.getEmail());
        response.setType(owner.getType());
        response.setImageUrl(owner.getImageUrl());
        response.setIsVerified(owner.getIsVerified());
        response.setSubscription(owner.getSubscription());
        response.setCreatedAt(owner.getCreatedAt());
        response.setUpdatedAt(owner.getUpdatedAt());
        return response;
    }
    
    private LocationDetailsResponse toLocationResponse(LocationDetails location) {
        LocationDetailsResponse response = new LocationDetailsResponse();
        response.setId(location.getId());
        response.setLatitude(location.getLatitude());
        response.setLongitude(location.getLongitude());
        response.setVillage(location.getVillage());
        response.setCreatedAt(location.getCreatedAt());
        response.setUpdatedAt(location.getUpdatedAt());
        return response;
    }
    
    private OwnerSettingsResponse toSettingsResponse(OwnerSettings settings) {
        OwnerSettingsResponse response = new OwnerSettingsResponse();
        response.setId(settings.getId());
        response.setDailyQuoteEnabled(settings.getDailyQuoteEnabled());
        response.setCallLogSyncEnabled(settings.getCallLogSyncEnabled());
        response.setOtherFlags(settings.getOtherFlags());
        response.setCreatedAt(settings.getCreatedAt());
        response.setUpdatedAt(settings.getUpdatedAt());
        return response;
    }
    
    private UpiAccountResponse toUpiAccountResponse(UPIAccount account) {
        UpiAccountResponse response = new UpiAccountResponse();
        response.setId(account.getId());
        response.setVpa(account.getVpa());
        response.setIsVerified(account.getIsVerified());
        response.setVerifiedAt(account.getVerifiedAt());
        response.setCreatedAt(account.getCreatedAt());
        response.setUpdatedAt(account.getUpdatedAt());
        return response;
    }
}

