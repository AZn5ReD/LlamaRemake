package com.kebab;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import com.kebab.PreferenceEx.Helper;
import java.util.UnknownFormatConversionException;

public class SimplePreference<TValue> extends Preference implements PreferenceEx<TValue> {
    CharSequence _ExistingSummary;
    OnGetValueEx<TValue> _OnGetValueEx;
    OnPreferenceClick _OnPreferenceClick;
    String _SingletonValueDescription;

    public SimplePreference(Context context) {
        super(context);
    }

    public SimplePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setSingletonValueDescription(String value) {
        this._SingletonValueDescription = value;
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
        return this._SingletonValueDescription;
    }
}
