package com.kebab.Llama.EventActions;

import android.app.Activity;
import android.content.Context;
import com.kebab.Llama.Event;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.R;

public class ChangeLlamaLocationPollingAction extends ChangeLlamaPollingActionBase<ChangeLlamaLocationPollingAction> {
    public ChangeLlamaLocationPollingAction(int pollingIntervalMins) {
        super(pollingIntervalMins);
    }

    public void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        service.ChangeAndroidLocationPolling(this._PollingIntervalMins);
    }

    public static ChangeLlamaLocationPollingAction CreateFrom(String[] parts, int currentPart) {
        return new ChangeLlamaLocationPollingAction(Integer.parseInt(parts[currentPart + 1]));
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.CHANGE_LLAMA_LOCATION_POLLING_ACTION;
    }

    /* Access modifiers changed, original: protected */
    public ChangeLlamaLocationPollingAction Create(int pollingMinutes) {
        return new ChangeLlamaLocationPollingAction(pollingMinutes);
    }

    /* Access modifiers changed, original: protected */
    public String getDescriptionText(Context context) {
        return context.getString(R.string.hrAndroidLocationPolling);
    }

    /* Access modifiers changed, original: protected */
    public String getPreferenceTitle(Context context) {
        return context.getString(R.string.hrChangeAndroidLocationPolling);
    }

    public boolean IsHarmful() {
        return false;
    }
}
