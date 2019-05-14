package com.kebab;

import java.util.Comparator;

public class Tuple<T1, T2> {
    public T1 Item1;
    public T2 Item2;

    public static class CaseInsensitiveItem1StringSorter implements Comparator<Tuple<String, ?>> {
        public int compare(Tuple<String, ?> x, Tuple<String, ?> y) {
            Object obj;
            Object obj2;
            String obj3 = "";
            String obj22 = "";
            Comparator comparator = String.CASE_INSENSITIVE_ORDER;
            if (x == null) {
                obj = null;
            } else {
                obj3 = (String) x.Item1;
            }
            if (y == null) {
                obj2 = null;
            } else {
                obj22 = (String) y.Item1;
            }
            return comparator.compare(obj3, obj22);
        }
    }

    public Tuple(T1 item1, T2 item2) {
        this.Item1 = item1;
        this.Item2 = item2;
    }

    public static <TT1, TT2> Tuple<TT1, TT2> Create(TT1 a, TT2 b) {
        return new Tuple(a, b);
    }
}
