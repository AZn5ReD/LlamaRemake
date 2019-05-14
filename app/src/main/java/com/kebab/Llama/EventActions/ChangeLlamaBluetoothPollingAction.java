package com.kebab.Llama.EventActions;

import android.app.Activity;
import android.content.Context;
import com.kebab.Llama.Event;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.R;

public class ChangeLlamaBluetoothPollingAction extends ChangeLlamaPollingActionBase<ChangeLlamaBluetoothPollingAction> {
    public ChangeLlamaBluetoothPollingAction(int pollingIntervalMins) {
        super(pollingIntervalMins);
    }

    public void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        service.ChangeBluetoothPolling(this._PollingIntervalMins);
    }

    public static ChangeLlamaBluetoothPollingAction CreateFrom(String[] parts, int currentPart) {
        return new ChangeLlamaBluetoothPollingAction(Integer.parseInt(parts[currentPart + 1]));
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.CHANGE_LLAMA_BLUETOOTH_POLLING_ACTION;
    }

    /* Access modifiers changed, original: protected */
    public ChangeLlamaBluetoothPollingAction Create(int pollingMinutes) {
        return new ChangeLlamaBluetoothPollingAction(pollingMinutes);
    }

    /* Access modifiers changed, original: protected */
    public String getDescriptionText(Context context) {
        return context.getString(R.string.hrLlamaBluetoothPolling);
    }

    /* Access modifiers changed, original: protected */
    public String getPreferenceTitle(Context context) {
        return context.getString(R.string.hrChangeLlamaBluetoothPolling);
    }

    public boolean IsHarmful() {
        return false;
    }
}
