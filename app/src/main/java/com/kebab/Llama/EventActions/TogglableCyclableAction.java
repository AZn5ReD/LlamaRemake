package com.kebab.Llama.EventActions;

import android.app.Activity;
import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import com.kebab.AppendableCharSequence;
import com.kebab.ListPreference;
import com.kebab.Llama.Event;
import com.kebab.Llama.LlamaService;
import com.kebab.OnGetValueEx;
import com.kebab.PreferenceEx;
import java.io.IOException;

public abstract class TogglableCyclableAction<T> extends EventAction<T> {
    public static final int CYCLE = 2;
    public static final int TURN_OFF = 0;
    public static final int TURN_ON = 1;
    int _TurnOnOrCycle;

    public abstract T CreateSelf(int i);

    public abstract boolean IsOnAlready(LlamaService llamaService);

    public abstract void PerformOffAction(LlamaService llamaService, Activity activity, Event event, int i);

    public abstract void PerformOnAction(LlamaService llamaService, Activity activity, Event event, int i);

    public abstract String getCycleDescriptionString(Context context);

    public abstract String getCycleString(Context context);

    public abstract String getId();

    public abstract String getOffDescriptionString(Context context);

    public abstract String getOffString(Context context);

    public abstract String getOnDescriptionString(Context context);

    public abstract String getOnString(Context context);

    public abstract String getTitleString(Context context);

    public TogglableCyclableAction(int turnOnOrCycle) {
        this._TurnOnOrCycle = turnOnOrCycle;
    }

    public TogglableCyclableAction(String[] parts, int currentPart) {
        this._TurnOnOrCycle = Integer.parseInt(parts[currentPart + 1]);
    }

    public final void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        switch (this._TurnOnOrCycle) {
            case 1:
                PerformOnAction(service, activity, event, eventRunMode);
                return;
            case 2:
                if (IsOnAlready(service)) {
                    PerformOffAction(service, activity, event, eventRunMode);
                    return;
                } else {
                    PerformOnAction(service, activity, event, eventRunMode);
                    return;
                }
            default:
                PerformOffAction(service, activity, event, eventRunMode);
                return;
        }
    }

    public final boolean RenameProfile(String oldName, String newName) {
        return false;
    }

    /* Access modifiers changed, original: protected|final */
    public final int GetPartsConsumption() {
        return 1;
    }

    /* Access modifiers changed, original: protected|final */
    public final void ToPsvInternal(StringBuilder sb) {
        sb.append(this._TurnOnOrCycle);
    }

    public final PreferenceEx<T> CreatePreference(PreferenceActivity context) {
        final String on = getOnString(context);
        String off = getOffString(context);
        final String cycle = getCycleString(context);
        String titleString = getTitleString(context);
        String[] strArr = new String[]{on, off, cycle};
        String str = this._TurnOnOrCycle == 0 ? off : this._TurnOnOrCycle == 1 ? on : cycle;
        return CreateListPreference(context, titleString, strArr, str, new OnGetValueEx<T>() {
            public T GetValue(Preference preference) {
                int actualValue;
                String selectedValue = ((ListPreference) preference).getValue();
                if (on.equals(selectedValue)) {
                    actualValue = 1;
                } else if (cycle.equals(selectedValue)) {
                    actualValue = 2;
                } else {
                    actualValue = 0;
                }
                return TogglableCyclableAction.this.CreateSelf(actualValue);
            }
        });
    }

    public final void AppendActionDescription(Context context, AppendableCharSequence sb) throws IOException {
        switch (this._TurnOnOrCycle) {
            case 1:
                sb.append(getOnDescriptionString(context));
                return;
            case 2:
                sb.append(getCycleDescriptionString(context));
                return;
            default:
                sb.append(getOffDescriptionString(context));
                return;
        }
    }

    public final String GetIsValidError(Context context) {
        return null;
    }
}
