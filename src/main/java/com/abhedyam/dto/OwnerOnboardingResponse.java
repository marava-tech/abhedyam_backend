package com.abhedyam.dto;

import com.abhedyam.model.enums.OnboardingStatus;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class OwnerOnboardingResponse {
    private UUID id;
    private UUID ownerId;
    private String ownerName;
    private String videoUrl;
    private String description;
    private OnboardingStatus status;
    private String statusDescription;
    private Instant createdAt;
    private Instant updatedAt;
}
