package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.dto.NotificationMarkReadRequest;
import com.abhedyam.dto.NotificationResponse;
import com.abhedyam.model.Notification;
import com.abhedyam.service.interfaces.INotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Notification management endpoints")
public class NotificationController {
    
    private final INotificationService notificationService;
    
    @GetMapping("/me")
    @Operation(summary = "Get my notifications", 
               description = "Get all notifications for the current user (owner or customer). Supports filtering by unread status.")
    public ApiResponse<List<NotificationResponse>> getMyNotifications(
            @Parameter(description = "Filter to show only unread notifications", example = "true")
            @RequestParam(required = false) Boolean unreadOnly) {
        List<Notification> notifications = notificationService.getMyNotifications(unreadOnly);
        List<NotificationResponse> responses = notifications.stream()
            .map(NotificationResponse::fromEntity)
            .collect(Collectors.toList());
        return ApiResponse.success(responses);
    }
    
    @GetMapping("/{id}")
    public ApiResponse<NotificationResponse> getById(@PathVariable UUID id) {
        Notification notification = notificationService.getById(id);
        return ApiResponse.success(NotificationResponse.fromEntity(notification));
    }
    
    @PatchMapping("/{id}/read")
    public ApiResponse<NotificationResponse> markAsRead(@PathVariable UUID id) {
        Notification notification = notificationService.markAsRead(id);
        return ApiResponse.success(NotificationResponse.fromEntity(notification));
    }
    
    @PostMapping("/mark-read")
    public ApiResponse<List<NotificationResponse>> markMultipleAsRead(@Valid @RequestBody NotificationMarkReadRequest request) {
        List<Notification> notifications = notificationService.markMultipleAsRead(request);
        List<NotificationResponse> responses = notifications.stream()
            .map(NotificationResponse::fromEntity)
            .collect(Collectors.toList());
        return ApiResponse.success(responses);
    }
}

