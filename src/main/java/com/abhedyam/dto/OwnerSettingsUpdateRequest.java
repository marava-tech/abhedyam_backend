package com.abhedyam.dto;

import lombok.Data;

import java.util.Map;

@Data
public class OwnerSettingsUpdateRequest {
    private Boolean dailyQuoteEnabled;
    private Boolean callLogSyncEnabled;
    private Map<String, Object> otherFlags;
}

