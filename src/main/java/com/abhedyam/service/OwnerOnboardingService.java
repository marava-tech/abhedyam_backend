package com.abhedyam.service;

import com.abhedyam.dto.OwnerOnboardingCreateRequest;
import com.abhedyam.dto.OwnerOnboardingResponse;
import com.abhedyam.dto.OwnerOnboardingStatusUpdateRequest;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Owner;
import com.abhedyam.model.OwnerOnboardingRequest;
import com.abhedyam.model.enums.OnboardingStatus;
import com.abhedyam.repository.OwnerOnboardingRequestRepository;
import com.abhedyam.repository.OwnerRepository;
import com.abhedyam.service.interfaces.IOwnerOnboardingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OwnerOnboardingService implements IOwnerOnboardingService {

    private final OwnerOnboardingRequestRepository requestRepository;
    private final OwnerRepository ownerRepository;

    @Override
    @Transactional
    public OwnerOnboardingResponse createRequest(OwnerOnboardingCreateRequest requestDto) {
        Owner owner = ownerRepository.findById(requestDto.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        OwnerOnboardingRequest newRequest = new OwnerOnboardingRequest();
        newRequest.setOwner(owner);
        newRequest.setVideoUrl(requestDto.getVideoUrl());
        newRequest.setDescription(requestDto.getDescription());
        newRequest.setStatus(OnboardingStatus.PENDING);

        OwnerOnboardingRequest savedRequest = requestRepository.save(newRequest);
        return mapToDto(savedRequest);
    }

    @Override
    @Transactional
    public OwnerOnboardingResponse updateStatus(UUID requestId, OwnerOnboardingStatusUpdateRequest statusUpdateDto) {
        // Find existing request
        OwnerOnboardingRequest existingRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        // Update status
        if (statusUpdateDto.getStatus() != null) {
            existingRequest.setStatus(statusUpdateDto.getStatus());
        }

        // Update description if provided
        if (statusUpdateDto.getStatusDescription() != null) {
            existingRequest.setStatusDescription(statusUpdateDto.getStatusDescription());
        }

        OwnerOnboardingRequest savedRequest = requestRepository.save(existingRequest);
        return mapToDto(savedRequest);
    }

    @Override
    public OwnerOnboardingResponse getRequest(UUID requestId) {
        OwnerOnboardingRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));
        return mapToDto(request);
    }

    @Override
    public List<OwnerOnboardingResponse> getRequestsByOwner(UUID ownerId) {
        List<OwnerOnboardingRequest> requests = requestRepository.findByOwnerId(ownerId);
        return requests.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<OwnerOnboardingResponse> getAllRequests(OnboardingStatus status) {
        List<OwnerOnboardingRequest> requests;
        if (status != null) {
            requests = requestRepository.findByStatus(status);
        } else {
            requests = requestRepository.findAll();
        }

        return requests.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private OwnerOnboardingResponse mapToDto(OwnerOnboardingRequest request) {
        OwnerOnboardingResponse dto = new OwnerOnboardingResponse();
        dto.setId(request.getId());
        dto.setOwnerId(request.getOwner().getId());
        dto.setOwnerName(request.getOwner().getName());
        dto.setVideoUrl(request.getVideoUrl());
        dto.setDescription(request.getDescription());
        dto.setStatus(request.getStatus());
        dto.setStatusDescription(request.getStatusDescription());
        dto.setCreatedAt(request.getCreatedAt());
        dto.setUpdatedAt(request.getUpdatedAt());
        return dto;
    }
}
