package com.kebab.Llama;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class IntentReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (!((Boolean) LlamaSettings.LlamaWasExitted.GetValue(context)).booleanValue()) {
            Logging.Report("IntentReceiver.onReceive", context);
            intent.setClass(context, LlamaService.class);
            if (isInitialStickyBroadcast()) {
                intent.putExtra(Constants.EXTRA_INITIAL_STICKY, true);
            }
            context.startService(intent);
        }
    }
}
