package com.abhedyam.service;

import com.abhedyam.dto.LocationDetailsCreateRequest;
import com.abhedyam.dto.LocationDetailsResponse;
import com.abhedyam.dto.LocationDetailsUpdateRequest;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.LocationDetails;
import com.abhedyam.model.Owner;
import com.abhedyam.repository.LocationDetailsRepository;
import com.abhedyam.repository.OwnerRepository;
import com.abhedyam.service.interfaces.ILocationDetailsService;
import com.abhedyam.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationDetailsService implements ILocationDetailsService {
    
    private final LocationDetailsRepository locationDetailsRepository;
    private final OwnerRepository ownerRepository;
    
    @Transactional
    public LocationDetailsResponse create(LocationDetailsCreateRequest request) {
        UUID userId = SecurityUtil.getCurrentUserId();
        LocationDetails locationDetails = new LocationDetails();
        locationDetails.setId(userId);
        locationDetails.setUserId(userId);
        locationDetails.setLatitude(request.getLatitude());
        locationDetails.setLongitude(request.getLongitude());
        locationDetails.setVillage(request.getVillage());
        
        LocationDetails saved = locationDetailsRepository.save(locationDetails);
        
        Owner owner = ownerRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
        owner.setLocationDetailsId(saved.getId());
        ownerRepository.save(owner);
        
        return toResponse(saved);
    }
    
    public LocationDetailsResponse getById(UUID id) {
        LocationDetails location = locationDetailsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LocationDetails not found with id: " + id));
        return toResponse(location);
    }
    
    @Transactional
    public LocationDetailsResponse getCurrentUserLocation() {
        UUID userId = SecurityUtil.getCurrentUserId();
        LocationDetails location = locationDetailsRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("LocationDetails not found for current user"));
        return toResponse(location);
    }
    
    public List<LocationDetailsResponse> getAll() {
        return locationDetailsRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public LocationDetailsResponse update(UUID id, LocationDetailsUpdateRequest request) {
        UUID userId = SecurityUtil.getCurrentUserId();
        LocationDetails location = locationDetailsRepository.findById(userId)
                .orElseGet(() -> {
                    LocationDetails newLocation = new LocationDetails();
                    newLocation.setId(userId);
                    newLocation.setUserId(userId);
                    return newLocation;
                });
        
        if (request.getLatitude() != null) location.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) location.setLongitude(request.getLongitude());
        if (request.getVillage() != null) location.setVillage(request.getVillage());
        
        return toResponse(locationDetailsRepository.save(location));
    }
    
    @Transactional
    public LocationDetailsResponse updateCurrentUserLocation(LocationDetailsUpdateRequest request) {
        UUID userId = SecurityUtil.getCurrentUserId();
        LocationDetails location = locationDetailsRepository.findById(userId)
                .orElseGet(() -> {
                    LocationDetails newLocation = new LocationDetails();
                    newLocation.setId(userId);
                    newLocation.setUserId(userId);
                    return newLocation;
                });
        
        if (request.getLatitude() != null) location.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) location.setLongitude(request.getLongitude());
        if (request.getVillage() != null) location.setVillage(request.getVillage());
        
        LocationDetails saved = locationDetailsRepository.save(location);
        
        Owner owner = ownerRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
        owner.setLocationDetailsId(saved.getId());
        ownerRepository.save(owner);
        
        return toResponse(saved);
    }
    
    private LocationDetailsResponse toResponse(LocationDetails location) {
        LocationDetailsResponse response = new LocationDetailsResponse();
        response.setId(location.getId());
        response.setLatitude(location.getLatitude());
        response.setLongitude(location.getLongitude());
        response.setVillage(location.getVillage());
        response.setCreatedAt(location.getCreatedAt());
        response.setUpdatedAt(location.getUpdatedAt());
        return response;
    }
    
    @Transactional
    public void delete(UUID id) {
        LocationDetails location = locationDetailsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LocationDetails not found with id: " + id));
        location.setDeletedAt(Instant.now());
        location.setIsActive(false);
        locationDetailsRepository.save(location);
    }
    
    @Transactional
    public void deleteCurrentUserLocation() {
        UUID userId = SecurityUtil.getCurrentUserId();
        LocationDetails location = locationDetailsRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("LocationDetails not found for current user"));
        location.setDeletedAt(Instant.now());
        location.setIsActive(false);
        locationDetailsRepository.save(location);
    }
}

