package com.abhedyam.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "location_details")
@Getter
@Setter
public class LocationDetails extends BaseEntity {
    
    @Column(nullable = false)
    private UUID userId;
    
    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;
    
    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude;
    
    @Column
    private String village;
}

