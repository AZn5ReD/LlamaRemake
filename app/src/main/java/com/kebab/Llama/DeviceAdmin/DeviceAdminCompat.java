package com.kebab.Llama.DeviceAdmin;

import android.content.Context;
import android.os.Build.VERSION;
import android.preference.PreferenceActivity;
import com.kebab.Llama.Constants;
import com.kebab.ResultRegisterableActivity;
import com.kebab.ResultRegisterableActivity.ResultCallback;

public class DeviceAdminCompat {
    public static boolean IsSupported() {
        return VERSION.SDK_INT >= 8;
    }

    public static boolean IsAdminEnabled(PreferenceActivity context) {
        return LlamaDeviceAdminReceiver.IsAdminEnabled(context);
    }

    public static void ShowEnableAdmin(ResultRegisterableActivity activity, int requestCodeDeviceAdmin, ResultCallback callback) {
        LlamaDeviceAdminReceiver.ShowEnableAdmin(activity, Constants.REQUEST_CODE_DEVICE_ADMIN, callback);
    }

    public static void LockNow(Context context) {
        LlamaDeviceAdminReceiver.LockNow(context);
    }

    public static String ChangePassword(Context context, String password) {
        return LlamaDeviceAdminReceiver.SetPassword(context, password);
    }
}
