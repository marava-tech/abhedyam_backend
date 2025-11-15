package com.abhedyam.model;

import com.abhedyam.model.enums.InventoryLedgerSourceType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "inventory_ledgers")
@Getter
@Setter
public class InventoryLedger extends BaseEntity {
    
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, columnDefinition = "VARCHAR(36)")
    private UUID ownerId;
    
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, columnDefinition = "VARCHAR(36)")
    private UUID productId;
    
    @Column(nullable = false)
    private BigDecimal changeQty;
    
    @Column(nullable = false)
    private BigDecimal balanceAfter;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InventoryLedgerSourceType sourceType;
    
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(columnDefinition = "VARCHAR(36)")
    private UUID sourceId;
    
    @Column
    private String note;
}

