package com.kebab.Llama.EventActions;

import android.app.Activity;
import android.content.Context;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.provider.Settings.System;
import com.kebab.AppendableCharSequence;
import com.kebab.Helpers;
import com.kebab.ListPreference;
import com.kebab.Llama.Constants;
import com.kebab.Llama.Event;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.R;
import com.kebab.OnGetValueEx;
import com.kebab.PreferenceEx;
import java.io.IOException;

public class ChangeBrightnessAction extends EventAction<ChangeBrightnessAction> {
    public static final int AUTO = -1;
    public static final int MINIMUM_BRIGHTNESS = 5;
    boolean _AllowExtraActivity = false;
    int _Brightness;

    public ChangeBrightnessAction(int brightness) {
        this._Brightness = brightness;
    }

    public void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        boolean z = true;
        if (eventRunMode != 2) {
            if (this._Brightness != -1) {
                z = false;
            }
            service.ChangeBrightness(z, this._Brightness, activity, this._AllowExtraActivity);
        } else if (this._Brightness < 5) {
            service.HandleFriendlyError(service.getString(R.string.hrBrightnessCustomEventWarning), false);
        } else {
            if (this._Brightness != -1) {
                z = false;
            }
            service.ChangeBrightness(z, this._Brightness, activity, this._AllowExtraActivity);
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
        sb.append(this._Brightness);
    }

    public static ChangeBrightnessAction CreateFrom(String[] parts, int currentPart) {
        return new ChangeBrightnessAction(Integer.parseInt(parts[currentPart + 1]));
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.CHANGE_BRIGHTNESS_ACTION;
    }

    public PreferenceEx<ChangeBrightnessAction> CreatePreference(final PreferenceActivity context) {
        String currentValue;
        boolean supportsAuto;
        switch (this._Brightness) {
            case -1:
                currentValue = context.getString(R.string.hrAuto);
                break;
            default:
                currentValue = this._Brightness + "%";
                break;
        }
        if (System.getInt(context.getContentResolver(), Constants.SCREEN_BRIGHTNESS_MODE_KEY, -666) != -666) {
            supportsAuto = true;
        } else {
            supportsAuto = false;
        }
        return CreateListPreference((Context) context, context.getString(R.string.hrChangeBrightness), supportsAuto ? new String[]{context.getString(R.string.hrAuto), "100%", "90%", "80%", "75%", "70%", "60%", "50%", "40%", "30%", "25%", "20%", "15%", "10%", "5%", "4%", "3%", "2%", "1%"} : new String[]{"100%", "90%", "80%", "75%", "70%", "60%", "50%", "40%", "30%", "25%", "20%", "15%", "10%", "5%", "4%", "3%", "2%", "1%"}, currentValue, (OnGetValueEx) new OnGetValueEx<ChangeBrightnessAction>() {
            public ChangeBrightnessAction GetValue(Preference preference) {
                return new ChangeBrightnessAction(ChangeBrightnessAction.this.GetValueFromPreference(context, ((ListPreference) preference).getValue()));
            }
        }, (OnPreferenceChangeListener) new OnPreferenceChangeListener() {
            boolean whinged;
            boolean whinged2;

            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (!this.whinged) {
                    Helpers.ShowSimpleDialogMessage(context, context.getString(R.string.hrScreenBrightnessWarning));
                    this.whinged = true;
                }
                if (ChangeBrightnessAction.this.GetValueFromPreference(context, (String) newValue) < 5 && !this.whinged2) {
                    Helpers.ShowSimpleDialogMessage(context, context.getString(R.string.hrScreenBrightnessLowWarning));
                    this.whinged2 = true;
                }
                return true;
            }
        });
    }

    /* Access modifiers changed, original: protected */
    public int GetValueFromPreference(Context context, String value) {
        if (value == null) {
            return 50;
        }
        if (context.getString(R.string.hrAuto).equals(value)) {
            return -1;
        }
        return Integer.parseInt(value.replace("%", ""));
    }

    public void AppendActionDescription(Context context, AppendableCharSequence sb) throws IOException {
        switch (this._Brightness) {
            case -1:
                sb.append(context.getString(R.string.hrSetToAutomaticBrightness));
                return;
            default:
                sb.append(String.format(context.getString(R.string.hrSetTo1Brightness), new Object[]{Integer.valueOf(this._Brightness)}));
                return;
        }
    }

    public String GetIsValidError(Context context) {
        return null;
    }

    public boolean IsHarmful() {
        if (this._Brightness < 5) {
            return true;
        }
        return false;
    }
}
