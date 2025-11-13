package com.abhedyam.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class SaleSearchRequest {
    private UUID customerId;
    private String transactionId;
    private Instant startDate;
    private Instant endDate;
    private Integer page = 0;
    private Integer size = 20;
    private String sortBy = "createdAt";
    private String sortDirection = "DESC";
}

