package com.kebab.Llama;

import com.kebab.IterableHelpers;
import com.kebab.Selector;
import com.kebab.Tuple;
import java.util.List;

public class WifiNamedNetwork extends WifiBeacon {
    public static final Beacon Dummy = new WifiNamedNetwork("");
    String _NetworkName;

    public WifiNamedNetwork(String networkName) {
        this._NetworkName = networkName;
    }

    public int hashCode() {
        return this._NetworkName.hashCode();
    }

    public boolean equals(WifiNamedNetwork other) {
        return this._NetworkName.equals(other._NetworkName);
    }

    public boolean equals(Object other) {
        if (other != null && (other instanceof WifiNamedNetwork)) {
            return equals((WifiNamedNetwork) other);
        }
        return false;
    }

    public void ToColonSeparated(StringBuffer sb) {
        sb.append(Beacon.WIFI_NAME).append(":").append(Beacon.SimpleColonEscape(this._NetworkName));
    }

    public static WifiNamedNetwork CreateFromColonSeparated(String[] cellParts) {
        return new WifiNamedNetwork(Beacon.SimpleColonUnescape(cellParts[1]));
    }

    public String toFormattedString() {
        return this._NetworkName;
    }

    public String getFriendlyTypeName() {
        return TYPE_WIFI_NAME_SINGLE;
    }

    public String getFriendlyTypeNamePlural() {
        return TYPE_WIFI_NAME_PLURAL;
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
        return Beacon.WIFI_NAME;
    }
}
