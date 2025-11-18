package com.abhedyam.model;

import com.abhedyam.model.enums.CallDirection;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "call_logs")
@Getter
@Setter
public class CallLog extends BaseEntity {
    
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, columnDefinition = "VARCHAR(36)")
    private UUID ownerId;
    
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(columnDefinition = "VARCHAR(36)")
    private UUID customerId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CallDirection direction;
    
    @Column(nullable = false)
    private Instant startTime;
    
    @Column
    private Instant endTime;
    
    @Column(nullable = false)
    private Integer durationSeconds;
    
    @Column(nullable = false)
    private String phone;
    
    @Column(name = "`key`", nullable = false, unique = true, length = 100)
    private String key;
}

