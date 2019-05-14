package com.kebab.Llama;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RtcReceiver extends BroadcastReceiver {
    static SmartWakeLock _WakeLock = new SmartWakeLock("RtcWakeLock");

    public void onReceive(Context context, Intent intent) {
        if (!((Boolean) LlamaSettings.LlamaWasExitted.GetValue(context)).booleanValue()) {
            AcquireLock(context, intent == null ? "nullIntent" : "action=" + intent.getAction());
            intent.setClass(context, LlamaService.class);
            context.startService(intent);
        }
    }

    public static void AcquireLock(Context context, String reason) {
        _WakeLock.AcquireLock(context, reason);
    }

    public static void ReleaseLock(Context context) {
        _WakeLock.ReleaseLock(context);
    }
}
