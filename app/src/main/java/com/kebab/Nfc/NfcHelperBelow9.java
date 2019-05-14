package com.kebab.Nfc;

import android.content.Context;
import android.os.Build.VERSION;

public class NfcHelperBelow9 {
    public static boolean isSupported(Context context) {
        if (VERSION.SDK_INT <= 8) {
            return false;
        }
        return NfcHelper9Plus.isSupported(context);
    }

    public static boolean isEnabled(Context context) {
        if (VERSION.SDK_INT <= 8) {
            return false;
        }
        return NfcHelper9Plus.isEnabled(context);
    }
}
