package com.abhedyam.service;

import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Owner;
import com.abhedyam.repository.OwnerRepository;
import com.abhedyam.service.interfaces.IOwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OwnerService implements IOwnerService {
    
    private final OwnerRepository ownerRepository;
    
    public Owner create(Owner owner) {
        return ownerRepository.save(owner);
    }
    
    public Owner getById(UUID id) {
        return ownerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + id));
    }
    
    public List<Owner> getAll() {
        return ownerRepository.findAll();
    }
    
    @Transactional
    public Owner update(UUID id, Owner ownerDetails) {
        Owner owner = getById(id);
        if (ownerDetails.getBusinessName() != null) owner.setBusinessName(ownerDetails.getBusinessName());
        if (ownerDetails.getEmail() != null) owner.setEmail(ownerDetails.getEmail());
        if (ownerDetails.getImageUrl() != null) owner.setImageUrl(ownerDetails.getImageUrl());
        if (ownerDetails.getIsVerified() != null) owner.setIsVerified(ownerDetails.getIsVerified());
        if (ownerDetails.getSubscription() != null) owner.setSubscription(ownerDetails.getSubscription());
        if (ownerDetails.getUpiAccountId() != null) owner.setUpiAccountId(ownerDetails.getUpiAccountId());
        if (ownerDetails.getUserSettingsId() != null) owner.setUserSettingsId(ownerDetails.getUserSettingsId());
        if (ownerDetails.getLocationDetailsId() != null) owner.setLocationDetailsId(ownerDetails.getLocationDetailsId());
        return ownerRepository.save(owner);
    }
    
    @Transactional
    public void delete(UUID id) {
        Owner owner = getById(id);
        owner.setDeletedAt(Instant.now());
        owner.setIsActive(false);
        ownerRepository.save(owner);
    }
}

