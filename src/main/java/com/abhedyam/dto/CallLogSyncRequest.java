package com.abhedyam.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CallLogSyncRequest {
    @NotEmpty(message = "At least one call log is required")
    @Valid
    private List<CallLogCreateRequest> callLogs;
}

