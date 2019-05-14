package com.kebab.Llama.EventActions;

import android.app.Activity;
import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import com.kebab.AppendableCharSequence;
import com.kebab.EditTextPreference;
import com.kebab.Helpers;
import com.kebab.Llama.Event;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.LlamaStorage;
import com.kebab.Llama.R;
import com.kebab.OnGetValueEx;
import com.kebab.PreferenceEx;
import java.io.IOException;

public class ToastAction extends EventAction<ToastAction> {
    String _Message;

    public ToastAction(String message) {
        this._Message = message;
    }

    public void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        Helpers.ShowTip((Context) service, service.ExpandVariables(this._Message));
    }

    public boolean RenameProfile(String oldName, String newName) {
        return false;
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 1;
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.TOAST_ACTION;
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(LlamaStorage.SimpleEscape(this._Message));
    }

    public static ToastAction CreateFrom(String[] parts, int currentPart) {
        return new ToastAction(LlamaStorage.SimpleUnescape(parts[currentPart + 1]));
    }

    public PreferenceEx<ToastAction> CreatePreference(PreferenceActivity context) {
        return super.CreateEditTextPreference(context, context.getString(R.string.hrToastAction), this._Message, new OnGetValueEx<ToastAction>() {
            public ToastAction GetValue(Preference preference) {
                return new ToastAction(((EditTextPreference) preference).getText());
            }
        });
    }

    public void AppendActionDescription(Context context, AppendableCharSequence sb) throws IOException {
        sb.append(String.format(context.getString(R.string.hrShowAToast1), new Object[]{this._Message}));
    }

    public String GetIsValidError(Context context) {
        return null;
    }

    public boolean IsHarmful() {
        return false;
    }
}
