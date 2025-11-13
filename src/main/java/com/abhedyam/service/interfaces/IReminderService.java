package com.abhedyam.service.interfaces;

import com.abhedyam.model.Reminder;

import java.util.List;
import java.util.UUID;

public interface IReminderService {
    Reminder create(Reminder reminder);
    Reminder getById(UUID id);
    List<Reminder> getAll();
    List<Reminder> getByOwnerId(UUID ownerId);
    List<Reminder> getByCustomerId(UUID customerId);
    Reminder update(UUID id, Reminder reminderDetails);
    void delete(UUID id);
}

