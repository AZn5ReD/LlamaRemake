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

public class CarModeCondition extends EventCondition<CarModeCondition> {
    public static String MY_ID;
    public static int[] MY_TRIGGERS;
    public static int MY_TRIGGER_OFF;
    public static int MY_TRIGGER_ON;
    boolean _IsCarMode;

    static {
        EventMeta.InitCondition(EventFragment.CAR_MODE_CONDITION, new ConditionStaticInitter2() {
            public void UpdateStatics(String id, int[] triggers, int triggerOn, int triggerOff) {
                CarModeCondition.MY_ID = id;
                CarModeCondition.MY_TRIGGERS = triggers;
                CarModeCondition.MY_TRIGGER_ON = triggerOn;
                CarModeCondition.MY_TRIGGER_OFF = triggerOff;
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

    public CarModeCondition(boolean isCarMode) {
        this._IsCarMode = isCarMode;
    }

    public int TestCondition(StateChange state, Context context, Ref<EventTrigger> ref) {
        int i = 0;
        if (this._IsCarMode) {
            if (state.TriggerType == MY_TRIGGER_ON) {
                return 2;
            }
            if (state.IsCarMode) {
                return 1;
            }
            return 0;
        } else if (state.TriggerType == MY_TRIGGER_OFF) {
            return 2;
        } else {
            if (!state.IsCarMode) {
                i = 1;
            }
            return i;
        }
    }

    public boolean RenameArea(String oldName, String newName) {
        return false;
    }

    public void AppendConditionSimple(Context context, Appendable sb) throws IOException {
        if (this._IsCarMode) {
            sb.append(context.getString(R.string.hrWhenInCarMode));
        } else {
            sb.append(context.getString(R.string.hrWhenNotInCarMode));
        }
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 1;
    }

    public static CarModeCondition CreateFrom(String[] parts, int currentPart) {
        return new CarModeCondition(parts[currentPart + 1].equals("1"));
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(this._IsCarMode ? "1" : "0");
    }

    public PreferenceEx<CarModeCondition> CreatePreference(PreferenceActivity context) {
        final String on = context.getString(R.string.hrInCarMode);
        return CreateListPreference(context, context.getString(R.string.hrCarMode), new String[]{on, context.getString(R.string.hrNotInCarMode)}, this._IsCarMode ? on : context.getString(R.string.hrNotInCarMode), new OnGetValueEx<CarModeCondition>() {
            public CarModeCondition GetValue(Preference preference) {
                return new CarModeCondition(((ListPreference) preference).getValue().equals(on));
            }
        });
    }

    public String GetIsValidError(Context c) {
        return null;
    }
}
