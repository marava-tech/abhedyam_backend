package com.abhedyam.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "sale_items")
@Getter
@Setter
public class SaleItem extends BaseEntity {
    
    @Column(nullable = false)
    private UUID productId;
    
    @Column(nullable = false)
    private UUID customerId;
    
    @Column(nullable = false)
    private UUID ownerId;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;
    
    @Column
    private Instant dueDate;
    
    @Column
    private String transactionId;
}

