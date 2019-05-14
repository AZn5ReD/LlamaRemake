package com.kebab.Llama.EventConditions;

import android.content.Context;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.kebab.DialogHandler;
import com.kebab.DialogPreference;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.EventMeta;
import com.kebab.Llama.EventMeta.ConditionStaticInitter1;
import com.kebab.Llama.EventTrigger;
import com.kebab.Llama.Instances;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.R;
import com.kebab.Llama.StateChange;
import com.kebab.OnGetValueEx;
import com.kebab.PreferenceEx;
import com.kebab.Ref;
import com.kebab.SeekBarDialogView;
import java.io.IOException;

public class SignalLevelCondition extends EventCondition<SignalLevelCondition> {
    public static final int MAX_VALUE = -51;
    static final int MIN_VALUE_GSM = -113;
    public static final int MIN_VALUE_NO_SIGNAL = -150;
    public static String MY_ID;
    public static int MY_TRIGGER;
    public static int[] MY_TRIGGERS;
    boolean _IsDecrease;
    int _TargetSignalLevel;
    boolean _TriggerHasOccured;

    static {
        EventMeta.InitCondition(EventFragment.SIGNAL_LEVEL_CONDITION, new ConditionStaticInitter1() {
            public void UpdateStatics(String id, int[] triggers, int trigger) {
                SignalLevelCondition.MY_ID = id;
                SignalLevelCondition.MY_TRIGGERS = triggers;
                SignalLevelCondition.MY_TRIGGER = trigger;
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

    public SignalLevelCondition(int targetSignalLevel, boolean isDecrease, boolean triggerHasOccured) {
        this._TargetSignalLevel = targetSignalLevel;
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
        boolean z = true;
        if (state.SignalStrength == null) {
            return 0;
        }
        boolean signalLevelSatisfied = this._IsDecrease ? state.SignalStrength.intValue() <= this._TargetSignalLevel : state.SignalStrength.intValue() >= this._TargetSignalLevel;
        if (state.TriggerType != MY_TRIGGER) {
            if (!signalLevelSatisfied) {
                z = false;
            }
            return z;
        } else if (signalLevelSatisfied) {
            if (this._TriggerHasOccured) {
                return 1;
            }
            this._TriggerHasOccured = true;
            state.SetEventsNeedSaving();
            return 2;
        } else if (!this._TriggerHasOccured) {
            return 0;
        } else {
            this._TriggerHasOccured = false;
            state.SetEventsNeedSaving();
            return 0;
        }
    }

    public boolean RenameArea(String oldName, String newName) {
        return false;
    }

    public void AppendConditionSimple(Context context, Appendable sb) throws IOException {
        if (this._IsDecrease) {
            sb.append(String.format(context.getString(R.string.hrWhenSignalGoesBelow1), new Object[]{Integer.valueOf(this._TargetSignalLevel)}));
            return;
        }
        sb.append(String.format(context.getString(R.string.hrWhenSignalRisesAbove1), new Object[]{Integer.valueOf(this._TargetSignalLevel)}));
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 3;
    }

    public static SignalLevelCondition CreateFrom(String[] parts, int currentPart) {
        boolean z = true;
        int parseInt = Integer.parseInt(parts[currentPart + 1]);
        boolean z2 = 1 == Integer.parseInt(parts[currentPart + 2]);
        if (1 != Integer.parseInt(parts[currentPart + 3])) {
            z = false;
        }
        return new SignalLevelCondition(parseInt, z2, z);
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(this._TargetSignalLevel).append("|").append(this._IsDecrease ? "1" : "0").append("|").append(this._TriggerHasOccured ? "1" : "0");
    }

    public PreferenceEx<SignalLevelCondition> CreatePreference(final PreferenceActivity context) {
        return CreateDialogPreference(context, context.getString(R.string.hrConditionSignalLevel), new DialogHandler<SignalLevelCondition>() {
            TextView _CurrentStrengthText;
            Handler _Handler = new Handler();
            Runnable _Pinger = new Runnable() {
                public void run() {
                    LlamaService service = Instances.Service;
                    Integer strength = service != null ? service.GetLastSignalStrength() : null;
                    TextView textView = AnonymousClass2.this._CurrentStrengthText;
                    Context context = AnonymousClass2.this._CurrentStrengthText.getContext();
                    Object[] objArr = new Object[1];
                    if (strength == null) {
                        strength = "???";
                    }
                    objArr[0] = strength;
                    textView.setText(context.getString(R.string.hrCurrentStrengthColon1, objArr));
                    AnonymousClass2.this._Handler.postDelayed(AnonymousClass2.this._Pinger, 1000);
                }
            };
            SeekBarDialogView _SeekBar;
            Spinner _Spinner;

            public void DialogHasFinished(View view) {
                this._Handler.removeCallbacks(this._Pinger);
                this._CurrentStrengthText = null;
            }

            public boolean HideButtons() {
                return false;
            }

            public SignalLevelCondition GetResultFromView() {
                return new SignalLevelCondition(this._SeekBar.GetResult(), this._Spinner.getSelectedItemPosition() == 0, false);
            }

            public SignalLevelCondition fillValuesFromString(String value) {
                return SignalLevelCondition.CreateFrom(value.split("\\|", -1), 0);
            }

            public String getHumanReadableValue(SignalLevelCondition value) {
                if (value._IsDecrease) {
                    return String.format(context.getString(R.string.hrSignalLevelBelow1), new Object[]{Integer.valueOf(value._TargetSignalLevel)});
                }
                return String.format(context.getString(R.string.hrSignalLevelAbove1), new Object[]{Integer.valueOf(value._TargetSignalLevel)});
            }

            public View getView(SignalLevelCondition value, Context context, DialogPreference<?, SignalLevelCondition> dialogPreference) {
                View v = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.signal_level_dialog, null);
                this._SeekBar = new SeekBarDialogView(SignalLevelCondition.this._TargetSignalLevel, (int) SignalLevelCondition.MIN_VALUE_NO_SIGNAL, -51, null, null, "dBm");
                ((LinearLayout) v.findViewById(R.id.mainLayout)).addView(this._SeekBar.createSeekBarDialogView(context));
                this._Spinner = (Spinner) v.findViewById(R.id.signalLevelOptions);
                this._SeekBar.setValue(value._TargetSignalLevel);
                this._Spinner.setSelection(value._IsDecrease ? 0 : 1);
                this._CurrentStrengthText = (TextView) v.findViewById(R.id.text);
                this._Pinger.run();
                return v;
            }

            public String serialiseToString(SignalLevelCondition value) {
                return value.ToPsv();
            }

            public boolean RequiresScrollView() {
                return false;
            }
        }, this, new OnGetValueEx<SignalLevelCondition>() {
            public SignalLevelCondition GetValue(Preference preference) {
                return (SignalLevelCondition) ((DialogPreference) preference).getValue();
            }
        });
    }

    public String GetIsValidError(Context context) {
        return null;
    }
}
