package com.kebab.Llama;

import android.content.Context;
import com.kebab.Tuple;

public abstract class WrappedCreator<T extends EventFragment<T>, TUpgrade extends EventFragment<TUpgrade>> {
    public abstract T Create();

    public abstract T Create(String[] strArr, int i);

    public abstract String GetWarningMessage(Context context);

    public abstract boolean IsHeftyWarningMessage();

    public abstract TUpgrade TryUpgrade(T t);

    public Tuple<EventFragment<?>, Integer> CreateAndUpgrade(String[] parts, int currentPart) {
        T result = Create(parts, currentPart);
        return new Tuple(TryUpgrade(result), Integer.valueOf(result.GetPartsConsumption()));
    }
}
