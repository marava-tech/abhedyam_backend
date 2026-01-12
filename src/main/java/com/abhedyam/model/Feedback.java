package com.abhedyam.model;

import com.abhedyam.model.enums.FeedbackCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "feedbacks")
@Getter
@Setter
public class Feedback extends BaseEntity {
    
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, columnDefinition = "VARCHAR(36)")
    private UUID userId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private FeedbackCategory category;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String issueDescription;
    
    @Column(length = 2048)
    private String imageUrl;
}

