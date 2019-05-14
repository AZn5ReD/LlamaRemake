package com.kebab.Llama.EventConditions;

import android.content.Context;
import com.kebab.ArrayHelpers;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.EventMeta;
import com.kebab.Llama.EventMeta.ConditionStaticInitter2;
import com.kebab.Llama.EventTrigger;
import com.kebab.Llama.R;
import com.kebab.Llama.StateChange;
import com.kebab.Ref;

public class BluetoothDeviceDisconnectedCondition extends BluetoothDeviceConditionBase<BluetoothDeviceDisconnectedCondition> {
    public static String MY_ID;
    public static int MY_TRIGGER;
    public static int[] MY_TRIGGERS;

    static {
        EventMeta.InitCondition(EventFragment.BLUETOOTH_DEVICE_DISCONNECTED_CONDITION, new ConditionStaticInitter2() {
            public void UpdateStatics(String id, int[] triggers, int trigger, int triggerOther) {
                BluetoothDeviceDisconnectedCondition.MY_ID = id;
                BluetoothDeviceDisconnectedCondition.MY_TRIGGERS = triggers;
                BluetoothDeviceDisconnectedCondition.MY_TRIGGER = trigger;
            }
        });
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return MY_ID;
    }

    public int[] getEventTriggers() {
        return MY_TRIGGERS;
    }

    public BluetoothDeviceDisconnectedCondition(String[] bluetoothAddresses, String[] bluetoothNames) {
        super(bluetoothAddresses, bluetoothNames);
    }

    protected BluetoothDeviceDisconnectedCondition(String[] parts, int currentPart) {
        super(parts, currentPart);
    }

    /* Access modifiers changed, original: protected */
    public String GetFormattedConditionDescription(Context context, String deviceList) {
        return String.format(context.getString(R.string.hrWhen1Disconnects), new Object[]{deviceList});
    }

    public int TestCondition(StateChange state, Context context, Ref<EventTrigger> ref) {
        if (state.TriggerType == MY_TRIGGER && ArrayHelpers.Contains(this._BluetoothAddresses, state.TriggerBluetoothAddress)) {
            return 2;
        }
        return 0;
    }

    public static BluetoothDeviceDisconnectedCondition CreateFrom(String[] parts, int currentPart) {
        return new BluetoothDeviceDisconnectedCondition(parts, currentPart);
    }

    /* Access modifiers changed, original: protected */
    public BluetoothDeviceDisconnectedCondition CreateSelf(String[] addresses, String[] names) {
        return new BluetoothDeviceDisconnectedCondition(addresses, names);
    }

    /* Access modifiers changed, original: protected */
    public String GetPrefrenceDialogTitle(Context context) {
        return context.getString(R.string.hrBluetoothDeviceDisconnected);
    }
}
