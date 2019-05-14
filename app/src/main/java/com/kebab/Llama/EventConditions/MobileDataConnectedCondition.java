package com.kebab.Llama.EventConditions;

import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import com.kebab.ListPreference;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.EventMeta;
import com.kebab.Llama.EventMeta.ConditionStaticInitter2;
import com.kebab.Llama.EventTrigger;
import com.kebab.Llama.R;
import com.kebab.Llama.StateChange;
import com.kebab.OnGetValueEx;
import com.kebab.PreferenceEx;
import com.kebab.Ref;
import java.io.IOException;

public class MobileDataConnectedCondition extends EventCondition<MobileDataConnectedCondition> {
    public static String MY_ID;
    public static int[] MY_TRIGGERS;
    public static int MY_TRIGGER_OFF;
    public static int MY_TRIGGER_ON;
    boolean _IsEnabled;

    static {
        EventMeta.InitCondition(EventFragment.MOBILE_DATA_CONNECTED, new ConditionStaticInitter2() {
            public void UpdateStatics(String id, int[] triggers, int onTrigger, int offTrigger) {
                MobileDataConnectedCondition.MY_ID = id;
                MobileDataConnectedCondition.MY_TRIGGERS = triggers;
                MobileDataConnectedCondition.MY_TRIGGER_ON = onTrigger;
                MobileDataConnectedCondition.MY_TRIGGER_OFF = offTrigger;
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

    public MobileDataConnectedCondition(boolean isEnabled) {
        this._IsEnabled = isEnabled;
    }

    public int TestCondition(StateChange state, Context context, Ref<EventTrigger> ref) {
        if (state.MobileDataConnected) {
            if (this._IsEnabled) {
                if (state.TriggerType == MY_TRIGGER_ON) {
                    return 2;
                }
                return 1;
            }
        } else if (!this._IsEnabled) {
            if (state.TriggerType != MY_TRIGGER_OFF) {
                return 1;
            }
            return 2;
        }
        return 0;
    }

    public boolean RenameArea(String oldName, String newName) {
        return false;
    }

    public void AppendConditionSimple(Context context, Appendable sb) throws IOException {
        if (this._IsEnabled) {
            sb.append(context.getString(R.string.hrWhenMobileDataConnected));
        } else {
            sb.append(context.getString(R.string.hrWhenMobileDataNotConnected));
        }
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 1;
    }

    public static MobileDataConnectedCondition CreateFrom(String[] parts, int currentPart) {
        return new MobileDataConnectedCondition(parts[currentPart + 1].equals("1"));
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(this._IsEnabled ? "1" : "0");
    }

    public PreferenceEx<MobileDataConnectedCondition> CreatePreference(PreferenceActivity context) {
        final String on = context.getString(R.string.hrMobileDataConnected);
        return CreateListPreference(context, context.getString(R.string.hrConditionMobileDataConnected), new String[]{on, context.getString(R.string.hrMobileDataNotConnected)}, this._IsEnabled ? on : context.getString(R.string.hrMobileDataNotConnected), new OnGetValueEx<MobileDataConnectedCondition>() {
            public MobileDataConnectedCondition GetValue(Preference preference) {
                return new MobileDataConnectedCondition(((ListPreference) preference).getValue().equals(on));
            }
        });
    }

    public String GetIsValidError(Context c) {
        return null;
    }
}
