package com.kebab.Llama;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.kebab.AndroidSystemIntentHelpers;
import com.kebab.CachedSetting;
import com.kebab.RunnableArg;
import com.kebab.Tuple;

public class ConfirmationMessages {
    public static final int CONFIRMMESSAGE_APPS2SD = 5;
    public static final int CONFIRMMESSAGE_BROKEN_WIFI_MULTIUSER = 8;
    public static final int CONFIRMMESSAGE_BROKEN_WIFI_STATS = 7;
    public static final int CONFIRMMESSAGE_ICS_APN = 4;
    public static final int CONFIRMMESSAGE_ICS_HIDDEN_NOTIFICATION_ICON = 3;
    public static final int CONFIRMMESSAGE_ICS_NOTIFICATION_USE_RING_VOLUME = 2;
    public static final int CONFIRMMESSAGE_MIUI = 1;
    public static final int CONFIRMMESSAGE_RINGER_CHANGE_WITHOUT_VOLUMES = 6;

    public static boolean HasAcceptedMessage(Context context, int confirmationMessageId) {
        if (((String) LlamaSettings.AcceptedConfirmationMessages.GetValue(context)).contains("[" + confirmationMessageId + "]")) {
            return true;
        }
        return false;
    }

    public static void SetAcceptedMessage(Context context, int confirmationMessageId) {
        String acceptedMessages = (String) LlamaSettings.AcceptedConfirmationMessages.GetValue(context);
        if (!acceptedMessages.contains("[" + confirmationMessageId + "]")) {
            LlamaSettings.AcceptedConfirmationMessages.SetValueAndCommit(context, acceptedMessages + "[" + confirmationMessageId + "]", new CachedSetting[0]);
        }
    }

    public static String GetMessageText(Context context, int confirmationMessageId) {
        switch (confirmationMessageId) {
            case 1:
                return context.getString(R.string.hrMIUIVolumeChangeWarning);
            case 2:
                return context.getString(R.string.hrIcsNotificationUseRingVolumeWarning);
            case 3:
                return context.getString(R.string.hrIcsHiddenNotificationIcon);
            case 4:
                return context.getString(R.string.hrIcsApn);
            case 5:
                return context.getString(R.string.hrApps2SdWarning);
            case 6:
                return context.getString(R.string.hrRingerChangeNoVolumeWarning);
            case 7:
                return context.getString(R.string.hrBrokenWifiToggleUpdateDeviceStats);
            case 8:
                return context.getString(R.string.hrBrokenWifiToggleMultiuser);
            default:
                return null;
        }
    }

    public static Tuple<Integer, RunnableArg<Activity>> GetCustomisedDialogButton(int confirmationMessageId) {
        switch (confirmationMessageId) {
            case 2:
                return new Tuple(Integer.valueOf(R.string.hrWhichProfiles), new RunnableArg<Activity>() {
                    public void Run(Activity context) {
                        LlamaUi.ShowIcsDifferentVolumesProfiles(context);
                    }
                });
            case 5:
                return new Tuple(Integer.valueOf(R.string.hrViewLlamaAppInfo), new RunnableArg<Activity>() {
                    public void Run(Activity context) {
                        AndroidSystemIntentHelpers.showInstalledAppDetails(context, context.getPackageName());
                    }
                });
            case 6:
                return new Tuple(Integer.valueOf(R.string.hrWhichProfiles), new RunnableArg<Activity>() {
                    public void Run(Activity context) {
                        LlamaUi.ShowRingerChangeNoVolumesProfiles(context);
                    }
                });
            case 7:
                return new Tuple(Integer.valueOf(R.string.hrVisitWebsite), new RunnableArg<Activity>() {
                    public void Run(Activity context) {
                        Intent i = new Intent("android.intent.action.VIEW");
                        i.addFlags(268435456);
                        i.setData(Uri.parse("http://code.google.com/p/android/issues/detail?id=22036"));
                        context.startActivity(i);
                    }
                });
            default:
                return null;
        }
    }
}
