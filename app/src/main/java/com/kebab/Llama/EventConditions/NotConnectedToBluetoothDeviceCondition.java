package com.kebab.Llama.EventConditions;

import android.content.Context;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.EventMeta;
import com.kebab.Llama.EventMeta.ConditionStaticInitter2;
import com.kebab.Llama.EventTrigger;
import com.kebab.Llama.R;
import com.kebab.Llama.StateChange;
import com.kebab.Ref;

public class NotConnectedToBluetoothDeviceCondition extends BluetoothDeviceConditionBase<NotConnectedToBluetoothDeviceCondition> {
    public static String MY_ID;
    public static int MY_TRIGGER;
    public static int[] MY_TRIGGERS;

    static {
        EventMeta.InitCondition(EventFragment.BLUETOOTH_DEVICE_NOT_CONNECTED, new ConditionStaticInitter2() {
            public void UpdateStatics(String id, int[] triggers, int trigger, int triggerOther) {
                NotConnectedToBluetoothDeviceCondition.MY_ID = id;
                NotConnectedToBluetoothDeviceCondition.MY_TRIGGERS = triggers;
                NotConnectedToBluetoothDeviceCondition.MY_TRIGGER = trigger;
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

    public NotConnectedToBluetoothDeviceCondition(String[] bluetoothAddresses, String[] bluetoothNames) {
        super(bluetoothAddresses, bluetoothNames);
    }

    protected NotConnectedToBluetoothDeviceCondition(String[] parts, int currentPart) {
        super(parts, currentPart);
    }

    /* Access modifiers changed, original: protected */
    public String GetFormattedConditionDescription(Context context, String deviceList) {
        return String.format(context.getString(R.string.hrWhen1IsNotConnected), new Object[]{deviceList});
    }

    public int TestCondition(StateChange state, Context context, Ref<EventTrigger> ref) {
        boolean foundDisconnectedTrigger = false;
        for (String address : this._BluetoothAddresses) {
            if (state.OtherBluetoothDevices.contains(address)) {
                return 0;
            }
            if (state.TriggerType == MY_TRIGGER && address.equals(state.TriggerBluetoothAddress)) {
                foundDisconnectedTrigger = true;
            }
        }
        return foundDisconnectedTrigger ? 2 : 1;
    }

    public static NotConnectedToBluetoothDeviceCondition CreateFrom(String[] parts, int currentPart) {
        return new NotConnectedToBluetoothDeviceCondition(parts, currentPart);
    }

    /* Access modifiers changed, original: protected */
    public NotConnectedToBluetoothDeviceCondition CreateSelf(String[] addresses, String[] names) {
        return new NotConnectedToBluetoothDeviceCondition(addresses, names);
    }

    /* Access modifiers changed, original: protected */
    public String GetPrefrenceDialogTitle(Context context) {
        return context.getString(R.string.hrBluetoothDeviceNotConnected);
    }
}
