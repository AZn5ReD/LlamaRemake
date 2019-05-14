package com.kebab;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class CachedLongSetting extends CachedSetting<Long> {
    public CachedLongSetting(String group, String name, long defaultValue) {
        super(group, name, Long.valueOf(defaultValue));
    }

    /* Access modifiers changed, original: protected */
    public Long GetValueInternal(SharedPreferences prefs) {
        return Long.valueOf(prefs.getLong(this.name, ((Long) this.defaultValue).longValue()));
    }

    /* Access modifiers changed, original: protected */
    public void SetValueInternal(Editor editor) {
        editor.putLong(this.name, ((Long) this.value).longValue());
    }
}
