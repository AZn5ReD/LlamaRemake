package com.kebab.Llama.EventConditions;

import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;
import com.kebab.DialogHandler;
import com.kebab.DialogPreference;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.EventMeta;
import com.kebab.Llama.EventMeta.ConditionStaticInitter3;
import com.kebab.Llama.EventTrigger;
import com.kebab.Llama.R;
import com.kebab.Llama.StateChange;
import com.kebab.OnGetValueEx;
import com.kebab.PreferenceEx;
import com.kebab.Ref;
import com.kebab.SeekBarDialogView;
import java.io.IOException;

public class BatteryLevelCondition extends EventCondition<BatteryLevelCondition> {
    public static String MY_ID;
    public static int[] MY_TRIGGERS;
    public static int MY_TRIGGER_OFF;
    public static int MY_TRIGGER_ON;
    public static int MY_TRIGGER_OTHER;
    boolean _IsDecrease;
    int _TargetBatteryLevel;
    boolean _TriggerHasOccured;

    static {
        EventMeta.InitCondition(EventFragment.BATTERY_LEVEL_CONDITION, new ConditionStaticInitter3() {
            public void UpdateStatics(String id, int[] triggers, int onTrigger, int offTrigger, int otherTrigger) {
                BatteryLevelCondition.MY_ID = id;
                BatteryLevelCondition.MY_TRIGGERS = triggers;
                BatteryLevelCondition.MY_TRIGGER_ON = onTrigger;
                BatteryLevelCondition.MY_TRIGGER_OFF = offTrigger;
                BatteryLevelCondition.MY_TRIGGER_OTHER = otherTrigger;
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

    public BatteryLevelCondition(int targetBatteryLevel, boolean isDecrease, boolean triggerHasOccured) {
        this._TargetBatteryLevel = targetBatteryLevel;
        this._IsDecrease = isDecrease;
        this._TriggerHasOccured = triggerHasOccured;
    }

    public void PeekStateChange(StateChange state, Context context) {
        TestCondition(state, context, null);
    }

    public int TestCondition(StateChange state, Context context, Ref<EventTrigger> ref) {
        return TestOrPeek(state, context);
    }

    private int TestOrPeek(StateChange state, Context context) {
        boolean batteryLevelSatisfied = this._IsDecrease ? state.BatteryLevel <= this._TargetBatteryLevel : state.BatteryLevel >= this._TargetBatteryLevel;
        if (state.TriggerType == MY_TRIGGER_ON || state.TriggerType == MY_TRIGGER_OFF || state.TriggerType == MY_TRIGGER_OTHER) {
            if (!batteryLevelSatisfied) {
                if (this._TriggerHasOccured) {
                    this._TriggerHasOccured = false;
                    state.SetEventsNeedSaving();
                }
                return 0;
            } else if (this._TriggerHasOccured) {
                return 1;
            } else {
                this._TriggerHasOccured = true;
                state.SetEventsNeedSaving();
                return 2;
            }
        } else if (batteryLevelSatisfied) {
            return 1;
        } else {
            return 0;
        }
    }

    public boolean RenameArea(String oldName, String newName) {
        return false;
    }

    public void AppendConditionSimple(Context context, Appendable sb) throws IOException {
        if (this._IsDecrease) {
            sb.append(String.format(context.getString(R.string.hrWhenBatteryGoesBelow1Percent), new Object[]{Integer.valueOf(this._TargetBatteryLevel)}));
            return;
        }
        sb.append(String.format(context.getString(R.string.hrWhenBatteryRisesAbove1Percent), new Object[]{Integer.valueOf(this._TargetBatteryLevel)}));
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 3;
    }

    public static BatteryLevelCondition CreateFrom(String[] parts, int currentPart) {
        boolean z = true;
        int parseInt = Integer.parseInt(parts[currentPart + 1]);
        boolean z2 = 1 == Integer.parseInt(parts[currentPart + 2]);
        if (1 != Integer.parseInt(parts[currentPart + 3])) {
            z = false;
        }
        return new BatteryLevelCondition(parseInt, z2, z);
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(this._TargetBatteryLevel).append("|").append(this._IsDecrease ? "1" : "0").append("|").append(this._TriggerHasOccured ? "1" : "0");
    }

    public PreferenceEx<BatteryLevelCondition> CreatePreference(final PreferenceActivity context) {
        return CreateDialogPreference(context, context.getString(R.string.hrBatteryLevel), new DialogHandler<BatteryLevelCondition>() {
            SeekBarDialogView _SeekBar;
            Spinner _Spinner;

            public void DialogHasFinished(View view) {
            }

            public boolean HideButtons() {
                return false;
            }

            public BatteryLevelCondition GetResultFromView() {
                return new BatteryLevelCondition(this._SeekBar.GetResult(), this._Spinner.getSelectedItemPosition() == 0, false);
            }

            public BatteryLevelCondition fillValuesFromString(String value) {
                return BatteryLevelCondition.CreateFrom(value.split("\\|", -1), 0);
            }

            public String getHumanReadableValue(BatteryLevelCondition value) {
                if (value._IsDecrease) {
                    return String.format(context.getString(R.string.hrBatteryLevelBelow1), new Object[]{Integer.valueOf(value._TargetBatteryLevel)});
                }
                return String.format(context.getString(R.string.hrBatteryLevelAbove1), new Object[]{Integer.valueOf(value._TargetBatteryLevel)});
            }

            public View getView(BatteryLevelCondition value, Context context, DialogPreference<?, BatteryLevelCondition> dialogPreference) {
                int i = 0;
                View v = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.batteryleveldialog, null);
                this._SeekBar = new SeekBarDialogView(BatteryLevelCondition.this._TargetBatteryLevel, 0, 100, null, null, "");
                ((LinearLayout) v.findViewById(R.id.mainLayout)).addView(this._SeekBar.createSeekBarDialogView(context));
                this._Spinner = (Spinner) v.findViewById(R.id.batteryLevelOptions);
                this._SeekBar.setValue(value._TargetBatteryLevel);
                Spinner spinner = this._Spinner;
                if (!value._IsDecrease) {
                    i = 1;
                }
                spinner.setSelection(i);
                return v;
            }

            public String serialiseToString(BatteryLevelCondition value) {
                return value.ToPsv();
            }

            public boolean RequiresScrollView() {
                return false;
            }
        }, this, new OnGetValueEx<BatteryLevelCondition>() {
            public BatteryLevelCondition GetValue(Preference preference) {
                return (BatteryLevelCondition) ((DialogPreference) preference).getValue();
            }
        });
    }

    public String GetIsValidError(Context context) {
        return null;
    }
}
