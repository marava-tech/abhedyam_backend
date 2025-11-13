package com.abhedyam.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "daily_quotes")
@Getter
@Setter
public class DailyQuote extends BaseEntity {
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;
    
    @Column
    private Instant lastUsedAt;
    
    @Column(nullable = false)
    private Boolean isActive = true;
}

