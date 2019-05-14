package com.kebab.Llama.EventActions;

import android.app.Activity;
import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import com.kebab.AppendableCharSequence;
import com.kebab.ArrayHelpers;
import com.kebab.ListPreference;
import com.kebab.Llama.Event;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.R;
import com.kebab.OnGetValueEx;
import com.kebab.PreferenceEx;
import java.io.IOException;

public class TwoGThreeGAction extends EventAction<TwoGThreeGAction> {
    int _Action;

    public TwoGThreeGAction(int action) {
        this._Action = action;
    }

    public void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        service.ChangePhoneNetworkMode(this._Action);
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
        return EventFragment.TWO_G_THREE_G_ACTION;
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(this._Action);
    }

    public static TwoGThreeGAction CreateFrom(String[] parts, int currentPart) {
        return new TwoGThreeGAction(Integer.parseInt(parts[currentPart + 1]));
    }

    public PreferenceEx<TwoGThreeGAction> CreatePreference(PreferenceActivity context) {
        String[] names = context.getResources().getStringArray(R.array.twoGThreeGNames);
        return CreateListPreference((Context) context, context.getString(R.string.hrActionTwoGThreeG), context.getResources().getStringArray(R.array.twoGThreeGValues), names, String.valueOf(this._Action), (OnGetValueEx) new OnGetValueEx<TwoGThreeGAction>() {
            public TwoGThreeGAction GetValue(Preference preference) {
                return new TwoGThreeGAction(Integer.parseInt(((ListPreference) preference).getValue()));
            }
        });
    }

    public void AppendActionDescription(Context context, AppendableCharSequence sb) throws IOException {
        String actionName;
        String[] names = context.getResources().getStringArray(R.array.twoGThreeGNames);
        Integer index = ArrayHelpers.FindIndex(context.getResources().getStringArray(R.array.twoGThreeGValues), String.valueOf(this._Action));
        if (index != null) {
            int iconIndex = index.intValue();
            if (iconIndex >= 0 && iconIndex < names.length) {
                actionName = names[iconIndex];
                sb.append(String.format(context.getString(R.string.hrChangeNetworkTo1), new Object[]{actionName}));
            }
        }
        actionName = context.getString(R.string.hrUnknown);
        sb.append(String.format(context.getString(R.string.hrChangeNetworkTo1), new Object[]{actionName}));
    }

    public String GetIsValidError(Context context) {
        return null;
    }

    public boolean IsHarmful() {
        return false;
    }
}
