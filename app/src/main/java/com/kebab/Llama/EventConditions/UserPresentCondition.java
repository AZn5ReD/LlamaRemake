package com.kebab.Llama.EventConditions;

import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.EventMeta;
import com.kebab.Llama.EventMeta.ConditionStaticInitter1;
import com.kebab.Llama.EventTrigger;
import com.kebab.Llama.R;
import com.kebab.Llama.StateChange;
import com.kebab.OnGetValueEx;
import com.kebab.PreferenceEx;
import com.kebab.Ref;
import java.io.IOException;

public class UserPresentCondition extends EventCondition<UserPresentCondition> {
    public static String MY_ID;
    public static int MY_TRIGGER;
    public static int[] MY_TRIGGERS;
    boolean _IsOn;

    static {
        EventMeta.InitCondition(EventFragment.USER_PRESENT_CONDITION, new ConditionStaticInitter1() {
            public void UpdateStatics(String id, int[] triggers, int trigger) {
                UserPresentCondition.MY_ID = id;
                UserPresentCondition.MY_TRIGGERS = triggers;
                UserPresentCondition.MY_TRIGGER = trigger;
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

    public UserPresentCondition(boolean dummy) {
        this._IsOn = dummy;
    }

    public int TestCondition(StateChange state, Context context, Ref<EventTrigger> ref) {
        if (state.TriggerType == MY_TRIGGER) {
            return 2;
        }
        return 1;
    }

    public boolean RenameArea(String oldName, String newName) {
        return false;
    }

    public void AppendConditionSimple(Context context, Appendable sb) throws IOException {
        sb.append(context.getString(R.string.hrWhenUserIsPresent));
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 1;
    }

    public static UserPresentCondition CreateFrom(String[] parts, int currentPart) {
        return new UserPresentCondition(false);
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(this._IsOn ? "1" : "0");
    }

    public PreferenceEx<UserPresentCondition> CreatePreference(PreferenceActivity context) {
        return CreateSimplePreference(context, context.getString(R.string.hrConditionUserPresent), context.getString(R.string.hrConditionUserPresent), new OnGetValueEx<UserPresentCondition>() {
            public UserPresentCondition GetValue(Preference preference) {
                return new UserPresentCondition(false);
            }
        });
    }

    public String GetIsValidError(Context context) {
        return null;
    }
}
