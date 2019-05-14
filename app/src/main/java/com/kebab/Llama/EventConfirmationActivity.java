package com.kebab.Llama;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import com.kebab.AlertDialogEx.Builder;
import com.kebab.AppendableCharSequence;
import com.kebab.Helpers;
import com.kebab.Llama.Instances.HelloableActivity;
import java.io.IOException;

public class EventConfirmationActivity extends HelloableActivity {
    Dialog _Dialog;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        showConfimationDialog();
    }

    public void onPause() {
        if (this._Dialog != null) {
            this._Dialog.dismiss();
        }
        super.onPause();
    }

    public void onResume() {
        super.onResume();
        showConfimationDialog();
    }

    private void showConfimationDialog() {
        Intent intent = getIntent();
        if (intent == null || (intent.getFlags() & 1048576) != 0) {
            finish();
            return;
        }
        this._Dialog = PrepareMessageForEvent(this, intent.getStringExtra(Constants.EXTRA_NOTIFICATION_EVENT_NAME), intent.getIntExtra(Constants.EXTRA_NOTIFICATION_ID_TO_CLEAR, 0));
        if (this._Dialog == null) {
            finish();
        } else {
            this._Dialog.show();
        }
    }

    public static Dialog PrepareMessageForEvent(final Activity activity, final String eventName, final int clearNotificationId) {
        Event e = Instances.Service.GetEventByName(eventName);
        Builder dialog = new Builder(activity);
        if (e == null) {
            Instances.Service.HandleFriendlyError(String.format(activity.getString(R.string.hrTheEventNamed1NoLongerExists), new Object[]{eventName}), false);
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(activity.getString(R.string.hrEvent1RequiresConfirmation), new Object[]{eventName}));
        sb.append("\n\n");
        int firstChar = sb.length();
        try {
            e.AppendEventActionDescription(activity, AppendableCharSequence.Wrap(sb), false);
            sb.append("\n\n");
            sb.append(activity.getString(R.string.hrEventRequiresConfirmationFooter));
            Helpers.CapitaliseFirstLetter(sb, firstChar);
            dialog.setMessage(sb);
            dialog.setPositiveButton(R.string.hrEventConfirmationRunNow, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Instances.Service.SetEventConfirmationGranted(eventName, clearNotificationId);
                    dialog.dismiss();
                    activity.finish();
                }
            }).setNeutralButton(R.string.hrEventConfirmationSaveForLater, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    activity.finish();
                }
            }).setNegativeButton(R.string.hrEventConfirmationDontRun, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Instances.Service.SetEventConfirmationDenied(eventName, clearNotificationId);
                    dialog.dismiss();
                    activity.finish();
                }
            });
            dialog.setOnCancelListener(new OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                    dialog.dismiss();
                    activity.finish();
                }
            });
            Dialog d = dialog.create();
            d.setOwnerActivity(activity);
            return d;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
