package com.abhedyam.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    
    @Column(nullable = false)
    private UUID ownerId;
}

