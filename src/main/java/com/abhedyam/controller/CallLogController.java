package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.dto.CallLogCreateRequest;
import com.abhedyam.dto.CallLogResponse;
import com.abhedyam.dto.CallLogSyncRequest;
import com.abhedyam.dto.PageResponse;
import com.abhedyam.model.CallLog;
import com.abhedyam.service.interfaces.ICallLogService;
import com.abhedyam.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/call-logs")
@RequiredArgsConstructor
public class CallLogController {
    
    private final ICallLogService callLogService;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CallLogResponse> create(@Valid @RequestBody CallLogCreateRequest request) {
        CallLog callLog = callLogService.createCallLog(request);
        if (callLog == null) {
            return ApiResponse.success(null);
        }
        return ApiResponse.success(CallLogResponse.fromEntity(callLog));
    }
    
    @PostMapping("/sync")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<List<CallLogResponse>> syncCallLogs(@Valid @RequestBody CallLogSyncRequest request) {
        List<CallLog> callLogs = callLogService.syncCallLogs(request);
        List<CallLogResponse> responses = callLogs.stream()
            .map(CallLogResponse::fromEntity)
            .collect(Collectors.toList());
        return ApiResponse.success(responses);
    }
    
    @GetMapping("/{id}")
    public ApiResponse<CallLogResponse> getById(@PathVariable UUID id) {
        CallLog callLog = callLogService.getById(id);
        return ApiResponse.success(CallLogResponse.fromEntity(callLog));
    }
    
    @GetMapping("/my-logs")
    public ApiResponse<List<CallLogResponse>> getMyCallLogs() {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        List<CallLog> callLogs = callLogService.getByOwnerId(ownerId);
        List<CallLogResponse> responses = callLogs.stream()
            .filter(log -> log.getIsActive() != null && log.getIsActive())
            .map(CallLogResponse::fromEntity)
            .collect(Collectors.toList());
        return ApiResponse.success(responses);
    }
    
    @GetMapping("/customer/{customerId}")
    public ApiResponse<PageResponse<CallLogResponse>> getByCustomerId(
            @PathVariable UUID customerId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ApiResponse.success(callLogService.getByCustomerId(customerId, page, size));
    }
}

