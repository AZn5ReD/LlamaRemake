package com.kebab;

public class Tuple3Mutable<T1, T2, T3> extends Tuple3<T1, T2, T3> {
    public Tuple3Mutable(T1 item1, T2 item2, T3 item3) {
        super(item1, item2, item3);
    }

    public Tuple3Mutable() {
        super();
    }

    public static <TT1, TT2, TT3> Tuple3Mutable<TT1, TT2, TT3> Create(TT1 a, TT2 b, TT3 c) {
        return new Tuple3Mutable(a, b, c);
    }
}
