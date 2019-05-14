package com.kebab.Llama.EventActions;

import android.app.Activity;
import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import com.kebab.AppendableCharSequence;
import com.kebab.ListPreference;
import com.kebab.Llama.Event;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.Instances;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.R;
import com.kebab.OnGetValueEx;
import com.kebab.PreferenceEx;
import java.io.IOException;

public class ShutdownPhoneAction extends EventAction<ShutdownPhoneAction> {
    int _ShutdownType;

    public ShutdownPhoneAction(int shutdownType) {
        this._ShutdownType = shutdownType;
    }

    public void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        if (!super.ActionIsProhibited(service, eventRunMode) && this._ShutdownType == 1) {
            service.ShutdownPhone();
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
        sb.append(this._ShutdownType);
    }

    public static ShutdownPhoneAction CreateFrom(String[] parts, int currentPart) {
        return new ShutdownPhoneAction(Integer.parseInt(parts[currentPart + 1]));
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.SHUTDOWN_PHONE_ACTION;
    }

    public PreferenceEx<ShutdownPhoneAction> CreatePreference(PreferenceActivity context) {
        String SHUTDOWN_NO = context.getString(R.string.hrNoDontDoAnything);
        final String SHUTDOWN_YES = context.getString(R.string.hrYesIReallyWantToShutdown);
        return CreateListPreference(context, context.getString(R.string.hrActionShutdown), new String[]{SHUTDOWN_NO, SHUTDOWN_YES}, this._ShutdownType == 1 ? SHUTDOWN_YES : SHUTDOWN_NO, new OnGetValueEx<ShutdownPhoneAction>() {
            public ShutdownPhoneAction GetValue(Preference preference) {
                int finalValue;
                if (Instances.Service != null) {
                    Instances.Service.AcquireRoot();
                }
                if (SHUTDOWN_YES.equals(((ListPreference) preference).getValue())) {
                    finalValue = 1;
                } else {
                    finalValue = 0;
                }
                return new ShutdownPhoneAction(finalValue);
            }
        });
    }

    public void AppendActionDescription(Context context, AppendableCharSequence sb) throws IOException {
        if (this._ShutdownType == 1) {
            sb.append(context.getString(R.string.hrShutdownPhoneDescription));
        } else {
            sb.append(context.getString(R.string.hrDontShutdownPhoneDescription));
        }
    }

    public String GetIsValidError(Context context) {
        return null;
    }

    public boolean IsHarmful() {
        return true;
    }
}
