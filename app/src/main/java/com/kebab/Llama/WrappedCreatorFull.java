package com.kebab.Llama;

import android.content.Context;

public abstract class WrappedCreatorFull<T extends EventFragment<T>> extends WrappedCreator<T, T> {
    public abstract T Create();

    public abstract T Create(String[] strArr, int i);

    public String GetWarningMessage(Context context) {
        return null;
    }

    public boolean IsHeftyWarningMessage() {
        return false;
    }

    public final T TryUpgrade(T original) {
        return original;
    }
}
