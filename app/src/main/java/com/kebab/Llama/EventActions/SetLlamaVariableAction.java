package com.kebab.Llama.EventActions;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.PreferenceActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import com.kebab.AlertDialogEx.Builder;
import com.kebab.AppendableCharSequence;
import com.kebab.ClickablePreferenceEx;
import com.kebab.ClickablePreferenceEx.GotResultHandler;
import com.kebab.Helpers;
import com.kebab.Llama.Event;
import com.kebab.Llama.EventConditions.LlamaVariableCondition.VariableUiHelper;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.Instances;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.LlamaStorage;
import com.kebab.Llama.R;
import com.kebab.PreferenceEx;
import com.kebab.ResultRegisterableActivity;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class SetLlamaVariableAction extends EventAction<SetLlamaVariableAction> {
    String _VariableName;
    String _VariableValue;

    public SetLlamaVariableAction(String name, String value) {
        this._VariableName = name;
        this._VariableValue = value;
    }

    public String GetVariableName() {
        return this._VariableName;
    }

    public String GetVariableValue() {
        return this._VariableValue;
    }

    public void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        String newValue;
        int i = 0;
        Integer variableValue;
        if ("LV:Increment".equalsIgnoreCase(this._VariableValue)) {
            variableValue = GetVariableValueAsInt(service.GetVariableValue(this._VariableName));
            if (variableValue != null) {
                i = variableValue.intValue();
            }
            newValue = String.valueOf(Integer.valueOf(i).intValue() + 1);
        } else if ("LV:Decrement".equalsIgnoreCase(this._VariableValue)) {
            variableValue = GetVariableValueAsInt(service.GetVariableValue(this._VariableName));
            if (variableValue != null) {
                i = variableValue.intValue();
            }
            newValue = String.valueOf(Integer.valueOf(i).intValue() - 1);
        } else if ("LV:Toggle01".equalsIgnoreCase(this._VariableValue)) {
            variableValue = GetVariableValueAsInt(service.GetVariableValue(this._VariableName));
            newValue = (variableValue == null ? 0 : variableValue.intValue()) == 0 ? "1" : "0";
        } else {
            newValue = this._VariableValue;
        }
        service.SetVariableValue(this._VariableName, newValue);
    }

    public static Integer GetVariableValueAsInt(String valueAsString) {
        if (valueAsString == null || valueAsString.length() == 0) {
            return Integer.valueOf(0);
        }
        if ("0".equals(valueAsString)) {
            return Integer.valueOf(0);
        }
        if ("1".equals(valueAsString)) {
            return Integer.valueOf(1);
        }
        if ("2".equals(valueAsString)) {
            return Integer.valueOf(2);
        }
        if ("3".equals(valueAsString)) {
            return Integer.valueOf(3);
        }
        try {
            return Integer.valueOf(Integer.parseInt(valueAsString));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public boolean RenameProfile(String oldName, String newName) {
        return false;
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 2;
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(LlamaStorage.SimpleEscape(this._VariableName));
        sb.append("|");
        sb.append(LlamaStorage.SimpleEscape(this._VariableValue));
    }

    public static SetLlamaVariableAction CreateFrom(String[] parts, int currentPart) {
        return new SetLlamaVariableAction(LlamaStorage.SimpleUnescape(parts[currentPart + 1]), LlamaStorage.SimpleUnescape(parts[currentPart + 2]));
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.SET_LLAMA_VARIABLE;
    }

    public PreferenceEx<SetLlamaVariableAction> CreatePreference(PreferenceActivity context) {
        return new ClickablePreferenceEx<SetLlamaVariableAction>((ResultRegisterableActivity) context, context.getString(R.string.hrActionSetLlamaVariable), this) {
            /* Access modifiers changed, original: protected */
            public String GetHumanReadableValue(Context context, SetLlamaVariableAction value) {
                StringBuilder sb = new StringBuilder();
                try {
                    value.AppendActionDescription(context, AppendableCharSequence.Wrap(sb));
                    Helpers.CapitaliseFirstLetter(sb);
                    return sb.toString();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }

            /* Access modifiers changed, original: protected */
            public void OnPreferenceClicked(final ResultRegisterableActivity host, SetLlamaVariableAction existingValue, final GotResultHandler<SetLlamaVariableAction> gotResultHandler) {
                HashMap<String, HashSet<String>> existingVariables = Instances.Service.GetAllLlamaVariableKeyValues();
                View view = View.inflate(host.GetActivity(), R.layout.set_llama_variable, null);
                final AutoCompleteTextView nameText = (AutoCompleteTextView) view.findViewById(R.id.variable_name);
                final AutoCompleteTextView valueText = (AutoCompleteTextView) view.findViewById(R.id.variable_value);
                Button advancedVariableActions = (Button) view.findViewById(R.id.advancedVariableActions);
                VariableUiHelper.InitAutocompletionLists(host.GetActivity(), existingVariables, nameText, valueText, (Button) view.findViewById(R.id.name_button), (Button) view.findViewById(R.id.value_button));
                nameText.setText(existingValue._VariableName == null ? "" : existingValue._VariableName);
                valueText.setText(existingValue._VariableValue == null ? "" : existingValue._VariableValue);
                advancedVariableActions.setOnClickListener(new OnClickListener() {
                    String[] actionNames = host.GetActivity().getResources().getStringArray(R.array.advancedVariableActionNames);
                    String[] actionValues = host.GetActivity().getResources().getStringArray(R.array.advancedVariableActionValues);

                    public void onClick(View v) {
                        AlertDialog advancedListPicker = new Builder(host.GetActivity()).setItems(this.actionNames, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                valueText.setText(AnonymousClass1.this.actionValues[which]);
                            }
                        }).show();
                    }
                });
                AlertDialog dialog = new Builder(host.GetActivity()).setPositiveButton(R.string.hrOk, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        gotResultHandler.HandleResult(new SetLlamaVariableAction(nameText.getText().toString().trim(), valueText.getText().toString().trim()));
                        dialog.dismiss();
                    }
                }).setNegativeButton(R.string.hrCancel, null).setView(view).create();
                dialog.setOwnerActivity(host.GetActivity());
                dialog.show();
            }
        };
    }

    public void AppendActionDescription(Context context, AppendableCharSequence sb) throws IOException {
        CharSequence message;
        String safeVariableName = this._VariableName == null ? "" : this._VariableName;
        if ("LV:Increment".equalsIgnoreCase(this._VariableValue)) {
            message = context.getString(R.string.hrSetLlamaVariableIncrement1, new Object[]{safeVariableName});
        } else if ("LV:Decrement".equalsIgnoreCase(this._VariableValue)) {
            message = context.getString(R.string.hrSetLlamaVariableDecrement1, new Object[]{safeVariableName});
        } else if ("LV:Toggle01".equalsIgnoreCase(this._VariableValue)) {
            message = context.getString(R.string.hrSetLlamaVariableToggle01, new Object[]{safeVariableName});
        } else {
            Object[] objArr = new Object[2];
            objArr[0] = safeVariableName;
            objArr[1] = this._VariableValue == null ? "" : this._VariableValue;
            message = context.getString(R.string.hrSetLlamaVariable1To2, objArr);
        }
        sb.append(message);
    }

    public String GetIsValidError(Context context) {
        if (this._VariableName == null || this._VariableName.length() == 0) {
            return context.getString(R.string.hrPleaseEnterAVariableName);
        }
        if (this._VariableValue == null) {
            return context.getString(R.string.hrPleaseEnterAVariableName);
        }
        return null;
    }

    public boolean IsHarmful() {
        return false;
    }
}
