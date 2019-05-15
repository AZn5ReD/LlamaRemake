package com.kebab.Llama.EventActions;

import android.app.Activity;
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
import com.kebab.Llama.Event;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.R;
import com.kebab.PreferenceEx;
import com.kebab.ResultRegisterableActivity;
import java.io.IOException;
import java.util.ArrayList;

public class ScreenRotationAction extends EventAction<ScreenRotationAction> {
    static final int ROTATE_AUTO = -1;
    public static final int ROTATE_BITMASK_0 = 8;
    public static final int ROTATE_BITMASK_180 = 2;
    public static final int ROTATE_BITMASK_270 = 4;
    public static final int ROTATE_BITMASK_90 = 1;
    public static final int ROTATE_DEFAULT = 0;
    int _RotationAngleBitmask;

    public ScreenRotationAction(int policyMode) {
        this._RotationAngleBitmask = policyMode;
    }

    public void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        if (this._RotationAngleBitmask == 0) {
            service.ToggleScreenRotation(Boolean.valueOf(false), 0);
        } else if (this._RotationAngleBitmask == -1) {
            service.ToggleScreenRotation(Boolean.valueOf(true), 0);
        } else {
            service.ToggleScreenRotation(null, this._RotationAngleBitmask);
        }
    }

    public boolean RenameProfile(String oldName, String newName) {
        return false;
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 1;
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(this._RotationAngleBitmask);
    }

    public static ScreenRotationAction CreateFrom(String[] parts, int currentPart) {
        return new ScreenRotationAction(Integer.parseInt(parts[currentPart + 1]));
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.SCREEN_ROTATION_ACTION;
    }

    public PreferenceEx<ScreenRotationAction> CreatePreference(PreferenceActivity context) {
        return new ClickablePreferenceEx<ScreenRotationAction>((ResultRegisterableActivity) context, context.getString(R.string.hrActionScreenRotation), this) {
            /* Access modifiers changed, original: protected */
            public String GetHumanReadableValue(Context context, ScreenRotationAction value) {
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
            public void OnPreferenceClicked(final ResultRegisterableActivity host, final ScreenRotationAction existingValue, final GotResultHandler<ScreenRotationAction> gotResultHandler) {
                String[] optionsArray = host.GetActivity().getResources().getStringArray(R.array.simpleRotationOptions);
                for (int i = 0; i < optionsArray.length; i++) {
                    optionsArray[i] = Helpers.CapitaliseFirstLetter(optionsArray[i]);
                }
                new Builder(host.GetActivity()).setItems(optionsArray, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            gotResultHandler.HandleResult(new ScreenRotationAction(0));
                        } else if (which == 1) {
                            gotResultHandler.HandleResult(new ScreenRotationAction(-1));
                        } else {
                            dialog.dismiss();
                            new Builder(host.GetActivity()).setMessage(R.string.hrAdvancedRotationWarning).setPositiveButton(R.string.hrOkeyDoke, new OnClickListener() {
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    ShowAdvancedRotationDialog(host, existingValue, gotResultHandler);
                                }
                            }).show();
                        }
                    }
                }).show();
            }

            /* Access modifiers changed, original: 0000 */
            public void ShowAdvancedRotationDialog(ResultRegisterableActivity host, ScreenRotationAction existingValue, final GotResultHandler<ScreenRotationAction> gotResultHandler) {
                Activity context2 = host.GetActivity();
                final boolean[] checks = new boolean[4];
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
                new Builder(context2).setMultiChoiceItems(angles, checks, new OnMultiChoiceClickListener() {
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
                            gotResultHandler.HandleResult(new ScreenRotationAction(value));
                            dialog.dismiss();
                            return;
                        }
                        gotResultHandler.HandleResult(new ScreenRotationAction(0));
                    }
                }).setNegativeButton(context2.getString(R.string.hrCancel), null).show();
            }
        };
    }

    public void AppendActionDescription(Context context, AppendableCharSequence sb) throws IOException {
        switch (this._RotationAngleBitmask) {
            case -1:
                sb.append(context.getString(R.string.hrEnableRotation));
                return;
            case 0:
                sb.append(context.getString(R.string.hrDisableRotation));
                return;
            default:
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
                sb.append(String.format(context.getString(R.string.hrAllow1Rotations), new Object[]{angleListString}));
                return;
        }
    }

    public String GetIsValidError(Context context) {
        return null;
    }

    public boolean IsHarmful() {
        return false;
    }
}
