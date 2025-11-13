package com.abhedyam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatsResponse {
    private LocalDate date;
    private BigDecimal totalSales;
    private Integer totalOrders;
    private Integer totalCustomers;
    private Integer totalProductsSold;
    private List<TopProductStats> topProducts;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopProductStats {
        private String productId;
        private String productName;
        private BigDecimal totalSales;
        private BigDecimal totalQuantity;
        private Integer orderCount;
        private Integer rank;
    }
}

