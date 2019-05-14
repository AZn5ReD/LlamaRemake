package com.kebab.ApiCompat;

import android.os.Build.VERSION;

public class IntentCompat {
    public static int FLAG_ACTIVITY_CLEAR_TASK = 32768;

    public static boolean SupportsClearTask() {
        return VERSION.SDK_INT >= 11;
    }
}
