package com.abhedyam.model;

import com.abhedyam.model.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
public class Subscription extends BaseEntity {
    
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, columnDefinition = "VARCHAR(36)")
    private UUID ownerId;
    
    @Column(nullable = false, unique = true)
    private String razorpaySubscriptionId;
    
    @Column
    private String razorpayPlanId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status = SubscriptionStatus.PENDING;
    
    @Column
    private Instant validTill;
    
    @Column
    private Instant activatedAt;
    
    @Column
    private Instant expiredAt;
    
    @Column
    private Instant cancelledAt;
}

