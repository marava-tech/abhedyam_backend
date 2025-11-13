package com.abhedyam.service;

import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.LocationDetails;
import com.abhedyam.repository.LocationDetailsRepository;
import com.abhedyam.service.interfaces.ILocationDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocationDetailsService implements ILocationDetailsService {
    
    private final LocationDetailsRepository locationDetailsRepository;
    
    public LocationDetails create(LocationDetails locationDetails) {
        return locationDetailsRepository.save(locationDetails);
    }
    
    public LocationDetails getById(UUID id) {
        return locationDetailsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LocationDetails not found with id: " + id));
    }
    
    public List<LocationDetails> getAll() {
        return locationDetailsRepository.findAll();
    }
    
    @Transactional
    public LocationDetails update(UUID id, LocationDetails locationDetails) {
        LocationDetails location = getById(id);
        if (locationDetails.getLatitude() != null) location.setLatitude(locationDetails.getLatitude());
        if (locationDetails.getLongitude() != null) location.setLongitude(locationDetails.getLongitude());
        if (locationDetails.getVillage() != null) location.setVillage(locationDetails.getVillage());
        return locationDetailsRepository.save(location);
    }
    
    @Transactional
    public void delete(UUID id) {
        LocationDetails location = getById(id);
        location.setDeletedAt(Instant.now());
        location.setIsActive(false);
        locationDetailsRepository.save(location);
    }
}

