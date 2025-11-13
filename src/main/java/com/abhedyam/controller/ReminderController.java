package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.model.Reminder;
import com.abhedyam.service.interfaces.IReminderService;
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
    public ApiResponse<Reminder> create(@RequestBody Reminder reminder) {
        return ApiResponse.success(reminderService.create(reminder));
    }
    
    @GetMapping("/{id}")
    public ApiResponse<Reminder> getById(@PathVariable UUID id) {
        return ApiResponse.success(reminderService.getById(id));
    }
    
    @GetMapping
    public ApiResponse<List<Reminder>> getAll() {
        return ApiResponse.success(reminderService.getAll());
    }
    
    @GetMapping("/owner/{ownerId}")
    public ApiResponse<List<Reminder>> getByOwnerId(@PathVariable UUID ownerId) {
        return ApiResponse.success(reminderService.getByOwnerId(ownerId));
    }
    
    @GetMapping("/customer/{customerId}")
    public ApiResponse<List<Reminder>> getByCustomerId(@PathVariable UUID customerId) {
        return ApiResponse.success(reminderService.getByCustomerId(customerId));
    }
    
    @PutMapping("/{id}")
    public ApiResponse<Reminder> update(@PathVariable UUID id, @RequestBody Reminder reminder) {
        return ApiResponse.success(reminderService.update(id, reminder));
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        reminderService.delete(id);
        return ApiResponse.success(null);
    }
}

