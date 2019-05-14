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

public class ToggleSyncAction extends EventAction<ToggleSyncAction> {
    public static final int SYNC_TOGGLE_OFF = 0;
    public static final int SYNC_TOGGLE_ON = 1;
    public static final int SYNC_TOGGLE_ON_AND_REQUEST = 2;
    int _ToggleType;

    public ToggleSyncAction(int toggleType) {
        this._ToggleType = toggleType;
    }

    public void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        if (this._ToggleType == 1) {
            service.EnableSync(true, false);
        } else if (this._ToggleType == 0) {
            service.EnableSync(false, false);
        } else {
            service.EnableSync(true, true);
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
        sb.append(this._ToggleType);
    }

    public static ToggleSyncAction CreateFrom(String[] parts, int currentPart) {
        return new ToggleSyncAction(Integer.parseInt(parts[currentPart + 1]));
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.TOGGLE_SYNC_ACTION;
    }

    public PreferenceEx<ToggleSyncAction> CreatePreference(PreferenceActivity context) {
        String currentValue;
        final String SYNC_TOGGLE_ON_AND_REQUEST_NAME = context.getString(R.string.hrEnableSyncRequestUpdate);
        final String SYNC_TOGGLE_ON_NAME = context.getString(R.string.hrEnableSyncNoUpdate);
        String SYNC_TOGGLE_OFF_NAME = context.getString(R.string.hrDisableSync);
        switch (this._ToggleType) {
            case 0:
                currentValue = SYNC_TOGGLE_OFF_NAME;
                break;
            case 1:
                currentValue = SYNC_TOGGLE_ON_NAME;
                break;
            default:
                currentValue = SYNC_TOGGLE_ON_AND_REQUEST_NAME;
                break;
        }
        return CreateListPreference(context, context.getString(R.string.hrToggleAccountSync), new String[]{SYNC_TOGGLE_ON_AND_REQUEST_NAME, SYNC_TOGGLE_ON_NAME, SYNC_TOGGLE_OFF_NAME}, currentValue, new OnGetValueEx<ToggleSyncAction>() {
            public ToggleSyncAction GetValue(Preference preference) {
                int finalValue;
                String value = ((ListPreference) preference).getValue();
                if (SYNC_TOGGLE_ON_AND_REQUEST_NAME.equals(value)) {
                    finalValue = 2;
                } else if (SYNC_TOGGLE_ON_NAME.equals(value)) {
                    finalValue = 1;
                } else {
                    finalValue = 0;
                }
                return new ToggleSyncAction(finalValue);
            }
        });
    }

    public void AppendActionDescription(Context context, AppendableCharSequence sb) throws IOException {
        switch (this._ToggleType) {
            case 1:
                sb.append(context.getString(R.string.hrEnableAccountSyncDescription));
                return;
            case 2:
                sb.append(context.getString(R.string.hrEnableAccountSyncThenRequestUpdateDescription));
                return;
            default:
                sb.append(context.getString(R.string.hrDisableAccountSyncDescription));
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
