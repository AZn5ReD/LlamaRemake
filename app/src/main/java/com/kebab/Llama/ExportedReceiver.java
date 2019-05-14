package com.kebab.Llama;

import android.content.Context;
import android.content.Intent;

public class ExportedReceiver extends IntentReceiver {
    public void onReceive(Context context, Intent intent) {
        if (Constants.ACTION_SET_LLAMA_VARIABLE.equals(intent == null ? "" : intent.getAction())) {
            super.onReceive(context, intent);
        } else {
            Logging.Report(getClass().getName() + " refused to accept " + intent.getAction(), context);
        }
    }
}
