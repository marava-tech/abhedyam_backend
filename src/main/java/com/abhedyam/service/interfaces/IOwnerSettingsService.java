package com.abhedyam.service.interfaces;

import com.abhedyam.dto.OwnerSettingsUpdateRequest;
import com.abhedyam.model.OwnerSettings;

import java.util.List;
import java.util.UUID;

public interface IOwnerSettingsService {
    OwnerSettings create(OwnerSettings ownerSettings);
    OwnerSettings getById(UUID id);
    List<OwnerSettings> getAll();
    OwnerSettings update(UUID id, OwnerSettings settingsDetails);
    void delete(UUID id);
    OwnerSettings getCurrentOwnerSettings();
    OwnerSettings updateCurrentOwnerSettings(OwnerSettingsUpdateRequest request);
}

