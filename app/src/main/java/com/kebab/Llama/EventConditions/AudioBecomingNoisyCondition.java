package com.kebab.Llama.EventConditions;

import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.EventMeta;
import com.kebab.Llama.EventMeta.ConditionStaticInitter1;
import com.kebab.Llama.EventTrigger;
import com.kebab.Llama.R;
import com.kebab.Llama.StateChange;
import com.kebab.OnGetValueEx;
import com.kebab.PreferenceEx;
import com.kebab.Ref;
import java.io.IOException;

public class AudioBecomingNoisyCondition extends EventCondition<AudioBecomingNoisyCondition> {
    public static String MY_ID;
    public static int MY_TRIGGER;
    public static int[] MY_TRIGGERS;
    boolean _IsOn;

    static {
        EventMeta.InitCondition(EventFragment.AUDIO_BECOMING_NOISY_CONDITION, new ConditionStaticInitter1() {
            public void UpdateStatics(String id, int[] triggers, int trigger) {
                AudioBecomingNoisyCondition.MY_ID = id;
                AudioBecomingNoisyCondition.MY_TRIGGERS = triggers;
                AudioBecomingNoisyCondition.MY_TRIGGER = trigger;
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

    public AudioBecomingNoisyCondition(boolean dummy) {
        this._IsOn = dummy;
    }

    public int TestCondition(StateChange state, Context context, Ref<EventTrigger> ref) {
        if (state.TriggerType == MY_TRIGGER) {
            return 2;
        }
        return 1;
    }

    public boolean RenameArea(String oldName, String newName) {
        return false;
    }

    public void AppendConditionSimple(Context context, Appendable sb) throws IOException {
        sb.append(context.getString(R.string.hrWhenAudioBecomesNoisy));
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 1;
    }

    public static AudioBecomingNoisyCondition CreateFrom(String[] parts, int currentPart) {
        return new AudioBecomingNoisyCondition(false);
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(this._IsOn ? "1" : "0");
    }

    public PreferenceEx<AudioBecomingNoisyCondition> CreatePreference(PreferenceActivity context) {
        return CreateSimplePreference(context, context.getString(R.string.hrConditionAudioBecomingNoisy), context.getString(R.string.hrConditionAudioBecomingNoisy), new OnGetValueEx<AudioBecomingNoisyCondition>() {
            public AudioBecomingNoisyCondition GetValue(Preference preference) {
                return new AudioBecomingNoisyCondition(false);
            }
        });
    }

    public String GetIsValidError(Context context) {
        return null;
    }
}
