package com.abhedyam.service;

import com.abhedyam.dto.PageResponse;
import com.abhedyam.dto.VideoStoreCreateRequest;
import com.abhedyam.dto.VideoStoreResponse;
import com.abhedyam.dto.VideoStoreSearchResult;
import com.abhedyam.dto.VideoStoreUpdateRequest;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.VideoStore;
import com.abhedyam.repository.VideoStoreRepository;
import com.abhedyam.service.interfaces.IVideoStoreService;
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
public class VideoStoreService implements IVideoStoreService {

    private static final int SEARCH_LIMIT = 3;

    private final VideoStoreRepository videoStoreRepository;

    @Override
    @Transactional
    public VideoStore create(VideoStoreCreateRequest request) {
        VideoStore entity = new VideoStore();
        entity.setName(request.getName().trim());
        entity.setVideoUrl(request.getVideoUrl().trim());
        entity.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        entity.setTags(request.getTags() != null ? request.getTags() : List.of());
        entity.setIsActive(true);
        return videoStoreRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public VideoStoreResponse getById(UUID id) {
        VideoStore entity = videoStoreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Video store entry not found"));
        return toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<VideoStoreResponse> list(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<VideoStore> p = videoStoreRepository.findByIsActiveTrue(pageable);
        List<VideoStoreResponse> content = p.getContent().stream().map(this::toResponse).collect(Collectors.toList());
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
    public VideoStore update(UUID id, VideoStoreUpdateRequest request) {
        VideoStore entity = videoStoreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Video store entry not found"));
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            entity.setName(request.getName().trim());
        }
        if (request.getVideoUrl() != null && !request.getVideoUrl().trim().isEmpty()) {
            entity.setVideoUrl(request.getVideoUrl().trim());
        }
        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription().trim().isEmpty() ? null : request.getDescription().trim());
        }
        if (request.getTags() != null) {
            entity.setTags(request.getTags());
        }
        return videoStoreRepository.save(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        VideoStore entity = videoStoreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Video store entry not found"));
        videoStoreRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VideoStoreSearchResult> search(String searchKey) {
        String key = searchKey != null ? searchKey.trim() : "";
        if (key.isEmpty()) {
            return List.of();
        }
        Pageable limit3 = PageRequest.of(0, SEARCH_LIMIT);
        List<VideoStore> byName = videoStoreRepository.findByNameContainingIgnoreCase(key, limit3);
        Set<UUID> seen = new LinkedHashSet<>();
        List<VideoStoreSearchResult> result = new ArrayList<>();
        for (VideoStore v : byName) {
            if (seen.add(v.getId())) {
                result.add(toSearchResult(v));
            }
        }
        if (result.size() < SEARCH_LIMIT) {
            List<VideoStore> byTag = videoStoreRepository.findByTagContainingIgnoreCase(key,
                    PageRequest.of(0, SEARCH_LIMIT * 2));
            for (VideoStore v : byTag) {
                if (seen.add(v.getId())) {
                    result.add(toSearchResult(v));
                    if (result.size() >= SEARCH_LIMIT)
                        break;
                }
            }
        }
        return result.stream().limit(SEARCH_LIMIT).collect(Collectors.toList());
    }

    private VideoStoreResponse toResponse(VideoStore e) {
        VideoStoreResponse r = new VideoStoreResponse();
        r.setId(e.getId());
        r.setName(e.getName());
        r.setTags(e.getTags() != null ? new ArrayList<>(e.getTags()) : List.of());
        r.setVideoUrl(e.getVideoUrl());
        r.setDescription(e.getDescription());
        r.setCreatedAt(e.getCreatedAt());
        r.setUpdatedAt(e.getUpdatedAt());
        return r;
    }

    private VideoStoreSearchResult toSearchResult(VideoStore v) {
        return new VideoStoreSearchResult(
                v.getId(),
                v.getName(),
                v.getTags() != null ? new ArrayList<>(v.getTags()) : List.of(),
                v.getVideoUrl());
    }
}
