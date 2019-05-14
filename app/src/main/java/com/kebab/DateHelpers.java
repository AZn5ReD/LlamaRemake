package com.kebab;

import android.content.Context;
import android.text.format.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class DateHelpers {
    static HashMap<String, Integer> DayNameToDayOfWeekMap = null;
    static String[] DayNames = null;
    static String[] DayNamesEnglishShort = null;
    static String[] DayNamesShort = null;
    public static final int HOURS_PER_DAY = 24;
    public static final int MILLIS_PER_DAY = 86400000;
    public static final int MILLIS_PER_HOUR = 3600000;
    public static final int MILLIS_PER_MINUTE = 60000;
    public static final int MILLIS_PER_SECOND = 1000;
    public static final int MINUTES_PER_HOUR = 60;
    public static final int SECONDS_PER_HOUR = 3600;
    public static final int SECONDS_PER_MINUTE = 60;
    static HashMap<String, Integer> ShortDayNameToDayOfWeekMap;
    static HashMap<String, Integer> ShortEnglishDayNameToDayOfWeekMap;
    static SimpleDateFormat _DateFormat12 = new SimpleDateFormat("dd MMM, hh:mm:ss a");
    static SimpleDateFormat _DateFormat12WithYear = new SimpleDateFormat("dd MMM yyyy, hh:mm:ss a");
    static SimpleDateFormat _DateFormat24 = new SimpleDateFormat("dd MMM, HH:mm:ss");
    static SimpleDateFormat _DateFormat24WithYear = new SimpleDateFormat("dd MMM yyyy, HH:mm:ss");
    static SimpleDateFormat _DateFormatIso = new SimpleDateFormat("yyyyMMddHHmmss");
    static SimpleDateFormat _DateFormatIsoUtcNoTime = new SimpleDateFormat("yyyyMMdd");
    static SimpleDateFormat _DebugDate = new SimpleDateFormat("dd MMM yyyy, HH:mm:ss Z");
    static boolean _Is24HourFormat = true;
    static SimpleDateFormat _TimeFormat12 = new SimpleDateFormat("h:mm:ss a");
    static SimpleDateFormat _TimeFormat24 = new SimpleDateFormat("HH:mm:ss");
    static SimpleDateFormat _TimeFormatNoSeconds12 = new SimpleDateFormat("h:mm a");
    static SimpleDateFormat _TimeFormatNoSeconds24 = new SimpleDateFormat("HH:mm");

    public static void InitLocalisation(Context c) {
        DateFormatSymbols symbols = new DateFormatSymbols(Locale.getDefault());
        String[] dayNames = symbols.getWeekdays();
        String[] dayNamesShort = symbols.getShortWeekdays();
        DayNames = new String[7];
        DayNames[0] = dayNames[2];
        DayNames[1] = dayNames[3];
        DayNames[2] = dayNames[4];
        DayNames[3] = dayNames[5];
        DayNames[4] = dayNames[6];
        DayNames[5] = dayNames[7];
        DayNames[6] = dayNames[1];
        DayNamesShort = new String[7];
        DayNamesShort[0] = dayNamesShort[2];
        DayNamesShort[1] = dayNamesShort[3];
        DayNamesShort[2] = dayNamesShort[4];
        DayNamesShort[3] = dayNamesShort[5];
        DayNamesShort[4] = dayNamesShort[6];
        DayNamesShort[5] = dayNamesShort[7];
        DayNamesShort[6] = dayNamesShort[1];
        if (DayNames[0].equals("2")) {
            dayNames = new DateFormatSymbols(Locale.ENGLISH).getWeekdays();
            DayNames[0] = dayNames[2];
            DayNames[1] = dayNames[3];
            DayNames[2] = dayNames[4];
            DayNames[3] = dayNames[5];
            DayNames[4] = dayNames[6];
            DayNames[5] = dayNames[7];
            DayNames[6] = dayNames[1];
        }
        if (DayNamesShort[0].equals("2")) {
            dayNamesShort = new DateFormatSymbols(Locale.ENGLISH).getShortWeekdays();
            DayNamesShort[0] = dayNamesShort[2];
            DayNamesShort[1] = dayNamesShort[3];
            DayNamesShort[2] = dayNamesShort[4];
            DayNamesShort[3] = dayNamesShort[5];
            DayNamesShort[4] = dayNamesShort[6];
            DayNamesShort[5] = dayNamesShort[7];
            DayNamesShort[6] = dayNamesShort[1];
        }
        DayNameToDayOfWeekMap = null;
    }

    public static String GetDayName(int dayOfTheWeek) {
        if (dayOfTheWeek == 1) {
            return DayNames[6];
        }
        return DayNames[dayOfTheWeek - 2];
    }

    public static String GetShortDayName(int dayOfTheWeek) {
        if (dayOfTheWeek == 1) {
            return DayNamesShort[6];
        }
        return DayNamesShort[dayOfTheWeek - 2];
    }

    public static String GetShortEnglishDayName(int dayOfTheWeek) {
        if (dayOfTheWeek == 1) {
            return DayNamesEnglishShort[6];
        }
        return DayNamesEnglishShort[dayOfTheWeek - 2];
    }

    public static String[] GetDayNames() {
        return DayNames;
    }

    public static Integer GetDayOfWeekForDayName(String value) {
        if (value == null) {
            return null;
        }
        if (DayNameToDayOfWeekMap == null) {
            DayNameToDayOfWeekMap = new HashMap();
            DayNameToDayOfWeekMap.put(GetDayName(2).toLowerCase(), Integer.valueOf(2));
            DayNameToDayOfWeekMap.put(GetDayName(3).toLowerCase(), Integer.valueOf(3));
            DayNameToDayOfWeekMap.put(GetDayName(4).toLowerCase(), Integer.valueOf(4));
            DayNameToDayOfWeekMap.put(GetDayName(5).toLowerCase(), Integer.valueOf(5));
            DayNameToDayOfWeekMap.put(GetDayName(6).toLowerCase(), Integer.valueOf(6));
            DayNameToDayOfWeekMap.put(GetDayName(7).toLowerCase(), Integer.valueOf(7));
            DayNameToDayOfWeekMap.put(GetDayName(1).toLowerCase(), Integer.valueOf(1));
        }
        return (Integer) DayNameToDayOfWeekMap.get(value.toLowerCase());
    }

    public static Integer GetDayOfWeekForShortDayName(String value) {
        if (value == null) {
            return null;
        }
        if (ShortDayNameToDayOfWeekMap == null) {
            ShortDayNameToDayOfWeekMap = new HashMap();
            ShortDayNameToDayOfWeekMap.put(GetShortDayName(2).toLowerCase(), Integer.valueOf(2));
            ShortDayNameToDayOfWeekMap.put(GetShortDayName(3).toLowerCase(), Integer.valueOf(3));
            ShortDayNameToDayOfWeekMap.put(GetShortDayName(4).toLowerCase(), Integer.valueOf(4));
            ShortDayNameToDayOfWeekMap.put(GetShortDayName(5).toLowerCase(), Integer.valueOf(5));
            ShortDayNameToDayOfWeekMap.put(GetShortDayName(6).toLowerCase(), Integer.valueOf(6));
            ShortDayNameToDayOfWeekMap.put(GetShortDayName(7).toLowerCase(), Integer.valueOf(7));
            ShortDayNameToDayOfWeekMap.put(GetShortDayName(1).toLowerCase(), Integer.valueOf(1));
        }
        return (Integer) ShortDayNameToDayOfWeekMap.get(value.toLowerCase());
    }

    public static Integer GetDayOfWeekForShortEnglishDayName(String value) {
        if (value == null) {
            return null;
        }
        if (ShortEnglishDayNameToDayOfWeekMap == null) {
            ShortEnglishDayNameToDayOfWeekMap = new HashMap();
            ShortEnglishDayNameToDayOfWeekMap.put(GetShortEnglishDayName(2).toLowerCase(), Integer.valueOf(2));
            ShortEnglishDayNameToDayOfWeekMap.put(GetShortEnglishDayName(3).toLowerCase(), Integer.valueOf(3));
            ShortEnglishDayNameToDayOfWeekMap.put(GetShortEnglishDayName(4).toLowerCase(), Integer.valueOf(4));
            ShortEnglishDayNameToDayOfWeekMap.put(GetShortEnglishDayName(5).toLowerCase(), Integer.valueOf(5));
            ShortEnglishDayNameToDayOfWeekMap.put(GetShortEnglishDayName(6).toLowerCase(), Integer.valueOf(6));
            ShortEnglishDayNameToDayOfWeekMap.put(GetShortEnglishDayName(7).toLowerCase(), Integer.valueOf(7));
            ShortEnglishDayNameToDayOfWeekMap.put(GetShortEnglishDayName(1).toLowerCase(), Integer.valueOf(1));
        }
        return (Integer) ShortEnglishDayNameToDayOfWeekMap.get(value.toLowerCase());
    }

    public static int GetSimpleDifferenceInDays(Date date1, Date date2) {
        return (int) ((date1.getTime() - date2.getTime()) / 86400000);
    }

    public static int GetTimeSpanInMinutes(long milliseconds) {
        return (int) (milliseconds / 60000);
    }

    public static Calendar MinCalendarAndNotNull(Calendar date1, Calendar date2) {
        if (date1 == null) {
            return date2;
        }
        if (date2 == null || date1.compareTo(date2) < 0) {
            return date1;
        }
        return date2;
    }

    static {
        _DateFormatIsoUtcNoTime.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public static String FormatIsoUtcDateNoTime(long millisSinceEpoch) {
        return _DateFormatIsoUtcNoTime.format(new Date(millisSinceEpoch));
    }

    public static String FormatIsoDate(Date time) {
        return _DateFormatIso.format(time);
    }

    public static String FormatDate(Calendar cal) {
        return FormatDate(cal.getTime());
    }

    public static String FormatDate(Date date) {
        if (_Is24HourFormat) {
            return _DateFormat24.format(date);
        }
        return _DateFormat12.format(date);
    }

    public static String FormatDebugDate(Calendar cal) {
        return FormatDebugDate(cal.getTime());
    }

    public static String FormatDebugDate(Date date) {
        return _DebugDate.format(date);
    }

    public static String FormatDateWithYear(Calendar cal) {
        return FormatDateWithYear(cal.getTime());
    }

    public static String FormatDateWithYear(Date date) {
        if (_Is24HourFormat) {
            return _DateFormat24WithYear.format(date);
        }
        return _DateFormat12WithYear.format(date);
    }

    public static boolean Is24HourFormat() {
        return _Is24HourFormat;
    }

    public static void Init(Context context) {
        _Is24HourFormat = DateFormat.is24HourFormat(context);
    }

    public static String formatTime(Date calendar) {
        Calendar c = Calendar.getInstance();
        c.setTime(calendar);
        return formatTime(c);
    }

    public static String formatTime(Calendar calendar) {
        if (_Is24HourFormat) {
            return _TimeFormat24.format(calendar.getTime());
        }
        return _TimeFormat12.format(calendar.getTime());
    }

    public static String formatTimeNoSeconds(Date calendar) {
        Calendar c = Calendar.getInstance();
        c.setTime(calendar);
        return formatTimeNoSeconds(c);
    }

    public static String formatTimeNoSeconds(Calendar calendar) {
        if (_Is24HourFormat) {
            return _TimeFormatNoSeconds24.format(calendar.getTime());
        }
        return _TimeFormatNoSeconds12.format(calendar.getTime());
    }

    public static String formatTimeNoSecondsIfZero(Date calendar) {
        Calendar c = Calendar.getInstance();
        c.setTime(calendar);
        return formatTimeNoSecondsIfZero(c);
    }

    public static String formatTimeNoSecondsIfZero(Calendar calendar) {
        if (calendar.get(13) == 0) {
            return formatTimeNoSeconds(calendar);
        }
        return formatTime(calendar);
    }

    public static Calendar CreateCalendar(long ticks) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(ticks);
        return c;
    }

    public static Long RoundToNextMinute(Calendar currentDate) {
        Calendar rounded = (Calendar) currentDate.clone();
        if (rounded.get(13) > 0 || rounded.get(14) > 0) {
            rounded.add(12, 1);
            rounded.set(13, 0);
            rounded.set(14, 0);
        }
        return Long.valueOf(rounded.getTimeInMillis());
    }

    public static int GetMillisSinceMidnight(Date date) {
        return (int) (date.getTime() % 86400000);
    }

    public static int GetDifferenceHours(Calendar a, Calendar b) {
        return (int) ((a.getTimeInMillis() - b.getTimeInMillis()) / 3600000);
    }
}
