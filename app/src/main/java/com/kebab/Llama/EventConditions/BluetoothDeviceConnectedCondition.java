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

public class BluetoothDeviceConnectedCondition extends BluetoothDeviceConditionBase<BluetoothDeviceConnectedCondition> {
    public static String MY_ID;
    public static int MY_TRIGGER;
    public static int[] MY_TRIGGERS;

    static {
        EventMeta.InitCondition(EventFragment.BLUETOOTH_DEVICE_CONNECTED_CONDITION, new ConditionStaticInitter2() {
            public void UpdateStatics(String id, int[] triggers, int triggerActive, int triggerOpposite) {
                BluetoothDeviceConnectedCondition.MY_ID = id;
                BluetoothDeviceConnectedCondition.MY_TRIGGERS = triggers;
                BluetoothDeviceConnectedCondition.MY_TRIGGER = triggerActive;
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

    public BluetoothDeviceConnectedCondition(String[] bluetoothAddresses, String[] bluetoothNames) {
        super(bluetoothAddresses, bluetoothNames);
    }

    protected BluetoothDeviceConnectedCondition(String[] parts, int currentPart) {
        super(parts, currentPart);
    }

    /* Access modifiers changed, original: protected */
    public String GetFormattedConditionDescription(Context context, String deviceList) {
        return String.format(context.getString(R.string.hrWhen1IsConnected), new Object[]{deviceList});
    }

    public int TestCondition(StateChange state, Context context, Ref<EventTrigger> ref) {
        if (state.TriggerType == MY_TRIGGER && ArrayHelpers.Contains(this._BluetoothAddresses, state.TriggerBluetoothAddress)) {
            return 2;
        }
        for (String address : state.OtherBluetoothDevices) {
            if (ArrayHelpers.Contains(this._BluetoothAddresses, address)) {
                return 1;
            }
        }
        return 0;
    }

    public static BluetoothDeviceConnectedCondition CreateFrom(String[] parts, int currentPart) {
        return new BluetoothDeviceConnectedCondition(parts, currentPart);
    }

    /* Access modifiers changed, original: protected */
    public BluetoothDeviceConnectedCondition CreateSelf(String[] addresses, String[] names) {
        return new BluetoothDeviceConnectedCondition(addresses, names);
    }

    /* Access modifiers changed, original: protected */
    public String GetPrefrenceDialogTitle(Context context) {
        return context.getString(R.string.hrBluetoothDeviceConnected);
    }
}
