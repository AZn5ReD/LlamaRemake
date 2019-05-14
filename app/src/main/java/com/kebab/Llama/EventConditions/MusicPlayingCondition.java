package com.kebab.Llama.EventConditions;

import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import com.kebab.ListPreference;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.EventMeta;
import com.kebab.Llama.EventMeta.ConditionStaticInitter2;
import com.kebab.Llama.EventTrigger;
import com.kebab.Llama.R;
import com.kebab.Llama.StateChange;
import com.kebab.OnGetValueEx;
import com.kebab.PreferenceEx;
import com.kebab.Ref;
import java.io.IOException;

public class MusicPlayingCondition extends EventCondition<MusicPlayingCondition> {
    public static String MY_ID;
    public static int[] MY_TRIGGERS;
    public static int MY_TRIGGER_OFF;
    public static int MY_TRIGGER_ON;
    boolean _IsPlaying;

    static {
        EventMeta.InitCondition(EventFragment.MUSIC_PLAYING_CONDITION, new ConditionStaticInitter2() {
            public void UpdateStatics(String id, int[] triggers, int onTrigger, int offTrigger) {
                MusicPlayingCondition.MY_ID = id;
                MusicPlayingCondition.MY_TRIGGERS = triggers;
                MusicPlayingCondition.MY_TRIGGER_ON = onTrigger;
                MusicPlayingCondition.MY_TRIGGER_OFF = offTrigger;
            }
        });
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return MY_ID;
    }

    public int[] getEventTriggers() {
        return MY_TRIGGERS;
    }

    public MusicPlayingCondition(boolean isPlaying) {
        this._IsPlaying = isPlaying;
    }

    public int TestCondition(StateChange state, Context context, Ref<EventTrigger> ref) {
        if (this._IsPlaying) {
            if (state.TriggerType == MY_TRIGGER_ON) {
                return 2;
            }
            if (state.IsMusicActive) {
                return 1;
            }
            return 0;
        } else if (state.TriggerType == MY_TRIGGER_OFF) {
            return 2;
        } else {
            if (state.IsMusicActive) {
                return 0;
            }
            return 1;
        }
    }

    public boolean RenameArea(String oldName, String newName) {
        return false;
    }

    public void AppendConditionSimple(Context context, Appendable sb) throws IOException {
        if (this._IsPlaying) {
            sb.append(context.getString(R.string.hrWhenMusicIsPlaying));
        } else {
            sb.append(context.getString(R.string.hrWhenMusicIsPaused));
        }
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 1;
    }

    public static MusicPlayingCondition CreateFrom(String[] parts, int currentPart) {
        return new MusicPlayingCondition(parts[currentPart + 1].equals("1"));
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(this._IsPlaying ? "1" : "0");
    }

    public PreferenceEx<MusicPlayingCondition> CreatePreference(PreferenceActivity context) {
        final String play = context.getString(R.string.hrMusicPlaying);
        return CreateListPreference(context, context.getString(R.string.hrMusicPlayback), new String[]{play, context.getString(R.string.hrMusicPaused)}, this._IsPlaying ? play : context.getString(R.string.hrMusicPaused), new OnGetValueEx<MusicPlayingCondition>() {
            public MusicPlayingCondition GetValue(Preference preference) {
                return new MusicPlayingCondition(((ListPreference) preference).getValue().equals(play));
            }
        });
    }

    public String GetIsValidError(Context context) {
        return null;
    }
}
