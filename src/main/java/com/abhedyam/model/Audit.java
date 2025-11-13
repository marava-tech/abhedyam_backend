package com.abhedyam.model;

import com.abhedyam.model.enums.AuditAction;
import com.abhedyam.model.enums.AuditType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audits")
@Getter
@Setter
public class Audit extends BaseEntity {
    
    @Column(nullable = false)
    private UUID ownerId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditAction action;
    
    @Column
    private UUID entityId;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal amount;
    
    @Column(nullable = false)
    private String headline;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private Instant timestamp;
}

