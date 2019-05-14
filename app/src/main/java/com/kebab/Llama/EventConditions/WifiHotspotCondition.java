package com.kebab.Llama.EventConditions;

import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import com.kebab.ListPreference;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.EventMeta;
import com.kebab.Llama.EventMeta.ConditionStaticInitterNoTriggers;
import com.kebab.Llama.EventTrigger;
import com.kebab.Llama.R;
import com.kebab.Llama.StateChange;
import com.kebab.OnGetValueEx;
import com.kebab.PreferenceEx;
import com.kebab.Ref;
import java.io.IOException;

public class WifiHotspotCondition extends EventCondition<WifiHotspotCondition> {
    public static String MY_ID;
    public static int[] MY_TRIGGERS;
    boolean _IsOn;

    static {
        EventMeta.InitCondition(EventFragment.WIFI_HOTSPOT_CONDITION, new ConditionStaticInitterNoTriggers() {
            public void UpdateStatics(String id, int[] triggers) {
                WifiHotspotCondition.MY_ID = id;
                WifiHotspotCondition.MY_TRIGGERS = triggers;
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

    public WifiHotspotCondition(boolean isOn) {
        this._IsOn = isOn;
    }

    public int TestCondition(StateChange state, Context context, Ref<EventTrigger> ref) {
        int i = 0;
        if (!state.WifiHotSpotEnabled(context)) {
            if (!this._IsOn) {
                i = 1;
            }
            return i;
        } else if (this._IsOn) {
            return 1;
        } else {
            return 0;
        }
    }

    public boolean RenameArea(String oldName, String newName) {
        return false;
    }

    public void AppendConditionSimple(Context context, Appendable sb) throws IOException {
        if (this._IsOn) {
            sb.append(context.getString(R.string.hrWhenWifiHotspotIsEnabled));
        } else {
            sb.append(context.getString(R.string.hrWhenWifiHotspotIsDisabled));
        }
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 1;
    }

    public static WifiHotspotCondition CreateFrom(String[] parts, int currentPart) {
        return new WifiHotspotCondition(parts[currentPart + 1].equals("1"));
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(this._IsOn ? "1" : "0");
    }

    public PreferenceEx<WifiHotspotCondition> CreatePreference(PreferenceActivity context) {
        final String on = context.getString(R.string.hrEnabled);
        return CreateListPreference(context, context.getString(R.string.hrConditionWifiHotspot), new String[]{on, context.getString(R.string.hrDisabled)}, this._IsOn ? on : context.getString(R.string.hrDisabled), new OnGetValueEx<WifiHotspotCondition>() {
            public WifiHotspotCondition GetValue(Preference preference) {
                return new WifiHotspotCondition(((ListPreference) preference).getValue().equals(on));
            }
        });
    }

    public String GetIsValidError(Context context) {
        return null;
    }
}
