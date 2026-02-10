package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.dto.ImageStoreCreateRequest;
import com.abhedyam.dto.ImageStoreResponse;
import com.abhedyam.dto.ImageStoreSearchResult;
import com.abhedyam.dto.ImageStoreUpdateRequest;
import com.abhedyam.dto.PageResponse;
import com.abhedyam.model.ImageStore;
import com.abhedyam.service.interfaces.IImageStoreService;
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
@Tag(name = "Image Store", description = "CRUD and search for commonly used images")
public class ImageStoreController {

    private final IImageStoreService imageStoreService;

    @PostMapping("/admin/image-store")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create image store entry (Admin)", description = "Create a new image store entry. Admin key required.")
    public ApiResponse<ImageStore> create(@Valid @RequestBody ImageStoreCreateRequest request) {
        return ApiResponse.success(imageStoreService.create(request));
    }

    @GetMapping("/admin/image-store")
    @Operation(summary = "List image store (Admin)", description = "List image store entries with pagination. Admin key required.")
    public ApiResponse<PageResponse<ImageStoreResponse>> list(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") Integer size) {
        return ApiResponse.success(imageStoreService.list(page, size));
    }

    @PatchMapping("/admin/image-store/{id}")
    @Operation(summary = "Update image store entry (Admin)", description = "Update an image store entry by id. Admin key required.")
    public ApiResponse<ImageStore> update(
            @PathVariable UUID id,
            @Valid @RequestBody ImageStoreUpdateRequest request) {
        return ApiResponse.success(imageStoreService.update(id, request));
    }

    @GetMapping("/image-store/search")
    @Operation(summary = "Search for recommendations (User/JWT)", description = "Search by name first, then by tags; returns up to 3 best matching images. JWT required.")
    public ApiResponse<List<ImageStoreSearchResult>> search(
            @Parameter(description = "Search term (product name or tag)") @RequestParam String searchKey) {
        return ApiResponse.success(imageStoreService.search(searchKey));
    }
}
