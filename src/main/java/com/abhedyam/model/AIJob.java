package com.abhedyam.model;

import com.abhedyam.model.enums.AIJobStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ai_jobs")
@Getter
@Setter
public class AIJob extends BaseEntity {
    
    @Column(nullable = false)
    private UUID ownerId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AIJobStatus status = AIJobStatus.PENDING;
    
    @Column(nullable = false)
    private String filePath;
    
    @Column(nullable = false)
    private String fileName;
    
    @Column(nullable = false)
    private String fileType;
    
    @Column
    private String errorMessage;
    
    @Column
    private Instant processedAt;
    
    @Column
    private UUID draftSaleId;
    
    @Column(columnDefinition = "TEXT")
    private String parsedData;
}

