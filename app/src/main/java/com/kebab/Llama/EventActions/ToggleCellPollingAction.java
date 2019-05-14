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

public class ToggleCellPollingAction extends EventAction<ToggleCellPollingAction> {
    boolean _TurnOn;

    public ToggleCellPollingAction(boolean turnOn) {
        this._TurnOn = turnOn;
    }

    public void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        service.ToggleCellPolling(this._TurnOn);
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

    public static ToggleCellPollingAction CreateFrom(String[] parts, int currentPart) {
        return new ToggleCellPollingAction(parts[currentPart + 1].equals("1"));
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.TOGGLE_CELL_POLLING_ACTION;
    }

    public PreferenceEx<ToggleCellPollingAction> CreatePreference(PreferenceActivity context) {
        final String on = context.getString(R.string.hrCellPollingOn);
        return CreateListPreference(context, context.getString(R.string.hrToggleLlamaCellPolling), new String[]{on, context.getString(R.string.hrCellPollingOff)}, this._TurnOn ? on : context.getString(R.string.hrCellPollingOff), new OnGetValueEx<ToggleCellPollingAction>() {
            public ToggleCellPollingAction GetValue(Preference preference) {
                return new ToggleCellPollingAction(((ListPreference) preference).getValue().equals(on));
            }
        });
    }

    public void AppendActionDescription(Context context, AppendableCharSequence sb) throws IOException {
        if (this._TurnOn) {
            sb.append(context.getString(R.string.hrEnableLlamaCellPolling));
        } else {
            sb.append(context.getString(R.string.hrDisableLlamaCellPolling));
        }
    }

    public String GetIsValidError(Context context) {
        return null;
    }

    public boolean IsHarmful() {
        return false;
    }
}
