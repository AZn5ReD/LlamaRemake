package com.kebab;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import java.util.Date;

public class CachedStringBackedDateSetting extends CachedSetting<Date> {
    public CachedStringBackedDateSetting(String group, String name, Date defaultValue) {
        super(group, name, defaultValue);
    }

    /* Access modifiers changed, original: protected */
    public Date GetValueInternal(SharedPreferences prefs) {
        try {
            return new Date(Long.valueOf(Long.parseLong(prefs.getString(this.name, null))).longValue());
        } catch (NumberFormatException e) {
            return (Date) this.defaultValue;
        }
    }

    /* Access modifiers changed, original: protected */
    public void SetValueInternal(Editor editor) {
        editor.putString(this.name, String.valueOf(((Date) this.value).getTime()));
    }
}
