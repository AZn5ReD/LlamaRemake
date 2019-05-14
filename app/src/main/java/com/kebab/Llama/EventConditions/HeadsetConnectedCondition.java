package com.kebab.Llama.EventConditions;

import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import com.kebab.ArrayHelpers;
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

public class HeadsetConnectedCondition extends EventCondition<HeadsetConnectedCondition> {
    public static final int HEADSET_ANY_CONNECTED = 1;
    public static final int HEADSET_NOT_CONNECTED = 0;
    public static final int HEADSET_NO_MIC_CONNECTED = 3;
    public static final int HEADSET_WITH_MIC_CONNECTED = 2;
    public static String MY_ID;
    public static int[] MY_TRIGGERS;
    public static int MY_TRIGGER_OFF;
    public static int MY_TRIGGER_ON;
    int _IsConnected;

    static {
        EventMeta.InitCondition(EventFragment.HEADSET_CONNECTED_CONDITION, new ConditionStaticInitter2() {
            public void UpdateStatics(String id, int[] triggers, int onTrigger, int offTrigger) {
                HeadsetConnectedCondition.MY_ID = id;
                HeadsetConnectedCondition.MY_TRIGGERS = triggers;
                HeadsetConnectedCondition.MY_TRIGGER_ON = onTrigger;
                HeadsetConnectedCondition.MY_TRIGGER_OFF = offTrigger;
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

    public HeadsetConnectedCondition(int isConnected) {
        this._IsConnected = isConnected;
    }

    public int TestCondition(StateChange state, Context context, Ref<EventTrigger> ref) {
        if (this._IsConnected != 0) {
            boolean isCorrectHeadsetType = this._IsConnected == 1 ? true : this._IsConnected == 2 ? state.HeadSetHasMic : !state.HeadSetHasMic;
            if (!isCorrectHeadsetType) {
                return 0;
            }
            if (state.TriggerType == MY_TRIGGER_ON) {
                return 2;
            }
            if (state.HeadSetConnected) {
                return 1;
            }
            return 0;
        } else if (state.TriggerType == MY_TRIGGER_OFF) {
            return 2;
        } else {
            if (state.HeadSetConnected) {
                return 0;
            }
            return 1;
        }
    }

    public boolean RenameArea(String oldName, String newName) {
        return false;
    }

    public void AppendConditionSimple(Context context, Appendable sb) throws IOException {
        switch (this._IsConnected) {
            case 1:
                sb.append(context.getString(R.string.hrWhenWiredHeadsetAnyIsConnected));
                return;
            case 2:
                sb.append(context.getString(R.string.hrWhenWiredHeadsetWithMicIsConnected));
                return;
            case 3:
                sb.append(context.getString(R.string.hrWhenWiredHeadsetNoMicIsConnected));
                return;
            default:
                sb.append(context.getString(R.string.hrWhenWiredHeadsetIsDisconnected));
                return;
        }
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 1;
    }

    public static HeadsetConnectedCondition CreateFrom(String[] parts, int currentPart) {
        return new HeadsetConnectedCondition(Integer.parseInt(parts[currentPart + 1]));
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(this._IsConnected);
    }

    public PreferenceEx<HeadsetConnectedCondition> CreatePreference(PreferenceActivity context) {
        String off = context.getString(R.string.hrWiredHeadsetDisconnected);
        String onAny = context.getString(R.string.hrWiredHeadsetConnected);
        String onWithMic = context.getString(R.string.hrWiredHeadsetWithMicConnected);
        String onNoMic = context.getString(R.string.hrWiredHeadsetNoMicConnected);
        final String[] valueArray = new String[]{off, onAny, onWithMic, onNoMic};
        return CreateListPreference(context, context.getString(R.string.hrWiredHeadset), valueArray, valueArray[this._IsConnected], new OnGetValueEx<HeadsetConnectedCondition>() {
            public HeadsetConnectedCondition GetValue(Preference preference) {
                Integer isConnected = ArrayHelpers.FindIndex(valueArray, ((ListPreference) preference).getValue());
                return new HeadsetConnectedCondition(isConnected == null ? 0 : isConnected.intValue());
            }
        });
    }

    public String GetIsValidError(Context context) {
        return null;
    }
}
