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

public class UsbStorageAction extends EventAction<UsbStorageAction> {
    public static final int USB_OFF = 0;
    public static final int USB_ON = 1;
    public static final int USB_SWAP = 2;
    int _ToggleType;

    public UsbStorageAction(int toggleType) {
        this._ToggleType = toggleType;
    }

    public void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        if (this._ToggleType == 1) {
            service.ToggleUsb(true);
        } else if (this._ToggleType == 0) {
            service.ToggleUsb(false);
        } else {
            service.ToggleUsbOnOff();
        }
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
        sb.append(this._ToggleType);
    }

    public static UsbStorageAction CreateFrom(String[] parts, int currentPart) {
        return new UsbStorageAction(Integer.parseInt(parts[currentPart + 1]));
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.TOGGLE_USB_ACTION;
    }

    public PreferenceEx<UsbStorageAction> CreatePreference(PreferenceActivity context) {
        String currentValue;
        final String USB_ON_TEXT = context.getString(R.string.hrUsbStorageOn);
        final String USB_OFF_TEXT = context.getString(R.string.hrUsbStorageOff);
        String USB_TOGGLE_TEXT = context.getString(R.string.hrUsbStorageToggle);
        switch (this._ToggleType) {
            case 0:
                currentValue = USB_OFF_TEXT;
                break;
            case 1:
                currentValue = USB_ON_TEXT;
                break;
            default:
                currentValue = USB_TOGGLE_TEXT;
                break;
        }
        return CreateListPreference(context, context.getString(R.string.hrActionUsbStorage), new String[]{USB_ON_TEXT, USB_OFF_TEXT, USB_TOGGLE_TEXT}, currentValue, new OnGetValueEx<UsbStorageAction>() {
            public UsbStorageAction GetValue(Preference preference) {
                int finalValue;
                String value = ((ListPreference) preference).getValue();
                if (USB_ON_TEXT.equals(value)) {
                    finalValue = 1;
                } else if (USB_OFF_TEXT.equals(value)) {
                    finalValue = 0;
                } else {
                    finalValue = 2;
                }
                return new UsbStorageAction(finalValue);
            }
        });
    }

    public void AppendActionDescription(Context context, AppendableCharSequence sb) throws IOException {
        switch (this._ToggleType) {
            case 0:
                sb.append(context.getString(R.string.hrUsbStorageOffDescription));
                return;
            case 1:
                sb.append(context.getString(R.string.hrUsbStorageOnDescription));
                return;
            default:
                sb.append(context.getString(R.string.hrUsbStorageToggleDescription));
                return;
        }
    }

    public String GetIsValidError(Context context) {
        return null;
    }

    public boolean IsHarmful() {
        return false;
    }
}
