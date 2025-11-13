package com.abhedyam.model;

import com.abhedyam.model.enums.InventoryLedgerSourceType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "inventory_ledgers")
@Getter
@Setter
public class InventoryLedger extends BaseEntity {
    
    @Column(nullable = false)
    private UUID ownerId;
    
    @Column(nullable = false)
    private UUID productId;
    
    @Column(nullable = false)
    private BigDecimal changeQty;
    
    @Column(nullable = false)
    private BigDecimal balanceAfter;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InventoryLedgerSourceType sourceType;
    
    @Column
    private UUID sourceId;
    
    @Column
    private String note;
}

