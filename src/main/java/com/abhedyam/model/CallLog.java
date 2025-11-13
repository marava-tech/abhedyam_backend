package com.abhedyam.model;

import com.abhedyam.model.enums.CallDirection;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "call_logs")
@Getter
@Setter
public class CallLog extends BaseEntity {
    
    @Column(nullable = false)
    private UUID ownerId;
    
    @Column
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
}

