package com.abhedyam.service;

import com.abhedyam.dto.CallLogCreateRequest;
import com.abhedyam.dto.CallLogResponse;
import com.abhedyam.dto.CallLogSyncRequest;
import com.abhedyam.dto.PageResponse;
import com.abhedyam.exception.BusinessException;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.CallLog;
import com.abhedyam.model.Customer;
import com.abhedyam.repository.CallLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.abhedyam.repository.CustomerRepository;
import com.abhedyam.service.interfaces.IAuditService;
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
    private final IAuditService auditService;
    
    @Override
    public CallLog create(CallLog callLog) {
        if (!isCallLogSyncEnabled()) {
            log.debug("Call log sync disabled. Skipping call log creation.");
            return null;
        }
        
        String key = callLog.getKey();
        if (key == null || key.trim().isEmpty()) {
            key = generateKey(callLog.getStartTime(), callLog.getOwnerId());
            callLog.setKey(key);
        }
        
        if (callLogRepository.existsByKey(key)) {
            log.debug("Call log with key {} already exists. Skipping duplicate.", key);
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
        
        UUID ownerId = SecurityUtil.getCurrentUserId();
        
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        if (customer.getOwnerId() != null && !customer.getOwnerId().equals(ownerId)) {
            throw new BusinessException("UNAUTHORIZED", "You don't have access to this customer");
        }
        
        String key = request.getKey();
        if (key == null || key.trim().isEmpty()) {
            key = generateKey(request.getStartTime(), ownerId);
        }
        
        if (callLogRepository.existsByKey(key)) {
            log.debug("Call log with key {} already exists. Skipping duplicate.", key);
            return null;
        }
        
        CallLog callLog = new CallLog();
        callLog.setOwnerId(ownerId);
        callLog.setCustomerId(request.getCustomerId());
        callLog.setDirection(request.getDirection());
        callLog.setStartTime(request.getStartTime());
        callLog.setEndTime(request.getEndTime());
        callLog.setDurationSeconds(request.getDurationSeconds());
        callLog.setPhone(request.getPhone());
        callLog.setKey(key);
        
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
            Customer customer = customerRepository.findById(logRequest.getCustomerId())
                    .orElse(null);
            
            if (customer == null || (customer.getOwnerId() != null && !customer.getOwnerId().equals(ownerId))) {
                log.warn("Customer not found or unauthorized for call log: {}", logRequest.getCustomerId());
                continue;
            }
            
            String key = logRequest.getKey();
            if (key == null || key.trim().isEmpty()) {
                key = generateKey(logRequest.getStartTime(), ownerId);
            }
            
            if (callLogRepository.existsByKey(key)) {
                log.debug("Call log with key {} already exists. Skipping duplicate.", key);
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
            callLog.setKey(key);
            
            savedLogs.add(callLogRepository.save(callLog));
        }
        
        log.info("Synced {} call logs for owner {}", savedLogs.size(), ownerId);
        
        try {
            auditService.logFinancialOperation(
                com.abhedyam.model.enums.AuditType.NOTE,
                com.abhedyam.model.enums.AuditAction.CREATE,
                null,
                ownerId,
                java.math.BigDecimal.valueOf(savedLogs.size()),
                String.format("Call logs synced: %d call log(s) imported", savedLogs.size())
            );
        } catch (Exception e) {
            log.warn("Audit logging failed for call log sync: {}", e.getMessage());
        }
        
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
    @Transactional(readOnly = true)
    public PageResponse<CallLogResponse> getByCustomerId(UUID customerId, Integer page, Integer size) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        if (customer.getOwnerId() != null && !customer.getOwnerId().equals(ownerId)) {
            throw new BusinessException("UNAUTHORIZED", "You don't have access to this customer's call logs");
        }
        
        if (page == null || page < 0) {
            page = 0;
        }
        if (size == null || size < 1) {
            size = 10;
        }
        
        Sort sort = Sort.by(Sort.Direction.DESC, "startTime");
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<CallLog> callLogPage = callLogRepository.findByCustomerIdAndOwnerId(customerId, ownerId, pageable);
        
        List<CallLogResponse> responses = callLogPage.getContent().stream()
            .map(CallLogResponse::fromEntity)
            .toList();
        
        return new PageResponse<>(
            responses,
            callLogPage.getNumber(),
            callLogPage.getSize(),
            callLogPage.getTotalElements(),
            callLogPage.getTotalPages(),
            callLogPage.hasNext(),
            callLogPage.hasPrevious()
        );
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
    public boolean isCallLogSyncEnabled() {
        try {
            return ownerSettingsService.getCurrentOwnerSettings().getCallLogSyncEnabled();
        } catch (Exception e) {
            log.warn("Could not check call log sync setting, defaulting to disabled", e);
            return false;
        }
    }
    
    private String generateKey(Instant timestamp, UUID ownerId) {
        return timestamp.toEpochMilli() + "_" + ownerId.toString();
    }
}

