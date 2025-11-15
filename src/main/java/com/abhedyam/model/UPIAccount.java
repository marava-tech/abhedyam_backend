package com.abhedyam.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "upi_accounts", uniqueConstraints = @UniqueConstraint(columnNames = "vpa"))
@Getter
@Setter
public class UPIAccount extends BaseEntity {
    
    @Column(nullable = false, unique = true)
    private String vpa;
    
    @Column(nullable = false)
    private Boolean isVerified = false;
    
    @Column
    private Instant verifiedAt;
    
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, columnDefinition = "VARCHAR(36)")
    private UUID ownerId;
}

