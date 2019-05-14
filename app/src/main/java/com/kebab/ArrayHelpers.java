package com.kebab;

import java.lang.reflect.Array;

public class ArrayHelpers {
    public static <T> boolean Contains(T[] values, T value) {
        if (value == null) {
            for (T item : values) {
                if (item == null) {
                    return true;
                }
            }
        } else {
            for (T item2 : values) {
                if (item2.equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Integer FindIndex(String[] items, String itemToFind) {
        int index = 0;
        for (String item : items) {
            if (itemToFind == item) {
                return Integer.valueOf(index);
            }
            if (item.equals(itemToFind)) {
                return Integer.valueOf(index);
            }
            index++;
        }
        return null;
    }

    public static <T> T[] SpliceArrays(T[] x, T[] y, Class<T> exampleClass) {
        int i;
        int first = x.length;
        Object[] result = (Object[]) ((Object[]) Array.newInstance(exampleClass, first + y.length));
        for (i = 0; i < first; i++) {
            result[i] = x[i];
        }
        for (i = 0; i < y.length; i++) {
            result[i + first] = y[i];
        }
        return result;
    }
}
