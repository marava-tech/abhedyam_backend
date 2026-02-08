package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dashboard statistics response")
public class DashboardStatsResponse {
    @Schema(description = "Total stock/inventory count across all products", example = "1500.00")
    private BigDecimal totalStockCount;

    @Schema(description = "Count of products with stock less than 2", example = "5")
    private Long lowStockCount;

    @Schema(description = "Total sales amount from last 7 days", example = "50000.00")
    private BigDecimal lastWeekSalesAmount;

    @Schema(description = "Weekly growth percentage based on number of products sold", example = "15.5")
    private BigDecimal weeklyGrowthPercentage;
}
