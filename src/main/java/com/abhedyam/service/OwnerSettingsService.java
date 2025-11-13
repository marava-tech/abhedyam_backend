package com.abhedyam.service;

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

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OwnerSettingsService implements IOwnerSettingsService {
    
    private final OwnerSettingsRepository ownerSettingsRepository;
    private final OwnerRepository ownerRepository;
    
    public OwnerSettings create(OwnerSettings ownerSettings) {
        return ownerSettingsRepository.save(ownerSettings);
    }
    
    public OwnerSettings getById(UUID id) {
        return ownerSettingsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OwnerSettings not found with id: " + id));
    }
    
    public List<OwnerSettings> getAll() {
        return ownerSettingsRepository.findAll();
    }
    
    @Transactional
    public OwnerSettings update(UUID id, OwnerSettings settingsDetails) {
        OwnerSettings settings = getById(id);
        if (settingsDetails.getDailyQuoteEnabled() != null) settings.setDailyQuoteEnabled(settingsDetails.getDailyQuoteEnabled());
        if (settingsDetails.getCallLogSyncEnabled() != null) settings.setCallLogSyncEnabled(settingsDetails.getCallLogSyncEnabled());
        if (settingsDetails.getOtherFlags() != null) settings.setOtherFlags(settingsDetails.getOtherFlags());
        return ownerSettingsRepository.save(settings);
    }
    
    @Transactional
    public void delete(UUID id) {
        OwnerSettings settings = getById(id);
        settings.setDeletedAt(Instant.now());
        settings.setIsActive(false);
        ownerSettingsRepository.save(settings);
    }
    
    @Override
    public OwnerSettings getCurrentOwnerSettings() {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
        
        if (owner.getUserSettingsId() == null) {
            OwnerSettings settings = new OwnerSettings();
            settings.setOwnerId(ownerId);
            settings.setDailyQuoteEnabled(true);
            settings.setCallLogSyncEnabled(true);
            return ownerSettingsRepository.save(settings);
        }
        
        return ownerSettingsRepository.findById(owner.getUserSettingsId())
                .orElseThrow(() -> new ResourceNotFoundException("OwnerSettings not found"));
    }
    
    @Override
    @Transactional
    public OwnerSettings updateCurrentOwnerSettings(OwnerSettingsUpdateRequest request) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
        
        OwnerSettings settings;
        if (owner.getUserSettingsId() == null) {
            settings = new OwnerSettings();
            settings.setOwnerId(ownerId);
            settings.setDailyQuoteEnabled(true);
            settings.setCallLogSyncEnabled(true);
            settings = ownerSettingsRepository.save(settings);
            owner.setUserSettingsId(settings.getId());
            ownerRepository.save(owner);
        } else {
            settings = ownerSettingsRepository.findById(owner.getUserSettingsId())
                    .orElseThrow(() -> new ResourceNotFoundException("OwnerSettings not found"));
        }
        
        if (request.getDailyQuoteEnabled() != null) {
            settings.setDailyQuoteEnabled(request.getDailyQuoteEnabled());
        }
        if (request.getCallLogSyncEnabled() != null) {
            settings.setCallLogSyncEnabled(request.getCallLogSyncEnabled());
        }
        if (request.getOtherFlags() != null) {
            settings.setOtherFlags(request.getOtherFlags());
        }
        
        return ownerSettingsRepository.save(settings);
    }
}

