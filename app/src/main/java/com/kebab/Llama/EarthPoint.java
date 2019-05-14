package com.kebab.Llama;

import android.location.Location;
import com.kebab.Tuple;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EarthPoint extends Beacon {
    static NumberFormat format = new DecimalFormat("0.0000");
    public double Latitude;
    public double Longitude;
    public float Radius;

    public EarthPoint(Location loc) {
        this(loc.getLatitude(), loc.getLongitude(), loc.getAccuracy());
    }

    public EarthPoint(double latitude, double longitude, float radius) {
        this.Longitude = longitude;
        this.Latitude = latitude;
        this.Radius = radius;
    }

    public int hashCode() {
        return hashCode(this.Latitude) ^ hashCode(this.Longitude);
    }

    public static int hashCode(double dub) {
        long lng = Double.doubleToLongBits(dub);
        return (int) ((lng >>> 32) ^ lng);
    }

    public boolean equals(EarthPoint other) {
        return this.Latitude == other.Latitude && this.Longitude == other.Longitude;
    }

    public boolean equals(Object other) {
        if (other != null && (other instanceof EarthPoint)) {
            return equals((EarthPoint) other);
        }
        return false;
    }

    public void ToColonSeparated(StringBuffer sb) {
        sb.append(Beacon.EARTH_POINT).append(":").append(this.Latitude).append(":").append(this.Longitude).append(":").append(this.Radius);
    }

    public static EarthPoint CreateFromColonSeparated(String[] cellParts) {
        return new EarthPoint(Double.parseDouble(cellParts[1]), Double.parseDouble(cellParts[2]), cellParts.length > 3 ? Float.parseFloat(cellParts[3]) : 400.0f);
    }

    public String toFormattedString() {
        return format.format(this.Latitude) + ", " + format.format(this.Longitude);
    }

    public String getFriendlyTypeName() {
        return TYPE_EARTH_POINT_SINGLE;
    }

    public String getFriendlyTypeNamePlural() {
        return TYPE_EARTH_POINT_PLURAL;
    }

    public List<String> GetAreaNames(LlamaService llamaService) {
        ArrayList<String> newAreas = new ArrayList();
        for (Area a : llamaService.GetAreas()) {
            Iterator i$ = a._Cells.iterator();
            while (i$.hasNext()) {
                Beacon b = (Beacon) i$.next();
                if (b instanceof EarthPoint) {
                    EarthPoint other = (EarthPoint) b;
                    float[] results = new float[1];
                    Location.distanceBetween(this.Latitude, this.Longitude, other.Latitude, other.Longitude, results);
                    if (results[0] < other.Radius) {
                        newAreas.add(a.Name);
                        break;
                    }
                }
            }
        }
        return newAreas;
    }

    public List<Tuple<String, String>> GetAreaNamesWithInfo(LlamaService service) {
        ArrayList<Tuple<String, String>> newAreas = new ArrayList();
        for (Area a : service.GetAreas()) {
            Iterator i$ = a._Cells.iterator();
            while (i$.hasNext()) {
                Beacon b = (Beacon) i$.next();
                if (b instanceof EarthPoint) {
                    EarthPoint other = (EarthPoint) b;
                    float[] results = new float[1];
                    Location.distanceBetween(this.Latitude, this.Longitude, other.Latitude, other.Longitude, results);
                    if (results[0] < other.Radius) {
                        newAreas.add(new Tuple(a.Name, String.format(service.getString(R.string.hrWithin1m), new Object[]{Integer.valueOf((int) results[0])})));
                        break;
                    }
                }
            }
        }
        return newAreas;
    }

    public boolean CanSimpleDetectArea() {
        return false;
    }

    public String GetTypeId() {
        return Beacon.EARTH_POINT;
    }

    public Location ToLocation() {
        Location loc = new Location(Constants.LLAMA_EXTERNAL_STORAGE_ROOT);
        loc.setAccuracy(this.Radius);
        loc.setLatitude(this.Latitude);
        loc.setLongitude(this.Longitude);
        return loc;
    }

    public boolean IsMapBased() {
        return true;
    }
}
