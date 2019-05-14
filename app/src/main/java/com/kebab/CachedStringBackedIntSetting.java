package com.kebab;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class CachedStringBackedIntSetting extends CachedSetting<Integer> {
    public CachedStringBackedIntSetting(String group, String name, Integer defaultValue) {
        super(group, name, defaultValue);
    }

    /* Access modifiers changed, original: protected */
    public Integer GetValueInternal(SharedPreferences prefs) {
        try {
            return Integer.valueOf(Integer.parseInt(prefs.getString(this.name, null)));
        } catch (NumberFormatException e) {
            return (Integer) this.defaultValue;
        }
    }

    /* Access modifiers changed, original: protected */
    public void SetValueInternal(Editor editor) {
        editor.putString(this.name, String.valueOf(this.value));
    }
}
