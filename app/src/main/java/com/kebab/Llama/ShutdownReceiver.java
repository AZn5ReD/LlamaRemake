package com.kebab.Llama;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ShutdownReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (!((Boolean) LlamaSettings.LlamaWasExitted.GetValue(context)).booleanValue()) {
            Logging.Report("ShutdownReceiver received shutdown", context);
            if (Instances.Service != null) {
                Instances.Service.HandlePhoneShutdown();
            }
        }
    }
}
