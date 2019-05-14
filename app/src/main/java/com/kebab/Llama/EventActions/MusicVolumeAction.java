package com.kebab.Llama.EventActions;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import com.kebab.AppendableCharSequence;
import com.kebab.Llama.Event;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.R;
import com.kebab.OnGetValueEx;
import com.kebab.PreferenceEx;
import com.kebab.SeekBarPreference;
import java.io.IOException;

public class MusicVolumeAction extends EventAction<MusicVolumeAction> {
    int _Volume;

    public MusicVolumeAction(int volume) {
        this._Volume = volume;
    }

    public void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        service.ChangeMusicVolume(this._Volume);
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
        sb.append(this._Volume);
    }

    public static MusicVolumeAction CreateFrom(String[] parts, int currentPart) {
        return new MusicVolumeAction(Integer.parseInt(parts[currentPart + 1]));
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.CHANGE_VOLUME_ACTION;
    }

    public PreferenceEx<MusicVolumeAction> CreatePreference(PreferenceActivity context) {
        return CreateSeekBarPreference(context, context.getString(R.string.hrChangeMusicVolume), 0, ((AudioManager) context.getSystemService("audio")).getStreamMaxVolume(3), this._Volume, new OnGetValueEx<MusicVolumeAction>() {
            public MusicVolumeAction GetValue(Preference preference) {
                return new MusicVolumeAction(((SeekBarPreference) preference).getValue());
            }
        });
    }

    public void AppendActionDescription(Context context, AppendableCharSequence sb) throws IOException {
        sb.append(String.format(context.getString(R.string.hrSetMusicVolumeTo1), new Object[]{Integer.valueOf(this._Volume)}));
    }

    public String GetIsValidError(Context context) {
        return null;
    }

    public boolean IsHarmful() {
        return false;
    }
}
