package com.abhedyam.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class NotificationMarkReadRequest {
    private List<UUID> notificationIds;
    private Boolean markAllAsRead = false;
}

