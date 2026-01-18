package com.abhedyam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSalesSummaryResponse {
    private UUID customerId;
    private Long totalSales;
    private BigDecimal totalAmount;
}


