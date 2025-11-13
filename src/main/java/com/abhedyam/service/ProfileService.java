package com.abhedyam.service;

import com.abhedyam.dto.ProfileUpdateRequest;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Owner;
import com.abhedyam.model.User;
import com.abhedyam.repository.OwnerRepository;
import com.abhedyam.repository.UserRepository;
import com.abhedyam.service.interfaces.IProfileService;
import com.abhedyam.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService implements IProfileService {
    
    private final UserRepository userRepository;
    private final OwnerRepository ownerRepository;
    
    @Override
    public User getCurrentUserProfile() {
        UUID userId = SecurityUtil.getCurrentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
    
    @Override
    public Owner getCurrentOwnerProfile() {
        UUID userId = SecurityUtil.getCurrentUserId();
        return ownerRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
    }
    
    @Override
    @Transactional
    public User updateProfile(ProfileUpdateRequest request) {
        UUID userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getImageUrl() != null) {
            user.setImageUrl(request.getImageUrl());
        }
        
        User updatedUser = userRepository.save(user);
        
        Owner owner = ownerRepository.findById(userId).orElse(null);
        if (owner != null && request.getBusinessName() != null) {
            owner.setBusinessName(request.getBusinessName());
            ownerRepository.save(owner);
        }
        
        return updatedUser;
    }
}

