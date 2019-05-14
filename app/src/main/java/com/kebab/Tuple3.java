package com.kebab;

public class Tuple3<T1, T2, T3> {
    public T1 Item1;
    public T2 Item2;
    public T3 Item3;

    public Tuple3(T1 item1, T2 item2, T3 item3) {
        this.Item1 = item1;
        this.Item2 = item2;
        this.Item3 = item3;
    }

    public static <TT1, TT2, TT3> Tuple3<TT1, TT2, TT3> Create(TT1 a, TT2 b, TT3 c) {
        return new Tuple3(a, b, c);
    }
}
