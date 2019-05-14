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

public class AirplaneModeCondition extends EventCondition<AirplaneModeCondition> {
    public static String MY_ID;
    public static int[] MY_TRIGGERS;
    public static int MY_TRIGGER_OFF;
    public static int MY_TRIGGER_ON;
    boolean _IsEnabled;

    static {
        EventMeta.InitCondition(EventFragment.AIRPLANE_MODE_CONDITION, new ConditionStaticInitter2() {
            public void UpdateStatics(String id, int[] triggers, int onTrigger, int offTrigger) {
                AirplaneModeCondition.MY_ID = id;
                AirplaneModeCondition.MY_TRIGGERS = triggers;
                AirplaneModeCondition.MY_TRIGGER_ON = onTrigger;
                AirplaneModeCondition.MY_TRIGGER_OFF = offTrigger;
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

    public AirplaneModeCondition(boolean isEnabled) {
        this._IsEnabled = isEnabled;
    }

    public int TestCondition(StateChange state, Context context, Ref<EventTrigger> ref) {
        if (state.IsAirplaneModeEnabled) {
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
            sb.append(context.getString(R.string.hrWhenInAirplaneMode));
        } else {
            sb.append(context.getString(R.string.hrWhenNotInAirplaneMode));
        }
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 1;
    }

    public static AirplaneModeCondition CreateFrom(String[] parts, int currentPart) {
        return new AirplaneModeCondition(parts[currentPart + 1].equals("1"));
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(this._IsEnabled ? "1" : "0");
    }

    public PreferenceEx<AirplaneModeCondition> CreatePreference(PreferenceActivity context) {
        final String on = context.getString(R.string.hrInAirplaneMode);
        return CreateListPreference(context, context.getString(R.string.hrConditionAirplaneMode), new String[]{on, context.getString(R.string.hrNotInAirplaneMode)}, this._IsEnabled ? on : context.getString(R.string.hrNotInAirplaneMode), new OnGetValueEx<AirplaneModeCondition>() {
            public AirplaneModeCondition GetValue(Preference preference) {
                return new AirplaneModeCondition(((ListPreference) preference).getValue().equals(on));
            }
        });
    }

    public String GetIsValidError(Context c) {
        return null;
    }
}
