package com.kebab;

import android.content.Context;
import android.preference.DialogPreference;
import android.preference.Preference;
import com.kebab.PreferenceEx.Helper;

public abstract class ClickablePreferenceEx<TValue> extends DialogPreference implements PreferenceEx<TValue> {
    private TValue _CurrentValue;
    CharSequence _ExistingSummary;
    protected ResultRegisterableActivity _Host;
    OnGetValueEx<TValue> _OnGetValueEx;
    OnPreferenceClick _OnPreferenceClick;

    public interface GotResultHandler<TValue> {
        void HandleResult(TValue tValue);
    }

    public abstract String GetHumanReadableValue(Context context, TValue tValue);

    public abstract void OnPreferenceClicked(ResultRegisterableActivity resultRegisterableActivity, TValue tValue, GotResultHandler<TValue> gotResultHandler);

    public ClickablePreferenceEx(ResultRegisterableActivity activity, String title, TValue currentValue) {
        super(activity.GetActivity(), null);
        this._Host = activity;
        this._CurrentValue = currentValue;
        setTitle(title);
        setSummary("");
        setPersistent(false);
        SetOnGetValueExCallback(new OnGetValueEx<TValue>() {
            public TValue GetValue(Preference preference) {
                return ClickablePreferenceEx.this._CurrentValue;
            }
        });
    }

    public final String getHumanReadableValue() {
        return GetHumanReadableValue(this._Host.GetActivity(), this._CurrentValue);
    }

    public String getString(int resId) {
        return this._Host.GetActivity().getString(resId);
    }

    /* Access modifiers changed, original: protected */
    public TValue _GetCurrentValue() {
        return this._CurrentValue;
    }

    public final void onClick() {
        if (this._OnPreferenceClick == null || this._OnPreferenceClick.CanShowDialog(this)) {
            OnPreferenceClicked(this._Host, this._CurrentValue, new GotResultHandler<TValue>() {
                public void HandleResult(TValue result) {
                    ClickablePreferenceEx.this._CurrentValue = result;
                    ClickablePreferenceEx.this.onChanged();
                }
            });
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
}
