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

public class Toggle4GAction extends EventAction<Toggle4GAction> {
    boolean _TurnOn;

    public Toggle4GAction(boolean turnOn) {
        this._TurnOn = turnOn;
    }

    public void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        service.Toggle4G(this._TurnOn);
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

    public static Toggle4GAction CreateFrom(String[] parts, int currentPart) {
        return new Toggle4GAction(parts[currentPart + 1].equals("1"));
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.TOGGLE_FOUR_G_ACTION;
    }

    public PreferenceEx<Toggle4GAction> CreatePreference(PreferenceActivity context) {
        final String on = context.getString(R.string.hr4GOn);
        return CreateListPreference(context, context.getString(R.string.hrActionToggle4G), new String[]{on, context.getString(R.string.hr4GOff)}, this._TurnOn ? on : context.getString(R.string.hr4GOff), new OnGetValueEx<Toggle4GAction>() {
            public Toggle4GAction GetValue(Preference preference) {
                return new Toggle4GAction(((ListPreference) preference).getValue().equals(on));
            }
        });
    }

    public void AppendActionDescription(Context context, AppendableCharSequence sb) throws IOException {
        if (this._TurnOn) {
            sb.append(context.getString(R.string.hrEnable4G));
        } else {
            sb.append(context.getString(R.string.hrDisable4G));
        }
    }

    public String GetIsValidError(Context context) {
        return null;
    }

    public boolean IsHarmful() {
        return false;
    }
}
