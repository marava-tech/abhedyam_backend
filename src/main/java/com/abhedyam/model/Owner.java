package com.abhedyam.model;

import com.abhedyam.model.enums.Subscription;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "owners")
@Getter
@Setter
@PrimaryKeyJoinColumn(name = "id")
public class Owner extends User {
    
    @Column(nullable = false)
    private String businessName;
    
    @Column
    private String email;
    
    @Column
    private String imageUrl;
    
    @Column(nullable = false)
    private Boolean isVerified = false;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Subscription subscription = Subscription.GO;
    
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(columnDefinition = "VARCHAR(36)")
    private UUID upiAccountId;
    
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(columnDefinition = "VARCHAR(36)")
    private UUID userSettingsId;
    
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(columnDefinition = "VARCHAR(36)")
    private UUID locationDetailsId;
}

