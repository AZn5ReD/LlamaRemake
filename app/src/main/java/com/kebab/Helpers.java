package com.kebab;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.widget.Toast;
import com.kebab.AlertDialogEx.Builder;
import com.kebab.Llama.Logging;
import com.kebab.Llama.R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Helpers {
    public static Boolean _IsOnMasterLlamasPhone;

    public static void ShowTip(Context context, String message) {
        Toast.makeText(context, message, 1).show();
    }

    public static void ShowTip(Context context, int resId) {
        Toast.makeText(context, context.getString(resId), 1).show();
    }

    public static Integer ParseIntOrNull(String value) {
        if (value == null || value.equalsIgnoreCase("null")) {
            return null;
        }
        return Integer.valueOf(Integer.parseInt(value, 10));
    }

    public static Integer TryParseInt(String value) {
        try {
            return Integer.valueOf(Integer.parseInt(value, 10));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static void ConcatenateListOfStrings(StringBuilder sb, List<String> items, String normalSeparator, String finalSeparator) {
        if (items.size() != 0) {
            if (items.size() == 1) {
                sb.append((String) items.get(0));
                return;
            }
            for (int i = 0; i < items.size() - 1; i++) {
                sb.append((String) items.get(i));
                sb.append(normalSeparator);
            }
            sb.setLength(sb.length() - 2);
            sb.append(finalSeparator);
            sb.append((String) items.get(items.size() - 1));
        }
    }

    public static String ConcatenateListOfStrings(List<String> items, String normalSeparator, String finalSeparator) {
        StringBuilder sb = new StringBuilder();
        ConcatenateListOfStrings(sb, items, normalSeparator, finalSeparator);
        return sb.toString();
    }

    public static long[] StringsToLongs(String[] values) {
        long[] result = new long[values.length];
        for (int i = 0; i < values.length; i++) {
            long l;
            String v = values[i];
            if (v.equals("")) {
                l = 0;
            } else {
                try {
                    l = Long.parseLong(v);
                } catch (NumberFormatException e) {
                    l = 0;
                }
            }
            result[i] = l;
        }
        return result;
    }

    public static String CapitaliseFirstLetter(String friendlyTypeName) {
        if (friendlyTypeName == null) {
            return null;
        }
        return friendlyTypeName.length() != 0 ? friendlyTypeName.substring(0, 1).toUpperCase() + friendlyTypeName.substring(1) : friendlyTypeName;
    }

    public static StringBuilder CapitaliseFirstLetter(StringBuilder sb) {
        return CapitaliseFirstLetter(sb, 0);
    }

    public static StringBuilder CapitaliseFirstLetter(StringBuilder sb, int charPos) {
        if (sb == null) {
            return null;
        }
        if (sb.length() < charPos) {
            return sb;
        }
        sb.setCharAt(charPos, Character.toUpperCase(sb.charAt(charPos)));
        return sb;
    }

    public static SpannableStringBuilder CapitaliseFirstLetter(SpannableStringBuilder sb) {
        return CapitaliseFirstLetter(sb, 0);
    }

    public static SpannableStringBuilder CapitaliseFirstLetter(SpannableStringBuilder sb, int charPos) {
        if (sb == null) {
            return null;
        }
        if (sb.length() < charPos) {
            return sb;
        }
        sb.replace(charPos, charPos + 1, String.valueOf(Character.toUpperCase(sb.charAt(charPos))));
        return sb;
    }

    public static String DumpIntent(Intent intent) {
        StringBuffer sb = new StringBuffer();
        if (intent == null) {
            sb.append("Intent is null");
            return sb.toString();
        }
        sb.append("action=").append(intent.getAction()).append("\n");
        Bundle extras = intent.getExtras();
        if (extras != null) {
            for (String key : extras.keySet()) {
                sb.append(key).append("=");
                Object value = extras.get(key);
                sb.append(value == null ? "[[NULL]]" : value.toString()).append("\n");
            }
        } else {
            sb.append("no extras");
        }
        return sb.toString();
    }

    public static String ChoosePlural(int value, String singular, String plural) {
        return value == 1 ? singular : plural;
    }

    public static String ChoosePlural(int value, String singular, String plural, boolean prependNumberAndSpace) {
        if (prependNumberAndSpace) {
            return value + " " + ChoosePlural(value, singular, plural);
        }
        return ChoosePlural(value, singular, plural);
    }

    public static String GetAttributeValue(AttributeSet attrs, String namespace, String key, Context context) {
        int resourceId = attrs.getAttributeResourceValue(namespace, key, 0);
        if (resourceId == 0) {
            return attrs.getAttributeValue(namespace, key);
        }
        return context.getString(resourceId);
    }

    public static boolean IsOnMasterLlamasPhone(Context context) {
        return false;
    }

    public static String GetStringFromBundle(Bundle bundle) {
        Parcel p = null;
        try {
            p = Parcel.obtain();
            p.setDataPosition(0);
            p.writeBundle(bundle);
            String encodeToString = Base64.encodeToString(p.marshall(), 2);
            return encodeToString;
        } finally {
            if (p != null) {
                p.recycle();
            }
        }
    }

    public static Bundle GetBundleFromString(String s) {
        Parcel p = null;
        try {
            p = Parcel.obtain();
            p.setDataPosition(0);
            byte[] data = Base64.decode(s, 2);
            p.unmarshall(data, 0, data.length);
            p.setDataPosition(0);
            Bundle readBundle = p.readBundle(Bundle.class.getClassLoader());
            return readBundle;
        } finally {
            if (p != null) {
                p.recycle();
            }
        }
    }

    public static void WriteParcelToAppendable(Parcelable parcel, Appendable buffer) {
        try {
            buffer.append(GetParcelAsString(parcel));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String GetParcelAsString(Parcelable parcelable) {
        Parcel p = null;
        try {
            p = Parcel.obtain();
            p.setDataPosition(0);
            p.writeParcelable(parcelable, 0);
            String encodeToString = Base64.encodeToString(p.marshall(), 2);
            return encodeToString;
        } finally {
            if (p != null) {
                p.recycle();
            }
        }
    }

    /* JADX WARNING: Failed to extract finally block: empty outs */
    /* JADX WARNING: Missing block: B:15:?, code skipped:
            return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static <T> T GetParcelableFromString(String s, Class<T> parcelableType) {
        if (s == null || s.length() == 0) {
            return null;
        }
        Parcel p = null;
        try {
            p = Parcel.obtain();
            p.setDataPosition(0);
            byte[] data = Base64.decode(s, 2);
            p.unmarshall(data, 0, data.length);
            p.setDataPosition(0);
            T item = p.readParcelable(parcelableType.getClassLoader());
            if (p == null) {
                return item;
            }
            p.recycle();
        } catch (Throwable th) {
            if (p != null) {
                p.recycle();
            }
        }
    }

    public static boolean VerifyIntentsMatch(Context c, Intent original, Intent y) {
        String result = VerifyIntentsMatchHelper(original, y);
        if (result == null) {
            return true;
        }
        Logging.Report("IntentParcel", "Intent mismatch for " + result, c);
        return false;
    }

    public static String VerifyIntentsMatchHelper(Intent x, Intent y) {
        if (x == null) {
            return "Intent x was null";
        }
        if (y == null) {
            return "Intent y was null";
        }
        if (!StringsMatch(x.getAction(), y.getAction())) {
            return "getAction";
        }
        if (!ComponentNamesMatch(x.getComponent(), y.getComponent())) {
            return "getComponent";
        }
        if (!UrisMatch(x.getData(), y.getData())) {
            return "getData";
        }
        if (!StringsMatch(x.getDataString(), y.getDataString())) {
            return "getDataString";
        }
        if (x.getFlags() != y.getFlags()) {
            return "getFlags";
        }
        if (!StringsMatch(x.getPackage(), y.getPackage())) {
            return "getPackage";
        }
        if (!StringsMatch(x.getScheme(), y.getScheme())) {
            return "getScheme";
        }
        if (!RectsMatch(x.getSourceBounds(), y.getSourceBounds())) {
            return " getSourceBounds";
        }
        if (!StringsMatch(x.getType(), y.getType())) {
            return "getType";
        }
        Bundle bx = x.getExtras();
        Bundle by = y.getExtras();
        if (bx != null) {
            if (by == null) {
                return "extras";
            }
            Set<String> byKeySet = by.keySet();
            for (String key : bx.keySet()) {
                if (!byKeySet.contains(key)) {
                    return "extras-" + key;
                }
            }
        }
        return null;
    }

    static boolean ComponentNamesMatch(ComponentName a, ComponentName b) {
        if (a == null) {
            return b == null;
        } else {
            if (b != null) {
                return a.toString().equals(b.toString());
            }
            return false;
        }
    }

    static boolean UrisMatch(Uri a, Uri b) {
        if (a == null) {
            return b == null;
        } else {
            if (b != null) {
                return a.toString().equals(b.toString());
            }
            return false;
        }
    }

    static boolean RectsMatch(Rect a, Rect b) {
        if (a == null) {
            return b == null;
        } else {
            if (b != null) {
                return a.toString().equals(b.toString());
            }
            return false;
        }
    }

    static boolean StringsMatch(String a, String b) {
        if (a == null) {
            return b == null;
        } else {
            return a.equals(b);
        }
    }

    public static String GetHoursMinutesSeconds(Context context, int totalSeconds) {
        return GetHoursMinutesSeconds(context, totalSeconds, "");
    }

    public static String GetHoursMinutesSeconds(Context context, int totalSeconds, String zeroMessage) {
        int seconds = totalSeconds % 60;
        int remainder = totalSeconds / 60;
        int minutes = remainder % 60;
        int hours = remainder / 60;
        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(ChoosePlural(hours, context.getString(R.string.hrHour), context.getString(R.string.hrHours), true));
            sb.append(" ");
        }
        if (minutes > 0) {
            sb.append(ChoosePlural(minutes, context.getString(R.string.hrMinute), context.getString(R.string.hrMinutes), true));
            sb.append(" ");
        }
        if (seconds > 0) {
            sb.append(ChoosePlural(seconds, context.getString(R.string.hrSecond), context.getString(R.string.hrSeconds), true));
            sb.append(" ");
        }
        if (sb.length() <= 0) {
            return zeroMessage;
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    public static String GetHoursMinutesSecondsMillis(Context context, int totalMillis, String zeroMessage) {
        int millis = totalMillis % 1000;
        int remainder = totalMillis / 1000;
        int seconds = remainder % 60;
        remainder /= 60;
        int minutes = remainder % 60;
        int hours = remainder / 60;
        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(ChoosePlural(hours, context.getString(R.string.hrHour), context.getString(R.string.hrHours), true));
            sb.append(" ");
        }
        if (minutes > 0) {
            sb.append(ChoosePlural(minutes, context.getString(R.string.hrMinute), context.getString(R.string.hrMinutes), true));
            sb.append(" ");
        }
        if (seconds > 0) {
            sb.append(ChoosePlural(seconds, context.getString(R.string.hrSecond), context.getString(R.string.hrSeconds), true));
            sb.append(" ");
        }
        if (millis > 0) {
            sb.append(millis);
            sb.append(context.getString(R.string.hrMilliAbbreviation));
            sb.append(" ");
        }
        if (sb.length() <= 0) {
            return zeroMessage;
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    public static ArrayList<String> SplitToArrayList(String values, String separatorRegEx, int regExLimit) {
        String[] stuff = values.split(separatorRegEx, regExLimit);
        ArrayList<String> result = new ArrayList(stuff.length);
        if (!(regExLimit == -1 && stuff[0].length() == 0)) {
            for (Object add : stuff) {
                result.add(add);
            }
        }
        return result;
    }

    public static void ArrayListToLowerCase(ArrayList<String> calendarNames) {
        for (int i = 0; i < calendarNames.size(); i++) {
            calendarNames.set(i, ((String) calendarNames.get(i)).toLowerCase());
        }
    }

    public static void ShowSimpleDialogMessage(Activity context, String message) {
        ShowSimpleDialogMessage(context, message, null);
    }

    public static void ShowSimpleDialogMessage(Activity context, String message, OnClickListener onClick) {
        new Builder(context).setTitle(context.getApplicationInfo().loadLabel(context.getPackageManager())).setMessage(message).setPositiveButton(R.string.hrOkeyDoke, onClick).show();
    }

    public static String NullableBooleanToString(Boolean value) {
        if (value == null) {
            return "";
        }
        if (value.booleanValue()) {
            return "1";
        }
        return "0";
    }

    public static Boolean NullableBooleanFromString(String value) {
        if ("1".equals(value)) {
            return Boolean.valueOf(true);
        }
        if ("0".equals(value)) {
            return Boolean.valueOf(false);
        }
        return null;
    }

    public static String CharSequenceToStringOrEmpty(CharSequence c) {
        if (c == null) {
            return "";
        }
        return c.toString();
    }

    public static void ScrollToPreference(PreferenceActivity activity, Preference pref) {
        PreferenceScreen screen = activity.getPreferenceScreen();
        int index = 0;
        boolean foundIt = false;
        for (int i = 0; i < screen.getPreferenceCount(); i++) {
            Preference p = screen.getPreference(i);
            index++;
            if (p instanceof PreferenceCategory) {
                PreferenceCategory pcat = (PreferenceCategory) p;
                for (int j = 0; j < pcat.getPreferenceCount(); j++) {
                    index++;
                    if (pcat.getPreference(j) == pref) {
                        foundIt = true;
                        break;
                    }
                }
            }
            if (foundIt) {
                break;
            }
        }
        activity.getListView().setSelectionFromTop(index, 0);
    }

    public static String GenerateRandomString(int charCount) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < charCount; i++) {
            sb.append((char) (random.nextInt(26) + 97));
        }
        return sb.toString();
    }
}
