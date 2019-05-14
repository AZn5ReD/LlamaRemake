package com.kebab;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class CachedStringSetting extends CachedSetting<String> {
    public CachedStringSetting(String group, String name, String defaultValue) {
        super(group, name, defaultValue);
    }

    /* Access modifiers changed, original: protected */
    public String GetValueInternal(SharedPreferences prefs) {
        return prefs.getString(this.name, (String) this.defaultValue);
    }

    /* Access modifiers changed, original: protected */
    public void SetValueInternal(Editor editor) {
        editor.putString(this.name, (String) this.value);
    }
}
