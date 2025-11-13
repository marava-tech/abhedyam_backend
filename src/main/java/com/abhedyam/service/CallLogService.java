package com.abhedyam.service;

import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.CallLog;
import com.abhedyam.repository.CallLogRepository;
import com.abhedyam.service.interfaces.ICallLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CallLogService implements ICallLogService {
    
    private final CallLogRepository callLogRepository;
    
    public CallLog create(CallLog callLog) {
        return callLogRepository.save(callLog);
    }
    
    public CallLog getById(UUID id) {
        return callLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CallLog not found with id: " + id));
    }
    
    public List<CallLog> getAll() {
        return callLogRepository.findAll();
    }
    
    public List<CallLog> getByOwnerId(UUID ownerId) {
        return callLogRepository.findByOwnerId(ownerId);
    }
    
    @Transactional
    public CallLog update(UUID id, CallLog callLogDetails) {
        CallLog callLog = getById(id);
        if (callLogDetails.getEndTime() != null) callLog.setEndTime(callLogDetails.getEndTime());
        if (callLogDetails.getDurationSeconds() != null) callLog.setDurationSeconds(callLogDetails.getDurationSeconds());
        return callLogRepository.save(callLog);
    }
    
    @Transactional
    public void delete(UUID id) {
        CallLog callLog = getById(id);
        callLog.setDeletedAt(Instant.now());
        callLog.setIsActive(false);
        callLogRepository.save(callLog);
    }
}

