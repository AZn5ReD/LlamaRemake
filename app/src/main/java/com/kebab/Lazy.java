package com.kebab;

public class Lazy<T> {
    Getter<T> _Getter;
    boolean _Got;
    T _Value;

    public interface Getter<T> {
        T Get();
    }

    public Lazy(Getter<T> getter) {
        this._Getter = getter;
    }

    public T Get() {
        if (!this._Got) {
            this._Value = this._Getter.Get();
            this._Got = true;
        }
        return this._Value;
    }
}
