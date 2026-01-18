package com.abhedyam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerBasicSummaryResponse {
    private UUID customerId;
    private String name;
    private String phone;
    private String imageUrl;
}


