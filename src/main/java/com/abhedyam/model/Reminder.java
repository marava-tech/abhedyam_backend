package com.abhedyam.model;

import com.abhedyam.model.enums.ReminderChannel;
import com.abhedyam.model.enums.ReminderStatus;
import com.abhedyam.model.enums.ReminderType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reminders")
@Getter
@Setter
public class Reminder extends BaseEntity {
    
    @Column(nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReminderType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReminderChannel channel;
    
    @Column(nullable = false)
    private Instant time;
    
    @Column(nullable = false)
    private String text;
    
    @Column(nullable = false)
    private UUID customerId;
    
    @Column(nullable = false)
    private UUID ownerId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReminderStatus status = ReminderStatus.PENDING;
}

