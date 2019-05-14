package com.kebab.Llama.EventConditions;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.preference.PreferenceActivity;
import com.kebab.AlertDialogEx.Builder;
import com.kebab.AppendableCharSequence;
import com.kebab.ClickablePreferenceEx;
import com.kebab.ClickablePreferenceEx.GotResultHandler;
import com.kebab.Helpers;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.EventMeta;
import com.kebab.Llama.EventMeta.ConditionStaticInitter1;
import com.kebab.Llama.EventTrigger;
import com.kebab.Llama.R;
import com.kebab.Llama.StateChange;
import com.kebab.PreferenceEx;
import com.kebab.Ref;
import com.kebab.ResultRegisterableActivity;
import java.io.IOException;
import java.util.ArrayList;

public class ScreenRotationCondition extends EventCondition<ScreenRotationCondition> {
    public static String MY_ID;
    public static int MY_TRIGGER;
    public static int[] MY_TRIGGERS;
    int _RotationAngleBitmask;

    static {
        EventMeta.InitCondition(EventFragment.SCREEN_ROTATION_CONDITION, new ConditionStaticInitter1() {
            public void UpdateStatics(String id, int[] triggers, int trigger) {
                ScreenRotationCondition.MY_ID = id;
                ScreenRotationCondition.MY_TRIGGERS = triggers;
                ScreenRotationCondition.MY_TRIGGER = trigger;
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

    public ScreenRotationCondition(int rotationAngleBitmask) {
        this._RotationAngleBitmask = rotationAngleBitmask;
    }

    public int TestCondition(StateChange state, Context context, Ref<EventTrigger> ref) {
        if ((state.ScreenRotation & this._RotationAngleBitmask) != 0) {
            return state.TriggerType == MY_TRIGGER ? 2 : 1;
        } else {
            return 0;
        }
    }

    public boolean RenameArea(String oldName, String newName) {
        return false;
    }

    public void AppendConditionSimple(Context context, Appendable sb) throws IOException {
        ArrayList<String> allowedRotations = new ArrayList();
        if ((this._RotationAngleBitmask & 8) != 0) {
            allowedRotations.add("0°");
        }
        if ((this._RotationAngleBitmask & 1) != 0) {
            allowedRotations.add("90°");
        }
        if ((this._RotationAngleBitmask & 2) != 0) {
            allowedRotations.add("180°");
        }
        if ((this._RotationAngleBitmask & 4) != 0) {
            allowedRotations.add("270°");
        }
        String angleListString = Helpers.ConcatenateListOfStrings(allowedRotations, ", ", " " + context.getString(R.string.hrAnd) + " ");
        sb.append(String.format(context.getString(R.string.hrWhenScreenIsRotated1), new Object[]{angleListString}));
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 1;
    }

    public static ScreenRotationCondition CreateFrom(String[] parts, int currentPart) {
        return new ScreenRotationCondition(Integer.parseInt(parts[currentPart + 1]));
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(this._RotationAngleBitmask);
    }

    public PreferenceEx<ScreenRotationCondition> CreatePreference(PreferenceActivity context) {
        return new ClickablePreferenceEx<ScreenRotationCondition>((ResultRegisterableActivity) context, context.getString(R.string.hrConditionScreenRotation), this) {
            /* Access modifiers changed, original: protected */
            public String GetHumanReadableValue(Context context, ScreenRotationCondition value) {
                StringBuilder sb = new StringBuilder();
                try {
                    value.AppendConditionSimple(context, AppendableCharSequence.Wrap(sb));
                    Helpers.CapitaliseFirstLetter(sb);
                    return sb.toString();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }

            /* Access modifiers changed, original: protected */
            public void OnPreferenceClicked(ResultRegisterableActivity host, ScreenRotationCondition existingValue, final GotResultHandler<ScreenRotationCondition> gotResultHandler) {
                Context context2 = host.GetActivity();
                checks = new boolean[4];
                String[] angles = new String[]{"0°", "90°", "180°", "270°"};
                if (existingValue._RotationAngleBitmask > 0) {
                    if ((existingValue._RotationAngleBitmask & 8) != 0) {
                        checks[0] = true;
                    }
                    if ((existingValue._RotationAngleBitmask & 1) != 0) {
                        checks[1] = true;
                    }
                    if ((existingValue._RotationAngleBitmask & 2) != 0) {
                        checks[2] = true;
                    }
                    if ((existingValue._RotationAngleBitmask & 4) != 0) {
                        checks[3] = true;
                    }
                }
                new Builder(context2, false).setMultiChoiceItems(angles, checks, new OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checks[which] = isChecked;
                    }
                }).setPositiveButton(context2.getString(R.string.hrOk), new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        int value = 0;
                        if (checks[0]) {
                            value = 0 | 8;
                        }
                        if (checks[1]) {
                            value |= 1;
                        }
                        if (checks[2]) {
                            value |= 2;
                        }
                        if (checks[3]) {
                            value |= 4;
                        }
                        if (value != 0) {
                            gotResultHandler.HandleResult(new ScreenRotationCondition(value));
                            dialog.dismiss();
                            return;
                        }
                        gotResultHandler.HandleResult(new ScreenRotationCondition(8));
                    }
                }).setNegativeButton(context2.getString(R.string.hrCancel), null).show();
            }
        };
    }

    public String GetIsValidError(Context c) {
        return null;
    }
}
