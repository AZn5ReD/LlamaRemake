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

public class ScreenOnAction extends EventAction<ScreenOnAction> {
    int _WakeType;

    public ScreenOnAction(int wakeType) {
        this._WakeType = wakeType;
    }

    public void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        service.TurnOnScreen(this._WakeType);
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

    public static ScreenOnAction CreateFrom(String[] parts, int currentPart) {
        return new ScreenOnAction(Integer.parseInt(parts[currentPart + 1]));
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.SCREEN_ON_ACTION;
    }

    public PreferenceEx<ScreenOnAction> CreatePreference(PreferenceActivity context) {
        String[] names = context.getResources().getStringArray(R.array.screenOnNames);
        return CreateListPreference((Context) context, context.getString(R.string.hrActionScreenOn), context.getResources().getStringArray(R.array.screenOnValues), names, String.valueOf(this._WakeType), (OnGetValueEx) new OnGetValueEx<ScreenOnAction>() {
            public ScreenOnAction GetValue(Preference preference) {
                return new ScreenOnAction(Integer.parseInt(((ListPreference) preference).getValue()));
            }
        });
    }

    public void AppendActionDescription(Context context, AppendableCharSequence sb) throws IOException {
        if (this._WakeType == 0) {
            sb.append(context.getString(R.string.hrTurnOnTheScreenDim));
        } else if (this._WakeType == 1) {
            sb.append(context.getString(R.string.hrTurnOnTheScreenBright));
        } else {
            sb.append(context.getString(R.string.hrTurnOnTheScreenBrightAndKeyboard));
        }
    }

    public String GetIsValidError(Context context) {
        return null;
    }

    public boolean IsHarmful() {
        return false;
    }
}
