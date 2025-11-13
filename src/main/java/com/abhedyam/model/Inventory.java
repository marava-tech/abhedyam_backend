package com.abhedyam.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "inventories", uniqueConstraints = @UniqueConstraint(columnNames = {"ownerId", "productId"}))
@Getter
@Setter
public class Inventory extends BaseEntity {
    
    @Column(nullable = false)
    private UUID productId;
    
    @Column(nullable = false, precision = 18, scale = 3)
    private BigDecimal stock;
    
    @Column(nullable = false)
    private UUID ownerId;
}

