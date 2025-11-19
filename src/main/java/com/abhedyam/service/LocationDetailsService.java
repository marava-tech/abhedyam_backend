package com.abhedyam.service;

import com.abhedyam.dto.CustomerLocationRequest;
import com.abhedyam.dto.CustomerLocationResponse;
import com.abhedyam.dto.LocationDetailsCreateRequest;
import com.abhedyam.dto.LocationDetailsResponse;
import com.abhedyam.dto.LocationDetailsUpdateRequest;
import com.abhedyam.dto.VillageSearchResult;
import com.abhedyam.util.DistanceUtil;
import com.abhedyam.exception.BusinessException;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Customer;
import com.abhedyam.model.LocationDetails;
import com.abhedyam.repository.CustomerRepository;
import com.abhedyam.repository.LocationDetailsRepository;
import com.abhedyam.service.interfaces.ILocationDetailsService;
import com.abhedyam.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationDetailsService implements ILocationDetailsService {
    
    private final LocationDetailsRepository locationDetailsRepository;
    private final CustomerRepository customerRepository;
    
    @Transactional
    public LocationDetailsResponse create(LocationDetailsCreateRequest request) {
        UUID userId = SecurityUtil.getCurrentUserId();
        LocationDetails locationDetails = new LocationDetails();
        locationDetails.setId(userId);
        locationDetails.setUserId(userId);
        locationDetails.setLatitude(request.getLatitude());
        locationDetails.setLongitude(request.getLongitude());
        locationDetails.setVillage(request.getVillage());
        locationDetails.setAddressText(request.getAddressText());
        
        LocationDetails saved = locationDetailsRepository.save(locationDetails);
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
        LocationDetails location = locationDetailsRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("LocationDetails not found for current user"));
        return toResponse(location);
    }
    
    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public LocationDetailsResponse getCustomerLocation(UUID customerId) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        
        if (customer.getOwnerId() == null || !customer.getOwnerId().equals(ownerId)) {
            throw new BusinessException("UNAUTHORIZED", "You don't have access to this customer");
        }
        
        LocationDetails location = locationDetailsRepository.findByUserId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("LocationDetails not found for customer"));
        
        return toResponse(location);
    }
    
    public List<LocationDetailsResponse> getAll() {
        return locationDetailsRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<VillageSearchResult> searchVillagesByName(String name) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        List<String> villages = locationDetailsRepository.findDistinctVillagesByNameContainingIgnoreCaseAndOwnerId(name, ownerId);
        return villages.stream()
            .map(VillageSearchResult::new)
            .toList();
    }
    
    @Transactional
    public LocationDetailsResponse update(UUID id, LocationDetailsUpdateRequest request) {
        UUID userId = SecurityUtil.getCurrentUserId();
        LocationDetails location = locationDetailsRepository.findByUserId(userId)
                .orElseGet(() -> {
                    LocationDetails newLocation = new LocationDetails();
                    newLocation.setId(userId);
                    newLocation.setUserId(userId);
                    return newLocation;
                });
        
        if (request.getLatitude() != null) location.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) location.setLongitude(request.getLongitude());
        if (request.getVillage() != null) location.setVillage(request.getVillage());
        if (request.getAddressText() != null) location.setAddressText(request.getAddressText());
        
        return toResponse(locationDetailsRepository.save(location));
    }
    
    @Transactional
    public LocationDetailsResponse updateCurrentUserLocation(LocationDetailsUpdateRequest request) {
        UUID userId = SecurityUtil.getCurrentUserId();
        LocationDetails location = locationDetailsRepository.findByUserId(userId)
                .orElseGet(() -> {
                    LocationDetails newLocation = new LocationDetails();
                    newLocation.setId(userId);
                    newLocation.setUserId(userId);
                    return newLocation;
                });
        
        if (request.getLatitude() != null) location.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) location.setLongitude(request.getLongitude());
        if (request.getVillage() != null) location.setVillage(request.getVillage());
        if (request.getAddressText() != null) location.setAddressText(request.getAddressText());
        
        LocationDetails saved = locationDetailsRepository.save(location);
        return toResponse(saved);
    }
    
    @Override
    @Transactional
    public LocationDetailsResponse updateCustomerLocation(UUID customerId, LocationDetailsUpdateRequest request) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        
        if (customer.getOwnerId() == null || !customer.getOwnerId().equals(ownerId)) {
            throw new BusinessException("UNAUTHORIZED", "You don't have access to this customer");
        }
        
        LocationDetails location = locationDetailsRepository.findByUserId(customerId).orElse(null);
        
        if (location == null) {
            if (request.getLatitude() == null || request.getLongitude() == null) {
                throw new BusinessException("MISSING_REQUIRED_FIELDS", "Latitude and longitude are required when creating location");
            }
            location = new LocationDetails();
            location.setId(customerId);
            location.setUserId(customerId);
            location.setLatitude(request.getLatitude());
            location.setLongitude(request.getLongitude());
            if (request.getVillage() != null) {
                location.setVillage(request.getVillage());
            }
            if (request.getAddressText() != null) {
                location.setAddressText(request.getAddressText());
            }
        } else {
            if (request.getLatitude() != null) location.setLatitude(request.getLatitude());
            if (request.getLongitude() != null) location.setLongitude(request.getLongitude());
            if (request.getVillage() != null) location.setVillage(request.getVillage());
            if (request.getAddressText() != null) location.setAddressText(request.getAddressText());
        }
        
        LocationDetails saved = locationDetailsRepository.save(location);
        return toResponse(saved);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CustomerLocationResponse> getCustomerLocations(CustomerLocationRequest request) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        
        List<LocationDetails> locations = locationDetailsRepository.findCustomerLocationsByCustomerIds(
            ownerId, request.getCustomerIds());
        
        List<CustomerLocationResponse> responses = locations.stream()
            .filter(location -> location.getLatitude() != null && location.getLongitude() != null)
            .map(location -> {
                Customer customer = customerRepository.findById(location.getUserId()).orElse(null);
                if (customer == null) {
                    return null;
                }
                return new CustomerLocationResponse(
                    location.getUserId(),
                    customer.getName(),
                    location.getLatitude(),
                    location.getLongitude()
                );
            })
            .filter(response -> response != null)
            .collect(Collectors.toList());
        
        if (request.getCurrentLat() != null && request.getCurrentLng() != null) {
            responses.sort((r1, r2) -> {
                double distance1 = DistanceUtil.calculateDistanceKm(
                    request.getCurrentLat(), request.getCurrentLng(),
                    r1.getLat(), r1.getLng());
                double distance2 = DistanceUtil.calculateDistanceKm(
                    request.getCurrentLat(), request.getCurrentLng(),
                    r2.getLat(), r2.getLng());
                return Double.compare(distance1, distance2);
            });
        }
        
        return responses;
    }
    
    private LocationDetailsResponse toResponse(LocationDetails location) {
        LocationDetailsResponse response = new LocationDetailsResponse();
        response.setId(location.getId());
        response.setLatitude(location.getLatitude());
        response.setLongitude(location.getLongitude());
        response.setVillage(location.getVillage());
        response.setAddressText(location.getAddressText());
        response.setCreatedAt(location.getCreatedAt());
        response.setUpdatedAt(location.getUpdatedAt());
        return response;
    }
}

