package com.kebab;

import android.preference.Preference;

public interface OnGetValueEx<TValue> {
    TValue GetValue(Preference preference);
}
