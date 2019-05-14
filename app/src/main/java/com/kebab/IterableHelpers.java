package com.kebab;

import com.kebab.Llama.Event;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class IterableHelpers {
    public static Collection<?> EMPTY_COLLECTION = new Collection<Object>() {
        public Iterator<Object> iterator() {
            return new Iterator<Object>() {
                public boolean hasNext() {
                    return false;
                }

                public Event next() {
                    return null;
                }

                public void remove() {
                }
            };
        }

        public boolean add(Object object) {
            throw new NotSupportedException();
        }

        public boolean addAll(Collection<? extends Object> collection) {
            throw new NotSupportedException();
        }

        public void clear() {
            throw new NotSupportedException();
        }

        public boolean contains(Object object) {
            throw new NotSupportedException();
        }

        public boolean containsAll(Collection<?> collection) {
            throw new NotSupportedException();
        }

        public boolean isEmpty() {
            throw new NotSupportedException();
        }

        public boolean remove(Object object) {
            throw new NotSupportedException();
        }

        public boolean removeAll(Collection<?> collection) {
            throw new NotSupportedException();
        }

        public boolean retainAll(Collection<?> collection) {
            throw new NotSupportedException();
        }

        public int size() {
            return 0;
        }

        public Object[] toArray() {
            return new Object[0];
        }

        public <T> T[] toArray(T[] array) {
            return Arrays.copyOf(array, 0);
        }
    };

    public static <T extends Comparable<T>> Integer FindIndex(Iterable<T> items, T itemToFind) {
        for (T item : items) {
            if (itemToFind == item) {
                return Integer.valueOf(0);
            }
            if (itemToFind.compareTo(item) == 0) {
                return Integer.valueOf(0);
            }
        }
        return null;
    }

    public static Integer FindIndexIgnoreCase(Iterable<String> items, String itemToFind) {
        for (String item : items) {
            if (itemToFind == item) {
                return Integer.valueOf(0);
            }
            if (itemToFind.equalsIgnoreCase(item)) {
                return Integer.valueOf(0);
            }
        }
        return null;
    }

    public static Integer FindIndex(CharSequence[] items, String itemToFind) {
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

    public static <T> int Count(Iterable<T> values) {
        if (values instanceof List) {
            return ((List) values).size();
        }
        int i = 0;
        for (T t : values) {
            i++;
        }
        return i;
    }

    public static <TIn, TOut> List<TOut> Select(Iterable<TIn> values, Selector<TIn, TOut> selector) {
        ArrayList<TOut> result;
        if (values instanceof List) {
            result = new ArrayList(((List) values).size());
        } else {
            result = new ArrayList();
        }
        for (TIn value : values) {
            result.add(selector.Do(value));
        }
        return result;
    }

    public static <T> List<T> OrderBy(Iterable<T> values, Comparator<T> comparator) {
        ArrayList<T> copy = new ArrayList(Count(values));
        for (T v : values) {
            copy.add(v);
        }
        Collections.sort(copy, comparator);
        return copy;
    }

    public static <T extends Comparable<T>> List<T> OrderBy(Iterable<T> values) {
        ArrayList<T> copy = new ArrayList(Count(values));
        for (T v : values) {
            copy.add(v);
        }
        Collections.sort(copy);
        return copy;
    }

    public static <T> T[] ToArray(Iterable<T> values, Class<T> clazz) {
        if (!(values instanceof List)) {
            return ToArray(ToArrayList(values), clazz);
        }
        List<T> valuesAsList = (List) values;
        return valuesAsList.toArray((Object[]) ((Object[]) Array.newInstance(clazz, valuesAsList.size())));
    }

    public static <T> ArrayList<T> ToArrayList(Iterable<T> values) {
        ArrayList<T> result;
        if (values instanceof List) {
            result = new ArrayList(((List) values).size());
        } else {
            result = new ArrayList();
        }
        for (T value : values) {
            result.add(value);
        }
        return result;
    }

    public static <T> String ConcatenateString(Iterable<T> values, String separator, Selector<T, String> func) {
        StringBuffer sb = new StringBuffer();
        boolean needComma = false;
        for (T i : values) {
            if (needComma) {
                sb.append(separator);
            }
            sb.append((String) func.Do(i));
            needComma = true;
        }
        return sb.toString();
    }

    public static String ConcatenateString(Iterable<String> values, String separator) {
        StringBuffer sb = new StringBuffer();
        boolean needComma = false;
        for (String s : values) {
            if (needComma) {
                sb.append(separator);
            }
            sb.append(s);
            needComma = true;
        }
        return sb.toString();
    }

    public static String ConcatenateString(String[] values, String separator) {
        StringBuffer sb = new StringBuffer();
        boolean needComma = false;
        for (String s : values) {
            if (needComma) {
                sb.append(separator);
            }
            sb.append(s);
            needComma = true;
        }
        return sb.toString();
    }

    public static CharSequence[] ToCharSequenceArray(List<String> devices) {
        CharSequence[] result = new CharSequence[devices.size()];
        for (int i = 0; i < devices.size(); i++) {
            result[i] = (CharSequence) devices.get(i);
        }
        return result;
    }

    public static <T> Collection<T> Create(T v) {
        ArrayList<T> list = new ArrayList(1);
        list.add(v);
        return list;
    }

    public static <T> Collection<T> WrapSingle(final T e) {
        return new Collection<T>() {
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    boolean hasIterated;

                    public boolean hasNext() {
                        if (this.hasIterated) {
                            return false;
                        }
                        this.hasIterated = true;
                        return true;
                    }

                    public T next() {
                        this.hasIterated = true;
                        return e;
                    }

                    public void remove() {
                    }
                };
            }

            public boolean add(T t) {
                throw new NotSupportedException();
            }

            public boolean addAll(Collection<? extends T> collection) {
                throw new NotSupportedException();
            }

            public void clear() {
                throw new NotSupportedException();
            }

            public boolean contains(Object object) {
                if (object == null) {
                    return e == null;
                } else {
                    return object.equals(e);
                }
            }

            public boolean containsAll(Collection<?> arg0) {
                if (arg0 == this) {
                    return true;
                }
                return arg0.containsAll(this);
            }

            public boolean isEmpty() {
                return false;
            }

            public boolean remove(Object object) {
                throw new NotSupportedException();
            }

            public boolean removeAll(Collection<?> collection) {
                throw new NotSupportedException();
            }

            public boolean retainAll(Collection<?> collection) {
                throw new NotSupportedException();
            }

            public int size() {
                return 1;
            }

            public Object[] toArray() {
                return new Object[]{e};
            }

            public <T> T[] toArray(T[] array) {
                Object obj = e;
                array[0] = obj;
                return (Object[]) obj;
            }
        };
    }

    public static <T> Collection<T> Empty() {
        return EMPTY_COLLECTION;
    }

    public static <T> Collection<T> Empty(T t) {
        return EMPTY_COLLECTION;
    }

    public static <T> boolean Any(Iterable<T> item1) {
        return item1.iterator().hasNext();
    }
}
