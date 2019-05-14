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

public class WifiNetworkConnectedCondition extends WifiNetworkConditionBase<WifiNetworkConnectedCondition> {
    public static String MY_ID;
    public static int MY_TRIGGER;
    public static int[] MY_TRIGGERS;

    static {
        EventMeta.InitCondition(EventFragment.WIFI_NETWORK_CONNECTED_CONDITION, new ConditionStaticInitter2() {
            public void UpdateStatics(String id, int[] triggers, int trigger, int triggerOther) {
                WifiNetworkConnectedCondition.MY_ID = id;
                WifiNetworkConnectedCondition.MY_TRIGGERS = triggers;
                WifiNetworkConnectedCondition.MY_TRIGGER = trigger;
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

    public WifiNetworkConnectedCondition(String[] namesOrAddresses) {
        super(namesOrAddresses);
    }

    protected WifiNetworkConnectedCondition(String[] parts, int currentPart) {
        super(parts, currentPart);
    }

    /* Access modifiers changed, original: protected */
    public String GetFormattedConditionDescription(Context context, String deviceList) {
        return String.format(context.getString(R.string.hrWhenWifi1Connects), new Object[]{deviceList});
    }

    public int TestCondition(StateChange state, Context context, Ref<EventTrigger> ref) {
        if (state.TriggerType == MY_TRIGGER) {
            if ((ArrayHelpers.Contains(this._WifiNamesOrAddresses, ":ANY:") && state.CurrentWifiAddress.Get() != null) || ArrayHelpers.Contains(this._WifiNamesOrAddresses, state.CurrentWifiName.Get()) || ArrayHelpers.Contains(this._WifiNamesOrAddresses, state.CurrentWifiAddress.Get())) {
                return 2;
            }
        } else if (ArrayHelpers.Contains(this._WifiNamesOrAddresses, ":ANY:") && state.CurrentWifiAddress.Get() != null) {
            return 1;
        } else {
            if (ArrayHelpers.Contains(this._WifiNamesOrAddresses, state.CurrentWifiName.Get())) {
                return 1;
            }
            if (ArrayHelpers.Contains(this._WifiNamesOrAddresses, state.CurrentWifiAddress.Get())) {
                return 1;
            }
        }
        return 0;
    }

    public static WifiNetworkConnectedCondition CreateFrom(String[] parts, int currentPart) {
        return new WifiNetworkConnectedCondition(parts, currentPart);
    }

    /* Access modifiers changed, original: protected */
    public WifiNetworkConnectedCondition CreateSelf(String[] namesOrAddresses) {
        return new WifiNetworkConnectedCondition(namesOrAddresses);
    }

    /* Access modifiers changed, original: protected */
    public String GetPrefrenceDialogTitle(Context context) {
        return context.getString(R.string.hrConditionWifiConnected);
    }
}
