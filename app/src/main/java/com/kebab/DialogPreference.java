package com.kebab;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ScrollView;
import com.kebab.PreferenceEx.Helper;
import com.kebab.ResultRegisterableActivity.ResultCallback;

public class DialogPreference<TValue, TDialogResult> extends android.preference.DialogPreference implements PreferenceEx<TValue> {
    Activity _Activity;
    DialogHandlerInterface<TDialogResult> _DialogHandler;
    CharSequence _ExistingSummary;
    OnGetValueEx<TValue> _OnGetValueEx;
    OnPreferenceClick _OnPreferenceClick;
    TDialogResult _Value;
    TDialogResult _ValueBeforeDialog;
    View _View;

    public DialogPreference(Activity activity, TDialogResult currentValue, DialogHandlerInterface<TDialogResult> dialogHelper) {
        super(activity, null);
        this._Activity = activity;
        this._Value = currentValue;
        this._DialogHandler = dialogHelper;
        if (this._DialogHandler.HideButtons()) {
            setPositiveButtonText(null);
            setNegativeButtonText(null);
        }
    }

    /* Access modifiers changed, original: protected */
    public View onCreateDialogView() {
        TDialogResult value = this._Value;
        if (shouldPersist()) {
            String serialisedValue = getPersistedString(null);
            value = serialisedValue == null ? null : this._DialogHandler.fillValuesFromString(serialisedValue);
        }
        this._View = this._DialogHandler.getView(value, getContext(), this);
        if (!this._DialogHandler.RequiresScrollView()) {
            return this._View;
        }
        ScrollView sv = new ScrollView(getContext());
        sv.addView(this._View);
        return sv;
    }

    /* Access modifiers changed, original: protected */
    public void onBindDialogView(View v) {
        super.onBindDialogView(v);
        this._ValueBeforeDialog = this._Value;
    }

    /* Access modifiers changed, original: protected */
    public void onSetInitialValue(boolean restore, Object defaultValue) {
        super.onSetInitialValue(restore, defaultValue);
        if (!restore) {
            this._Value = DeserialisedStringOrNull((String) defaultValue);
        } else if (shouldPersist()) {
            this._Value = DeserialisedStringOrNull(getPersistedString(null));
        } else {
            this._Value = null;
        }
    }

    /* Access modifiers changed, original: 0000 */
    public TDialogResult DeserialisedStringOrNull(String value) {
        return value == null ? null : this._DialogHandler.fillValuesFromString(value);
    }

    /* Access modifiers changed, original: protected */
    public void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        this._DialogHandler.DialogHasFinished(this._View);
        if (positiveResult) {
            this._Value = this._DialogHandler.GetResultFromView();
            if (shouldPersist()) {
                persistString(this._DialogHandler.serialiseToString(this._Value));
            }
            callChangeListener(this._Value);
            onChanged();
        } else {
            this._Value = this._ValueBeforeDialog;
        }
        onChanged();
    }

    public void onClick() {
        if (this._OnPreferenceClick == null || this._OnPreferenceClick.CanShowDialog(this)) {
            this._DialogHandler.PrepareDataForDialog(new Runnable() {
                public void run() {
//                    super.onClick();
                }
            });
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
        return this._DialogHandler.getHumanReadableValue(this._Value);
    }

    public TDialogResult getValue() {
        return this._Value;
    }

    public void RegisterActivityResult(Intent intent, ResultCallback runnable, Object extraStateInfo) {
        ((ResultRegisterableActivity) this._Activity).RegisterActivityResult(intent, runnable, extraStateInfo);
    }

    public void setValueAndRunOnChanged(TDialogResult value) {
        this._Value = value;
        this._ValueBeforeDialog = value;
        if (shouldPersist()) {
            persistString(this._DialogHandler.serialiseToString(this._Value));
        }
        callChangeListener(this._Value);
        onChanged();
    }
}
