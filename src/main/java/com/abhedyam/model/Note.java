package com.abhedyam.model;

import com.abhedyam.model.enums.NoteStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "notes")
@Getter
@Setter
public class Note extends BaseEntity {
    
    @Column(nullable = false)
    private UUID customerId;
    
    @Column(nullable = false)
    private UUID ownerId;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NoteStatus status = NoteStatus.PENDING;
}

