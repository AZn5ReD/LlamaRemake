package com.kebab.Llama;

import android.content.Context;

public class LlamaPollingWakeLock {
    static SmartWakeLock _WakeLock = new SmartWakeLock("LlamaPoll");

    public static void AcquireLock(Context context, String reason) {
        _WakeLock.AcquireLock(context, reason);
    }

    public static void ReleaseLock(Context context) {
        _WakeLock.ReleaseLock(context);
    }
}
