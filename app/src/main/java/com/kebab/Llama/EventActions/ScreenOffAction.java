package com.kebab.Llama.EventActions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import com.kebab.AppendableCharSequence;
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

public class ScreenOffAction extends EventAction<ScreenOffAction> {
    int _WakeType;

    public ScreenOffAction(int wakeType) {
        this._WakeType = wakeType;
    }

    public void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        if (!super.ActionIsProhibited(service, eventRunMode)) {
            service.TurnOffScreen();
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
        sb.append(this._WakeType);
    }

    public static ScreenOffAction CreateFrom(String[] parts, int currentPart) {
        return new ScreenOffAction(Integer.parseInt(parts[currentPart + 1]));
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.SCREEN_OFF_ACTION;
    }

    public PreferenceEx<ScreenOffAction> CreatePreference(PreferenceActivity context) {
        String[] blah = new String[0];
        if (DeviceAdminCompat.IsSupported() && !DeviceAdminCompat.IsAdminEnabled(context)) {
            DeviceAdminCompat.ShowEnableAdmin((ResultRegisterableActivity) context, Constants.REQUEST_CODE_DEVICE_ADMIN, new ResultCallback() {
                public void HandleResult(int resultCode, Intent data, Object extraStateInfo) {
                }
            });
        }
        return CreateSimplePreference(context, context.getString(R.string.hrActionScreenOff), context.getString(R.string.hrActionScreenOff), new OnGetValueEx<ScreenOffAction>() {
            public ScreenOffAction GetValue(Preference preference) {
                return new ScreenOffAction(0);
            }
        });
    }

    public void AppendActionDescription(Context context, AppendableCharSequence sb) throws IOException {
        sb.append(context.getString(R.string.hrTurnOffTheScreen));
    }

    public String GetIsValidError(Context context) {
        return null;
    }

    public boolean IsHarmful() {
        return true;
    }
}
