package com.kebab.Llama.EventActions;

import android.content.Context;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.R;

public class ToggleWifiAction extends TogglableConnectableAction<ToggleWifiAction> {
    public ToggleWifiAction(int toggleType, int atLeastOnForMinutes) {
        super(toggleType, atLeastOnForMinutes);
    }

    public ToggleWifiAction(String[] parts, int currentPart) {
        super(parts, currentPart);
    }

    /* Access modifiers changed, original: protected */
    public ToggleWifiAction CreateSelf(int toggleType, int atLeastOnForMinutes) {
        return new ToggleWifiAction(toggleType, atLeastOnForMinutes);
    }

    /* Access modifiers changed, original: protected */
    public String GetDescriptionOff(Context context) {
        return context.getString(R.string.hrDisableWifi);
    }

    /* Access modifiers changed, original: protected */
    public String GetDescriptionOn(Context context) {
        return context.getString(R.string.hrEnableWifi);
    }

    /* Access modifiers changed, original: protected */
    public String GetPreferenceTitleText(Context context) {
        return context.getString(R.string.hrToggleWifi);
    }

    /* Access modifiers changed, original: protected */
    public String GetPreferenceValueDisable(Context context) {
        return context.getString(R.string.hrWifiOff);
    }

    /* Access modifiers changed, original: protected */
    public String GetPreferenceValueEnable(Context context) {
        return context.getString(R.string.hrWifiOn);
    }

    /* Access modifiers changed, original: protected */
    public void PerformActionInternal(LlamaService service, boolean turnOn, boolean turnOffEventIfConnected) {
        service.ToggleWifi(turnOn, turnOffEventIfConnected);
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.TOGGLE_WIFI_ACTION;
    }

    public static ToggleWifiAction CreateFrom(String[] parts, int currentPart) {
        return new ToggleWifiAction(parts, currentPart);
    }

    public boolean IsHarmful() {
        return false;
    }
}
