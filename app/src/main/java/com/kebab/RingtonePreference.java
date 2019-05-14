package com.kebab;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import com.kebab.Llama.Logging;
import com.kebab.longpref.CheckBoxPreference;

public class RingtonePreference extends android.preference.RingtonePreference {
    ValueChangedListener _ValueChanged;
    OnInitialPreferenceListener initialPreferenceListener;
    Uri value;

    public interface ValueChangedListener {
        void OnValueChange(Uri uri);
    }

    public RingtonePreference(Context context) {
        super(context);
    }

    public RingtonePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RingtonePreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /* Access modifiers changed, original: protected */
    public Uri onRestoreRingtone() {
        return this.value;
    }

    /* Access modifiers changed, original: protected */
    public void onPrepareRingtonePickerIntent(Intent ringtonePickerIntent) {
        super.onPrepareRingtonePickerIntent(ringtonePickerIntent);
        ringtonePickerIntent.putExtra("android.intent.extra.ringtone.EXISTING_URI", this.value);
    }

    public void setOnInitialPreferenceListener(OnInitialPreferenceListener listener) {
        this.initialPreferenceListener = listener;
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean result = super.onActivityResult(requestCode, resultCode, data);
        if (result && resultCode == -1) {
            this.value = (Uri) data.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            Logging.Report("RingTonePicker", "Picked ringtone pref was " + this.value, getContext());
            if (this._ValueChanged != null) {
                this._ValueChanged.OnValueChange(this.value);
            }
        } else {
            Logging.Report("RingTonePicker", "Picked ringtone pref not success " + this.value, getContext());
        }
        return result;
    }

    public void SetRingtoneValue(Uri newValue) {
        this.value = newValue;
    }

    public Uri GetRingtoneValue() {
        return this.value;
    }

    public void SetOnValueChangedCallback(ValueChangedListener valueChanged) {
        this._ValueChanged = valueChanged;
    }

    /* Access modifiers changed, original: protected */
    public void onBindView(View view) {
        super.onBindView(view);
        CheckBoxPreference.FixPrefView(view);
    }
}
