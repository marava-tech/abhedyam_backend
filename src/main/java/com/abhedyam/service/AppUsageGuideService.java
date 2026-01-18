package com.abhedyam.service;

import com.abhedyam.dto.AppUsageGuideResponse;
import com.abhedyam.exception.BusinessException;
import com.abhedyam.service.interfaces.IAppUsageGuideService;
import com.abhedyam.constants.ErrorCodes;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@Slf4j
public class AppUsageGuideService implements IAppUsageGuideService {
    
    private final ObjectMapper objectMapper;
    
    public AppUsageGuideService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    @Override
    public AppUsageGuideResponse getAppUsageGuide() {
        return loadGuideResponse();
    }

    private AppUsageGuideResponse loadGuideResponse() {
        try (InputStream inputStream = new ClassPathResource("app-guide/APP_GUIDE_API_RESPONSE_EXAMPLE.json").getInputStream()) {
            JsonNode root = objectMapper.readTree(inputStream);
            JsonNode data = root.get("data");
            if (data == null || data.isNull()) {
                throw new BusinessException(ErrorCodes.GUIDE_DATA_MISSING, "App guide information is unavailable");
            }
            return objectMapper.treeToValue(data, AppUsageGuideResponse.class);
        } catch (Exception e) {
            log.error("Failed to load app usage guide", e);
            throw new BusinessException(ErrorCodes.GUIDE_LOAD_FAILED, "Unable to load app guide information");
        }
    }
}

