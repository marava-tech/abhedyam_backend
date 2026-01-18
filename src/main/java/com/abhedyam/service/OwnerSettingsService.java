package com.abhedyam.service;

import com.abhedyam.dto.OwnerSettingsResponse;
import com.abhedyam.dto.OwnerSettingsUpdateRequest;
import com.abhedyam.model.OwnerSettings;
import com.abhedyam.repository.OwnerSettingsRepository;
import com.abhedyam.service.interfaces.IOwnerSettingsService;
import com.abhedyam.util.SecurityUtil;
import com.abhedyam.exception.BusinessException;
import com.abhedyam.constants.ErrorCodes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OwnerSettingsService implements IOwnerSettingsService {
    
    private final OwnerSettingsRepository ownerSettingsRepository;
    
    @Override
    @Transactional
    public OwnerSettingsResponse getOwnerSettings(UUID ownerId) {
        validateOwnerAccess(ownerId);
        
        OwnerSettings settings = ownerSettingsRepository.findByOwnerId(ownerId)
                .orElseGet(() -> {
                OwnerSettings newSettings = new OwnerSettings();
                newSettings.setId(ownerId);
                newSettings.setOwnerId(ownerId);
                newSettings.setDailyQuoteEnabled(true);
                newSettings.setIsDarkModeEnabled(false);
                return ownerSettingsRepository.save(newSettings);
                });
        
        return toResponse(settings);
    }
    
    @Override
    @Transactional
    public OwnerSettingsResponse updateOwnerSettings(UUID ownerId, OwnerSettingsUpdateRequest request) {
        validateOwnerAccess(ownerId);
        
        OwnerSettings settings = ownerSettingsRepository.findByOwnerId(ownerId)
                .orElseGet(() -> {
                OwnerSettings newSettings = new OwnerSettings();
                newSettings.setId(ownerId);
                newSettings.setOwnerId(ownerId);
                newSettings.setDailyQuoteEnabled(true);
                newSettings.setIsDarkModeEnabled(false);
                return newSettings;
                });
        
        if (request.getDailyQuoteEnabled() != null) {
            settings.setDailyQuoteEnabled(request.getDailyQuoteEnabled());
        }
        if (request.getIsDarkModeEnabled() != null) {
            settings.setIsDarkModeEnabled(request.getIsDarkModeEnabled());
        }
        if (request.getOtherFlags() != null) {
            settings.setOtherFlags(request.getOtherFlags());
        }
        
        OwnerSettings saved = ownerSettingsRepository.save(settings);
        return toResponse(saved);
    }
    
    private OwnerSettingsResponse toResponse(OwnerSettings settings) {
        OwnerSettingsResponse response = new OwnerSettingsResponse();
        response.setId(settings.getId());
        response.setDailyQuoteEnabled(settings.getDailyQuoteEnabled());
        response.setIsDarkModeEnabled(settings.getIsDarkModeEnabled());
        response.setOtherFlags(settings.getOtherFlags());
        response.setCreatedAt(settings.getCreatedAt());
        response.setUpdatedAt(settings.getUpdatedAt());
        return response;
    }

    private void validateOwnerAccess(UUID ownerId) {
        UUID currentOwnerId = SecurityUtil.getCurrentUserId();
        if (ownerId == null || !ownerId.equals(currentOwnerId)) {
            throw new BusinessException(ErrorCodes.UNAUTHORIZED, "You can only access your own settings");
        }
    }
}

