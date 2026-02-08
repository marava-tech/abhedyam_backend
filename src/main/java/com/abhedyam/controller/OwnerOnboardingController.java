package com.abhedyam.controller;

import com.abhedyam.dto.PageResponse;
import com.abhedyam.dto.ApiResponse;
import com.abhedyam.dto.OwnerOnboardingCreateRequest;
import com.abhedyam.dto.OwnerOnboardingResponse;
import com.abhedyam.dto.OwnerOnboardingStatusUpdateRequest;
import com.abhedyam.model.enums.OnboardingStatus;
import com.abhedyam.service.interfaces.IOwnerOnboardingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Owner Onboarding", description = "Owner onboarding request management APIs")
public class OwnerOnboardingController {

    private final IOwnerOnboardingService onboardingService;

    @PostMapping("/owner-onboarding")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create onboarding request", description = "Submit a new onboarding request with video URL and description")
    public ApiResponse<OwnerOnboardingResponse> create(@Valid @RequestBody OwnerOnboardingCreateRequest request) {
        return ApiResponse.success(onboardingService.createRequest(request));
    }

    @PatchMapping("/owner-onboarding/{id}/status")
    @Operation(summary = "Update request status", description = "Update the status of an onboarding request (Admin only)")
    public ApiResponse<OwnerOnboardingResponse> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody OwnerOnboardingStatusUpdateRequest request) {
        return ApiResponse.success(onboardingService.updateStatus(id, request));
    }

    @GetMapping("/owner-onboarding/{id}")
    @Operation(summary = "Get request by ID", description = "Get details of a specific onboarding request")
    public ApiResponse<OwnerOnboardingResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(onboardingService.getRequest(id));
    }

    @GetMapping("/owner-onboarding/owner/{ownerId}")
    @Operation(summary = "Get requests by Owner", description = "Get all onboarding requests submitted by a specific owner")
    public ApiResponse<List<OwnerOnboardingResponse>> getByOwner(@PathVariable UUID ownerId) {
        return ApiResponse.success(onboardingService.getRequestsByOwner(ownerId));
    }

    @GetMapping("/owner-onboarding")
    @Operation(summary = "Get all requests", description = "Get all onboarding requests (optionally filtered by status)")
    public ApiResponse<List<OwnerOnboardingResponse>> getAll(
            @RequestParam(required = false) OnboardingStatus status) {
        return ApiResponse.success(onboardingService.getAllRequests(status));
    }

    @GetMapping("/admin/owner-onboarding")
    @Operation(summary = "Get all requests (Admin)", description = "Get all requests with search, filter, sort and pagination")
    public ApiResponse<PageResponse<OwnerOnboardingResponse>> getAdminRequests(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) OnboardingStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        return ApiResponse.success(onboardingService.getAdminRequests(search, status, pageRequest));
    }
}
