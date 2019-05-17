package com.kebab.ApiCompat;

import android.app.Notification;
import android.os.Build.VERSION;


import com.kebab.Llama.OngoingNotification;

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

    public static class Builder {

        public Builder(OngoingNotification ongoingNotification, String notificationChannelIdService) {

        }

        public android.support.v4.app.NotificationCompat.Builder setOngoing(boolean b) {
            return null;
        }
    }
}
