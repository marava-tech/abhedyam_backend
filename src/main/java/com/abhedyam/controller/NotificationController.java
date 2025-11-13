package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.model.Notification;
import com.abhedyam.service.interfaces.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    
    private final INotificationService notificationService;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Notification> create(@RequestBody Notification notification) {
        return ApiResponse.success(notificationService.create(notification));
    }
    
    @GetMapping("/{id}")
    public ApiResponse<Notification> getById(@PathVariable UUID id) {
        return ApiResponse.success(notificationService.getById(id));
    }
    
    @GetMapping
    public ApiResponse<List<Notification>> getAll() {
        return ApiResponse.success(notificationService.getAll());
    }
    
    @GetMapping("/owner/{ownerId}")
    public ApiResponse<List<Notification>> getByOwnerId(@PathVariable UUID ownerId) {
        return ApiResponse.success(notificationService.getByOwnerId(ownerId));
    }
    
    @GetMapping("/user/{userId}")
    public ApiResponse<List<Notification>> getByUserId(@PathVariable UUID userId) {
        return ApiResponse.success(notificationService.getByUserId(userId));
    }
    
    @PutMapping("/{id}")
    public ApiResponse<Notification> update(@PathVariable UUID id, @RequestBody Notification notification) {
        return ApiResponse.success(notificationService.update(id, notification));
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        notificationService.delete(id);
        return ApiResponse.success(null);
    }
}

