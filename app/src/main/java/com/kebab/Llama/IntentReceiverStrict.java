package com.kebab.Llama;

import android.content.Context;
import android.content.Intent;

public class IntentReceiverStrict extends IntentReceiver {
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent == null ? "" : intent.getAction();
        if ("android.intent.action.AIRPLANE_MODE".equals(intentAction) || "android.media.VOLUME_CHANGED_ACTION".equals(intentAction)) {
            super.onReceive(context, intent);
        } else {
            Logging.Report(getClass().getName() + " refused to accept " + intent.getAction(), context);
        }
    }
}
