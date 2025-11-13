package com.abhedyam.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OtpSendRequest {
    @NotBlank(message = "Phone number is required")
    private String phone;
}

