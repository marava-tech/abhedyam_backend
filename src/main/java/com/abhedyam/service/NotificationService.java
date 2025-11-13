package com.abhedyam.service;

import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Notification;
import com.abhedyam.repository.NotificationRepository;
import com.abhedyam.service.interfaces.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService implements INotificationService {
    
    private final NotificationRepository notificationRepository;
    
    public Notification create(Notification notification) {
        return notificationRepository.save(notification);
    }
    
    public Notification getById(UUID id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));
    }
    
    public List<Notification> getAll() {
        return notificationRepository.findAll();
    }
    
    public List<Notification> getByOwnerId(UUID ownerId) {
        return notificationRepository.findByOwnerId(ownerId);
    }
    
    public List<Notification> getByUserId(UUID userId) {
        return notificationRepository.findByUserId(userId);
    }
    
    @Transactional
    public Notification update(UUID id, Notification notificationDetails) {
        Notification notification = getById(id);
        if (notificationDetails.getMessage() != null) notification.setMessage(notificationDetails.getMessage());
        if (notificationDetails.getIsRead() != null) notification.setIsRead(notificationDetails.getIsRead());
        return notificationRepository.save(notification);
    }
    
    @Transactional
    public void delete(UUID id) {
        Notification notification = getById(id);
        notification.setDeletedAt(Instant.now());
        notification.setIsActive(false);
        notificationRepository.save(notification);
    }
}

