package com.abhedyam.service;

import com.abhedyam.model.Reminder;
import com.abhedyam.model.enums.ReminderChannel;
import com.abhedyam.model.enums.ReminderStatus;
import com.abhedyam.repository.ReminderRepository;
import com.abhedyam.service.interfaces.IReminderSchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReminderSchedulerService implements IReminderSchedulerService {
    
    private final ReminderRepository reminderRepository;
    private final NotificationService notificationService;
    
    @Scheduled(fixedRate = 60000)
    @Override
    @Transactional
    public void processDueReminders() {
        Instant now = Instant.now();
        List<Reminder> dueReminders = reminderRepository.findAll().stream()
            .filter(r -> r.getStatus() == ReminderStatus.PENDING)
            .filter(r -> r.getTime().isBefore(now) || r.getTime().equals(now))
            .filter(r -> r.getIsActive() != null && r.getIsActive())
            .toList();
        
        for (Reminder reminder : dueReminders) {
            try {
                sendReminder(reminder);
                reminder.setStatus(ReminderStatus.SENT);
                reminderRepository.save(reminder);
                log.info("Reminder sent: {}", reminder.getId());
            } catch (Exception e) {
                log.error("Error sending reminder: {}", reminder.getId(), e);
            }
        }
    }
    
    private void sendReminder(Reminder reminder) {
        if (reminder.getChannel() == ReminderChannel.IN_APP) {
            com.abhedyam.model.Notification notification = new com.abhedyam.model.Notification();
            notification.setOwnerId(reminder.getOwnerId());
            notification.setUserId(reminder.getCustomerId());
            notification.setType(com.abhedyam.model.enums.NotificationType.INFO);
            notification.setMessage(reminder.getText());
            notification.setTimestamp(Instant.now());
            notification.setIsRead(false);
            notification.setRelatedEntityId(reminder.getId());
            notification.setRelatedEntityType("REMINDER");
            notification.setRetryCount(0);
            notificationService.create(notification);
        } else {
            log.debug("Skipping reminder send for non-IN_APP channel: {} (reminder: {})", reminder.getChannel(), reminder.getId());
        }
    }
}

