package com.abhedyam.service.interfaces;

import com.abhedyam.model.CallLog;

import java.util.List;
import java.util.UUID;

public interface ICallLogService {
    CallLog create(CallLog callLog);
    CallLog getById(UUID id);
    List<CallLog> getAll();
    List<CallLog> getByOwnerId(UUID ownerId);
    CallLog update(UUID id, CallLog callLogDetails);
    void delete(UUID id);
}

