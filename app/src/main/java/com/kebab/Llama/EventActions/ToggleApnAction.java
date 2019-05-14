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

public class ToggleApnAction extends EventAction<ToggleApnAction> {
    boolean _TurnOn;

    public ToggleApnAction(boolean turnOn) {
        this._TurnOn = turnOn;
    }

    public void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        service.ToggleApn(this._TurnOn);
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

    public static ToggleApnAction CreateFrom(String[] parts, int currentPart) {
        return new ToggleApnAction(parts[currentPart + 1].equals("1"));
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.TOGGLE_APN_ACTION;
    }

    public PreferenceEx<ToggleApnAction> CreatePreference(PreferenceActivity context) {
        final String on = context.getString(R.string.hrApnOn);
        return CreateListPreference(context, context.getString(R.string.hrToggleApn), new String[]{on, context.getString(R.string.hrApnOff)}, this._TurnOn ? on : context.getString(R.string.hrApnOff), new OnGetValueEx<ToggleApnAction>() {
            public ToggleApnAction GetValue(Preference preference) {
                return new ToggleApnAction(((ListPreference) preference).getValue().equals(on));
            }
        });
    }

    public void AppendActionDescription(Context context, AppendableCharSequence sb) throws IOException {
        if (this._TurnOn) {
            sb.append(context.getString(R.string.hrEnableApn));
        } else {
            sb.append(context.getString(R.string.hrDisableApn));
        }
    }

    public String GetIsValidError(Context context) {
        return null;
    }

    public boolean IsHarmful() {
        return false;
    }
}
