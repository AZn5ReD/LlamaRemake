package com.kebab.Llama;

import android.content.Context;
import com.kebab.IterableHelpers;
import com.kebab.Selector;
import com.kebab.Tuple;
import java.util.List;

public class BluetoothBeacon extends Beacon {
    public static String UNKNOWN;
    String _Address;
    String _Name;

    public static void InitLocalisation(Context context) {
        UNKNOWN = context.getString(R.string.hrUnknown);
    }

    public BluetoothBeacon(String name, String address) {
        this._Name = name;
        this._Address = address;
    }

    public int hashCode() {
        return this._Address.hashCode();
    }

    public boolean equals(BluetoothBeacon other) {
        return this._Address.equals(other._Address);
    }

    public boolean equals(Object other) {
        if (other != null && (other instanceof BluetoothBeacon)) {
            return equals((BluetoothBeacon) other);
        }
        return false;
    }

    public void ToColonSeparated(StringBuffer sb) {
        sb.append(Beacon.BLUETOOTH).append(":").append(Beacon.SimpleColonEscape(this._Name)).append(":").append(this._Address);
    }

    public static BluetoothBeacon CreateFromColonSeparated(String[] parts) {
        return new BluetoothBeacon(Beacon.SimpleColonUnescape(parts[1]), parts[2] + ":" + parts[3] + ":" + parts[4] + ":" + parts[5] + ":" + parts[6] + ":" + parts[7]);
    }

    public String toFormattedString() {
        StringBuilder stringBuilder = new StringBuilder();
        String str = (this._Name == null || this._Name.length() <= 0) ? UNKNOWN : this._Name;
        return stringBuilder.append(str).append(" (").append(this._Address).append(")").toString();
    }

    public String getFriendlyTypeNamePlural() {
        return TYPE_BLUETOOTH_PLURAL;
    }

    public String getFriendlyTypeName() {
        return TYPE_BLUETOOTH_SINGLE;
    }

    public List<String> GetAreaNames(LlamaService service) {
        return (List) service._CellToAreaMap.get(this);
    }

    public List<Tuple<String, String>> GetAreaNamesWithInfo(LlamaService service) {
        List<String> areas = (List) service._CellToAreaMap.get(this);
        if (areas == null) {
            return null;
        }
        return IterableHelpers.Select(areas, new Selector<String, Tuple<String, String>>() {
            public Tuple<String, String> Do(String value) {
                return new Tuple(value, null);
            }
        });
    }

    public String GetTypeId() {
        return Beacon.BLUETOOTH;
    }

    public boolean CanSimpleDetectArea() {
        return true;
    }

    public boolean IsMapBased() {
        return false;
    }
}
