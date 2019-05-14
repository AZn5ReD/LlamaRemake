package com.kebab.Llama.EventActions;

import android.app.Activity;
import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import com.kebab.AppendableCharSequence;
import com.kebab.ListPreference;
import com.kebab.Llama.Event;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.R;
import com.kebab.OnGetValueEx;
import com.kebab.PreferenceEx;
import java.io.IOException;

public class ToggleGpsAction extends EventAction<ToggleGpsAction> {
    boolean _TurnOn;

    public ToggleGpsAction(boolean turnOn) {
        this._TurnOn = turnOn;
    }

    public void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        service.ToggleGps(this._TurnOn);
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

    public static ToggleGpsAction CreateFrom(String[] parts, int currentPart) {
        return new ToggleGpsAction(parts[currentPart + 1].equals("1"));
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.TOGGLE_GPS_ACTION;
    }

    public PreferenceEx<ToggleGpsAction> CreatePreference(PreferenceActivity context) {
        final String on = context.getString(R.string.hrGpsOn);
        return CreateListPreference(context, context.getString(R.string.hrToggleGps), new String[]{on, context.getString(R.string.hrGpsOff)}, this._TurnOn ? on : context.getString(R.string.hrGpsOff), new OnGetValueEx<ToggleGpsAction>() {
            public ToggleGpsAction GetValue(Preference preference) {
                return new ToggleGpsAction(((ListPreference) preference).getValue().equals(on));
            }
        });
    }

    public void AppendActionDescription(Context context, AppendableCharSequence sb) throws IOException {
        if (this._TurnOn) {
            sb.append(context.getString(R.string.hrEnableGps));
        } else {
            sb.append(context.getString(R.string.hrDisableGps));
        }
    }

    public String GetIsValidError(Context context) {
        return null;
    }

    public boolean IsHarmful() {
        return false;
    }
}
