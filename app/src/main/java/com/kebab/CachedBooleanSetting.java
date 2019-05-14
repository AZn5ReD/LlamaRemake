package com.kebab;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class CachedBooleanSetting extends CachedSetting<Boolean> {
    public CachedBooleanSetting(String group, String name, Boolean defaultValue) {
        super(group, name, defaultValue);
    }

    /* Access modifiers changed, original: protected */
    public Boolean GetValueInternal(SharedPreferences prefs) {
        return Boolean.valueOf(prefs.getBoolean(this.name, ((Boolean) this.defaultValue).booleanValue()));
    }

    /* Access modifiers changed, original: protected */
    public void SetValueInternal(Editor editor) {
        editor.putBoolean(this.name, ((Boolean) this.value).booleanValue());
    }
}
