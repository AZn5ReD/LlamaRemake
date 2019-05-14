package com.kebab.Llama;

import android.content.Context;
import com.kebab.Llama.EventActions.EventFragmentCompat;

public abstract class WrappedCreatorCompat<T extends EventFragmentCompat<T>, TUpgrade extends EventFragment<TUpgrade>> extends WrappedCreator<T, TUpgrade> {
    public abstract T Create(String[] strArr, int i);

    public abstract TUpgrade TryUpgrade(T t);

    public final T Create() {
        throw new RuntimeException("Not allowed to create an instance of a compatibility event fragment.");
    }

    public final String GetWarningMessage(Context context) {
        return null;
    }

    public final boolean IsHeftyWarningMessage() {
        return false;
    }
}
