package com.kebab.Llama.DeviceAdmin;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import com.kebab.Helpers;
import com.kebab.Llama.Logging;
import com.kebab.Llama.R;
import com.kebab.ResultRegisterableActivity;
import com.kebab.ResultRegisterableActivity.ResultCallback;

public class LlamaDeviceAdminReceiver extends DeviceAdminReceiver {
    public static void ShowEnableAdmin(final ResultRegisterableActivity activity, int requestCode, final ResultCallback callback) {
        Helpers.ShowSimpleDialogMessage(activity.GetActivity(), activity.GetActivity().getString(R.string.hrDeviceAdminWarning), new OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent("android.app.action.ADD_DEVICE_ADMIN");
                intent.putExtra("android.app.extra.DEVICE_ADMIN", LlamaDeviceAdminReceiver.CreateComponentName(activity.GetActivity()));
                intent.putExtra("android.app.extra.ADD_EXPLANATION", "Llama needs to act as a 'device administrator' to perform actions such as screen off and change password. Llama won't perform these actions unless your events say to do so.");
                activity.RegisterActivityResult(intent, callback, null);
            }
        });
    }

    private static ComponentName CreateComponentName(Context context) {
        return new ComponentName(context, LlamaDeviceAdminReceiver.class);
    }

    public static void LockNow(Context context) {
        ((DevicePolicyManager) context.getSystemService("device_policy")).lockNow();
    }

    public static String SetPassword(Context context, String password) {
        try {
            if (((DevicePolicyManager) context.getSystemService("device_policy")).resetPassword(password, 0)) {
                return null;
            }
            return "";
        } catch (IllegalArgumentException iae) {
            Logging.Report(iae, context);
            String message = iae.getMessage();
            return message == null ? "" : message;
        }
    }

    public static boolean IsAdminEnabled(Context context) {
        return ((DevicePolicyManager) context.getSystemService("device_policy")).isAdminActive(CreateComponentName(context));
    }
}
