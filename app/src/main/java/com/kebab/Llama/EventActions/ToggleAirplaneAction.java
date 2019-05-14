package com.kebab.Llama.EventActions;

import android.app.Activity;
import android.content.Context;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import com.kebab.ApiCompat.AirplaneCompat;
import com.kebab.AppendableCharSequence;
import com.kebab.Helpers;
import com.kebab.ListPreference;
import com.kebab.Llama.Event;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.R;
import com.kebab.OnGetValueEx;
import com.kebab.PreferenceEx;
import java.io.IOException;

public class ToggleAirplaneAction extends EventAction<ToggleAirplaneAction> {
    boolean _TurnOn;

    public ToggleAirplaneAction(boolean turnOn) {
        this._TurnOn = turnOn;
    }

    public void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        service.ToggleAirplane(this._TurnOn);
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
        sb.append(this._TurnOn ? "1" : "0");
    }

    public static ToggleAirplaneAction CreateFrom(String[] parts, int currentPart) {
        return new ToggleAirplaneAction(parts[currentPart + 1].equals("1"));
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.TOGGLE_AIRPLANE_ACTION;
    }

    public PreferenceEx<ToggleAirplaneAction> CreatePreference(final PreferenceActivity context) {
        final String on = context.getString(R.string.hrAirplaneModeOn);
        return CreateListPreference((Context) context, context.getString(R.string.hrToggleAirplaneMode), new String[]{on, context.getString(R.string.hrAirplaneModeOff)}, this._TurnOn ? on : context.getString(R.string.hrAirplaneModeOff), (OnGetValueEx) new OnGetValueEx<ToggleAirplaneAction>() {
            public ToggleAirplaneAction GetValue(Preference preference) {
                return new ToggleAirplaneAction(((ListPreference) preference).getValue().equals(on));
            }
        }, (OnPreferenceChangeListener) new OnPreferenceChangeListener() {
            boolean whinged;

            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (!this.whinged && AirplaneCompat.IsSecureSystemSetting()) {
                    Helpers.ShowSimpleDialogMessage(context, context.getString(R.string.hrAirplaneModeStupidness));
                    this.whinged = true;
                }
                return true;
            }
        });
    }

    public void AppendActionDescription(Context context, AppendableCharSequence sb) throws IOException {
        if (this._TurnOn) {
            sb.append(context.getString(R.string.hrEnableAirplaneMode));
        } else {
            sb.append(context.getString(R.string.hrDisableAirplaneMode));
        }
    }

    public String GetIsValidError(Context context) {
        return null;
    }

    public boolean IsHarmful() {
        return false;
    }
}
