package com.abhedyam.service;

import com.abhedyam.dto.AppConfigResponse;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.OwnerSettings;
import com.abhedyam.model.User;
import com.abhedyam.model.enums.UserType;
import com.abhedyam.repository.OwnerSettingsRepository;
import com.abhedyam.repository.UserRepository;
import com.abhedyam.service.interfaces.IAppConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppConfigService implements IAppConfigService {
    
    private final UserRepository userRepository;
    private final OwnerSettingsRepository ownerSettingsRepository;
    
    @Override
    @Transactional(readOnly = true)
    public AppConfigResponse getAppConfigByUserId(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        if (user.getType() != UserType.BUSINESS) {
            return getDefaultConfig();
        }
        
        OwnerSettings settings = ownerSettingsRepository.findByOwnerId(userId)
                .orElse(null);
        
        if (settings == null) {
            return getDefaultConfig();
        }
        
        AppConfigResponse response = new AppConfigResponse();
        response.setDailyQuoteEnabled(settings.getDailyQuoteEnabled());
        response.setCallLogSyncEnabled(settings.getCallLogSyncEnabled());
        response.setIsDarkModeEnabled(settings.getIsDarkModeEnabled());
        response.setOtherFlags(settings.getOtherFlags());
        
        return response;
    }
    
    private AppConfigResponse getDefaultConfig() {
        AppConfigResponse response = new AppConfigResponse();
        response.setDailyQuoteEnabled(true);
        response.setCallLogSyncEnabled(true);
        response.setIsDarkModeEnabled(false);
        response.setOtherFlags(null);
        return response;
    }
}

