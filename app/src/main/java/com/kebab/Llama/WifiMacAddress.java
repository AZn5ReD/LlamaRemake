package com.kebab.Llama;

import com.kebab.IterableHelpers;
import com.kebab.Selector;
import com.kebab.Tuple;
import java.util.List;

public class WifiMacAddress extends WifiBeacon {
    String _MacAddress;
    String _Name;

    public WifiMacAddress(String macAddress, String name) {
        this._MacAddress = macAddress;
        this._Name = name;
    }

    public int hashCode() {
        return this._MacAddress.hashCode();
    }

    public boolean equals(WifiMacAddress other) {
        return this._MacAddress.equals(other._MacAddress);
    }

    public boolean equals(Object other) {
        if (other != null && (other instanceof WifiMacAddress)) {
            return equals((WifiMacAddress) other);
        }
        return false;
    }

    public void ToColonSeparated(StringBuffer sb) {
        sb.append(Beacon.WIFI_MAC_ADDRESS).append(":").append(LlamaStorage.SimpleEscape(this._MacAddress) + (this._Name == null ? "" : "@" + LlamaStorage.SimpleEscape(this._Name)));
    }

    public static WifiMacAddress CreateFromColonSeparated(String data) {
        int atPos = data.indexOf(64);
        if (atPos <= 0) {
            return new WifiMacAddress(data.substring(Beacon.WIFI_MAC_ADDRESS.length() + 1), null);
        }
        return new WifiMacAddress(data.substring(Beacon.WIFI_MAC_ADDRESS.length() + 1, atPos), LlamaStorage.SimpleUnescape(data.substring(atPos + 1)));
    }

    public String toFormattedString() {
        return this._MacAddress + (this._Name == null ? "" : " (" + this._Name + ")");
    }

    public String getFriendlyTypeNamePlural() {
        return TYPE_WIFI_MAC_PLURAL;
    }

    public String getFriendlyTypeName() {
        return TYPE_WIFI_MAC_SINGLE;
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
        return Beacon.WIFI_MAC_ADDRESS;
    }
}
