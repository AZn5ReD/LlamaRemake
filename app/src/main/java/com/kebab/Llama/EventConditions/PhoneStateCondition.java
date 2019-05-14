package com.kebab.Llama.EventConditions;

import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import com.kebab.Helpers;
import com.kebab.ListPreferenceMultiselect;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.EventMeta;
import com.kebab.Llama.EventMeta.ConditionStaticInitter3;
import com.kebab.Llama.EventTrigger;
import com.kebab.Llama.LlamaStorage;
import com.kebab.Llama.R;
import com.kebab.Llama.StateChange;
import com.kebab.OnGetValueEx;
import com.kebab.PreferenceEx;
import com.kebab.Ref;
import java.io.IOException;
import java.util.ArrayList;

public class PhoneStateCondition extends EventCondition<PhoneStateCondition> {
    public static String MY_ID;
    public static int[] MY_TRIGGERS;
    public static int MY_TRIGGER_IN_CALL;
    public static int MY_TRIGGER_NOT_IN_CALL;
    public static int MY_TRIGGER_RINGING;
    int _CallStateBitmask;
    String _PersonLookupKey;

    static {
        EventMeta.InitCondition(EventFragment.PHONE_STATE_CONDITION, new ConditionStaticInitter3() {
            public void UpdateStatics(String id, int[] triggers, int onTrigger, int offTrigger, int otherTrigger) {
                PhoneStateCondition.MY_ID = id;
                PhoneStateCondition.MY_TRIGGERS = triggers;
                PhoneStateCondition.MY_TRIGGER_IN_CALL = onTrigger;
                PhoneStateCondition.MY_TRIGGER_NOT_IN_CALL = offTrigger;
                PhoneStateCondition.MY_TRIGGER_RINGING = otherTrigger;
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

    public PhoneStateCondition(int callStateBitmask, String personLookupKey) {
        if (callStateBitmask == 0) {
            this._CallStateBitmask = 2;
        } else if (callStateBitmask == 1) {
            this._CallStateBitmask = 12;
        } else {
            this._CallStateBitmask = callStateBitmask;
        }
        this._PersonLookupKey = personLookupKey;
    }

    public int TestCondition(StateChange state, Context context, Ref<EventTrigger> ref) {
        boolean lastStateIsSelected;
        boolean currentStateIsSelected;
        if ((state.LastPhoneState & this._CallStateBitmask) != 0) {
            lastStateIsSelected = true;
        } else {
            lastStateIsSelected = false;
        }
        if ((state.CurrentPhoneState & this._CallStateBitmask) != 0) {
            currentStateIsSelected = true;
        } else {
            currentStateIsSelected = false;
        }
        if (!currentStateIsSelected) {
            return 0;
        }
        if (lastStateIsSelected) {
            return 1;
        }
        if (state.TriggerType == MY_TRIGGER_IN_CALL) {
            if ((this._CallStateBitmask & 8) != 0) {
                return 2;
            }
            return 1;
        } else if (state.TriggerType == MY_TRIGGER_NOT_IN_CALL) {
            if ((this._CallStateBitmask & 2) != 0) {
                return 2;
            }
            return 1;
        } else if (state.TriggerType != MY_TRIGGER_RINGING || (this._CallStateBitmask & 4) == 0) {
            return 1;
        } else {
            return 2;
        }
    }

    public boolean RenameArea(String oldName, String newName) {
        return false;
    }

    public void AppendConditionSimple(Context context, Appendable sb) throws IOException {
        ArrayList<String> selectedValues = new ArrayList();
        if ((this._CallStateBitmask & 8) != 0) {
            selectedValues.add(context.getString(R.string.hrCallStateInCall));
        }
        if ((this._CallStateBitmask & 2) != 0) {
            selectedValues.add(context.getString(R.string.hrCallStateNotInCall));
        }
        if ((this._CallStateBitmask & 4) != 0) {
            selectedValues.add(context.getString(R.string.hrCallStateRinging));
        }
        String partsCsv = Helpers.ConcatenateListOfStrings(selectedValues, ", ", " " + context.getString(R.string.hrOr) + " ");
        sb.append(context.getString(R.string.hrWhenCallStateIs1, new Object[]{partsCsv}));
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 2;
    }

    public static PhoneStateCondition CreateFrom(String[] parts, int currentPart) {
        return new PhoneStateCondition(Integer.parseInt(parts[currentPart + 1]), LlamaStorage.SimpleUnescape(parts[currentPart + 2]));
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(this._CallStateBitmask).append("|");
        sb.append(LlamaStorage.SimpleEscape(this._PersonLookupKey));
    }

    public PreferenceEx<PhoneStateCondition> CreatePreference(PreferenceActivity context) {
        final String on = Helpers.CapitaliseFirstLetter(context.getString(R.string.hrCallStateInCall));
        final String off = Helpers.CapitaliseFirstLetter(context.getString(R.string.hrCallStateNotInCall));
        String ringing = Helpers.CapitaliseFirstLetter(context.getString(R.string.hrCallStateRinging));
        ArrayList<String> selectedValues = new ArrayList();
        if ((this._CallStateBitmask & 8) != 0) {
            selectedValues.add(on);
        }
        if ((this._CallStateBitmask & 2) != 0) {
            selectedValues.add(off);
        }
        if ((this._CallStateBitmask & 4) != 0) {
            selectedValues.add(ringing);
        }
        return CreateListPreferenceMultiselect(context, context.getString(R.string.hrConditionCallState), new String[]{on, off, ringing}, selectedValues, new OnGetValueEx<PhoneStateCondition>() {
            public PhoneStateCondition GetValue(Preference preference) {
                int callStateBitmask = 0;
                for (String value : ((ListPreferenceMultiselect) preference).getValues()) {
                    if (value.equals(on)) {
                        callStateBitmask |= 8;
                    } else if (value.equals(off)) {
                        callStateBitmask |= 2;
                    } else {
                        callStateBitmask |= 4;
                    }
                }
                return new PhoneStateCondition(callStateBitmask, "");
            }
        });
    }

    public String GetIsValidError(Context c) {
        if (this._CallStateBitmask == 0 || this._CallStateBitmask == 14) {
            return c.getString(R.string.hrPleaseChooseOneOrTwoCallStates);
        }
        return null;
    }
}
