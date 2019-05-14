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

public class ChargingCondition extends EventCondition<ChargingCondition> {
    public static final int CHARGING_AC = 2;
    public static final int CHARGING_ANY = 1;
    public static final int CHARGING_USB = 3;
    public static final int CHARGING_WIRELESS = 4;
    public static String MY_ID;
    public static int[] MY_TRIGGERS;
    public static int MY_TRIGGER_OFF;
    public static int MY_TRIGGER_ON;
    public static final int NOT_CHARGING = 0;
    int _ChargingType;

    static {
        EventMeta.InitCondition(EventFragment.CHARGING_CONDITION, new ConditionStaticInitter2() {
            public void UpdateStatics(String id, int[] triggers, int onTrigger, int offTrigger) {
                ChargingCondition.MY_ID = id;
                ChargingCondition.MY_TRIGGERS = triggers;
                ChargingCondition.MY_TRIGGER_ON = onTrigger;
                ChargingCondition.MY_TRIGGER_OFF = offTrigger;
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

    public ChargingCondition(int chargingType) {
        this._ChargingType = chargingType;
    }

    public int TestCondition(StateChange state, Context context, Ref<EventTrigger> ref) {
        if (state.ChargingState == null) {
            return 0;
        }
        if (state.ChargingState.booleanValue()) {
            if (this._ChargingType == 1) {
                return state.TriggerType == MY_TRIGGER_ON ? 2 : 1;
            } else {
                if (this._ChargingType == 2 && state.ChargingFrom == 2) {
                    return state.TriggerType == MY_TRIGGER_ON ? 2 : 1;
                } else {
                    if (this._ChargingType == 3 && state.ChargingFrom == 3) {
                        return state.TriggerType == MY_TRIGGER_ON ? 2 : 1;
                    } else {
                        if (this._ChargingType == 4 && state.ChargingFrom == 4) {
                            return state.TriggerType == MY_TRIGGER_ON ? 2 : 1;
                        } else {
                            return 0;
                        }
                    }
                }
            }
        } else if (this._ChargingType == 0) {
            return state.TriggerType == MY_TRIGGER_OFF ? 2 : 1;
        } else {
            return 0;
        }
    }

    public boolean RenameArea(String oldName, String newName) {
        return false;
    }

    public void AppendConditionSimple(Context context, Appendable sb) throws IOException {
        switch (this._ChargingType) {
            case 0:
                sb.append(context.getString(R.string.hrWhenUsingBattery));
                return;
            case 1:
                sb.append(context.getString(R.string.hrWhenCharging));
                return;
            case 2:
                sb.append(context.getString(R.string.hrWhenChargingFromAc));
                return;
            case 3:
                sb.append(context.getString(R.string.hrWhenChargingFromUsb));
                return;
            default:
                sb.append(context.getString(R.string.hrWhenChargingWirelessy));
                return;
        }
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 1;
    }

    public static ChargingCondition CreateFrom(String[] parts, int currentPart) {
        return new ChargingCondition(Integer.parseInt(parts[currentPart + 1]));
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(this._ChargingType);
    }

    public PreferenceEx<ChargingCondition> CreatePreference(PreferenceActivity context) {
        String currentValue;
        String NOT_CHARGING_TEXT = context.getString(R.string.hrUsingBattery);
        final String CHARGING_ANY_TEXT = context.getString(R.string.hrChargingFromAny);
        final String CHARGING_AC_TEXT = context.getString(R.string.hrChargingFromAc);
        final String CHARGING_USB_TEXT = context.getString(R.string.hrChargingFromUsb);
        final String CHARGING_WIRELESS_TEXT = context.getString(R.string.hrChargingFromWireless);
        switch (this._ChargingType) {
            case 1:
                currentValue = CHARGING_ANY_TEXT;
                break;
            case 2:
                currentValue = CHARGING_AC_TEXT;
                break;
            case 3:
                currentValue = CHARGING_USB_TEXT;
                break;
            case 4:
                currentValue = CHARGING_WIRELESS_TEXT;
                break;
            default:
                currentValue = NOT_CHARGING_TEXT;
                break;
        }
        return CreateListPreference(context, context.getString(R.string.hrChargingStatus), new String[]{CHARGING_ANY_TEXT, CHARGING_USB_TEXT, CHARGING_AC_TEXT, CHARGING_WIRELESS_TEXT, NOT_CHARGING_TEXT}, currentValue, new OnGetValueEx<ChargingCondition>() {
            public ChargingCondition GetValue(Preference preference) {
                int newValue;
                String value = ((ListPreference) preference).getValue();
                if (value.equals(CHARGING_ANY_TEXT)) {
                    newValue = 1;
                } else if (value.equals(CHARGING_USB_TEXT)) {
                    newValue = 3;
                } else if (value.equals(CHARGING_AC_TEXT)) {
                    newValue = 2;
                } else if (value.equals(CHARGING_WIRELESS_TEXT)) {
                    newValue = 4;
                } else {
                    newValue = 0;
                }
                return new ChargingCondition(newValue);
            }
        });
    }

    public String GetIsValidError(Context context) {
        return null;
    }
}
