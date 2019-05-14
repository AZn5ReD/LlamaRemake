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

public class WifiNetworkDisconnectedCondition extends WifiNetworkConditionBase<WifiNetworkDisconnectedCondition> {
    public static String MY_ID;
    public static int MY_TRIGGER;
    public static int[] MY_TRIGGERS;

    static {
        EventMeta.InitCondition(EventFragment.WIFI_NETWORK_DISCONNECTED_CONDITION, new ConditionStaticInitter2() {
            public void UpdateStatics(String id, int[] triggers, int trigger, int triggerOther) {
                WifiNetworkDisconnectedCondition.MY_ID = id;
                WifiNetworkDisconnectedCondition.MY_TRIGGERS = triggers;
                WifiNetworkDisconnectedCondition.MY_TRIGGER = trigger;
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

    public WifiNetworkDisconnectedCondition(String[] namesOrAddresses) {
        super(namesOrAddresses);
    }

    protected WifiNetworkDisconnectedCondition(String[] parts, int currentPart) {
        super(parts, currentPart);
    }

    /* Access modifiers changed, original: protected */
    public String GetFormattedConditionDescription(Context context, String deviceList) {
        return String.format(context.getString(R.string.hrWhenWifi1Disconnects), new Object[]{deviceList});
    }

    public int TestCondition(StateChange state, Context context, Ref<EventTrigger> ref) {
        if (state.TriggerType == MY_TRIGGER && ((ArrayHelpers.Contains(this._WifiNamesOrAddresses, ":ANY:") && state.CurrentWifiAddress.Get() == null) || ArrayHelpers.Contains(this._WifiNamesOrAddresses, state.DisconnectedWifiName) || ArrayHelpers.Contains(this._WifiNamesOrAddresses, state.DisconnectedWifiAddress))) {
            return 2;
        }
        if (ArrayHelpers.Contains(this._WifiNamesOrAddresses, ":ANY:") && state.CurrentWifiAddress.Get() != null) {
            return 0;
        }
        if (ArrayHelpers.Contains(this._WifiNamesOrAddresses, state.CurrentWifiAddress.Get())) {
            return 0;
        }
        if (ArrayHelpers.Contains(this._WifiNamesOrAddresses, state.CurrentWifiName.Get())) {
            return 0;
        }
        return 1;
    }

    public static WifiNetworkDisconnectedCondition CreateFrom(String[] parts, int currentPart) {
        return new WifiNetworkDisconnectedCondition(parts, currentPart);
    }

    /* Access modifiers changed, original: protected */
    public WifiNetworkDisconnectedCondition CreateSelf(String[] addresses) {
        return new WifiNetworkDisconnectedCondition(addresses);
    }

    /* Access modifiers changed, original: protected */
    public String GetPrefrenceDialogTitle(Context context) {
        return context.getString(R.string.hrConditionWifiDisconnected);
    }
}
