package com.kebab;

import android.content.Context;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import com.kebab.PreferenceEx.Helper;

public class EditTextPreference<TValue> extends android.preference.EditTextPreference implements PreferenceEx<TValue> {
    CharSequence _ExistingSummary;
    OnGetValueEx<TValue> _OnGetValueEx;
    OnPreferenceClick _OnPreferenceClick;

    public EditTextPreference(Context context) {
        super(context);
    }

    public EditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void onClick() {
        if (this._OnPreferenceClick == null || this._OnPreferenceClick.CanShowDialog(this)) {
            super.onClick();
        }
    }

    /* Access modifiers changed, original: protected */
    public void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        onChanged();
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
        super.setSummary(value);
    }

    public void onAttachedToActivity() {
        super.onAttachedToActivity();
        this._ExistingSummary = super.getSummary();
        Helper.UpdateValueAndSummary(this);
    }

    public void onChanged() {
        Helper.UpdateValueAndSummary(this);
    }

    public CharSequence getOriginalSummary() {
        return this._ExistingSummary;
    }

    public String getHumanReadableValue() {
        TransformationMethod transMethod = getEditText().getTransformationMethod();
        String text = getText();
        if (transMethod == null) {
            return text;
        }
        if (text == null) {
            return null;
        }
        return transMethod.getTransformation(text, getEditText()).toString();
    }
}
