package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Customer summary response with sales, payments, and dues information")
public class CustomerSummaryResponse {
    @Schema(description = "Customer ID")
    private UUID customerId;
    
    @Schema(description = "Customer name")
    private String name;
    
    @Schema(description = "Customer phone number")
    private String phone;
    
    @Schema(description = "Customer image URL")
    private String imageUrl;
    
    @Schema(description = "Total number of sales")
    private Long totalSales;
    
    @Schema(description = "Total amount of all sales")
    private BigDecimal totalAmount;
    
    @Schema(description = "Total amount paid")
    private BigDecimal totalPaid;
    
    @Schema(description = "Total amount due")
    private BigDecimal totalDue;
}
