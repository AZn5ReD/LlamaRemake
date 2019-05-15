package com.kebab.Llama;

import com.kebab.Tuple;

import java.util.Calendar;
import java.util.Currency;
import java.util.List;

public class BeaconAndCalendar {
//    private static Currency Calendar;
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
        Calendar c = new Calendar() {
            @Override
            protected void computeTime() {

            }

            @Override
            protected void computeFields() {

            }

            @Override
            public void add(int field, int amount) {

            }

            @Override
            public void roll(int field, boolean up) {

            }

            @Override
            public int getMinimum(int field) {
                return 0;
            }

            @Override
            public int getMaximum(int field) {
                return 0;
            }

            @Override
            public int getGreatestMinimum(int field) {
                return 0;
            }

            @Override
            public int getLeastMaximum(int field) {
                return 0;
            }
        };
        c.setTimeInMillis(Long.parseLong(parts[0]));
        com.kebab.Llama.Beacon b = new Beacon() {
            @Override
            public boolean CanSimpleDetectArea() {
                return false;
            }

            @Override
            public List<String> GetAreaNames(LlamaService llamaService) {
                return null;
            }

            @Override
            public List<Tuple<String, String>> GetAreaNamesWithInfo(LlamaService llamaService) {
                return null;
            }

            @Override
            public String GetTypeId() {
                return null;
            }

            @Override
            public boolean IsMapBased() {
                return false;
            }

            @Override
            public void ToColonSeparated(StringBuffer stringBuffer) {

            }

            @Override
            public String getFriendlyTypeName() {
                return null;
            }

            @Override
            public String getFriendlyTypeNamePlural() {
                return null;
            }

            @Override
            public String toFormattedString() {
                return null;
            }
        };
        return new BeaconAndCalendar(b.CreateFromColonSeparated(parts[1]), c);
    }
}
