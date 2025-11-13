package com.abhedyam.dto;

import com.abhedyam.model.SaleItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleDetailResponse {
    private String transactionId;
    private UUID customerId;
    private String customerName;
    private UUID ownerId;
    private List<SaleItem> items;
    private BigDecimal totalAmount;
    private Instant createdAt;
    private Instant dueDate;
    private BigDecimal totalPaid;
    private BigDecimal totalDue;
}

