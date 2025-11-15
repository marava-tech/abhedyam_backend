package com.abhedyam.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "owner_settings", uniqueConstraints = @UniqueConstraint(columnNames = "owner_id"))
@Getter
@Setter
public class OwnerSettings extends BaseEntity {
    
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, unique = true, columnDefinition = "VARCHAR(36)")
    private UUID ownerId;
    
    @Column(nullable = false)
    private Boolean dailyQuoteEnabled = true;
    
    @Column(nullable = false)
    private Boolean callLogSyncEnabled = true;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, Object> otherFlags;
}

