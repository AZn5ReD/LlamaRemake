package com.kebab;

public interface PreferenceEx<TValue> {

    public static class Helper {
        public static void UpdateValueAndSummary(PreferenceEx<?> pref) {
            pref.setActualSummary(pref.getHumanReadableValue());
        }
    }

    TValue GetValueEx();

    void SetOnGetValueExCallback(OnGetValueEx<TValue> onGetValueEx);

    CharSequence getHumanReadableValue();

    CharSequence getOriginalSummary();

    void onAttachedToActivity();

    void onChanged();

    void onClick();

    void setActualSummary(CharSequence charSequence);

    void setOnPreferenceClick(OnPreferenceClick onPreferenceClick);

    void setSummary(CharSequence charSequence);
}
