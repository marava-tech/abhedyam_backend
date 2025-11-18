package com.abhedyam.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "location_details", uniqueConstraints = @UniqueConstraint(columnNames = "user_id"))
@Getter
@Setter
public class LocationDetails extends BaseEntity {
    
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, unique = true, columnDefinition = "VARCHAR(36)")
    private UUID userId;
    
    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;
    
    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude;
    
    @Column
    private String village;
    
    @Column(columnDefinition = "TEXT")
    private String addressText;
}

