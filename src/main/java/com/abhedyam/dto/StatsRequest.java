package com.abhedyam.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StatsRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer topProductsLimit = 10;
}

