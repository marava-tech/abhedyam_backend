package com.abhedyam.service;

import com.abhedyam.dto.NotificationMarkReadRequest;
import com.abhedyam.exception.BusinessException;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Notification;
import com.abhedyam.repository.NotificationRepository;
import com.abhedyam.service.interfaces.INotificationService;
import com.abhedyam.util.SecurityUtil;
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
    
    @Override
    public Notification create(Notification notification) {
        if (notification.getRetryCount() == null) {
            notification.setRetryCount(0);
        }
        if (notification.getIsRead() == null) {
            notification.setIsRead(false);
        }
        return notificationRepository.save(notification);
    }
    
    @Override
    public Notification getById(UUID id) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));
        
        if (!notification.getOwnerId().equals(ownerId)) {
            throw new BusinessException("UNAUTHORIZED", "You don't have access to this notification");
        }
        
        return notification;
    }
    
    @Override
    public List<Notification> getAll() {
        return notificationRepository.findAll();
    }
    
    @Override
    public List<Notification> getByOwnerId(UUID ownerId) {
        UUID currentOwnerId = SecurityUtil.getCurrentUserId();
        if (!currentOwnerId.equals(ownerId)) {
            throw new BusinessException("UNAUTHORIZED", "You can only view your own notifications");
        }
        return notificationRepository.findByOwnerId(ownerId);
    }
    
    @Override
    public List<Notification> getByUserId(UUID userId) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        return notifications.stream()
            .filter(n -> n.getOwnerId().equals(ownerId))
            .toList();
    }
    
    @Override
    public List<Notification> getMyNotifications(Boolean unreadOnly) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        List<Notification> notifications = notificationRepository.findByOwnerId(ownerId);
        
        if (unreadOnly != null && unreadOnly) {
            return notifications.stream()
                .filter(n -> n.getIsActive() != null && n.getIsActive())
                .filter(n -> n.getIsRead() == null || !n.getIsRead())
                .toList();
        }
        
        return notifications.stream()
            .filter(n -> n.getIsActive() != null && n.getIsActive())
            .toList();
    }
    
    @Override
    @Transactional
    public Notification markAsRead(UUID id) {
        Notification notification = getById(id);
        notification.setIsRead(true);
        notification.setReadAt(Instant.now());
        return notificationRepository.save(notification);
    }
    
    @Override
    @Transactional
    public List<Notification> markMultipleAsRead(NotificationMarkReadRequest request) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        Instant now = Instant.now();
        
        if (request.getMarkAllAsRead() != null && request.getMarkAllAsRead()) {
            List<Notification> notifications = notificationRepository.findByOwnerId(ownerId).stream()
                .filter(n -> n.getIsActive() != null && n.getIsActive())
                .filter(n -> n.getIsRead() == null || !n.getIsRead())
                .toList();
            
            notifications.forEach(n -> {
                n.setIsRead(true);
                n.setReadAt(now);
            });
            
            return notificationRepository.saveAll(notifications);
        }
        
        if (request.getNotificationIds() != null && !request.getNotificationIds().isEmpty()) {
            List<Notification> notifications = notificationRepository.findAllById(request.getNotificationIds()).stream()
                .filter(n -> n.getOwnerId().equals(ownerId))
                .toList();
            
            notifications.forEach(n -> {
                n.setIsRead(true);
                n.setReadAt(now);
            });
            
            return notificationRepository.saveAll(notifications);
        }
        
        return List.of();
    }
    
    @Override
    @Transactional
    public Notification update(UUID id, Notification notificationDetails) {
        Notification notification = getById(id);
        if (notificationDetails.getMessage() != null) notification.setMessage(notificationDetails.getMessage());
        if (notificationDetails.getIsRead() != null) notification.setIsRead(notificationDetails.getIsRead());
        return notificationRepository.save(notification);
    }
    
    @Override
    @Transactional
    public void delete(UUID id) {
        Notification notification = getById(id);
        notification.setDeletedAt(Instant.now());
        notification.setIsActive(false);
        notificationRepository.save(notification);
    }
}

