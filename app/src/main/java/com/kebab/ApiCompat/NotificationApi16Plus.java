package com.kebab.ApiCompat;

import android.app.Notification;

public class NotificationApi16Plus {
    static void SetNotificationPriority(Notification notification, int priority) {
        notification.priority = priority;
    }
}
