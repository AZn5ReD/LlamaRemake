package com.kebab.Llama.EventActions;

import android.content.Context;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.R;

public class ToggleBluetoothAction extends TogglableConnectableAction<ToggleBluetoothAction> {
    public ToggleBluetoothAction(int toggleType, int atLeastOnForMinutes) {
        super(toggleType, atLeastOnForMinutes);
    }

    public ToggleBluetoothAction(String[] parts, int currentPart) {
        super(parts, currentPart);
    }

    /* Access modifiers changed, original: protected */
    public ToggleBluetoothAction CreateSelf(int toggleType, int atLeastOnForMinutes) {
        return new ToggleBluetoothAction(toggleType, atLeastOnForMinutes);
    }

    /* Access modifiers changed, original: protected */
    public String GetDescriptionOff(Context context) {
        return context.getString(R.string.hrDisableBluetooth);
    }

    /* Access modifiers changed, original: protected */
    public String GetDescriptionOn(Context context) {
        return context.getString(R.string.hrEnableBluetooth);
    }

    /* Access modifiers changed, original: protected */
    public String GetPreferenceTitleText(Context context) {
        return context.getString(R.string.hrToggleBluetooth);
    }

    /* Access modifiers changed, original: protected */
    public String GetPreferenceValueDisable(Context context) {
        return context.getString(R.string.hrBluetoothOff);
    }

    /* Access modifiers changed, original: protected */
    public String GetPreferenceValueEnable(Context context) {
        return context.getString(R.string.hrBluetoothOn);
    }

    /* Access modifiers changed, original: protected */
    public void PerformActionInternal(LlamaService service, boolean turnOn, boolean turnOffEventIfConnected) {
        service.ToggleBluetooth(turnOn, turnOffEventIfConnected);
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.TOGGLE_BLUETOOTH_ACTION;
    }

    public static ToggleBluetoothAction CreateFrom(String[] parts, int currentPart) {
        return new ToggleBluetoothAction(parts, currentPart);
    }

    public boolean IsHarmful() {
        return false;
    }
}
