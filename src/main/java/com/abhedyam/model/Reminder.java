package com.abhedyam.model;

import com.abhedyam.model.enums.ReminderChannel;
import com.abhedyam.model.enums.ReminderStatus;
import com.abhedyam.model.enums.ReminderType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
    @Column(nullable = false, length = 50)
    private ReminderType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ReminderChannel channel;
    
    @Column(nullable = false)
    private Instant time;
    
    @Column(nullable = false)
    private String text;
    
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, columnDefinition = "VARCHAR(36)")
    private UUID customerId;
    
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, columnDefinition = "VARCHAR(36)")
    private UUID ownerId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ReminderStatus status = ReminderStatus.PENDING;
}

