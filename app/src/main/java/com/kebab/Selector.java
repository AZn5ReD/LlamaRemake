package com.kebab;

public interface Selector<TIn, TOut> {
    TOut Do(TIn tIn);
}
