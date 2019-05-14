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
import com.kebab.Llama.LlamaSettings;
import com.kebab.Llama.R;
import com.kebab.OnGetValueEx;
import com.kebab.PreferenceEx;
import java.io.IOException;

public class LockProfileChangesAction extends EventAction<LockProfileChangesAction> {
    boolean _TurnOn;

    public LockProfileChangesAction(boolean turnOn) {
        this._TurnOn = turnOn;
    }

    public void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        if (this._TurnOn) {
            service.EnableProfileLock(((Integer) LlamaSettings.ProfileUnlockDelay.GetValue(service)).intValue(), service.GetLastProfileName(), false);
        } else {
            service.DisableProfileLock(true, false);
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
        sb.append(this._TurnOn ? "1" : "0");
    }

    public static LockProfileChangesAction CreateFrom(String[] parts, int currentPart) {
        return new LockProfileChangesAction(parts[currentPart + 1].equals("1"));
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.TOGGLE_PROFILE_LOCK_ACTION;
    }

    public PreferenceEx<LockProfileChangesAction> CreatePreference(PreferenceActivity context) {
        final String lockProfileChanges = context.getString(R.string.hrLockProfileChanges);
        return CreateListPreference(context, context.getString(R.string.hrToggleProfileChangesLock), new String[]{lockProfileChanges, context.getString(R.string.hrUnlockProfileChanges)}, this._TurnOn ? lockProfileChanges : context.getString(R.string.hrUnlockProfileChanges), new OnGetValueEx<LockProfileChangesAction>() {
            public LockProfileChangesAction GetValue(Preference preference) {
                return new LockProfileChangesAction(((ListPreference) preference).getValue().equals(lockProfileChanges));
            }
        });
    }

    public void AppendActionDescription(Context context, AppendableCharSequence sb) throws IOException {
        if (this._TurnOn) {
            sb.append(context.getString(R.string.hrLockProfileChangesDescription));
        } else {
            sb.append(context.getString(R.string.hrUnlockProfileChangesDescription));
        }
    }

    public String GetIsValidError(Context context) {
        return null;
    }

    public boolean IsHarmful() {
        return false;
    }
}
