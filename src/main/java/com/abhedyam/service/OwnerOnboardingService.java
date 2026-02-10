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
    @Transactional(readOnly = true)
    public List<OwnerOnboardingResponse> getRequestsByOwner(UUID ownerId) {
        List<OwnerOnboardingRequest> requests = requestRepository.findByOwnerId(ownerId);
        return requests.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public com.abhedyam.dto.PageResponse<OwnerOnboardingResponse> getAdminRequests(String search,
            OnboardingStatus status, org.springframework.data.domain.Pageable pageable) {
        org.springframework.data.domain.Page<OwnerOnboardingRequest> page = requestRepository
                .findAll((root, query, cb) -> {
                    java.util.List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();

                    if (status != null) {
                        predicates.add(cb.equal(root.get("status"), status));
                    }

                    if (search != null && !search.trim().isEmpty()) {
                        String likePattern = "%" + search.trim().toLowerCase() + "%";
                        jakarta.persistence.criteria.Join<OwnerOnboardingRequest, Owner> ownerJoin = root.join("owner");

                        jakarta.persistence.criteria.Predicate namePredicate = cb.like(cb.lower(ownerJoin.get("name")),
                                likePattern);
                        jakarta.persistence.criteria.Predicate phonePredicate = cb
                                .like(cb.lower(ownerJoin.get("phone")), likePattern);

                        predicates.add(cb.or(namePredicate, phonePredicate));
                    }

                    return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
                }, pageable);

        List<OwnerOnboardingResponse> content = page.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return new com.abhedyam.dto.PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious());
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
