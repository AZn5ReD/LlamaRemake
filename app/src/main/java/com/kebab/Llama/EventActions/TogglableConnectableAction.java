package com.kebab.Llama.EventActions;

import android.app.Activity;
import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import com.kebab.AppendableCharSequence;
import com.kebab.IterableHelpers;
import com.kebab.ListPreference;
import com.kebab.Llama.Event;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.R;
import com.kebab.OnGetValueEx;
import com.kebab.PreferenceEx;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public abstract class TogglableConnectableAction<TSelf extends EventAction<TSelf>> extends EventAction<TSelf> {
    public static final int TOGGLE_OFF = 0;
    public static final int TOGGLE_OFF_IF_NOT_CONNECTED = 2;
    public static final int TOGGLE_ON = 1;
    int _AtLeastOnForMinutes;
    int _ToggleType;

    public interface CREATOR<T> {
        T Create(int i, int i2);
    }

    public abstract TSelf CreateSelf(int i, int i2);

    public abstract String GetDescriptionOff(Context context);

    public abstract String GetDescriptionOn(Context context);

    public abstract String GetPreferenceTitleText(Context context);

    public abstract String GetPreferenceValueDisable(Context context);

    public abstract String GetPreferenceValueEnable(Context context);

    public abstract void PerformActionInternal(LlamaService llamaService, boolean z, boolean z2);

    public TogglableConnectableAction(int toggleType, int atLeastOnForMinutes) {
        this._ToggleType = toggleType;
        this._AtLeastOnForMinutes = atLeastOnForMinutes;
    }

    public TogglableConnectableAction(String[] parts, int currentPart) {
        int minutes = 0;
        String[] innerParts = parts[currentPart + 1].split("-", -1);
        int toggleType = Integer.parseInt(innerParts[0]);
        if (innerParts.length > 1) {
            minutes = Integer.parseInt(innerParts[1]);
        }
        this._ToggleType = toggleType;
        this._AtLeastOnForMinutes = minutes;
    }

    public void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        switch (this._ToggleType) {
            case 1:
                PerformActionInternal(service, true, false);
                if (this._AtLeastOnForMinutes > 0) {
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(currentTimeRoundedToNextMinute);
                    service.EnqueueEventForAfterTestEvents(event.Name + " (" + GetPreferenceTitleText(service) + ")", CreateSelf(2, 0), c, this._AtLeastOnForMinutes, 0);
                    return;
                }
                return;
            case 2:
                PerformActionInternal(service, false, false);
                return;
            default:
                PerformActionInternal(service, false, true);
                return;
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
        sb.append("-");
        sb.append(this._AtLeastOnForMinutes);
    }

    public PreferenceEx<TSelf> CreatePreference(PreferenceActivity context) {
        String offIfNotConnected = GetPreferenceValueDisable(context) + " (" + context.getString(R.string.hrIfNotConnected) + ")";
        ArrayList<String> values = new ArrayList();
        ArrayList<String> keys = new ArrayList();
        values.add(GetPreferenceValueEnable(context));
        keys.add("1:0");
        values.add(GetPreferenceValueDisable(context));
        keys.add("0:0");
        values.add(offIfNotConnected);
        keys.add("2:0");
        values.add(GetPreferenceValueEnableForMinutes(context, 1));
        keys.add("1:1");
        values.add(GetPreferenceValueEnableForMinutes(context, 3));
        keys.add("1:3");
        values.add(GetPreferenceValueEnableForMinutes(context, 5));
        keys.add("1:5");
        values.add(GetPreferenceValueEnableForMinutes(context, 10));
        keys.add("1:10");
        values.add(GetPreferenceValueEnableForMinutes(context, 15));
        keys.add("1:15");
        values.add(GetPreferenceValueEnableForMinutes(context, 20));
        keys.add("1:20");
        values.add(GetPreferenceValueEnableForMinutes(context, 30));
        keys.add("1:30");
        return CreateListPreference((Context) context, GetPreferenceTitleText(context), (String[]) IterableHelpers.ToArray(keys, String.class), (String[]) IterableHelpers.ToArray(values, String.class), this._ToggleType + (this._AtLeastOnForMinutes >= 0 ? ":" + this._AtLeastOnForMinutes : ""), (OnGetValueEx) new OnGetValueEx<TSelf>() {
            public TSelf GetValue(Preference preference) {
                String[] parts = ((ListPreference) preference).getValue().split(":", -1);
                int minutes = 0;
                int toggleType = Integer.parseInt(parts[0]);
                if (parts.length == 2) {
                    minutes = Integer.parseInt(parts[1]);
                }
                return TogglableConnectableAction.this.CreateSelf(toggleType, minutes);
            }
        });
    }

    /* Access modifiers changed, original: protected */
    public String GetPreferenceValueEnableForMinutes(Context context, int minutes) {
        if (minutes == 0) {
            return GetPreferenceValueEnable(context);
        }
        if (minutes == 1) {
            StringBuilder append = new StringBuilder().append(GetPreferenceValueEnable(context)).append(" (");
            Object[] objArr = new Object[1];
            objArr[0] = minutes + " " + context.getString(minutes == 1 ? R.string.hrMinute : R.string.hrMinutes);
            return append.append(context.getString(R.string.hrForAtLeast1, objArr)).append(")").toString();
        }
        return GetPreferenceValueEnable(context) + " (" + String.format(context.getString(R.string.hrForAtLeast1Minutes), new Object[]{Integer.valueOf(minutes)}) + ")";
    }

    public void AppendActionDescription(Context context, AppendableCharSequence sb) throws IOException {
        switch (this._ToggleType) {
            case 0:
                sb.append(GetDescriptionOff(context));
                return;
            case 1:
                if (this._AtLeastOnForMinutes == 0) {
                    sb.append(GetDescriptionOn(context));
                    return;
                } else if (this._AtLeastOnForMinutes == 1) {
                    StringBuilder append = new StringBuilder().append(GetDescriptionOn(context)).append(" ");
                    Object[] objArr = new Object[1];
                    objArr[0] = this._AtLeastOnForMinutes + " " + context.getString(this._AtLeastOnForMinutes == 1 ? R.string.hrMinute : R.string.hrMinutes);
                    sb.append(append.append(context.getString(R.string.hrFor1ThenDisconectIfNotConnected, objArr)).toString());
                    return;
                } else {
                    sb.append(GetDescriptionOn(context) + " " + String.format(context.getString(R.string.hrFor1MinutesThenDisconectIfNotConnected), new Object[]{Integer.valueOf(this._AtLeastOnForMinutes)}));
                    return;
                }
            case 2:
                sb.append(GetDescriptionOff(context) + " " + context.getString(R.string.hrIfNotConnectedDescription));
                return;
            default:
                return;
        }
    }

    public String GetIsValidError(Context context) {
        return null;
    }
}
