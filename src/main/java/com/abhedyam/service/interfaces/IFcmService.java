package com.abhedyam.service.interfaces;

import com.abhedyam.model.FcmDetails;
import com.abhedyam.model.enums.UserType;

import java.util.List;
import java.util.UUID;

public interface IFcmService {
    FcmDetails registerToken(UUID userId, String token, String packageName, String deviceId, String deviceType);
    void unregisterToken(UUID userId, String token, String packageName);
    void sendNotificationToUser(UUID userId, String title, String body, String packageName);
    void sendNotificationToUsers(List<UUID> userIds, String title, String body, String packageName);
    void sendNotificationToAllUsersByType(UserType userType, String title, String body, String packageName);
}

