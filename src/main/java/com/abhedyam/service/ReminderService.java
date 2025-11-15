package com.abhedyam.service;

import com.abhedyam.dto.ReminderCreateRequest;
import com.abhedyam.exception.BusinessException;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Reminder;
import com.abhedyam.model.enums.ReminderStatus;
import com.abhedyam.repository.CustomerRepository;
import com.abhedyam.repository.ReminderRepository;
import com.abhedyam.service.interfaces.IAuditService;
import com.abhedyam.service.interfaces.IReminderService;
import com.abhedyam.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReminderService implements IReminderService {
    
    private final ReminderRepository reminderRepository;
    private final CustomerRepository customerRepository;
    private final IAuditService auditService;
    
    @Override
    @Transactional
    public Reminder create(ReminderCreateRequest request) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        
        if (!customerRepository.existsById(request.getCustomerId())) {
            throw new ResourceNotFoundException("Customer not found");
        }
        
        if (request.getDueAt().isBefore(Instant.now())) {
            throw new BusinessException("INVALID_DUE_DATE", "Due date cannot be in the past");
        }
        
        Reminder reminder = new Reminder();
        reminder.setCustomerId(request.getCustomerId());
        reminder.setOwnerId(ownerId);
        reminder.setName(request.getName());
        reminder.setType(request.getType());
        reminder.setChannel(request.getChannel());
        reminder.setTime(request.getDueAt());
        reminder.setText(request.getText());
        reminder.setStatus(ReminderStatus.PENDING);
        
        Reminder savedReminder = reminderRepository.save(reminder);
        
        auditService.logReminderCreation(savedReminder.getId(), ownerId, request.getCustomerId(), request.getText());
        
        return savedReminder;
    }
    
    @Override
    public Reminder getById(UUID id) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder not found with id: " + id));
        
        if (!reminder.getOwnerId().equals(ownerId)) {
            throw new BusinessException("UNAUTHORIZED", "You don't have access to this reminder");
        }
        
        return reminder;
    }
    
    @Override
    public List<Reminder> getAll() {
        return reminderRepository.findAll();
    }
    
    @Override
    public List<Reminder> getByOwnerId(UUID ownerId) {
        UUID currentOwnerId = SecurityUtil.getCurrentUserId();
        if (!currentOwnerId.equals(ownerId)) {
            throw new BusinessException("UNAUTHORIZED", "You can only view your own reminders");
        }
        return reminderRepository.findByOwnerId(ownerId);
    }
    
    @Override
    public List<Reminder> getByCustomerId(UUID customerId) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        List<Reminder> reminders = reminderRepository.findByCustomerId(customerId);
        return reminders.stream()
            .filter(reminder -> reminder.getOwnerId().equals(ownerId))
            .toList();
    }
    
    @Override
    public List<Reminder> getPendingReminders() {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        return reminderRepository.findByOwnerId(ownerId).stream()
            .filter(r -> r.getStatus() == ReminderStatus.PENDING)
            .filter(r -> r.getTime().isBefore(Instant.now()) || r.getTime().equals(Instant.now()))
            .toList();
    }
    
    @Override
    @Transactional
    public Reminder update(UUID id, ReminderCreateRequest request) {
        Reminder reminder = getById(id);
        
        if (request.getName() != null) reminder.setName(request.getName());
        if (request.getType() != null) reminder.setType(request.getType());
        if (request.getChannel() != null) reminder.setChannel(request.getChannel());
        if (request.getDueAt() != null) {
            if (request.getDueAt().isBefore(Instant.now())) {
                throw new BusinessException("INVALID_DUE_DATE", "Due date cannot be in the past");
            }
            reminder.setTime(request.getDueAt());
        }
        if (request.getText() != null) reminder.setText(request.getText());
        
        return reminderRepository.save(reminder);
    }
    
    @Override
    @Transactional
    public Reminder markAsSent(UUID id) {
        Reminder reminder = getById(id);
        reminder.setStatus(ReminderStatus.SENT);
        return reminderRepository.save(reminder);
    }
}

