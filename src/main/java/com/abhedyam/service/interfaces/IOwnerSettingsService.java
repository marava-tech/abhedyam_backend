package com.abhedyam.service.interfaces;

import com.abhedyam.dto.OwnerSettingsResponse;
import com.abhedyam.dto.OwnerSettingsUpdateRequest;

public interface IOwnerSettingsService {
    OwnerSettingsResponse getCurrentOwnerSettings();
    OwnerSettingsResponse updateCurrentOwnerSettings(OwnerSettingsUpdateRequest request);
}

