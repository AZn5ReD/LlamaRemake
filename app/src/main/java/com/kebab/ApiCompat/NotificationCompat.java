package com.kebab.ApiCompat;

import android.app.Notification;
import android.os.Build.VERSION;

public class NotificationCompat {
    public static int PRIORITY_DEFAULT = 0;
    public static int PRIORITY_HIGH = 1;
    public static int PRIORITY_LOW = -1;
    public static int PRIORITY_MAX = 2;
    public static int PRIORITY_MIN = -2;

    public static void SetNotificationPriority(Notification notification, int priority) {
        if (SupportsNotificationPriority()) {
            NotificationApi16Plus.SetNotificationPriority(notification, priority);
        }
    }

    public static boolean SupportsNotificationPriority() {
        return VERSION.SDK_INT >= 16;
    }
}
