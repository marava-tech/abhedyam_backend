package com.abhedyam.model;

import com.abhedyam.model.enums.OnboardingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "owner_onboarding_requests")
@Getter
@Setter
public class OwnerOnboardingRequest extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @Column(nullable = false)
    private String videoUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OnboardingStatus status = OnboardingStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String statusDescription;
}
