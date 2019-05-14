package com.kebab.Llama;

import java.util.Calendar;

public class CalendarItem {
    public static final int ATTENDING_ACCEPTED = 0;
    public static final int ATTENDING_DECLINED = 2;
    public static final int ATTENDING_TENTATIVE = 1;
    public static final int ATTENDING_UNKNOWN = 4;
    public static final int PRIVACY_DEFAULT = 0;
    public static final int PRIVACY_PRIVATE = 2;
    public static final int PRIVACY_PUBLIC = 1;
    public int AttendingStatus;
    public String CalendarName;
    public Calendar End;
    public boolean IsAllDay;
    public boolean IsLowerCase;
    public String Name;
    public int Privacy;
    public boolean ShowAsAvailable;
    public Calendar Start;

    public CalendarItem(String name, String calendarName, Calendar start, Calendar end, boolean showAsAvailable, int attendingStatus, int privacy, boolean isAllDay) {
        this.Name = name;
        this.CalendarName = calendarName;
        this.Start = start;
        this.End = end;
        this.ShowAsAvailable = showAsAvailable;
        this.AttendingStatus = attendingStatus;
        this.IsAllDay = isAllDay;
        this.Privacy = privacy;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        toString(sb);
        return sb.toString();
    }

    /* Access modifiers changed, original: 0000 */
    public void toString(Appendable appendable) {
        try {
            appendable.append("[");
            appendable.append(this.Name);
            appendable.append(",");
            appendable.append(this.CalendarName);
            appendable.append("]");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void ToLowercase() {
        if (!this.IsLowerCase) {
            this.Name = " " + this.Name.toLowerCase() + " ";
            this.CalendarName = this.CalendarName.toLowerCase();
            this.IsLowerCase = true;
        }
    }
}
