package com.kebab.Llama.EventActions;

import android.app.Activity;
import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import com.kebab.AppendableCharSequence;
import com.kebab.Helpers;
import com.kebab.Llama.Event;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.R;
import com.kebab.OnGetValueEx;
import com.kebab.PreferenceEx;
import com.kebab.SeekBarPreference;
import java.io.IOException;

public abstract class ChangeLlamaPollingActionBase<T> extends EventAction<T> {
    int _PollingIntervalMins;

    public abstract T Create(int i);

    public abstract void PerformAction(LlamaService llamaService, Activity activity, Event event, long j, int i);

    public abstract String getDescriptionText(Context context);

    public abstract String getId();

    public abstract String getPreferenceTitle(Context context);

    public ChangeLlamaPollingActionBase(int pollingIntervalMins) {
        this._PollingIntervalMins = pollingIntervalMins;
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
        sb.append(this._PollingIntervalMins);
    }

    public PreferenceEx<T> CreatePreference(PreferenceActivity context) {
        return CreateSeekBarPreferenceNoMax(context, getPreferenceTitle(context), 1, 480, context.getString(R.string.hrNever), this._PollingIntervalMins, new OnGetValueEx<T>() {
            public T GetValue(Preference preference) {
                return ChangeLlamaPollingActionBase.this.Create(((SeekBarPreference) preference).getValue());
            }
        });
    }

    public void AppendActionDescription(Context context, AppendableCharSequence sb) throws IOException {
        String descriptionText = getDescriptionText(context);
        if (this._PollingIntervalMins == Integer.MAX_VALUE) {
            sb.append(String.format(context.getString(R.string.hrSet1ToNever), new Object[]{descriptionText}));
            return;
        }
        sb.append(String.format(context.getString(R.string.hrSet1To2), new Object[]{descriptionText, Helpers.ChoosePlural(this._PollingIntervalMins, context.getString(R.string.hrMinute), context.getString(R.string.hrMinutes), true)}));
    }

    public String GetIsValidError(Context context) {
        return null;
    }
}
