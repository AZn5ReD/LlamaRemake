package com.kebab.Llama;

import android.content.Context;
import com.kebab.Tuple;
import java.util.Hashtable;
import java.util.List;

public abstract class Beacon {
    public static String BLUETOOTH = "B";
    public static String CELL = "C";
    public static String EARTH_POINT = "P";
    static Hashtable<String, String> PluralNameLookup;
    public static String TYPE_BLUETOOTH_PLURAL;
    public static String TYPE_BLUETOOTH_SINGLE;
    public static String TYPE_CELL_PLURAL;
    public static String TYPE_CELL_SINGLE;
    public static String TYPE_EARTH_POINT_PLURAL;
    public static String TYPE_EARTH_POINT_SINGLE;
    public static String TYPE_WIFI_MAC_PLURAL;
    public static String TYPE_WIFI_MAC_SINGLE;
    public static String TYPE_WIFI_NAME_PLURAL;
    public static String TYPE_WIFI_NAME_SINGLE;
    public static String WIFI_MAC_ADDRESS = "M";
    public static String WIFI_NAME = "W";

    public abstract boolean CanSimpleDetectArea();

    public abstract List<String> GetAreaNames(LlamaService llamaService);

    public abstract List<Tuple<String, String>> GetAreaNamesWithInfo(LlamaService llamaService);

    public abstract String GetTypeId();

    public abstract boolean IsMapBased();

    public abstract void ToColonSeparated(StringBuffer stringBuffer);

    public abstract String getFriendlyTypeName();

    public abstract String getFriendlyTypeNamePlural();

    public abstract String toFormattedString();

    public static void InitLocalisation(Context context) {
        TYPE_EARTH_POINT_SINGLE = context.getString(R.string.hrMapPoint);
        TYPE_EARTH_POINT_PLURAL = context.getString(R.string.hrMapPoints);
        TYPE_CELL_SINGLE = context.getString(R.string.hrCell);
        TYPE_CELL_PLURAL = context.getString(R.string.hrCells);
        TYPE_WIFI_NAME_SINGLE = context.getString(R.string.hrWifiName);
        TYPE_WIFI_NAME_PLURAL = context.getString(R.string.hrWifiNames);
        TYPE_WIFI_MAC_SINGLE = context.getString(R.string.hrWifiMac);
        TYPE_WIFI_MAC_PLURAL = context.getString(R.string.hrWifiMacs);
        TYPE_BLUETOOTH_SINGLE = context.getString(R.string.hrBluetoothDevice);
        TYPE_BLUETOOTH_PLURAL = context.getString(R.string.hrBluetoothDevices);
        PluralNameLookup = new Hashtable();
        PluralNameLookup.put(TYPE_EARTH_POINT_SINGLE, TYPE_EARTH_POINT_PLURAL);
        PluralNameLookup.put(TYPE_CELL_SINGLE, TYPE_CELL_PLURAL);
        PluralNameLookup.put(TYPE_WIFI_NAME_SINGLE, TYPE_WIFI_NAME_PLURAL);
        PluralNameLookup.put(TYPE_WIFI_MAC_SINGLE, TYPE_WIFI_MAC_PLURAL);
    }

    public String ToColonSeparated() {
        StringBuffer sb = new StringBuffer();
        ToColonSeparated(sb);
        return sb.toString();
    }

    public static Beacon CreateFromColonSeparated(String colonSeparatedValue) {
        String[] parts = colonSeparatedValue.split(":");
        if (EARTH_POINT.equals(parts[0])) {
            return EarthPoint.CreateFromColonSeparated(parts);
        }
        if (WIFI_NAME.equals(parts[0])) {
            return new WifiNamedNetwork(SimpleColonUnescape(parts.length == 1 ? "" : parts[1]));
        } else if (WIFI_MAC_ADDRESS.equals(parts[0])) {
            return WifiMacAddress.CreateFromColonSeparated(colonSeparatedValue);
        } else {
            if (BLUETOOTH.equals(parts[0])) {
                return BluetoothBeacon.CreateFromColonSeparated(parts);
            }
            if (parts.length == 3) {
                return Cell.CreateFromColonSeparated(parts);
            }
            return Cell.BadData;
        }
    }

    public static String GetSingleOrPluralName(String name, int count) {
        return count == 1 ? name : (String) PluralNameLookup.get(name);
    }

    protected static String SimpleColonEscape(String value) {
        if (value == null) {
            return "";
        }
        if (value.indexOf(58) >= 0 || value.indexOf(10) >= 0 || value.indexOf(Constants.MENU_VIEW_ON_MAP) >= 0 || value.indexOf(92) >= 0) {
            return value.replace("\\", "\\b").replace(":", "\\c").replace("|", "\\p").replace("\n", "\\n");
        }
        return value;
    }

    protected static String SimpleColonUnescape(String value) {
        if (value == null) {
            return "";
        }
        if (value.indexOf(92) >= 0) {
            return value.replace("\\c", ":").replace("\\n", "\n").replace("\\p", "|").replace("\\b", "\\");
        }
        return value;
    }
}
