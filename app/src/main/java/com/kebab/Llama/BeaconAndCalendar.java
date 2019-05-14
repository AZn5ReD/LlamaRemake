package com.kebab.Llama;

import java.util.Calendar;

public class BeaconAndCalendar {
    public final Beacon Beacon;
    public final Calendar Calendar;

    public BeaconAndCalendar(Beacon beacon, Calendar calendar) {
        this.Beacon = beacon;
        this.Calendar = calendar;
    }

    public void ToPsv(StringBuffer sb) {
        sb.append(this.Calendar.getTimeInMillis());
        sb.append("|");
        sb.append(this.Beacon.ToColonSeparated());
    }

    public static BeaconAndCalendar CreateFromPsv(String s) {
        String[] parts = s.split("\\|");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(Long.parseLong(parts[0]));
        return new BeaconAndCalendar(Beacon.CreateFromColonSeparated(parts[1]), c);
    }
}
