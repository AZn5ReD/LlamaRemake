package com.kebab.Llama;

import java.util.Collection;
import java.util.List;

public class ArrayUtils {
    public static <T> boolean Contains(Iterable<T> iterable, T value) {
        for (T s : iterable) {
            if (s.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public static <T> boolean Contains(T[] iterable, T value) {
        for (T s : iterable) {
            if (s.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public static <T> boolean AddUnique(Collection<T> collection, T value) {
        if (Contains((Iterable) collection, (Object) value)) {
            return false;
        }
        collection.add(value);
        return true;
    }

    public static <T> boolean SwapItem(List<T> list, T oldValue, T newValue) {
        int index = list.indexOf(oldValue);
        if (index < 0) {
            return false;
        }
        list.set(index, newValue);
        return true;
    }
}
