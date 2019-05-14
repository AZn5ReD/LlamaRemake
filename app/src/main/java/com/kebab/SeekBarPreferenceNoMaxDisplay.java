package com.kebab;

import android.content.Context;
import android.util.AttributeSet;

public class SeekBarPreferenceNoMaxDisplay<TValue> extends SeekBarPreference<TValue> {
    public SeekBarPreferenceNoMaxDisplay(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SeekBarPreferenceNoMaxDisplay(Context context, int defaultValue, String message, int min, int max, String topMostValue, String suffix) {
        super(context, defaultValue, message, min, max, topMostValue, suffix);
    }

    public String getHumanReadableValue() {
        if (this._ValueFormatter != null) {
            return this._ValueFormatter.FormatValue(this.mValue, this.mValue > this.mMax, this._TopMostValue);
        } else if (this.mValue > this.mMax) {
            return this._TopMostValue;
        } else {
            return this.mValue + (this.mSuffix == null ? "" : this.mSuffix);
        }
    }
}
