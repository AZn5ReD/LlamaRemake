package com.kebab.Llama;

import android.location.Location;
import com.kebab.Tuple;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class Area {
    public static final Comparator<Area> NameComparator = new Comparator<Area>() {
        public int compare(Area x, Area y) {
            return x.Name.compareToIgnoreCase(y.Name);
        }
    };
    public String Name;
    ArrayList<Beacon> _Cells = new ArrayList();

    public Area(String name) {
        this.Name = name;
    }

    public static Area CreateFromPsv(String psv) {
        String[] parts = psv.split("\\|", -1);
        Area result = new Area(new String(LlamaStorage.SimpleUnescape(parts[0])));
        for (int i = 1; i < parts.length; i++) {
            if (parts[i].length() != 0) {
                result.AddBeacon(Beacon.CreateFromColonSeparated(parts[i]));
            }
        }
        result._Cells.trimToSize();
        return result;
    }

    public String ToPsv() {
        StringBuffer sb = new StringBuffer();
        ToPsv(sb);
        return new String(sb);
    }

    public void ToPsv(StringBuffer sb) {
        sb.append(LlamaStorage.SimpleEscape(this.Name));
        Iterator i$ = this._Cells.iterator();
        while (i$.hasNext()) {
            Beacon cell = (Beacon) i$.next();
            sb.append("|");
            cell.ToColonSeparated(sb);
        }
    }

    public boolean AddBeacon(Beacon beacon) {
        int hashCode = beacon.hashCode();
        boolean found = false;
        Iterator i$ = this._Cells.iterator();
        while (i$.hasNext()) {
            if (((Beacon) i$.next()).equals(beacon)) {
                found = true;
            }
        }
        if (found) {
            return false;
        }
        this._Cells.add(beacon);
        return true;
    }

    public boolean equals(Area a) {
        if (a != null && a.Name.compareTo(this.Name) == 0) {
            return true;
        }
        return false;
    }

    public boolean equals(Object o) {
        if (o != null && (o instanceof Area)) {
            return equals((Area) o);
        }
        return false;
    }

    public int hashCode() {
        return this.Name.hashCode();
    }

    public int GetCellCount() {
        return this._Cells.size();
    }

    public List<Tuple<String, Integer>> GetCountOfBeaconTypes() {
        Hashtable<String, Integer> counts = new Hashtable();
        Iterator i$ = this._Cells.iterator();
        while (i$.hasNext()) {
            String beaconType = ((Beacon) i$.next()).getFriendlyTypeName();
            Integer count = (Integer) counts.get(beaconType);
            counts.put(beaconType, Integer.valueOf((count == null ? 0 : count.intValue()) + 1));
        }
        List<Tuple<String, Integer>> result = new ArrayList(counts.size());
        for (Entry<String, Integer> item : counts.entrySet()) {
            result.add(new Tuple(item.getKey(), item.getValue()));
        }
        return result;
    }

    public boolean RemoveCell(Beacon cell) {
        return this._Cells.remove(cell);
    }

    public ArrayList<Location> GetMapPointsAsLocations() {
        ArrayList<Location> locations = new ArrayList();
        Iterator i$ = this._Cells.iterator();
        while (i$.hasNext()) {
            Beacon beacon = (Beacon) i$.next();
            if (beacon.IsMapBased()) {
                locations.add(((EarthPoint) beacon).ToLocation());
            }
        }
        return locations;
    }
}
