package com.abhedyam.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "customers")
@Getter
@Setter
@PrimaryKeyJoinColumn(name = "id")
public class Customer extends User {
    @Column
    private UUID ownerId;

}

