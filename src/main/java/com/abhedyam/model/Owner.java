package com.abhedyam.model;

import com.abhedyam.model.enums.Subscription;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "owners")
@Getter
@Setter
@PrimaryKeyJoinColumn(name = "id")
public class Owner extends User {
    
    @Column(nullable = false)
    private String businessName;
    
    @Column(nullable = false)
    private Boolean isVerified = false;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Subscription subscription = Subscription.GO;
}

