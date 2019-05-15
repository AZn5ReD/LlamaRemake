package com.kebab.Llama.EventActions;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import com.kebab.ApiCompat.IntentCompat;
import com.kebab.AppendableCharSequence;
import com.kebab.EditTextPreference;
import com.kebab.Llama.Constants;
import com.kebab.Llama.Event;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.LlamaSettings;
import com.kebab.Llama.LlamaStorage;
import com.kebab.Llama.LlamaUi;
import com.kebab.Llama.R;
import com.kebab.OnGetValueEx;
import com.kebab.PreferenceEx;
import java.io.IOException;
import java.util.Random;

public class NotificationAction extends EventAction<NotificationAction> {
    boolean _Fired = false;
    String _NotificationText;
    Random _Random = new Random();

    public NotificationAction(String notificationText) {
        this._NotificationText = notificationText;
    }

    public NotificationAction(String notificationText, boolean fired) {
        this._NotificationText = notificationText;
        this._Fired = fired;
    }

    public void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        if (!this._Fired) {
            int notificationId = Constants.OTHER_NOTIFICATION_STARTID + this._Random.nextInt(100000);
            String expandedText = service.ExpandVariables(this._NotificationText);
            String tickerText = expandedText;
            long when = System.currentTimeMillis();
            Context context = service.getBaseContext();
            CharSequence contentTitle = "Llama Event - " + event.Name;
            String contentText = expandedText;
            Intent notificationIntent = new Intent(service, LlamaUi.class);
            notificationIntent.addFlags(524288);
            notificationIntent.addFlags(67108864);
            notificationIntent.addFlags(268435456);
            notificationIntent.addFlags(IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
            notificationIntent.putExtra(Constants.EXTRA_NOTIFICATION_MESSAGE, expandedText);
            notificationIntent.putExtra(Constants.EXTRA_NOTIFICATION_TITLE, contentTitle);
            notificationIntent.putExtra(Constants.EXTRA_NOTIFICATION_ID_TO_CLEAR, notificationId);
            PendingIntent contentIntent = PendingIntent.getActivity(context, notificationId, notificationIntent, 0);
            Notification notification = new Notification(R.drawable.ic_tab_areas, tickerText, when);
//            notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
            String reminderUri = (String) LlamaSettings.ReminderRingtoneUri.GetValue(service);
            if (reminderUri != null && reminderUri.length() > 0) {
                notification.sound = Uri.parse(reminderUri);
                notification.audioStreamType = 5;
            }
            ((NotificationManager) service.getSystemService("notification")).notify(notificationId, notification);
        }
    }

    public boolean RenameProfile(String oldName, String newName) {
        return false;
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.NOTIFICATION_ACTION;
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 2;
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(LlamaStorage.SimpleEscape(this._NotificationText)).append("|");
        sb.append(this._Fired ? "1" : "0");
    }

    public static NotificationAction CreateFrom(String[] parts, int currentPart) {
        return new NotificationAction(LlamaStorage.SimpleUnescape(parts[currentPart + 1]), parts[currentPart + 2].equals("1"));
    }

    public PreferenceEx<NotificationAction> CreatePreference(PreferenceActivity context) {
        return CreateEditTextPreference(context, context.getString(R.string.hrReminder), this._NotificationText, new OnGetValueEx<NotificationAction>() {
            public NotificationAction GetValue(Preference preference) {
                return new NotificationAction(((EditTextPreference) preference).getText());
            }
        });
    }

    public void AppendActionDescription(Context context, AppendableCharSequence sb) throws IOException {
        sb.append(context.getString(R.string.hrShowAReminder));
    }

    public String GetIsValidError(Context context) {
        if (this._NotificationText == null || this._NotificationText.length() == 0) {
            return context.getString(R.string.hrEnterSomeReminderText);
        }
        return null;
    }

    public boolean IsHarmful() {
        return false;
    }
}
