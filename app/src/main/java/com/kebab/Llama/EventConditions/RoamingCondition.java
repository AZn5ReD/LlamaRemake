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

public class RoamingCondition extends EventCondition<RoamingCondition> {
    public static String MY_ID;
    public static int[] MY_TRIGGERS;
    public static int MY_TRIGGER_OFF;
    public static int MY_TRIGGER_ON;
    boolean _IsRoaming;

    static {
        EventMeta.InitCondition(EventFragment.ROAMING_CONDITION, new ConditionStaticInitter2() {
            public void UpdateStatics(String id, int[] triggers, int onTrigger, int offTrigger) {
                RoamingCondition.MY_ID = id;
                RoamingCondition.MY_TRIGGERS = triggers;
                RoamingCondition.MY_TRIGGER_ON = onTrigger;
                RoamingCondition.MY_TRIGGER_OFF = offTrigger;
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

    public RoamingCondition(boolean isRoaming) {
        this._IsRoaming = isRoaming;
    }

    public int TestCondition(StateChange state, Context context, Ref<EventTrigger> ref) {
        if (state.IsRoaming) {
            if (this._IsRoaming) {
                if (state.TriggerType == MY_TRIGGER_ON) {
                    return 2;
                }
                return 1;
            }
        } else if (!this._IsRoaming) {
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
        if (this._IsRoaming) {
            sb.append(context.getString(R.string.hrWhenRoaming));
        } else {
            sb.append(context.getString(R.string.hrWhenNotRoaming));
        }
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 1;
    }

    public static RoamingCondition CreateFrom(String[] parts, int currentPart) {
        return new RoamingCondition(parts[currentPart + 1].equals("1"));
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(this._IsRoaming ? "1" : "0");
    }

    public PreferenceEx<RoamingCondition> CreatePreference(PreferenceActivity context) {
        final String on = context.getString(R.string.hrRoaming);
        return CreateListPreference(context, context.getString(R.string.hrConditionRoaming), new String[]{on, context.getString(R.string.hrNotRoaming)}, this._IsRoaming ? on : context.getString(R.string.hrNotRoaming), new OnGetValueEx<RoamingCondition>() {
            public RoamingCondition GetValue(Preference preference) {
                return new RoamingCondition(((ListPreference) preference).getValue().equals(on));
            }
        });
    }

    public String GetIsValidError(Context c) {
        return null;
    }
}
