package com.kebab.Llama.EventActions;

import android.app.Activity;
import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import com.kebab.AppendableCharSequence;
import com.kebab.ArrayHelpers;
import com.kebab.ListPreference;
import com.kebab.Llama.Event;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.R;
import com.kebab.OnGetValueEx;
import com.kebab.PreferenceEx;
import java.io.IOException;

public class ChangeScreenTimeoutAction extends EventAction<ChangeScreenTimeoutAction> {
    public static final int NEVER = -1;
    int _Timeout;

    public ChangeScreenTimeoutAction(int timeout) {
        this._Timeout = timeout;
    }

    public void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        if (!super.ActionIsProhibited(service, eventRunMode)) {
            service.ChangeScreenTimeout(this._Timeout == -1 ? null : Integer.valueOf(this._Timeout));
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
        sb.append(this._Timeout);
    }

    public static ChangeScreenTimeoutAction CreateFrom(String[] parts, int currentPart) {
        return new ChangeScreenTimeoutAction(Integer.parseInt(parts[currentPart + 1]));
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.CHANGE_SCREEN_TIMEOUT_ACTION;
    }

    public PreferenceEx<ChangeScreenTimeoutAction> CreatePreference(PreferenceActivity context) {
        String seconds = context.getString(R.string.hrSeconds);
        String minute = context.getString(R.string.hrMinute);
        String minutes = context.getString(R.string.hrMinutes);
        String[] names = new String[]{"2 " + seconds, "5 " + seconds, "8 " + seconds, "10 " + seconds, "15 " + seconds, "30 " + seconds, "1 " + minute, "2 " + minutes, "3 " + minutes, "4 " + minutes, "5 " + minutes, "10 " + minutes, "15 " + minutes, "20 " + minutes, "25 " + minutes, "30 " + minutes, context.getString(R.string.hrNever)};
        String[] values = new String[]{"2", "5", "8", "10", "15", "30", "60", "120", "180", "240", "300", "600", "900", "1200", "1500", "1800", "-1"};
        Integer index = ArrayHelpers.FindIndex(values, String.valueOf(this._Timeout));
        if (index == null) {
            index = Integer.valueOf(5);
        }
        return CreateListPreference((Context) context, context.getString(R.string.hrChangeScreenTimeout), values, names, values[index.intValue()], (OnGetValueEx) new OnGetValueEx<ChangeScreenTimeoutAction>() {
            public ChangeScreenTimeoutAction GetValue(Preference preference) {
                String value = ((ListPreference) preference).getValue();
                return new ChangeScreenTimeoutAction(value == null ? 30 : Integer.parseInt(value));
            }
        });
    }

    public void AppendActionDescription(Context context, AppendableCharSequence sb) throws IOException {
        switch (this._Timeout) {
            case -1:
                sb.append(context.getString(R.string.hrDisableScreenTimeout));
                return;
            default:
                sb.append(String.format(context.getString(R.string.hrSetScreenTimeoutTo1Seconds), new Object[]{Integer.valueOf(this._Timeout)}));
                return;
        }
    }

    public String GetIsValidError(Context context) {
        return null;
    }

    public boolean IsHarmful() {
        if (this._Timeout < 2) {
            return true;
        }
        return false;
    }
}
