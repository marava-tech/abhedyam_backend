package com.abhedyam.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "top_products")
@Getter
@Setter
public class TopProduct extends BaseEntity {
    
    @Column(nullable = false)
    private UUID ownerId;
    
    @Column(nullable = false)
    private LocalDate statDate;
    
    @Column(nullable = false)
    private UUID productId;
    
    @Column(nullable = false)
    private String productName;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalSales = BigDecimal.ZERO;
    
    @Column(nullable = false, precision = 18, scale = 3)
    private BigDecimal totalQuantity = BigDecimal.ZERO;
    
    @Column(nullable = false)
    private Integer orderCount = 0;
    
    @Column(nullable = false)
    private Integer rank;
}

