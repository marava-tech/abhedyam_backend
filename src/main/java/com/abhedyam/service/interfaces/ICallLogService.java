package com.abhedyam.service.interfaces;

import com.abhedyam.dto.CallLogCreateRequest;
import com.abhedyam.dto.CallLogResponse;
import com.abhedyam.dto.CallLogSyncRequest;
import com.abhedyam.dto.PageResponse;
import com.abhedyam.model.CallLog;

import java.util.List;
import java.util.UUID;

public interface ICallLogService {
    CallLog create(CallLog callLog);
    CallLog createCallLog(CallLogCreateRequest request);
    List<CallLog> syncCallLogs(CallLogSyncRequest request);
    CallLog getById(UUID id);
    List<CallLog> getAll();
    List<CallLog> getByOwnerId(UUID ownerId);
    PageResponse<CallLogResponse> getByCustomerId(UUID customerId, Integer page, Integer size);
    CallLog update(UUID id, CallLog callLogDetails);
    boolean isCallLogSyncEnabled();
}

