package com.abhedyam.service.interfaces;

import com.abhedyam.dto.AppConfigResponse;

import java.util.UUID;

public interface IAppConfigService {
    AppConfigResponse getAppConfigByUserId(UUID userId);
}

