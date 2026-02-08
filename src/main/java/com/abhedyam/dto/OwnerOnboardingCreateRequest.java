package com.abhedyam.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class OwnerOnboardingCreateRequest {
    private UUID ownerId;
    private String videoUrl;
    private String description;
}
