package com.abhedyam.dto;

import com.abhedyam.model.enums.OnboardingStatus;
import lombok.Data;

@Data
public class OwnerOnboardingStatusUpdateRequest {
    private OnboardingStatus status;
    private String statusDescription;
}
