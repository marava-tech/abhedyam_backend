package com.abhedyam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerPaymentsSummaryResponse {
    private UUID customerId;
    private Long totalPayments;
    private BigDecimal totalPaid;
    private BigDecimal totalAmount;
    private BigDecimal dueAmount;
}


