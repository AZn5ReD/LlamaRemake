package com.kebab.Llama.EventActions;

import android.app.Activity;
import android.content.Context;
import com.kebab.Llama.Event;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.R;
import com.kebab.Llama.WifiAccessPoint;

public class WifiHotspotAction extends TogglableCyclableAction<WifiHotspotAction> {
    public WifiHotspotAction(int v) {
        super(v);
    }

    public WifiHotspotAction(String[] parts, int currentPart) {
        super(parts, currentPart);
    }

    public static WifiHotspotAction CreateFrom(String[] parts, int currentPart) {
        return new WifiHotspotAction(parts, currentPart);
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.WIFI_HOTSPOT_ACTION;
    }

    /* Access modifiers changed, original: protected */
    public WifiHotspotAction CreateSelf(int turnOnOrCycle) {
        return new WifiHotspotAction(turnOnOrCycle);
    }

    /* Access modifiers changed, original: protected */
    public String getCycleDescriptionString(Context context) {
        return context.getString(R.string.hrWifiHotSpotOnOffDescription);
    }

    /* Access modifiers changed, original: protected */
    public String getCycleString(Context context) {
        return context.getString(R.string.hrWifiHotSpotOnOff);
    }

    /* Access modifiers changed, original: protected */
    public String getOffDescriptionString(Context context) {
        return context.getString(R.string.hrWifiHotSpotOffDescription);
    }

    /* Access modifiers changed, original: protected */
    public String getOffString(Context context) {
        return context.getString(R.string.hrWifiHotSpotOff);
    }

    /* Access modifiers changed, original: protected */
    public String getOnDescriptionString(Context context) {
        return context.getString(R.string.hrWifiHotSpotOnDescription);
    }

    /* Access modifiers changed, original: protected */
    public String getOnString(Context context) {
        return context.getString(R.string.hrWifiHotSpotOn);
    }

    /* Access modifiers changed, original: protected */
    public String getTitleString(Context context) {
        return context.getString(R.string.hrActionWifiHotSpot);
    }

    /* Access modifiers changed, original: protected */
    public boolean IsOnAlready(LlamaService service) {
        return WifiAccessPoint.IsEnabled(service);
    }

    /* Access modifiers changed, original: protected */
    public void PerformOffAction(LlamaService service, Activity activity, Event event, int eventRunMode) {
        WifiAccessPoint.SetEnabled(service, false, true);
    }

    /* Access modifiers changed, original: protected */
    public void PerformOnAction(LlamaService service, Activity activity, Event event, int eventRunMode) {
        WifiAccessPoint.SetEnabled(service, true, true);
    }

    public boolean IsHarmful() {
        return false;
    }
}
