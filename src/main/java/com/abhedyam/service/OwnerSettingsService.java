package com.abhedyam.service;

import com.abhedyam.dto.OwnerSettingsResponse;
import com.abhedyam.dto.OwnerSettingsUpdateRequest;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Owner;
import com.abhedyam.model.OwnerSettings;
import com.abhedyam.repository.OwnerRepository;
import com.abhedyam.repository.OwnerSettingsRepository;
import com.abhedyam.service.interfaces.IOwnerSettingsService;
import com.abhedyam.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OwnerSettingsService implements IOwnerSettingsService {
    
    private final OwnerSettingsRepository ownerSettingsRepository;
    private final OwnerRepository ownerRepository;
    
    @Override
    @Transactional
    public OwnerSettingsResponse getCurrentOwnerSettings() {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        
        OwnerSettings settings = ownerSettingsRepository.findById(ownerId)
                .orElseGet(() -> {
                    OwnerSettings newSettings = new OwnerSettings();
                    newSettings.setId(ownerId);
                    newSettings.setOwnerId(ownerId);
                    newSettings.setDailyQuoteEnabled(true);
                    newSettings.setCallLogSyncEnabled(true);
                    OwnerSettings saved = ownerSettingsRepository.save(newSettings);
                    
                    Owner owner = ownerRepository.findById(ownerId)
                            .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
                    owner.setUserSettingsId(saved.getId());
                    ownerRepository.save(owner);
                    
                    return saved;
                });
        
        return toResponse(settings);
    }
    
    @Override
    @Transactional
    public OwnerSettingsResponse updateCurrentOwnerSettings(OwnerSettingsUpdateRequest request) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        
        OwnerSettings settings = ownerSettingsRepository.findById(ownerId)
                .orElseGet(() -> {
                    OwnerSettings newSettings = new OwnerSettings();
                    newSettings.setId(ownerId);
                    newSettings.setOwnerId(ownerId);
                    newSettings.setDailyQuoteEnabled(true);
                    newSettings.setCallLogSyncEnabled(true);
                    return newSettings;
                });
        
        if (request.getDailyQuoteEnabled() != null) {
            settings.setDailyQuoteEnabled(request.getDailyQuoteEnabled());
        }
        if (request.getCallLogSyncEnabled() != null) {
            settings.setCallLogSyncEnabled(request.getCallLogSyncEnabled());
        }
        if (request.getOtherFlags() != null) {
            settings.setOtherFlags(request.getOtherFlags());
        }
        
        OwnerSettings saved = ownerSettingsRepository.save(settings);
        
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
        owner.setUserSettingsId(saved.getId());
        ownerRepository.save(owner);
        
        return toResponse(saved);
    }
    
    private OwnerSettingsResponse toResponse(OwnerSettings settings) {
        OwnerSettingsResponse response = new OwnerSettingsResponse();
        response.setId(settings.getId());
        response.setDailyQuoteEnabled(settings.getDailyQuoteEnabled());
        response.setCallLogSyncEnabled(settings.getCallLogSyncEnabled());
        response.setOtherFlags(settings.getOtherFlags());
        response.setCreatedAt(settings.getCreatedAt());
        response.setUpdatedAt(settings.getUpdatedAt());
        return response;
    }
}

