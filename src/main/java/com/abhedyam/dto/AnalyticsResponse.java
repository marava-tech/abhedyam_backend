package com.abhedyam.dto;

import com.abhedyam.model.enums.AnalyticsType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponse {
    private AnalyticsType type;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<PeriodAnalytics> periods;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PeriodAnalytics {
        private String periodLabel;
        private LocalDate periodStart;
        private LocalDate periodEnd;
        private BigDecimal totalSales;
        private Integer totalOrders;
        private Integer totalCustomers;
        private Integer totalProductsSold;
    }
}

