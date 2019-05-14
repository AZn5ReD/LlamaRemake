package com.kebab;

public class Tuple4<T1, T2, T3, T4> {
    public T1 Item1;
    public T2 Item2;
    public T3 Item3;
    public T4 Item4;

    public Tuple4(T1 item1, T2 item2, T3 item3, T4 item4) {
        this.Item1 = item1;
        this.Item2 = item2;
        this.Item3 = item3;
        this.Item4 = item4;
    }

    public static <TT1, TT2, TT3, TT4> Tuple4<TT1, TT2, TT3, TT4> Create(TT1 a, TT2 b, TT3 c, TT4 d) {
        return new Tuple4(a, b, c, d);
    }
}
