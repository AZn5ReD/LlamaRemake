package com.kebab;

import android.content.Context;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import com.kebab.PreferenceEx.Helper;
import com.kebab.SeekBarDialogView.ValueFormatter;
import com.kebab.longpref.CheckBoxPreference;

public class SeekBarPreference<TValue> extends DialogPreference implements PreferenceEx<TValue> {
    private static final String androidns = "http://schemas.android.com/apk/res/android";
    protected String _DataType;
    CharSequence _ExistingSummary;
    OnGetValueEx<TValue> _OnGetValueEx;
    OnPreferenceClick _OnPreferenceClick;
    SeekBarDialogView _SeekBarView;
    boolean _ShowAsValueOverMax;
    protected String _TopMostValue;
    ValueFormatter _ValueFormatter;
    private Context mContext;
    protected int mDefault;
    protected String mDialogMessage;
    protected int mMax;
    protected int mMin;
    protected String mSuffix;
    protected int mValue;
    private int mValueBeforeDialog;

    public SeekBarPreference(Context context, int defaultValue, String message, int min, int max) {
        super(context, null);
        this.mValue = 0;
        this.mValueBeforeDialog = 0;
        this._ShowAsValueOverMax = true;
        this.mDialogMessage = message;
        this.mContext = context;
        this.mDefault = defaultValue;
        this.mMax = max;
        this.mMin = min;
    }

    public SeekBarPreference(Context context, int defaultValue, String message, int min, int max, String suffix) {
        this(context, defaultValue, message, min, max);
        this.mSuffix = suffix;
    }

    public SeekBarPreference(Context context, int defaultValue, String message, int min, int max, String topMostValue, String suffix) {
        this(context, defaultValue, message, min, max);
        this.mSuffix = suffix;
        this._TopMostValue = topMostValue;
    }

    public SeekBarPreference(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mValue = 0;
        this.mValueBeforeDialog = 0;
        this._ShowAsValueOverMax = true;
        this.mContext = context;
        this.mDialogMessage = Helpers.GetAttributeValue(attrs, androidns, "dialogMessage", context);
        this.mSuffix = Helpers.GetAttributeValue(attrs, androidns, "text", context);
        this.mDefault = attrs.getAttributeIntValue(androidns, "defaultValue", 0);
        this.mMax = attrs.getAttributeIntValue(androidns, "max", 100);
        this.mMin = attrs.getAttributeIntValue(null, "min", 0);
        this._ShowAsValueOverMax = attrs.getAttributeBooleanValue(null, "showAsValueOverMax", true);
        this._TopMostValue = Helpers.GetAttributeValue(attrs, null, "topMostValue", context);
        this._DataType = attrs.getAttributeValue(null, "timeSpanMinutes");
        String formatter = attrs.getAttributeValue(null, "formatter");
        final String zeroMessageCaptured = Helpers.GetAttributeValue(attrs, null, "zeroMessage", context);
        if ("hoursMinutes".equals(formatter)) {
            this._ValueFormatter = new ValueFormatter() {
                public String FormatValue(int value, boolean isTopMostValue, String topMostValue) {
                    return isTopMostValue ? topMostValue : Helpers.GetHoursMinutesSeconds(context, value * 60, zeroMessageCaptured);
                }

                public int GetTextSize() {
                    return 20;
                }
            };
        } else if ("hoursMinutesSecondsMillis".equals(formatter)) {
            this._ValueFormatter = new ValueFormatter() {
                public String FormatValue(int value, boolean isTopMostValue, String topMostValue) {
                    return isTopMostValue ? topMostValue : Helpers.GetHoursMinutesSecondsMillis(context, value, zeroMessageCaptured);
                }

                public int GetTextSize() {
                    return 20;
                }
            };
        }
        Helper.UpdateValueAndSummary(this);
    }

    /* Access modifiers changed, original: protected */
    public void onAttachedToHierarchy(PreferenceManager preferenceManager) {
        super.onAttachedToHierarchy(preferenceManager);
        this.mValue = shouldPersist() ? getPersistedInt(this.mDefault) : this.mDefault;
    }

    public void onClick() {
        if (this._OnPreferenceClick == null || this._OnPreferenceClick.CanShowDialog(this)) {
            super.onClick();
        }
    }

    public void setOnPreferenceClick(OnPreferenceClick onPreferenceClick) {
        this._OnPreferenceClick = onPreferenceClick;
    }

    /* Access modifiers changed, original: protected */
    public View onCreateDialogView() {
        int value = this.mValue;
        if (shouldPersist()) {
            value = getPersistedInt(this.mDefault);
        }
        if (this._ValueFormatter != null) {
            this._SeekBarView = new SeekBarDialogView(value, this.mMin, this.mMax, this._TopMostValue, this.mDialogMessage, this._ValueFormatter);
        } else {
            this._SeekBarView = new SeekBarDialogView(value, this.mMin, this.mMax, this._TopMostValue, this.mDialogMessage, this.mSuffix);
        }
        return this._SeekBarView.createSeekBarDialogView(this.mContext);
    }

    /* Access modifiers changed, original: protected */
    public void onBindDialogView(View v) {
        super.onBindDialogView(v);
        this.mValueBeforeDialog = this.mValue;
    }

    /* Access modifiers changed, original: protected */
    public void onSetInitialValue(boolean restore, Object defaultValue) {
        super.onSetInitialValue(restore, defaultValue);
        if (restore) {
            this.mValue = shouldPersist() ? getPersistedInt(this.mDefault) : 0;
        } else {
            this.mValue = ((Integer) defaultValue).intValue();
        }
    }

    /* Access modifiers changed, original: protected */
    public void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        this._SeekBarView.DialogHasFinished();
        if (positiveResult) {
            this.mValue = this._SeekBarView.GetResult();
            if (shouldPersist()) {
                persistInt(this.mValue);
            }
            callChangeListener(new Integer(this.mValue));
            onChanged();
            return;
        }
        this.mValue = this.mValueBeforeDialog;
    }

    public void setValue(int value) {
        this.mValue = value;
    }

    public int getValue() {
        return this.mValue;
    }

    public void setMax(int max) {
        this.mMax = max;
    }

    public int getMax() {
        return this.mMax;
    }

    public TValue GetValueEx() {
        return this._OnGetValueEx.GetValue(this);
    }

    public void SetOnGetValueExCallback(OnGetValueEx<TValue> onGetValueEx) {
        this._OnGetValueEx = onGetValueEx;
    }

    public void setSummary(CharSequence value) {
        this._ExistingSummary = value;
        Helper.UpdateValueAndSummary(this);
    }

    public void setActualSummary(CharSequence value) {
        super.setSummary(value);
    }

    public CharSequence getOriginalSummary() {
        return this._ExistingSummary;
    }

    public void onAttachedToActivity() {
        super.onAttachedToActivity();
        this._ExistingSummary = super.getSummary();
        Helper.UpdateValueAndSummary(this);
    }

    public void onChanged() {
        Helper.UpdateValueAndSummary(this);
    }

    public String getHumanReadableValue() {
        if (this._ValueFormatter != null) {
            return this._ValueFormatter.FormatValue(this.mValue, this.mValue > this.mMax, this._TopMostValue);
        } else if (this.mValue > this.mMax) {
            return this._TopMostValue;
        } else {
            return this.mValue + (this._ShowAsValueOverMax ? " / " + this.mMax : "") + (this.mSuffix == null ? "" : this.mSuffix);
        }
    }

    public void setValueFormatter(ValueFormatter valueFormatter) {
        if (this._SeekBarView != null) {
            throw new RuntimeException("Cannot set formatter after dialog is created.");
        }
        this._ValueFormatter = valueFormatter;
    }

    /* Access modifiers changed, original: protected */
    public void onBindView(View view) {
        super.onBindView(view);
        CheckBoxPreference.FixPrefView(view);
    }
}
