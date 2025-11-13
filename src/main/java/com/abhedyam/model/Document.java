package com.abhedyam.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "documents")
@Getter
@Setter
public class Document extends BaseEntity {
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String mimeType;
    
    @Column(nullable = false)
    private String uploadedUrl;
    
    @Column(nullable = false)
    private Integer orderIndex;
    
    @Column(nullable = false)
    private Boolean visibleToCustomers = false;
    
    @Column(nullable = false)
    private UUID ownerId;
}

