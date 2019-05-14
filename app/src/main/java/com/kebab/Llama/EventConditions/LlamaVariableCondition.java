package com.kebab.Llama.EventConditions;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.preference.PreferenceActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import com.kebab.AlertDialogEx.Builder;
import com.kebab.ClickablePreferenceEx;
import com.kebab.ClickablePreferenceEx.GotResultHandler;
import com.kebab.Helpers;
import com.kebab.HelpersC;
import com.kebab.Llama.AutoCompleteHelper;
import com.kebab.Llama.EventActions.SetLlamaVariableAction;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.EventMeta;
import com.kebab.Llama.EventMeta.ConditionStaticInitter1;
import com.kebab.Llama.EventTrigger;
import com.kebab.Llama.Instances;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.R;
import com.kebab.Llama.StateChange;
import com.kebab.PreferenceEx;
import com.kebab.Ref;
import com.kebab.ResultRegisterableActivity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class LlamaVariableCondition extends EventCondition<LlamaVariableCondition> {
    public static final int EQOP_EQUALS = 1;
    private static final int EQOP_GREATER_THAN = 4;
    private static final int EQOP_GREATER_THAN_OR_EQUALS = 5;
    private static final int EQOP_LESS_THAN = 2;
    private static final int EQOP_LESS_THAN_OR_EQUALS = 3;
    private static final int EQOP_NOT_EQUALS = 0;
    public static String MY_ID;
    public static int MY_TRIGGER;
    public static int[] MY_TRIGGERS;
    int _EqualityOperation;
    String _VariableName;
    String _VariableValue;

    public static class VariableUiHelper {
        public static void InitAutocompletionLists(Context context, final HashMap<String, HashSet<String>> existingVariables, final AutoCompleteTextView nameText, final AutoCompleteTextView valueText, Button nameButton, final Button valueButton) {
            ArrayList<String> variableNames = new ArrayList();
            for (String key : existingVariables.keySet()) {
                variableNames.add(key);
            }
            Collections.sort(variableNames);
            final Runnable valueUpdater = new Runnable() {
                String lastValue = null;

                public void run() {
                    String variableName = nameText.getText().toString();
                    if (!HelpersC.StringEquals(variableName, this.lastValue)) {
                        this.lastValue = variableName;
                        HashSet<String> valuesForKey = (HashSet) existingVariables.get(variableName);
                        if (valuesForKey == null) {
                            valuesForKey = new HashSet();
                        }
                        ArrayList<String> variableValues = new ArrayList();
                        Iterator i$ = valuesForKey.iterator();
                        while (i$.hasNext()) {
                            variableValues.add((String) i$.next());
                        }
                        Collections.sort(variableValues);
                        AutoCompleteHelper.InitAutoCompleteButton(valueText, valueButton, variableValues);
                    }
                }
            };
            nameText.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                    valueUpdater.run();
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });
            AutoCompleteHelper.InitAutoCompleteButton(nameText, nameButton, variableNames);
            valueUpdater.run();
        }
    }

    static {
        EventMeta.InitCondition(EventFragment.LLAMA_VARIABLE_CHANGED, new ConditionStaticInitter1() {
            public void UpdateStatics(String id, int[] triggers, int trigger) {
                LlamaVariableCondition.MY_ID = id;
                LlamaVariableCondition.MY_TRIGGERS = triggers;
                LlamaVariableCondition.MY_TRIGGER = trigger;
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

    public String GetVariableName() {
        return this._VariableName;
    }

    public String GetVariableValue() {
        return this._VariableValue;
    }

    public LlamaVariableCondition(int equalityOperation, String name, String value) {
        this._EqualityOperation = equalityOperation;
        this._VariableValue = value;
        this._VariableName = name;
    }

    public int TestCondition(StateChange state, Context context, Ref<EventTrigger> ref) {
        if (this._EqualityOperation == 1 || this._EqualityOperation == 0) {
            if (state.TriggerType == MY_TRIGGER && state.VariableName.equalsIgnoreCase(this._VariableName)) {
                if (this._EqualityOperation == 1) {
                    if (this._VariableValue.equalsIgnoreCase(state.VariableValueNew)) {
                        return 2;
                    }
                } else if (this._EqualityOperation == 0 && this._VariableValue.equalsIgnoreCase(state.VariableValueOld)) {
                    return 2;
                }
            }
            if (state.GetVariableForName(this._VariableName).equalsIgnoreCase(this._VariableValue)) {
                if (this._EqualityOperation == 1) {
                    return 1;
                }
            } else if (this._EqualityOperation == 0) {
                return 1;
            }
            return 0;
        }
        Integer testValue = SetLlamaVariableAction.GetVariableValueAsInt(this._VariableValue);
        if (testValue == null) {
            return 0;
        }
        Integer currentVariableValue;
        boolean checkForTrigger;
        int myValueToBeTested = testValue.intValue();
        if (state.TriggerType == MY_TRIGGER && state.VariableName.equalsIgnoreCase(this._VariableName)) {
            currentVariableValue = SetLlamaVariableAction.GetVariableValueAsInt(state.VariableValueNew);
            checkForTrigger = true;
        } else {
            currentVariableValue = SetLlamaVariableAction.GetVariableValueAsInt(state.GetVariableForName(this._VariableName));
            checkForTrigger = false;
        }
        if (currentVariableValue == null) {
            return 0;
        }
        boolean isTrue;
        int currentVariableValueToBeTested = currentVariableValue.intValue();
        boolean isTrigger = checkForTrigger;
        switch (this._EqualityOperation) {
            case 2:
                if (currentVariableValueToBeTested >= myValueToBeTested) {
                    isTrue = false;
                    break;
                }
                isTrue = true;
                break;
            case 3:
                if (currentVariableValueToBeTested > myValueToBeTested) {
                    isTrue = false;
                    break;
                }
                isTrue = true;
                break;
            case 4:
                if (currentVariableValueToBeTested <= myValueToBeTested) {
                    isTrue = false;
                    break;
                }
                isTrue = true;
                break;
            case 5:
                if (currentVariableValueToBeTested < myValueToBeTested) {
                    isTrue = false;
                    break;
                }
                isTrue = true;
                break;
            default:
                isTrue = false;
                break;
        }
        if (!isTrue) {
            return 0;
        }
        if (isTrigger) {
            return 2;
        }
        return 1;
    }

    public boolean RenameArea(String oldName, String newName) {
        return false;
    }

    public void AppendConditionSimple(Context context, Appendable sb) throws IOException {
        String fixedVariableName = this._VariableName == null ? "" : this._VariableName;
        String fixedVariableValue = this._VariableValue == null ? "" : this._VariableValue;
        switch (this._EqualityOperation) {
            case 0:
                sb.append(String.format(context.getString(R.string.hrWhen1DoesNotHaveAValueOf2), new Object[]{fixedVariableName, fixedVariableValue}));
                return;
            case 1:
                sb.append(String.format(context.getString(R.string.hrWhen1HasAValueOf2), new Object[]{fixedVariableName, fixedVariableValue}));
                return;
            case 2:
                sb.append(String.format(context.getString(R.string.hrWhen1HasAValueLessThan2), new Object[]{fixedVariableName, fixedVariableValue}));
                return;
            case 3:
                sb.append(String.format(context.getString(R.string.hrWhen1HasAValueLessThanOrEqualTo2), new Object[]{fixedVariableName, fixedVariableValue}));
                return;
            case 4:
                sb.append(String.format(context.getString(R.string.hrWhen1HasAValueGreaterThan2), new Object[]{fixedVariableName, fixedVariableValue}));
                return;
            case 5:
                sb.append(String.format(context.getString(R.string.hrWhen1HasAValueGreaterThanOrEqualTo2), new Object[]{fixedVariableName, fixedVariableValue}));
                return;
            default:
                return;
        }
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 3;
    }

    public static LlamaVariableCondition CreateFrom(String[] parts, int currentPart) {
        return new LlamaVariableCondition(Integer.parseInt(parts[currentPart + 1]), parts[currentPart + 2], parts[currentPart + 3]);
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(this._EqualityOperation).append("|").append(this._VariableName).append("|").append(this._VariableValue);
    }

    public PreferenceEx<LlamaVariableCondition> CreatePreference(PreferenceActivity context) {
        return new ClickablePreferenceEx<LlamaVariableCondition>((ResultRegisterableActivity) context, context.getString(R.string.hrConditionLlamaVariable), this) {
            /* Access modifiers changed, original: protected */
            public String GetHumanReadableValue(Context context, LlamaVariableCondition value) {
                StringBuilder sb = new StringBuilder();
                try {
                    value.AppendConditionSimple(context, sb);
                    Helpers.CapitaliseFirstLetter(sb);
                    return sb.toString();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }

            /* Access modifiers changed, original: protected */
            public void OnPreferenceClicked(ResultRegisterableActivity host, LlamaVariableCondition existingValue, GotResultHandler<LlamaVariableCondition> gotResultHandler) {
                LlamaService.ThreadComplainMustBeWorker();
                HashMap<String, HashSet<String>> existingVariables = Instances.Service.GetAllLlamaVariableKeyValues();
                View view = View.inflate(host.GetActivity(), R.layout.llama_variable_change, null);
                final Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
                AutoCompleteTextView nameText = (AutoCompleteTextView) view.findViewById(R.id.variable_name);
                AutoCompleteTextView valueText = (AutoCompleteTextView) view.findViewById(R.id.variable_value);
                Button nameButton = (Button) view.findViewById(R.id.name_button);
                Button valueButton = (Button) view.findViewById(R.id.value_button);
                nameText.setText(existingValue._VariableName);
                valueText.setText(existingValue._VariableValue);
                spinner.setSelection(LlamaVariableCondition.fixLegacyOperationIndex(existingValue._EqualityOperation), false);
                VariableUiHelper.InitAutocompletionLists(host.GetActivity(), existingVariables, nameText, valueText, nameButton, valueButton);
                final AutoCompleteTextView autoCompleteTextView = nameText;
                final AutoCompleteTextView autoCompleteTextView2 = valueText;
                final GotResultHandler<LlamaVariableCondition> gotResultHandler2 = gotResultHandler;
                AlertDialog dialog = new Builder(host.GetActivity()).setPositiveButton(R.string.hrOk, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        gotResultHandler2.HandleResult(new LlamaVariableCondition(LlamaVariableCondition.fixLegacyOperationIndex(spinner.getSelectedItemPosition()), autoCompleteTextView.getText().toString().trim(), autoCompleteTextView2.getText().toString().trim()));
                        dialog.dismiss();
                    }
                }).setNegativeButton(R.string.hrCancel, null).setView(view).create();
                dialog.setOwnerActivity(host.GetActivity());
                dialog.show();
            }
        };
    }

    public String GetIsValidError(Context c) {
        if (this._VariableName == null || this._VariableName.length() == 0) {
            return c.getString(R.string.hrPleaseEnterAVariableName);
        }
        if (this._VariableValue == null) {
            return c.getString(R.string.hrPleaseEnterAVariableName);
        }
        return null;
    }

    private static int fixLegacyOperationIndex(int alternativeOpIndex) {
        if (alternativeOpIndex == 0) {
            return 1;
        }
        if (alternativeOpIndex == 1) {
            return 0;
        }
        return alternativeOpIndex;
    }
}
