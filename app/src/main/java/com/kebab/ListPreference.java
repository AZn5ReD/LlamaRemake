package com.kebab;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.kebab.PreferenceEx.Helper;
import com.kebab.longpref.CheckBoxPreference;
import java.util.UnknownFormatConversionException;

public class ListPreference<TValue> extends android.preference.ListPreference implements PreferenceEx<TValue> {
    CharSequence _ExistingSummary;
    OnGetValueEx<TValue> _OnGetValueEx;
    OnPreferenceClick _OnPreferenceClick;

    public ListPreference(Context context) {
        super(context);
    }

    public ListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* Access modifiers changed, original: protected */
    public void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        onChanged();
    }

    public void onClick() {
        if (this._OnPreferenceClick == null || this._OnPreferenceClick.CanShowDialog(this)) {
            super.onClick();
        }
    }

    public void setOnPreferenceClick(OnPreferenceClick onPreferenceClick) {
        this._OnPreferenceClick = onPreferenceClick;
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
        try {
            super.setSummary(value);
            super.getSummary();
        } catch (UnknownFormatConversionException e) {
            if (value != null) {
                value = value.toString().replace("%", "%%");
            }
            super.setSummary(value);
        }
    }

    public CharSequence getOriginalSummary() {
        return this._ExistingSummary;
    }

    public void onChanged() {
        Helper.UpdateValueAndSummary(this);
    }

    public void onAttachedToActivity() {
        super.onAttachedToActivity();
        this._ExistingSummary = super.getSummary();
        Helper.UpdateValueAndSummary(this);
    }

    public CharSequence getHumanReadableValue() {
        return getEntry();
    }

    /* Access modifiers changed, original: protected */
    public void onBindView(View view) {
        super.onBindView(view);
        CheckBoxPreference.FixPrefView(view);
    }
}
