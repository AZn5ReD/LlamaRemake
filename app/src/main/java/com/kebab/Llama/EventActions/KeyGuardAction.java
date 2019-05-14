package com.kebab.Llama.EventActions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import com.kebab.AlertDialogEx.Builder;
import com.kebab.AppendableCharSequence;
import com.kebab.ListPreference;
import com.kebab.Llama.Constants;
import com.kebab.Llama.DeviceAdmin.DeviceAdminCompat;
import com.kebab.Llama.Event;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.R;
import com.kebab.OnGetValueEx;
import com.kebab.PreferenceEx;
import com.kebab.ResultRegisterableActivity;
import com.kebab.ResultRegisterableActivity.ResultCallback;
import java.io.IOException;

public class KeyGuardAction extends EventAction<KeyGuardAction> {
    public static final int KEYGUARD_LOCK = 1;
    public static final int KEYGUARD_LOCK_FORCE = 3;
    public static final int KEYGUARD_UNLOCK_IMMEDIATELY = 0;
    public static final int KEYGUARD_UNLOCK_REQUIRE_PASSWORD = 2;
    int _EnableKeyGuardMode;

    public KeyGuardAction(int enableKeyGaurdMode) {
        this._EnableKeyGuardMode = enableKeyGaurdMode;
    }

    public void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        if (!super.ActionIsProhibited(service, eventRunMode)) {
            switch (this._EnableKeyGuardMode) {
                case 0:
                    service.EnableKeyGuard(false, false, false);
                    return;
                case 2:
                    service.EnableKeyGuard(false, true, false);
                    return;
                case 3:
                    service.EnableKeyGuard(true, false, true);
                    break;
            }
            service.EnableKeyGuard(true, false, false);
        }
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.KEY_GUARD_ACTION;
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 1;
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(this._EnableKeyGuardMode);
    }

    public static KeyGuardAction CreateFrom(String[] parts, int currentPart) {
        return new KeyGuardAction(Integer.parseInt(parts[currentPart + 1]));
    }

    public PreferenceEx<KeyGuardAction> CreatePreference(final PreferenceActivity context) {
        String existingValue;
        String screenLockOn = context.getString(R.string.hrScreenLockOn);
        final String screenLockOffImmediately = context.getString(R.string.hrScreenLockOffImmediately);
        final String screenLockOffAfterPassword = context.getString(R.string.hrScreenLockOffAfterPassword);
        final String screenLockOnForce = context.getString(R.string.hrScreenLockOnForce);
        switch (this._EnableKeyGuardMode) {
            case 0:
                existingValue = screenLockOffImmediately;
                break;
            case 2:
                existingValue = screenLockOffAfterPassword;
                break;
            case 3:
                existingValue = screenLockOnForce;
                break;
            default:
                existingValue = screenLockOn;
                break;
        }
        Context context2 = context;
        ListPreference<KeyGuardAction> pref = CreateListPreference(context2, context.getString(R.string.hrToggleScreenLock), new String[]{screenLockOn, screenLockOnForce, screenLockOffImmediately, screenLockOffAfterPassword}, existingValue, new OnGetValueEx<KeyGuardAction>() {
            public KeyGuardAction GetValue(Preference preference) {
                int lockType;
                String value = ((ListPreference) preference).getValue();
                if (screenLockOffImmediately.equals(value)) {
                    lockType = 0;
                } else if (screenLockOffAfterPassword.equals(value)) {
                    lockType = 2;
                } else if (screenLockOnForce.equals(value)) {
                    lockType = 3;
                } else {
                    lockType = 1;
                }
                return new KeyGuardAction(lockType);
            }
        });
        pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (screenLockOffImmediately.equals(newValue)) {
                    new Builder(context).setMessage(R.string.hrKeyGuardBuggyWarning).setPositiveButton(R.string.hrOkeyDoke, null).show();
                } else if (screenLockOnForce.equals(newValue)) {
                    if (!DeviceAdminCompat.IsSupported()) {
                        new Builder(context).setMessage(R.string.hrDeviceAdminNotSupported).setPositiveButton(R.string.hrOkeyDoke, null).show();
                    } else if (!DeviceAdminCompat.IsAdminEnabled(context)) {
                        DeviceAdminCompat.ShowEnableAdmin((ResultRegisterableActivity) context, Constants.REQUEST_CODE_DEVICE_ADMIN, new ResultCallback() {
                            public void HandleResult(int resultCode, Intent data, Object extraStateInfo) {
                            }
                        });
                    }
                }
                return true;
            }
        });
        return pref;
    }

    public boolean RenameProfile(String oldName, String newName) {
        return false;
    }

    public void AppendActionDescription(Context context, AppendableCharSequence sb) throws IOException {
        switch (this._EnableKeyGuardMode) {
            case 0:
                sb.append(context.getString(R.string.hrDisableKeyGuardImmediately));
                return;
            case 2:
                sb.append(context.getString(R.string.hrDisableKeyGuardAfterUnlock));
                return;
            case 3:
                sb.append(context.getString(R.string.hrEnableKeyGuardForce));
                return;
            default:
                sb.append(context.getString(R.string.hrEnableKeyGuard));
                return;
        }
    }

    public String GetIsValidError(Context context) {
        return null;
    }

    public boolean IsHarmful() {
        return true;
    }
}
