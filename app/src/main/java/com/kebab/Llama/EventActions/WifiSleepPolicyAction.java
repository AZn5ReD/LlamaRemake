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

public class WifiSleepPolicyAction extends EventAction<WifiSleepPolicyAction> {
    public static final int SLEEP_POLICY_NEVER = 2;
    public static final int SLEEP_POLICY_NEVER_WHILE_PLUGGED_IN = 1;
    public static final int SLEEP_POLICY_SCREEN_OFF = 0;
    int _PolicyMode;

    public WifiSleepPolicyAction(int policyMode) {
        this._PolicyMode = policyMode;
    }

    public void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        service.ToggleWifiSleepPolicy(this._PolicyMode);
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
        sb.append(this._PolicyMode);
    }

    public static WifiSleepPolicyAction CreateFrom(String[] parts, int currentPart) {
        return new WifiSleepPolicyAction(Integer.parseInt(parts[currentPart + 1]));
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.WIFI_SLEEP_POLICY_ACTION;
    }

    public PreferenceEx<WifiSleepPolicyAction> CreatePreference(PreferenceActivity context) {
        String currentValue;
        final String never = context.getString(R.string.hrWifiSleepPolicyNever);
        final String neverWhilePlugged = context.getString(R.string.hrWifiSleepPolicyNeverWhilePlugged);
        String screenOff = context.getString(R.string.hrWifiSleepPolicyScreenOff);
        switch (this._PolicyMode) {
            case 1:
                currentValue = neverWhilePlugged;
                break;
            case 2:
                currentValue = never;
                break;
            default:
                currentValue = screenOff;
                break;
        }
        return CreateListPreference(context, context.getString(R.string.hrActionWifiSleepPolicy), new String[]{screenOff, neverWhilePlugged, never}, currentValue, new OnGetValueEx<WifiSleepPolicyAction>() {
            public WifiSleepPolicyAction GetValue(Preference preference) {
                int intValue;
                String value = ((ListPreference) preference).getValue();
                if (neverWhilePlugged.equals(value)) {
                    intValue = 1;
                } else if (never.equals(value)) {
                    intValue = 2;
                } else {
                    intValue = 0;
                }
                return new WifiSleepPolicyAction(intValue);
            }
        });
    }

    public void AppendActionDescription(Context context, AppendableCharSequence sb) throws IOException {
        switch (this._PolicyMode) {
            case 1:
                sb.append(context.getString(R.string.hrWifiSleepPolicyNeverWhilePluggedDescription));
                return;
            case 2:
                sb.append(context.getString(R.string.hrWifiSleepPolicyNeverDescription));
                return;
            default:
                sb.append(context.getString(R.string.hrWifiSleepPolicyScreenOffDescription));
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
