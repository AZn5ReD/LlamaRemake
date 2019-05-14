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

public class PhoneRebootCondition extends EventCondition<PhoneRebootCondition> {
    public static String MY_ID;
    public static int[] MY_TRIGGERS;
    public static int MY_TRIGGER_OFF;
    public static int MY_TRIGGER_ON;
    boolean _IsOnBoot;

    static {
        EventMeta.InitCondition(EventFragment.PHONE_REBOOT_CONDITION, new ConditionStaticInitter2() {
            public void UpdateStatics(String id, int[] triggers, int onTrigger, int offTrigger) {
                PhoneRebootCondition.MY_ID = id;
                PhoneRebootCondition.MY_TRIGGERS = triggers;
                PhoneRebootCondition.MY_TRIGGER_ON = onTrigger;
                PhoneRebootCondition.MY_TRIGGER_OFF = offTrigger;
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

    public PhoneRebootCondition(boolean isOnBoot) {
        this._IsOnBoot = isOnBoot;
    }

    public int TestCondition(StateChange state, Context context, Ref<EventTrigger> ref) {
        if (state.TriggerType == MY_TRIGGER_ON) {
            if (this._IsOnBoot) {
                return 2;
            }
        } else if (state.TriggerType == MY_TRIGGER_OFF && !this._IsOnBoot) {
            return 2;
        }
        return this._IsOnBoot ? 1 : 0;
    }

    public boolean RenameArea(String oldName, String newName) {
        return false;
    }

    public void AppendConditionSimple(Context context, Appendable sb) throws IOException {
        if (this._IsOnBoot) {
            sb.append(context.getString(R.string.hrWhenPhoneBoots));
        } else {
            sb.append(context.getString(R.string.hrWhenPhoneShutsdown));
        }
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 1;
    }

    public static PhoneRebootCondition CreateFrom(String[] parts, int currentPart) {
        return new PhoneRebootCondition(parts[currentPart + 1].equals("1"));
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(this._IsOnBoot ? "1" : "0");
    }

    public PreferenceEx<PhoneRebootCondition> CreatePreference(PreferenceActivity context) {
        final String on = context.getString(R.string.hrOnStartUp);
        return CreateListPreference(context, context.getString(R.string.hrConditionPhoneReboot), new String[]{on, context.getString(R.string.hrOnShutdown)}, this._IsOnBoot ? on : context.getString(R.string.hrOnShutdown), new OnGetValueEx<PhoneRebootCondition>() {
            public PhoneRebootCondition GetValue(Preference preference) {
                return new PhoneRebootCondition(((ListPreference) preference).getValue().equals(on));
            }
        });
    }

    public String GetIsValidError(Context c) {
        return null;
    }
}
