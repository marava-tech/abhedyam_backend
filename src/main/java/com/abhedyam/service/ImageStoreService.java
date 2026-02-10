package com.abhedyam.service;

import com.abhedyam.dto.ImageStoreCreateRequest;
import com.abhedyam.dto.ImageStoreResponse;
import com.abhedyam.dto.ImageStoreSearchResult;
import com.abhedyam.dto.ImageStoreUpdateRequest;
import com.abhedyam.dto.PageResponse;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.ImageStore;
import com.abhedyam.repository.ImageStoreRepository;
import com.abhedyam.service.interfaces.IImageStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageStoreService implements IImageStoreService {

    private static final int SEARCH_LIMIT = 3;

    private final ImageStoreRepository imageStoreRepository;

    @Override
    @Transactional
    public ImageStore create(ImageStoreCreateRequest request) {
        ImageStore entity = new ImageStore();
        entity.setName(request.getName().trim());
        entity.setImageUrl(request.getImageUrl().trim());
        entity.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        entity.setTags(request.getTags() != null ? request.getTags() : List.of());
        entity.setIsActive(true);
        return imageStoreRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ImageStoreResponse> list(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ImageStore> p = imageStoreRepository.findByIsActiveTrue(pageable);
        List<ImageStoreResponse> content = p.getContent().stream().map(this::toResponse).collect(Collectors.toList());
        return new PageResponse<>(
                content,
                p.getNumber(),
                p.getSize(),
                p.getTotalElements(),
                p.getTotalPages(),
                p.hasNext(),
                p.hasPrevious());
    }

    @Override
    @Transactional
    public ImageStore update(UUID id, ImageStoreUpdateRequest request) {
        ImageStore entity = imageStoreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image store entry not found"));
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            entity.setName(request.getName().trim());
        }
        if (request.getImageUrl() != null && !request.getImageUrl().trim().isEmpty()) {
            entity.setImageUrl(request.getImageUrl().trim());
        }
        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription().trim().isEmpty() ? null : request.getDescription().trim());
        }
        if (request.getTags() != null) {
            entity.setTags(request.getTags());
        }
        return imageStoreRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ImageStoreSearchResult> search(String searchKey) {
        String key = searchKey != null ? searchKey.trim() : "";
        if (key.isEmpty()) {
            return List.of();
        }
        Pageable limit3 = PageRequest.of(0, SEARCH_LIMIT);
        List<ImageStore> byName = imageStoreRepository.findByNameContainingIgnoreCase(key, limit3);
        Set<UUID> seen = new LinkedHashSet<>();
        List<ImageStoreSearchResult> result = new ArrayList<>();
        for (ImageStore i : byName) {
            if (seen.add(i.getId())) {
                result.add(toSearchResult(i));
            }
        }
        if (result.size() < SEARCH_LIMIT) {
            List<ImageStore> byTag = imageStoreRepository.findByTagContainingIgnoreCase(key,
                    PageRequest.of(0, SEARCH_LIMIT * 2));
            for (ImageStore i : byTag) {
                if (seen.add(i.getId())) {
                    result.add(toSearchResult(i));
                    if (result.size() >= SEARCH_LIMIT)
                        break;
                }
            }
        }
        return result.stream().limit(SEARCH_LIMIT).collect(Collectors.toList());
    }

    private ImageStoreResponse toResponse(ImageStore e) {
        ImageStoreResponse r = new ImageStoreResponse();
        r.setId(e.getId());
        r.setName(e.getName());
        r.setTags(e.getTags() != null ? new ArrayList<>(e.getTags()) : List.of());
        r.setImageUrl(e.getImageUrl());
        r.setDescription(e.getDescription());
        r.setCreatedAt(e.getCreatedAt());
        r.setUpdatedAt(e.getUpdatedAt());
        return r;
    }

    private ImageStoreSearchResult toSearchResult(ImageStore e) {
        return new ImageStoreSearchResult(
                e.getId(),
                e.getName(),
                e.getTags() != null ? new ArrayList<>(e.getTags()) : List.of(),
                e.getImageUrl());
    }
}
