package com.kebab;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import java.lang.reflect.Method;
import java.util.Arrays;

public class HelpersC {
    static final String HEXES = "0123456789ABCDEF";
    static Method _Apply;
    static boolean _TriedApply;

    public static boolean StringEquals(String x, String y) {
        if (x == null) {
            return y == null;
        } else {
            return x.equals(y);
        }
    }

    public static void CommitPrefs(Editor editor, Context context) {
        boolean z = true;
        if (!_TriedApply) {
            try {
                _Apply = Editor.class.getMethod("apply", new Class[0]);
            } catch (NoSuchMethodException | SecurityException e) {
            }
            _TriedApply = true;
            String str = "KebabCommon";
            StringBuilder append = new StringBuilder().append("Apply ");
            if (_Apply == null) {
                z = false;
            }
            Log.i(str, append.append(z).toString());
        }
        if (_Apply != null) {
            try {
                _Apply.invoke(editor, new Object[0]);
                return;
            } catch (Exception e2) {
                editor.commit();
                return;
            }
        }
        editor.commit();
    }

    public static String StringLeft(String message, int chars, boolean appendEllipsis) {
        if (message.length() <= chars) {
            return message;
        }
        return message.substring(0, chars) + (appendEllipsis ? "…" : "");
    }

    public static String GetVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            Log.e("GetVersionName", "Error getting version number", e);
            return null;
        }
    }

    public static <T> T[] ArrayJoin(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static int[] ArrayJoin(int[] first, int[] second) {
        int[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static String toHexString(byte[] raw) {
        if (raw == null) {
            return null;
        }
        StringBuilder hex = new StringBuilder(raw.length * 2);
        for (byte b : raw) {
            hex.append(HEXES.charAt((b & 240) >> 4)).append(HEXES.charAt(b & 15));
        }
        return hex.toString();
    }
}
