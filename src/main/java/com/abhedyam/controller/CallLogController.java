package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.model.CallLog;
import com.abhedyam.service.interfaces.ICallLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/call-logs")
@RequiredArgsConstructor
public class CallLogController {
    
    private final ICallLogService callLogService;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CallLog> create(@RequestBody CallLog callLog) {
        return ApiResponse.success(callLogService.create(callLog));
    }
    
    @GetMapping("/{id}")
    public ApiResponse<CallLog> getById(@PathVariable UUID id) {
        return ApiResponse.success(callLogService.getById(id));
    }
    
    @GetMapping
    public ApiResponse<List<CallLog>> getAll() {
        return ApiResponse.success(callLogService.getAll());
    }
    
    @GetMapping("/owner/{ownerId}")
    public ApiResponse<List<CallLog>> getByOwnerId(@PathVariable UUID ownerId) {
        return ApiResponse.success(callLogService.getByOwnerId(ownerId));
    }
    
    @PutMapping("/{id}")
    public ApiResponse<CallLog> update(@PathVariable UUID id, @RequestBody CallLog callLog) {
        return ApiResponse.success(callLogService.update(id, callLog));
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        callLogService.delete(id);
        return ApiResponse.success(null);
    }
}

