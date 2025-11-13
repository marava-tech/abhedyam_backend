package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.dto.ReminderCreateRequest;
import com.abhedyam.model.Reminder;
import com.abhedyam.service.interfaces.IReminderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reminders")
@RequiredArgsConstructor
public class ReminderController {
    
    private final IReminderService reminderService;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Reminder> create(@Valid @RequestBody ReminderCreateRequest request) {
        return ApiResponse.success(reminderService.create(request));
    }
    
    @GetMapping("/{id}")
    public ApiResponse<Reminder> getById(@PathVariable UUID id) {
        return ApiResponse.success(reminderService.getById(id));
    }
    
    @GetMapping("/customer/{customerId}")
    public ApiResponse<List<Reminder>> getByCustomerId(@PathVariable UUID customerId) {
        return ApiResponse.success(reminderService.getByCustomerId(customerId));
    }
    
    @GetMapping("/pending")
    public ApiResponse<List<Reminder>> getPendingReminders() {
        return ApiResponse.success(reminderService.getPendingReminders());
    }
    
    @PutMapping("/{id}")
    public ApiResponse<Reminder> update(@PathVariable UUID id, @Valid @RequestBody ReminderCreateRequest request) {
        return ApiResponse.success(reminderService.update(id, request));
    }
    
    @PatchMapping("/{id}/mark-sent")
    public ApiResponse<Reminder> markAsSent(@PathVariable UUID id) {
        return ApiResponse.success(reminderService.markAsSent(id));
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        reminderService.delete(id);
        return ApiResponse.success(null);
    }
}

