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

public class DeskDockCondition extends EventCondition<DeskDockCondition> {
    public static String MY_ID;
    public static int[] MY_TRIGGERS;
    public static int MY_TRIGGER_OFF;
    public static int MY_TRIGGER_ON;
    boolean _IsDeskMode;

    static {
        EventMeta.InitCondition(EventFragment.DESK_DOCK_CONDITION, new ConditionStaticInitter2() {
            public void UpdateStatics(String id, int[] triggers, int triggerOn, int triggerOff) {
                DeskDockCondition.MY_ID = id;
                DeskDockCondition.MY_TRIGGERS = triggers;
                DeskDockCondition.MY_TRIGGER_ON = triggerOn;
                DeskDockCondition.MY_TRIGGER_OFF = triggerOff;
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

    public DeskDockCondition(boolean isDeskMode) {
        this._IsDeskMode = isDeskMode;
    }

    public int TestCondition(StateChange state, Context context, Ref<EventTrigger> ref) {
        int i = 0;
        if (this._IsDeskMode) {
            if (state.TriggerType == MY_TRIGGER_ON) {
                return 2;
            }
            if (state.IsDeskMode) {
                return 1;
            }
            return 0;
        } else if (state.TriggerType == MY_TRIGGER_OFF) {
            return 2;
        } else {
            if (!state.IsDeskMode) {
                i = 1;
            }
            return i;
        }
    }

    public boolean RenameArea(String oldName, String newName) {
        return false;
    }

    public void AppendConditionSimple(Context context, Appendable sb) throws IOException {
        if (this._IsDeskMode) {
            sb.append(context.getString(R.string.hrWhenInDeskDock));
        } else {
            sb.append(context.getString(R.string.hrWhenNotInDeskDock));
        }
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 1;
    }

    public static DeskDockCondition CreateFrom(String[] parts, int currentPart) {
        return new DeskDockCondition(parts[currentPart + 1].equals("1"));
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(this._IsDeskMode ? "1" : "0");
    }

    public PreferenceEx<DeskDockCondition> CreatePreference(PreferenceActivity context) {
        final String on = context.getString(R.string.hrInDeskDock);
        return CreateListPreference(context, context.getString(R.string.hrConditionDeskDock), new String[]{on, context.getString(R.string.hrNotInDeskDock)}, this._IsDeskMode ? on : context.getString(R.string.hrNotInDeskDock), new OnGetValueEx<DeskDockCondition>() {
            public DeskDockCondition GetValue(Preference preference) {
                return new DeskDockCondition(((ListPreference) preference).getValue().equals(on));
            }
        });
    }

    public String GetIsValidError(Context c) {
        return null;
    }
}
