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

public class ScreenBacklightCondition extends EventCondition<ScreenBacklightCondition> {
    public static String MY_ID;
    public static int[] MY_TRIGGERS;
    public static int MY_TRIGGER_OFF;
    public static int MY_TRIGGER_ON;
    boolean _IsOn;

    static {
        EventMeta.InitCondition(EventFragment.SCREEN_BACKLIGHT_CONDITION, new ConditionStaticInitter2() {
            public void UpdateStatics(String id, int[] triggers, int onTrigger, int offTrigger) {
                ScreenBacklightCondition.MY_ID = id;
                ScreenBacklightCondition.MY_TRIGGERS = triggers;
                ScreenBacklightCondition.MY_TRIGGER_ON = onTrigger;
                ScreenBacklightCondition.MY_TRIGGER_OFF = offTrigger;
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

    public ScreenBacklightCondition(boolean isOn) {
        this._IsOn = isOn;
    }

    public int TestCondition(StateChange state, Context context, Ref<EventTrigger> ref) {
        if (this._IsOn) {
            if (state.TriggerType == MY_TRIGGER_ON) {
                return 2;
            }
            if (state.ScreenIsOn) {
                return 1;
            }
            return 0;
        } else if (state.TriggerType == MY_TRIGGER_OFF) {
            return 2;
        } else {
            if (state.ScreenIsOn) {
                return 0;
            }
            return 1;
        }
    }

    public boolean RenameArea(String oldName, String newName) {
        return false;
    }

    public void AppendConditionSimple(Context context, Appendable sb) throws IOException {
        if (this._IsOn) {
            sb.append(context.getString(R.string.hrWhenScreenIsOn));
        } else {
            sb.append(context.getString(R.string.hrWhenScreenIsOff));
        }
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 1;
    }

    public static ScreenBacklightCondition CreateFrom(String[] parts, int currentPart) {
        return new ScreenBacklightCondition(parts[currentPart + 1].equals("1"));
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(this._IsOn ? "1" : "0");
    }

    public PreferenceEx<ScreenBacklightCondition> CreatePreference(PreferenceActivity context) {
        final String on = context.getString(R.string.hrScreenOn);
        return CreateListPreference(context, context.getString(R.string.hrScreenOnOff), new String[]{on, context.getString(R.string.hrScreenOff)}, this._IsOn ? on : context.getString(R.string.hrScreenOff), new OnGetValueEx<ScreenBacklightCondition>() {
            public ScreenBacklightCondition GetValue(Preference preference) {
                return new ScreenBacklightCondition(((ListPreference) preference).getValue().equals(on));
            }
        });
    }

    public String GetIsValidError(Context context) {
        return null;
    }
}
