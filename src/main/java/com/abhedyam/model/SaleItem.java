package com.abhedyam.model;

import com.abhedyam.model.enums.SaleItemStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "sale_items", indexes = {
    @Index(name = "idx_sale_items_owner_id", columnList = "owner_id"),
    @Index(name = "idx_sale_items_customer_id", columnList = "customer_id"),
    @Index(name = "idx_sale_items_transaction_id", columnList = "transaction_id"),
    @Index(name = "idx_sale_items_created_at", columnList = "created_at")
})
@Getter
@Setter
public class SaleItem extends BaseEntity {
    
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, columnDefinition = "VARCHAR(36)")
    private UUID productId;
    
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, columnDefinition = "VARCHAR(36)")
    private UUID customerId;
    
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, columnDefinition = "VARCHAR(36)")
    private UUID ownerId;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;
    
    @Column(nullable = false, precision = 18, scale = 3)
    private BigDecimal quantity = BigDecimal.ONE;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal remainingAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private SaleItemStatus status = SaleItemStatus.NOT_PAID;
    
    @Column
    private Instant dueDate;
    
    @Column
    private String transactionId;
}

