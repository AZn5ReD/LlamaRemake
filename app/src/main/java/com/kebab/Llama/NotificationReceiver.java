package com.kebab.Llama;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Intent svc = new Intent(context, LlamaUi.class);
        svc.addFlags(536870912);
        svc.addFlags(268435456);
        context.startActivity(svc);
    }
}
