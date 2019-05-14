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

public class MediaButtonAction extends EventAction<MediaButtonAction> {
    public static final int KEYCODE_MEDIA_CLOSE = 128;
    public static final int KEYCODE_MEDIA_EJECT = 129;
    public static final int KEYCODE_MEDIA_FAST_FORWARD = 90;
    public static final int KEYCODE_MEDIA_NEXT = 87;
    public static final int KEYCODE_MEDIA_PAUSE = 127;
    public static final int KEYCODE_MEDIA_PLAY = 126;
    public static final int KEYCODE_MEDIA_PLAY_PAUSE = 85;
    public static final int KEYCODE_MEDIA_PREVIOUS = 88;
    public static final int KEYCODE_MEDIA_RECORD = 130;
    public static final int KEYCODE_MEDIA_REWIND = 89;
    public static final int KEYCODE_MEDIA_STOP = 86;
    int _Action;

    public MediaButtonAction(int action) {
        this._Action = action;
    }

    public void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        service.SendMediaAction(this._Action);
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
        return EventFragment.MEDIA_BUTTON_ACTION;
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(this._Action);
    }

    public static MediaButtonAction CreateFrom(String[] parts, int currentPart) {
        return new MediaButtonAction(Integer.parseInt(parts[currentPart + 1]));
    }

    public PreferenceEx<MediaButtonAction> CreatePreference(PreferenceActivity context) {
        String[] names = context.getResources().getStringArray(R.array.mediaButtonNames);
        return CreateListPreference((Context) context, context.getString(R.string.hrActionMediaPlayer), context.getResources().getStringArray(R.array.mediaButtonValues), names, String.valueOf(this._Action), (OnGetValueEx) new OnGetValueEx<MediaButtonAction>() {
            public MediaButtonAction GetValue(Preference preference) {
                return new MediaButtonAction(Integer.parseInt(((ListPreference) preference).getValue()));
            }
        });
    }

    public void AppendActionDescription(Context context, AppendableCharSequence sb) throws IOException {
        String actionName;
        String[] names = context.getResources().getStringArray(R.array.mediaButtonNames);
        Integer index = ArrayHelpers.FindIndex(context.getResources().getStringArray(R.array.mediaButtonValues), String.valueOf(this._Action));
        if (index != null) {
            int iconIndex = index.intValue();
            if (iconIndex >= 0 && iconIndex < names.length) {
                actionName = names[iconIndex].toLowerCase();
                sb.append(String.format(context.getString(R.string.hrSendMedia1Button), new Object[]{actionName}));
            }
        }
        actionName = context.getString(R.string.hrUnknown);
        sb.append(String.format(context.getString(R.string.hrSendMedia1Button), new Object[]{actionName}));
    }

    public String GetIsValidError(Context context) {
        return null;
    }

    public boolean IsHarmful() {
        return false;
    }
}
