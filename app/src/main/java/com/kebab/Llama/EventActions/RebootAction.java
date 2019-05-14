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

public class RebootAction extends EventAction<RebootAction> {
    int _RebootType;

    public RebootAction(int rebootType) {
        this._RebootType = rebootType;
    }

    public void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        if (!super.ActionIsProhibited(service, eventRunMode) && this._RebootType == 1) {
            service.Reboot();
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
        sb.append(this._RebootType);
    }

    public static RebootAction CreateFrom(String[] parts, int currentPart) {
        return new RebootAction(Integer.parseInt(parts[currentPart + 1]));
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.REBOOT_ACTION;
    }

    public PreferenceEx<RebootAction> CreatePreference(PreferenceActivity context) {
        String REBOOT_NO = context.getString(R.string.hrNoDontDoAnything);
        final String REBOOT_YES = context.getString(R.string.hrYesIReallyWantToReboot);
        return CreateListPreference(context, context.getString(R.string.hrRebootBracketRequiresRootBracket), new String[]{REBOOT_NO, REBOOT_YES}, this._RebootType == 1 ? REBOOT_YES : REBOOT_NO, new OnGetValueEx<RebootAction>() {
            public RebootAction GetValue(Preference preference) {
                int finalValue;
                if (Instances.Service != null) {
                    Instances.Service.AcquireRoot();
                }
                if (REBOOT_YES.equals(((ListPreference) preference).getValue())) {
                    finalValue = 1;
                } else {
                    finalValue = 0;
                }
                return new RebootAction(finalValue);
            }
        });
    }

    public void AppendActionDescription(Context context, AppendableCharSequence sb) throws IOException {
        if (this._RebootType == 1) {
            sb.append(context.getString(R.string.hrRebootPhoneDescription));
        } else {
            sb.append(context.getString(R.string.hrDontRebootPhoneDescription));
        }
    }

    public String GetIsValidError(Context context) {
        return null;
    }

    public boolean IsHarmful() {
        return true;
    }
}
