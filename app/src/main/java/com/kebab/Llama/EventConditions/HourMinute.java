package com.kebab.Llama.EventConditions;

import com.kebab.DateHelpers;
import java.util.Calendar;
import java.util.Date;

public class HourMinute {
    public static final int MIDNIGHT = HoursMinutesToInt(0, 0);
    public int Hours;
    public int Minutes;

    public HourMinute(Calendar date) {
        this(date.get(11), date.get(12));
    }

    public HourMinute(int hour, int minute) {
        this.Hours = hour;
        this.Minutes = minute;
    }

    public HourMinute(Date date) {
        int totalMinutes = (DateHelpers.GetMillisSinceMidnight(date) / 1000) / 60;
        this.Minutes = totalMinutes % 60;
        this.Hours = totalMinutes / 60;
    }

    public HourMinute() {

    }

    public String toString() {
        StringBuilder stringBuilder;
        Object valueOf;
        if (DateHelpers.Is24HourFormat()) {
            stringBuilder = new StringBuilder();
            if (this.Hours >= 10) {
                valueOf = Integer.valueOf(this.Hours);
            } else {
                valueOf = "0" + this.Hours;
            }
            stringBuilder = stringBuilder.append(valueOf).append(":");
            if (this.Minutes >= 10) {
                valueOf = Integer.valueOf(this.Minutes);
            } else {
                valueOf = "0" + this.Minutes;
            }
            return stringBuilder.append(valueOf).toString();
        }
        String hours;
        String amPm;
        if (this.Hours == 0) {
            hours = "12:";
            amPm = " AM";
        } else if (this.Hours == 12) {
            hours = "12:";
            amPm = " PM";
        } else if (this.Hours < 12) {
            hours = this.Hours + ":";
            amPm = " AM";
        } else {
            hours = (this.Hours - 12) + ":";
            amPm = " PM";
        }
        stringBuilder = new StringBuilder().append(hours);
        if (this.Minutes >= 10) {
            valueOf = Integer.valueOf(this.Minutes);
        } else {
            valueOf = "0" + this.Minutes;
        }
        return stringBuilder.append(valueOf).append(amPm).toString();
    }

    public static int HoursMinutesToInt(int hours, int minutes) {
        return (hours * 60) + minutes;
    }

    public static HourMinute IntToHoursMinutesTo(int hmValue) {
        HourMinute result = new HourMinute();
        result.Hours = hmValue / 60;
        result.Minutes = hmValue % 60;
        return result;
    }

    public static int CalendarToInt(Calendar date) {
        return HoursMinutesToInt(date.get(11), date.get(12));
    }

    public int compareTo(HourMinute other) {
        if (this.Hours == other.Hours) {
            if (this.Minutes == other.Minutes) {
                return 0;
            }
            if (this.Minutes >= other.Minutes) {
                return 1;
            }
            return -1;
        } else if (this.Hours >= other.Hours) {
            return 1;
        } else {
            return -1;
        }
    }
}
