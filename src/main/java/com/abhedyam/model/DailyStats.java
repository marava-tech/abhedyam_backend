package com.abhedyam.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "daily_stats", uniqueConstraints = @UniqueConstraint(columnNames = {"ownerId", "statDate"}))
@Getter
@Setter
public class DailyStats extends BaseEntity {
    
    @Column(nullable = false)
    private UUID ownerId;
    
    @Column(nullable = false)
    private LocalDate statDate;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalSales = BigDecimal.ZERO;
    
    @Column(nullable = false)
    private Integer totalOrders = 0;
    
    @Column(nullable = false)
    private Integer totalCustomers = 0;
    
    @Column(nullable = false)
    private Integer totalProductsSold = 0;
}

