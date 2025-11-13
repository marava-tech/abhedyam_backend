package com.abhedyam.model;

import com.abhedyam.model.enums.NotificationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter
@Setter
public class Notification extends BaseEntity {
    
    @Column(nullable = false)
    private UUID ownerId;
    
    @Column(nullable = false)
    private UUID userId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;
    
    @Column(nullable = false)
    private String message;
    
    @Column(nullable = false)
    private Instant timestamp;
    
    @Column(nullable = false)
    private Boolean isRead = false;
}

