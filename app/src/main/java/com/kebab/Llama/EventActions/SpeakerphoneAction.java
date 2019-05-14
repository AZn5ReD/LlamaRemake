package com.kebab.Llama.EventActions;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import com.kebab.Llama.Event;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.R;

public class SpeakerphoneAction extends TogglableCyclableAction<SpeakerphoneAction> {
    public SpeakerphoneAction(int v) {
        super(v);
    }

    public SpeakerphoneAction(String[] parts, int currentPart) {
        super(parts, currentPart);
    }

    public static SpeakerphoneAction CreateFrom(String[] parts, int currentPart) {
        return new SpeakerphoneAction(parts, currentPart);
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.SPEAKERPHONE_ACTION;
    }

    /* Access modifiers changed, original: protected */
    public SpeakerphoneAction CreateSelf(int turnOnOrCycle) {
        return new SpeakerphoneAction(turnOnOrCycle);
    }

    /* Access modifiers changed, original: protected */
    public String getCycleDescriptionString(Context context) {
        return context.getString(R.string.hrSpeakerphoneOnOffDescription);
    }

    /* Access modifiers changed, original: protected */
    public String getCycleString(Context context) {
        return context.getString(R.string.hrSpeakerphoneOnOff);
    }

    /* Access modifiers changed, original: protected */
    public String getOffDescriptionString(Context context) {
        return context.getString(R.string.hrSpeakerphoneOffDescription);
    }

    /* Access modifiers changed, original: protected */
    public String getOffString(Context context) {
        return context.getString(R.string.hrSpeakerphoneOff);
    }

    /* Access modifiers changed, original: protected */
    public String getOnDescriptionString(Context context) {
        return context.getString(R.string.hrSpeakerphoneOnDescription);
    }

    /* Access modifiers changed, original: protected */
    public String getOnString(Context context) {
        return context.getString(R.string.hrSpeakerphoneOn);
    }

    /* Access modifiers changed, original: protected */
    public String getTitleString(Context context) {
        return context.getString(R.string.hrActionSpeakerphone);
    }

    /* Access modifiers changed, original: protected */
    public boolean IsOnAlready(LlamaService service) {
        return ((AudioManager) service.getSystemService("audio")).isSpeakerphoneOn();
    }

    /* Access modifiers changed, original: protected */
    public void PerformOffAction(LlamaService service, Activity activity, Event event, int eventRunMode) {
        ((AudioManager) service.getSystemService("audio")).setSpeakerphoneOn(false);
    }

    /* Access modifiers changed, original: protected */
    public void PerformOnAction(LlamaService service, Activity activity, Event event, int eventRunMode) {
        ((AudioManager) service.getSystemService("audio")).setSpeakerphoneOn(true);
    }

    public boolean IsHarmful() {
        return false;
    }
}
