package com.abhedyam.dto;

import lombok.Data;

@Data
public class ProfileUpdateRequest {
    private String name;
    private String businessName;
    private String imageUrl;
}

