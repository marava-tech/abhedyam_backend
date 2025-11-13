package com.abhedyam.service;

import com.abhedyam.dto.CallLogCreateRequest;
import com.abhedyam.dto.CallLogSyncRequest;
import com.abhedyam.exception.BusinessException;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.CallLog;
import com.abhedyam.model.Customer;
import com.abhedyam.model.enums.CallDirection;
import com.abhedyam.repository.CallLogRepository;
import com.abhedyam.repository.CustomerRepository;
import com.abhedyam.service.interfaces.ICallLogService;
import com.abhedyam.service.interfaces.IOwnerSettingsService;
import com.abhedyam.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CallLogService implements ICallLogService {
    
    private final CallLogRepository callLogRepository;
    private final CustomerRepository customerRepository;
    private final IOwnerSettingsService ownerSettingsService;
    
    @Override
    public CallLog create(CallLog callLog) {
        if (!isCallLogSyncEnabled()) {
            log.debug("Call log sync disabled. Skipping call log creation.");
            return null;
        }
        
        if (callLog.getDirection() != CallDirection.OUTBOUND) {
            log.debug("Only outbound calls are logged. Skipping inbound call.");
            return null;
        }
        
        return callLogRepository.save(callLog);
    }
    
    @Override
    @Transactional
    public CallLog createCallLog(CallLogCreateRequest request) {
        if (!isCallLogSyncEnabled()) {
            log.debug("Call log sync disabled. Skipping call log creation.");
            return null;
        }
        
        if (request.getDirection() != CallDirection.OUTBOUND) {
            log.debug("Only outbound calls are logged. Skipping inbound call.");
            return null;
        }
        
        UUID ownerId = SecurityUtil.getCurrentUserId();
        
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        if (customer.getOwnerId() != null && !customer.getOwnerId().equals(ownerId)) {
            throw new BusinessException("UNAUTHORIZED", "You don't have access to this customer");
        }
        
        CallLog callLog = new CallLog();
        callLog.setOwnerId(ownerId);
        callLog.setCustomerId(request.getCustomerId());
        callLog.setDirection(request.getDirection());
        callLog.setStartTime(request.getStartTime());
        callLog.setEndTime(request.getEndTime());
        callLog.setDurationSeconds(request.getDurationSeconds());
        callLog.setPhone(request.getPhone());
        
        return callLogRepository.save(callLog);
    }
    
    @Override
    @Transactional
    public List<CallLog> syncCallLogs(CallLogSyncRequest request) {
        if (!isCallLogSyncEnabled()) {
            log.debug("Call log sync disabled. Skipping call log sync.");
            return List.of();
        }
        
        UUID ownerId = SecurityUtil.getCurrentUserId();
        List<CallLog> savedLogs = new ArrayList<>();
        
        for (CallLogCreateRequest logRequest : request.getCallLogs()) {
            if (logRequest.getDirection() != CallDirection.OUTBOUND) {
                log.debug("Only outbound calls are logged. Skipping inbound call.");
                continue;
            }
            
            Customer customer = customerRepository.findById(logRequest.getCustomerId())
                    .orElse(null);
            
            if (customer == null || (customer.getOwnerId() != null && !customer.getOwnerId().equals(ownerId))) {
                log.warn("Customer not found or unauthorized for call log: {}", logRequest.getCustomerId());
                continue;
            }
            
            CallLog callLog = new CallLog();
            callLog.setOwnerId(ownerId);
            callLog.setCustomerId(logRequest.getCustomerId());
            callLog.setDirection(logRequest.getDirection());
            callLog.setStartTime(logRequest.getStartTime());
            callLog.setEndTime(logRequest.getEndTime());
            callLog.setDurationSeconds(logRequest.getDurationSeconds());
            callLog.setPhone(logRequest.getPhone());
            
            savedLogs.add(callLogRepository.save(callLog));
        }
        
        log.info("Synced {} call logs for owner {}", savedLogs.size(), ownerId);
        return savedLogs;
    }
    
    @Override
    public CallLog getById(UUID id) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        CallLog callLog = callLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CallLog not found with id: " + id));
        
        if (!callLog.getOwnerId().equals(ownerId)) {
            throw new BusinessException("UNAUTHORIZED", "You don't have access to this call log");
        }
        
        return callLog;
    }
    
    @Override
    public List<CallLog> getAll() {
        return callLogRepository.findAll();
    }
    
    @Override
    public List<CallLog> getByOwnerId(UUID ownerId) {
        UUID currentOwnerId = SecurityUtil.getCurrentUserId();
        if (!currentOwnerId.equals(ownerId)) {
            throw new BusinessException("UNAUTHORIZED", "You can only view your own call logs");
        }
        return callLogRepository.findByOwnerId(ownerId);
    }
    
    @Override
    public List<CallLog> getByCustomerId(UUID customerId) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        if (customer.getOwnerId() != null && !customer.getOwnerId().equals(ownerId)) {
            throw new BusinessException("UNAUTHORIZED", "You don't have access to this customer's call logs");
        }
        
        return callLogRepository.findByCustomerId(customerId).stream()
            .filter(log -> log.getOwnerId().equals(ownerId))
            .filter(log -> log.getIsActive() != null && log.getIsActive())
            .toList();
    }
    
    @Override
    @Transactional
    public CallLog update(UUID id, CallLog callLogDetails) {
        CallLog callLog = getById(id);
        if (callLogDetails.getEndTime() != null) callLog.setEndTime(callLogDetails.getEndTime());
        if (callLogDetails.getDurationSeconds() != null) callLog.setDurationSeconds(callLogDetails.getDurationSeconds());
        return callLogRepository.save(callLog);
    }
    
    @Override
    @Transactional
    public void delete(UUID id) {
        CallLog callLog = getById(id);
        callLog.setDeletedAt(Instant.now());
        callLog.setIsActive(false);
        callLogRepository.save(callLog);
    }
    
    @Override
    public boolean isCallLogSyncEnabled() {
        try {
            return ownerSettingsService.getCurrentOwnerSettings().getCallLogSyncEnabled();
        } catch (Exception e) {
            log.warn("Could not check call log sync setting, defaulting to disabled", e);
            return false;
        }
    }
}

