package com.abhedyam.service.interfaces;

import com.abhedyam.dto.OwnerSettingsResponse;
import com.abhedyam.dto.OwnerSettingsUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface IOwnerSettingsService {
    OwnerSettingsResponse getCurrentOwnerSettings();
    OwnerSettingsResponse updateCurrentOwnerSettings(OwnerSettingsUpdateRequest request);
}

