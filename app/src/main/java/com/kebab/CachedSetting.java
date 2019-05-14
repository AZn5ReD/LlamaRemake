package com.kebab;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public abstract class CachedSetting<T> {
    protected T defaultValue;
    protected boolean gotValue;
    protected String group;
    protected String name;
    protected T value;

    public abstract T GetValueInternal(SharedPreferences sharedPreferences);

    public abstract void SetValueInternal(Editor editor);

    public CachedSetting(String group, String name, T defaultValue) {
        this.group = group;
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public T GetValue(Context context) {
        if (!this.gotValue) {
            SharedPreferences prefs;
            if (this.group == null) {
                prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
            } else {
                prefs = context.getApplicationContext().getSharedPreferences(this.group, 0);
            }
            this.value = GetValueInternal(prefs);
            this.gotValue = true;
        }
        return this.value;
    }

    public CachedSetting<T> SetValueForCommit(T value) {
        this.value = value;
        return this;
    }

    public void SetValueNoCommit(T value) {
        this.value = value;
    }

    public void SetValueAndCommit(Context context, T value, CachedSetting<?>... otherSettings) {
        SharedPreferences prefs;
        if (this.group == null) {
            prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        } else {
            prefs = context.getApplicationContext().getSharedPreferences(this.group, 0);
        }
        Editor editor = prefs.edit();
        if (value == null) {
            editor.remove(this.name);
        } else {
            this.value = value;
            SetValueInternal(editor);
        }
        for (CachedSetting<?> other : otherSettings) {
            if (other != null) {
                if (!HelpersC.StringEquals(other.group, this.group)) {
                    throw new RuntimeException("Settings named " + this.name + " and " + other.name + " were in different groups.");
                } else if (other.value == null) {
                    editor.remove(other.name);
                } else {
                    other.SetValueInternal(editor);
                }
            }
        }
        HelpersC.CommitPrefs(editor, context);
        Reset();
        for (CachedSetting<?> other2 : otherSettings) {
            if (other2 != null) {
                other2.Reset();
            }
        }
    }

    public void Reset() {
        this.gotValue = false;
    }
}
