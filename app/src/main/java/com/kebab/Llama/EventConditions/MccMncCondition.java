package com.kebab.Llama.EventConditions;

import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import com.kebab.DialogHandler;
import com.kebab.DialogPreference;
import com.kebab.Helpers;
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

public class MccMncCondition extends EventCondition<MccMncCondition> {
    public static final int ANY = -100;
    public static String MY_ID;
    public static int MY_TRIGGER;
    public static int[] MY_TRIGGERS;
    boolean _IsEqual;
    int _Mcc;
    int _Mnc;

    static {
        EventMeta.InitCondition(EventFragment.MCC_MNC_CONDITION, new ConditionStaticInitter1() {
            public void UpdateStatics(String id, int[] triggers, int trigger) {
                MccMncCondition.MY_ID = id;
                MccMncCondition.MY_TRIGGERS = triggers;
                MccMncCondition.MY_TRIGGER = trigger;
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

    public MccMncCondition(int mcc, int mnc, boolean isEqual) {
        this._Mcc = mcc;
        this._Mnc = mnc;
        this._IsEqual = isEqual;
    }

    public int TestCondition(StateChange state, Context context, Ref<EventTrigger> ref) {
        int i = 0;
        if (state.TriggerType == MY_TRIGGER) {
            boolean MncChanged;
            boolean MccChanged = state.PreviousCellForMcc.Mcc != state.CurrentCellForMcc.Mcc;
            if (state.PreviousCellForMcc.Mnc != state.CurrentCellForMcc.Mnc) {
                MncChanged = true;
            } else {
                MncChanged = false;
            }
            if (this._IsEqual) {
                boolean matchesCurrent = (this._Mcc == -100 || this._Mcc == state.CurrentCellForMcc.Mcc) && (this._Mnc == -100 || this._Mnc == state.CurrentCellForMcc.Mnc);
                if (this._Mcc == -100) {
                    if (MccChanged && matchesCurrent) {
                        return 2;
                    }
                } else if (this._Mnc == -100) {
                    if (MncChanged && matchesCurrent) {
                        return 2;
                    }
                } else if ((MccChanged || MncChanged) && matchesCurrent) {
                    return 2;
                }
            }
            boolean matchesPrevious = (this._Mcc == -100 || this._Mcc == state.PreviousCellForMcc.Mcc) && (this._Mnc == -100 || this._Mnc == state.PreviousCellForMcc.Mnc);
            if (this._Mcc == -100) {
                if (MccChanged && matchesPrevious) {
                    return 2;
                }
            } else if (this._Mnc == -100) {
                if (MncChanged && matchesPrevious) {
                    return 2;
                }
            } else if ((MccChanged || MncChanged) && matchesPrevious) {
                return 2;
            }
        }
        boolean MccMatches = this._Mcc == -100 ? true : state.CurrentCellForMcc.Mcc == this._Mcc;
        boolean MncMatches = this._Mnc == -100 ? true : state.CurrentCellForMcc.Mnc == this._Mnc;
        if (!MccMatches || !MncMatches) {
            if (!this._IsEqual) {
                i = 1;
            }
            return i;
        } else if (this._IsEqual) {
            return 1;
        } else {
            return 0;
        }
    }

    public boolean RenameArea(String oldName, String newName) {
        return false;
    }

    public void AppendConditionSimple(Context context, Appendable sb) throws IOException {
        String string;
        Object[] objArr;
        if (this._IsEqual) {
            string = context.getString(R.string.hrWhenMccMncIs12);
            objArr = new Object[2];
            objArr[0] = this._Mcc == -100 ? "*" : Integer.valueOf(this._Mcc);
            objArr[1] = this._Mnc == -100 ? "*" : Integer.valueOf(this._Mnc);
            sb.append(String.format(string, objArr));
            return;
        }
        string = context.getString(R.string.hrWhenMccMncIsNot12);
        objArr = new Object[2];
        objArr[0] = this._Mcc == -100 ? "*" : Integer.valueOf(this._Mcc);
        objArr[1] = this._Mnc == -100 ? "*" : Integer.valueOf(this._Mnc);
        sb.append(String.format(string, objArr));
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 3;
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(this._Mcc).append("|");
        sb.append(this._Mnc).append("|");
        sb.append(this._IsEqual ? "1" : "0");
    }

    public static MccMncCondition CreateFrom(String[] parts, int currentPart) {
        return new MccMncCondition(Integer.parseInt(parts[currentPart + 1]), Integer.parseInt(parts[currentPart + 2]), parts[currentPart + 3].equals("1"));
    }

    public PreferenceEx<MccMncCondition> CreatePreference(final PreferenceActivity context) {
        return CreateDialogPreference(context, context.getString(R.string.hrConditionMobileNetworkId), new DialogHandler<MccMncCondition>() {
            Spinner _EqualOrNot;
            EditText _MccText;
            EditText _MncText;

            public void DialogHasFinished(View view) {
            }

            public boolean HideButtons() {
                return false;
            }

            public MccMncCondition GetResultFromView() {
                int i = -100;
                Integer mcc = Helpers.TryParseInt(this._MccText.getText().toString());
                Integer mnc = Helpers.TryParseInt(this._MncText.getText().toString());
                boolean equalOrNot = this._EqualOrNot.getSelectedItemPosition() == 0;
                int intValue = (mcc == null || mcc.intValue() < 0) ? -100 : mcc.intValue();
                if (mnc != null && mnc.intValue() >= 0) {
                    i = mnc.intValue();
                }
                return new MccMncCondition(intValue, i, equalOrNot);
            }

            public MccMncCondition fillValuesFromString(String value) {
                return null;
            }

            public String getHumanReadableValue(MccMncCondition value) {
                StringBuilder sb = new StringBuilder();
                try {
                    value.AppendConditionSimple(context, sb);
                    Helpers.CapitaliseFirstLetter(sb);
                    return sb.toString();
                } catch (IOException ioex) {
                    throw new RuntimeException(ioex);
                }
            }

            public View getView(MccMncCondition value, Context context, DialogPreference<?, MccMncCondition> dialogPreference) {
                View v = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.mcc_mnc_condition, null);
                this._MccText = (EditText) v.findViewById(R.id.mcc);
                this._MncText = (EditText) v.findViewById(R.id.mnc);
                this._EqualOrNot = (Spinner) v.findViewById(R.id.spinner);
                this._MccText.setText(value._Mcc == -100 ? "" : String.valueOf(value._Mcc));
                this._MncText.setText(value._Mnc == -100 ? "" : String.valueOf(value._Mnc));
                this._EqualOrNot.setSelection(value._IsEqual ? 0 : 1);
                return v;
            }

            public String serialiseToString(MccMncCondition value) {
                return "";
            }

            public boolean RequiresScrollView() {
                return true;
            }
        }, this, new OnGetValueEx<MccMncCondition>() {
            public MccMncCondition GetValue(Preference preference) {
                return (MccMncCondition) ((DialogPreference) preference).getValue();
            }
        });
    }

    public String GetIsValidError(Context context) {
        return null;
    }
}
