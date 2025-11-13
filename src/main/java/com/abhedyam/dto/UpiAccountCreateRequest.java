package com.abhedyam.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpiAccountCreateRequest {
    @NotBlank(message = "VPA is required")
    private String vpa;
}

