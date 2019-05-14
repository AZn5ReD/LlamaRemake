package com.kebab.Llama;

import android.content.Context;

public class CellPollWakeLock {
    static SmartWakeLock _WakeLock = new SmartWakeLock("CellPollWakeLock");
    static SmartWakeLock _WakeLock2 = new SmartWakeLock("CellPollScreenWakeLock", 268435462);

    public static void AcquireLock(Context context, String reason) {
        _WakeLock.AcquireLock(context, reason);
    }

    public static void ReleaseLock(Context context) {
        _WakeLock.ReleaseLock(context);
    }

    public static void AcquireScreenLock(Context context, String reason) {
        _WakeLock2.AcquireLock(context, reason);
    }

    public static void ReleaseScreenLock(Context context) {
        _WakeLock2.ReleaseLock(context);
    }
}
