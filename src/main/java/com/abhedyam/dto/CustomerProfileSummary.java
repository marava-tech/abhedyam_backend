package com.abhedyam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerProfileSummary {
    private UUID customerId;
    private String name;
    private String phone;
    private String imageUrl;
    private Long totalSales;
    private BigDecimal totalAmount;
    private BigDecimal totalPaid;
    private BigDecimal totalDue;
    private Long totalNotes;
    private Long totalReminders;
    private Long pendingReminders;
}

