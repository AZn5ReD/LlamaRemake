package com.kebab.Llama;

import android.content.Context;

public class BluetoothEnableWakeLock {
    static SmartWakeLock _WakeLock = new SmartWakeLock("BluetoothEnable");

    public static void AcquireLock(Context context, String reason) {
        _WakeLock.AcquireLock(context, reason);
    }

    public static void ReleaseLock(Context context) {
        _WakeLock.ReleaseLock(context);
    }

    public static boolean IsAcquired(Context context) {
        return _WakeLock.IsAcquired(context);
    }
}
