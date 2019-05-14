package com.kebab.Llama.EventActions;

import android.app.Activity;
import android.content.Context;
import com.kebab.Llama.Event;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.R;

public class ChangeLlamaWifiPollingAction extends ChangeLlamaPollingActionBase<ChangeLlamaWifiPollingAction> {
    public ChangeLlamaWifiPollingAction(int pollingIntervalMins) {
        super(pollingIntervalMins);
    }

    public void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        service.ChangeWifiPolling(this._PollingIntervalMins);
    }

    public static ChangeLlamaWifiPollingAction CreateFrom(String[] parts, int currentPart) {
        return new ChangeLlamaWifiPollingAction(Integer.parseInt(parts[currentPart + 1]));
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.CHANGE_LLAMA_WIFI_POLLING_ACTION;
    }

    /* Access modifiers changed, original: protected */
    public ChangeLlamaWifiPollingAction Create(int pollingMinutes) {
        return new ChangeLlamaWifiPollingAction(pollingMinutes);
    }

    /* Access modifiers changed, original: protected */
    public String getDescriptionText(Context context) {
        return context.getString(R.string.hrLlamaWifiPolling);
    }

    /* Access modifiers changed, original: protected */
    public String getPreferenceTitle(Context context) {
        return context.getString(R.string.hrChangeLlamaWifiPolling);
    }

    public boolean IsHarmful() {
        return false;
    }
}
