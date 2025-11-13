package com.abhedyam.service;

import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Reminder;
import com.abhedyam.repository.ReminderRepository;
import com.abhedyam.service.interfaces.IReminderService;
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
    
    public Reminder create(Reminder reminder) {
        return reminderRepository.save(reminder);
    }
    
    public Reminder getById(UUID id) {
        return reminderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder not found with id: " + id));
    }
    
    public List<Reminder> getAll() {
        return reminderRepository.findAll();
    }
    
    public List<Reminder> getByOwnerId(UUID ownerId) {
        return reminderRepository.findByOwnerId(ownerId);
    }
    
    public List<Reminder> getByCustomerId(UUID customerId) {
        return reminderRepository.findByCustomerId(customerId);
    }
    
    @Transactional
    public Reminder update(UUID id, Reminder reminderDetails) {
        Reminder reminder = getById(id);
        if (reminderDetails.getName() != null) reminder.setName(reminderDetails.getName());
        if (reminderDetails.getType() != null) reminder.setType(reminderDetails.getType());
        if (reminderDetails.getChannel() != null) reminder.setChannel(reminderDetails.getChannel());
        if (reminderDetails.getTime() != null) reminder.setTime(reminderDetails.getTime());
        if (reminderDetails.getText() != null) reminder.setText(reminderDetails.getText());
        if (reminderDetails.getStatus() != null) reminder.setStatus(reminderDetails.getStatus());
        return reminderRepository.save(reminder);
    }
    
    @Transactional
    public void delete(UUID id) {
        Reminder reminder = getById(id);
        reminder.setDeletedAt(Instant.now());
        reminder.setIsActive(false);
        reminderRepository.save(reminder);
    }
}

