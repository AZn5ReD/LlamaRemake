package com.kebab;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class CachedIntSetting extends CachedSetting<Integer> {
    public CachedIntSetting(String group, String name, int defaultValue) {
        super(group, name, Integer.valueOf(defaultValue));
    }

    /* Access modifiers changed, original: protected */
    public Integer GetValueInternal(SharedPreferences prefs) {
        return Integer.valueOf(prefs.getInt(this.name, ((Integer) this.defaultValue).intValue()));
    }

    /* Access modifiers changed, original: protected */
    public void SetValueInternal(Editor editor) {
        editor.putInt(this.name, ((Integer) this.value).intValue());
    }
}
