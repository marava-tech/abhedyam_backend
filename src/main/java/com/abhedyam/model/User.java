package com.abhedyam.model;

import com.abhedyam.model.enums.UserType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
public class User extends BaseEntity {
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String phone;
    
    @Column(nullable = false, unique = true)
    private String phoneNormalized;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType type;
    
    @Column
    private String imageUrl;
}

