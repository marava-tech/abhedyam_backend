package com.abhedyam.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "top_products")
@Getter
@Setter
public class TopProduct extends BaseEntity {
    
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, columnDefinition = "VARCHAR(36)")
    private UUID ownerId;
    
    @Column(nullable = false)
    private LocalDate statDate;
    
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, columnDefinition = "VARCHAR(36)")
    private UUID productId;
    
    @Column(nullable = false)
    private String productName;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalSales = BigDecimal.ZERO;
    
    @Column(nullable = false, precision = 18, scale = 3)
    private BigDecimal totalQuantity = BigDecimal.ZERO;
    
    @Column(nullable = false)
    private Integer orderCount = 0;
    
    @Column(name = "`rank`", nullable = false)
    private Integer rank;
}

