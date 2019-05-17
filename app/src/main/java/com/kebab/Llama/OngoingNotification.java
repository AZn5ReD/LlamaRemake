package com.kebab.Llama;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Build.VERSION;
import android.widget.RemoteViews;
import android.widget.Toast;
import com.kebab.ApiCompat.NotificationCompat;
import com.kebab.HelpersC;

import static android.content.Context.NOTIFICATION_SERVICE;

public class OngoingNotification {
    private static final String NOTIFICATION_CHANNEL_ID_SERVICE = "com.kebab.llama";
    PendingIntent _ContentIntent;
    Context _Context;
    String _CurrentAreaName;
    int _CurrentIcon;
    int _CurrentIconDots;
    String _CurrentProfileName;
    boolean _IsLocked;
    boolean _IsWarning;
    Notification _Notification;
    NotificationManager _NotificationManager;

    public OngoingNotification(LlamaService context, String profileName, String currentArea, Integer currentIcon, Integer currentDots, Boolean locked, Boolean isWarning) {
        this._Context = context.getBaseContext();
        this._CurrentProfileName = profileName;
        this._CurrentAreaName = currentArea;
        if (currentIcon != null) {
            this._CurrentIcon = currentIcon.intValue();
        }
        if (currentDots != null) {
            this._CurrentIconDots = currentDots.intValue();
        }
        if (locked != null) {
            this._IsLocked = locked.booleanValue();
        }
        if (isWarning != null) {
            this._IsWarning = isWarning.booleanValue();
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void Update() {
        int mode = ((Integer) LlamaSettings.NotificationMode.GetValue(this._Context)).intValue();
        if (mode == 1 || mode == 4 || mode == 6 || mode == 7 || mode == 5 || mode == 3) {
            boolean useEmptyIcon;
            long notificationSortOrderWhen;
            switch (mode) {
                case 3:
                    if (!NotificationCompat.SupportsNotificationPriority()) {
                        useEmptyIcon = true;
                        break;
                    } else {
                        useEmptyIcon = false;
                        break;
                    }
                default:
                    useEmptyIcon = false;
                    break;
            }
            int iconId = useEmptyIcon ? R.drawable.empty : this._IsWarning ? ((Boolean) LlamaSettings.BlackIcons.GetValue(this._Context)).booleanValue() ? R.drawable.llamanotifyeblack : R.drawable.llamanotifyewhite : NotificationIcon.GetResourceId(this._CurrentIcon, this._CurrentIconDots, this._IsLocked, ((Boolean) LlamaSettings.BlackIcons.GetValue(this._Context)).booleanValue());
            long actualWhen = System.currentTimeMillis();
            if (mode != 3 || NotificationCompat.SupportsNotificationPriority()) {
                notificationSortOrderWhen = actualWhen;
            } else {
                notificationSortOrderWhen = VERSION.SDK_INT >= 9 ? -9223372036854775807L : Long.MAX_VALUE;
                if (VERSION.SDK_INT >= 14 && Instances.Service != null) {
                    Instances.Service.HandleFriendlyError(Integer.valueOf(3));
                }
            }
            String notificationTitleText = Constants.LLAMA_EXTERNAL_STORAGE_ROOT + (this._CurrentProfileName != null ? " - " + this._CurrentProfileName : "");
            if (this._NotificationManager == null) {
                this._NotificationManager = (NotificationManager) this._Context.getSystemService("notification");
                Intent notificationIntent = new Intent(this._Context, LlamaUi.class);
                notificationIntent.addFlags(524288);
                notificationIntent.addFlags(67108864);
                notificationIntent.addFlags(268435456);
                this._ContentIntent = PendingIntent.getActivity(this._Context, 0, notificationIntent, 134217728);
            }
            if (this._Notification == null) {
                this._Notification = new Notification(iconId, null, notificationSortOrderWhen);
                if (mode != 5) {
                    this._Notification.flags = 2;
                }
            }
            if (mode != 5) {
                if ((this._Notification.flags & 2) == 0) {
                    ClearClearable();
                }
                Notification notification = this._Notification;
                notification.flags |= 2;
            } else if ((this._Notification.flags & 2) != 0) {
                ClearOngoing();
                this._Notification = new Notification(iconId, notificationTitleText, notificationSortOrderWhen);
            }
            if (mode == 3) {
                NotificationCompat.SetNotificationPriority(this._Notification, NotificationCompat.PRIORITY_MIN);
            } else if (mode == 6) {
                NotificationCompat.SetNotificationPriority(this._Notification, NotificationCompat.PRIORITY_DEFAULT);
            } else if (mode == 7) {
                NotificationCompat.SetNotificationPriority(this._Notification, NotificationCompat.PRIORITY_MAX);
            } else {
                NotificationCompat.SetNotificationPriority(this._Notification, NotificationCompat.PRIORITY_LOW);
            }
            this._Notification.icon = iconId;
            this._Notification.when = notificationSortOrderWhen;
            StringBuffer infoText = new StringBuffer();
            if (this._CurrentAreaName == null) {
                infoText.append(this._Context.getString(R.string.hrUnknownArea));
            } else {
                infoText.append(this._CurrentAreaName);
            }
            if (mode != 4 || VERSION.SDK_INT > 8) {
                StringBuilder append = new StringBuilder().append(Constants.LLAMA_EXTERNAL_STORAGE_ROOT);
                String r12 = this._IsWarning ? " - " + this._Context.getString(R.string.hrUnknownProfile) : this._CurrentProfileName != null ? " - " + this._CurrentProfileName : "";
//                this._Notification.setLatestEventInfo(this._Context, append.append(r12).toString(), infoText.toString(), this._ContentIntent);
            } else {
                this._Notification.contentIntent = this._ContentIntent;
                this._Notification.contentView = new RemoteViews(this._Context.getPackageName(), R.layout.empty);
            }
            this._NotificationManager.notify(mode == 5 ? Constants.NON_ONGOING_NOTIFICATION_ID : Constants.ONGOING_NOTIFICATION_ID, this._Notification);
            if (mode != 5 && Instances.Service != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    startMyOwnForeground();
                Instances.Service.startForeground(Constants.ONGOING_NOTIFICATION_ID, this._Notification);
                return;
            }
            return;
        }
        ClearOngoing();
    }

    public void ClearOngoing() {
        if (this._NotificationManager != null) {
            this._NotificationManager.cancel(Constants.ONGOING_NOTIFICATION_ID);
        }
        if (Instances.Service != null) {
            Instances.Service.stopForeground(true);
        }
    }

    public void ClearClearable() {
        if (this._NotificationManager != null) {
            this._NotificationManager.cancel(Constants.NON_ONGOING_NOTIFICATION_ID);
        }
    }

    public void SetCurrentAreaName(String value) {
        boolean hasChanged;
        String oldArea = this._CurrentAreaName;
        if (HelpersC.StringEquals(value, oldArea)) {
            hasChanged = false;
        } else {
            hasChanged = true;
        }
        this._CurrentAreaName = value;
        switch (((Integer) LlamaSettings.NotificationMode.GetValue(this._Context)).intValue()) {
            case 1:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                if (hasChanged) {
                    Update();
                    return;
                }
                return;
            case 2:
                if (value != null) {
                    Toast.makeText(this._Context, String.format(this._Context.getString(R.string.hrEnteredArea1), new Object[]{value}), 0).show();
                    return;
                } else if (oldArea != null) {
                    Toast.makeText(this._Context, String.format(this._Context.getString(R.string.hrLeftArea1), new Object[]{oldArea}), 0).show();
                    return;
                } else {
                    Toast.makeText(this._Context, R.string.hrEnteredUnknownArea, 0).show();
                    return;
                }
            default:
                return;
        }
    }

    public void SetCurrentProfileName(String value) {
        this._CurrentProfileName = value;
        switch (((Integer) LlamaSettings.NotificationMode.GetValue(this._Context)).intValue()) {
            case 1:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                Update();
                return;
            case 2:
                Toast.makeText(this._Context, String.format("Changed profile: %1s", new Object[]{value}), 0).show();
                return;
            default:
                return;
        }
    }

    public void SetCurrentIcon(Integer icon, Integer dots, boolean isLocked) {
        if (!(icon == null || icon.intValue() == -1)) {
            this._CurrentIcon = icon.intValue();
        }
        if (!(dots == null || dots.intValue() == -1)) {
            this._CurrentIconDots = dots.intValue();
        }
        this._IsWarning = false;
        this._IsLocked = isLocked;
        switch (((Integer) LlamaSettings.NotificationMode.GetValue(this._Context)).intValue()) {
            case 0:
                return;
            default:
                Update();
                return;
        }
    }

    public void SetIconAsWarningAndUpdate() {
        this._IsWarning = true;
        Update();
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground(){
        this._NotificationManager.createNotificationChannel(new NotificationChannel(NOTIFICATION_CHANNEL_ID_SERVICE, "Llama", NotificationManager.IMPORTANCE_DEFAULT));
        android.support.v4.app.NotificationCompat.Builder notificationBuilder = new android.support.v4.app.NotificationCompat.Builder(this._Context, NOTIFICATION_CHANNEL_ID_SERVICE);
        this._Notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
    }
}
