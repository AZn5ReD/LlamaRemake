package com.kebab.Llama;

import android.content.Context;

public class WifiPollWakeLock {
    static SmartWakeLock _WakeLock = new SmartWakeLock("WifiPoll");

    public static void AcquireLock(Context context, String reason) {
        _WakeLock.AcquireLock(context, reason);
    }

    public static void ReleaseLock(Context context) {
        _WakeLock.ReleaseLock(context);
    }
}
