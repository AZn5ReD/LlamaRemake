package com.kebab.Llama.EventActions;

import android.app.Activity;
import android.content.Context;
import com.kebab.Llama.Event;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.R;

public class ToggleCarDockAction extends TogglableCyclableAction<ToggleCarDockAction> {
    public ToggleCarDockAction(int v) {
        super(v);
    }

    public ToggleCarDockAction(String[] parts, int currentPart) {
        super(parts, currentPart);
    }

    public static ToggleCarDockAction CreateFrom(String[] parts, int currentPart) {
        return new ToggleCarDockAction(parts, currentPart);
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.CAR_MODE_ACTION;
    }

    /* Access modifiers changed, original: protected */
    public ToggleCarDockAction CreateSelf(int turnOnOrCycle) {
        return new ToggleCarDockAction(turnOnOrCycle);
    }

    /* Access modifiers changed, original: protected */
    public String getCycleDescriptionString(Context context) {
        return context.getString(R.string.hrCarDockOnOffDescription);
    }

    /* Access modifiers changed, original: protected */
    public String getCycleString(Context context) {
        return context.getString(R.string.hrCarDockOnOff);
    }

    /* Access modifiers changed, original: protected */
    public String getOffDescriptionString(Context context) {
        return context.getString(R.string.hrCarDockOffDescription);
    }

    /* Access modifiers changed, original: protected */
    public String getOffString(Context context) {
        return context.getString(R.string.hrCarDockOff);
    }

    /* Access modifiers changed, original: protected */
    public String getOnDescriptionString(Context context) {
        return context.getString(R.string.hrCarDockOnDescription);
    }

    /* Access modifiers changed, original: protected */
    public String getOnString(Context context) {
        return context.getString(R.string.hrCarDockOn);
    }

    /* Access modifiers changed, original: protected */
    public String getTitleString(Context context) {
        return context.getString(R.string.hrCarMode);
    }

    /* Access modifiers changed, original: protected */
    public boolean IsOnAlready(LlamaService service) {
        return CarModeHelper.IsOn(service);
    }

    /* Access modifiers changed, original: protected */
    public void PerformOffAction(LlamaService service, Activity activity, Event event, int eventRunMode) {
        CarModeHelper.SetEnabled(service, false);
    }

    /* Access modifiers changed, original: protected */
    public void PerformOnAction(LlamaService service, Activity activity, Event event, int eventRunMode) {
        CarModeHelper.SetEnabled(service, true);
    }

    public boolean IsHarmful() {
        return false;
    }
}
