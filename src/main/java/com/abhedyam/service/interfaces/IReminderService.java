package com.abhedyam.service.interfaces;

import com.abhedyam.dto.ReminderCreateRequest;
import com.abhedyam.model.Reminder;

import java.util.List;
import java.util.UUID;

public interface IReminderService {
    Reminder create(ReminderCreateRequest request);
    Reminder getById(UUID id);
    List<Reminder> getAll();
    List<Reminder> getByOwnerId(UUID ownerId);
    List<Reminder> getByCustomerId(UUID customerId);
    List<Reminder> getPendingReminders();
    Reminder update(UUID id, ReminderCreateRequest request);
    Reminder markAsSent(UUID id);
}

