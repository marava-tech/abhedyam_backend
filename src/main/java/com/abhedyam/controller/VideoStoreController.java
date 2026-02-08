package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.dto.VideoStoreCreateRequest;
import com.abhedyam.dto.VideoStoreResponse;
import com.abhedyam.dto.VideoStoreSearchResult;
import com.abhedyam.dto.VideoStoreUpdateRequest;
import com.abhedyam.dto.PageResponse;
import com.abhedyam.model.VideoStore;
import com.abhedyam.service.interfaces.IVideoStoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Video Store", description = "CRUD and search for commonly used videos")
public class VideoStoreController {

    private final IVideoStoreService videoStoreService;

    @PostMapping("/admin/video-store")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create video store entry (Admin)", description = "Create a new video store entry. Admin key required.")
    public ApiResponse<VideoStore> create(@Valid @RequestBody VideoStoreCreateRequest request) {
        return ApiResponse.success(videoStoreService.create(request));
    }

    @GetMapping("/admin/video-store/{id}")
    @Operation(summary = "Get by ID (Admin)", description = "Get video store entry by id. Admin key required.")
    public ApiResponse<VideoStoreResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(videoStoreService.getById(id));
    }

    @GetMapping("/admin/video-store")
    @Operation(summary = "List video store (Admin)", description = "List video store entries with pagination. Admin key required.")
    public ApiResponse<PageResponse<VideoStoreResponse>> list(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") Integer size) {
        return ApiResponse.success(videoStoreService.list(page, size));
    }

    @PatchMapping("/admin/video-store/{id}")
    @Operation(summary = "Update video store entry (Admin)", description = "Update a video store entry by id. Admin key required.")
    public ApiResponse<VideoStore> update(
            @PathVariable UUID id,
            @Valid @RequestBody VideoStoreUpdateRequest request) {
        return ApiResponse.success(videoStoreService.update(id, request));
    }

    @DeleteMapping("/admin/video-store/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete video store entry (Admin)", description = "Delete a video store entry by id. Admin key required.")
    public void delete(@PathVariable UUID id) {
        videoStoreService.delete(id);
    }

    @GetMapping("/video-store/search")
    @Operation(summary = "Search for recommendations (User/JWT)", description = "Search by name first, then by tags; returns up to 3 best matching videos. JWT required.")
    public ApiResponse<List<VideoStoreSearchResult>> search(
            @Parameter(description = "Search term (video name or tag)") @RequestParam String searchKey) {
        return ApiResponse.success(videoStoreService.search(searchKey));
    }
}
