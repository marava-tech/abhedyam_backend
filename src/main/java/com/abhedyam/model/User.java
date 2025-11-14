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
    
    @Column
    private String phone;
    
    @Column(unique = true)
    private String phoneNormalized;
    
    @Column(unique = true)
    private String email;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType type;
    
    @Column
    private String imageUrl;
}

