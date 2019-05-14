package com.kebab.Llama;

import android.content.Context;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;
import java.lang.reflect.Method;

public class MobileData {
    private static void SetMobileDataEnabled2(Context context, boolean enabled) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService("connectivity");
            Method cmDE = cm.getClass().getMethod("setMobileDataEnabled", new Class[]{Boolean.TYPE});
            if (!cmDE.isAccessible()) {
                cmDE.setAccessible(true);
            }
            if (enabled) {
                cmDE.invoke(cm, new Object[]{Boolean.valueOf(true)});
                return;
            }
            cmDE.invoke(cm, new Object[]{Boolean.valueOf(false)});
        } catch (Exception e) {
            Logging.Report(e, context);
        }
    }

    public static void SetMobileDataEnabled(Context context, boolean enabled) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService("phone");
            Method getITelephony = tm.getClass().getDeclaredMethod("getITelephony", new Class[0]);
            if (!getITelephony.isAccessible()) {
                getITelephony.setAccessible(true);
            }
            Object iTelephony = getITelephony.invoke(tm, new Object[0]);
            iTelephony.getClass().getMethod(enabled ? "enableDataConnectivity" : "disableDataConnectivity", new Class[0]).invoke(iTelephony, new Object[0]);
            Logging.Report("Set Mobile data " + enabled, context);
        } catch (Exception e) {
            Logging.Report(e, context);
            Logging.Report("Attempting to change data using method2", context);
            SetMobileDataEnabled2(context, enabled);
        }
    }
}
