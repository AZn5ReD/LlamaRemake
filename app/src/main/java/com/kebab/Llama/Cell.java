package com.kebab.Llama;

import android.content.Context;
import com.kebab.IterableHelpers;
import com.kebab.Selector;
import com.kebab.Tuple;
import java.util.List;

public class Cell extends Beacon {
    public static final Cell BadData = new Cell(-6, (short) -6, (short) -6);
    public static final int CELL_ID_BAD_DATA = -6;
    public static final int CELL_ID_LOW_MEM = -3;
    public static final int CELL_ID_NO_SIGNAL = -1;
    public static final int CELL_ID_PHONE_SHUTDOWN = -5;
    public static final int CELL_ID_PROXIMITY_REQUEST = -9;
    public static final int CELL_ID_PROXIMITY_TIMEOUT = -10;
    public static final int CELL_ID_REBIRTH = -2;
    public static final int CELL_ID_SERVICE_CREATED = -4;
    public static final int CELL_ID_WIFI_EMPTY = -7;
    public static final int CELL_ID_WIFI_OFF = -13;
    public static final int CELL_ID_WIFI_ON = -12;
    public static final int CELL_ID_WIFI_SCAN_FAIL = -15;
    public static final int CELL_ID_WIFI_SCAN_RESULTS = -11;
    public static final int CELL_ID_WIFI_SCAN_START = -14;
    public static final int CELL_ID_WIFI_STILL_EMPTY = -8;
    public static final Cell LowMem = new Cell(-3, (short) -3, (short) -3);
    public static final Cell NoSignal = new Cell(-1, (short) -1, (short) -1);
    public static final Cell PhoneShutdown = new Cell(-5, (short) -5, (short) -5);
    public static final Cell ProximityRequest = new Cell(-9, (short) -9, (short) -9);
    public static final Cell ProximityTimeout = new Cell(-10, (short) -10, (short) -10);
    public static final Cell Rebirth = new Cell(-2, (short) -2, (short) -2);
    public static final Cell ServiceCreated = new Cell(-4, (short) -4, (short) -4);
    public static boolean StrictEquality = true;
    public static final Cell WifiEmpty = new Cell(-7, (short) -7, (short) -7);
    public static final Cell WifiOff = new Cell(-13, (short) -13, (short) -13);
    public static final Cell WifiOn = new Cell(-12, (short) -12, (short) -12);
    public static final Cell WifiScanFail = new Cell(-15, (short) -15, (short) -15);
    public static final Cell WifiScanResults = new Cell(-11, (short) -11, (short) -11);
    public static final Cell WifiScanStart = new Cell(-14, (short) -14, (short) -14);
    public static final Cell WifiStillEmpty = new Cell(-8, (short) -8, (short) -8);
    public int CellId;
    public short Mcc;
    public short Mnc;

    public Cell(int cellId, short mcc, short mnc) {
        short s = (short) -1;
        this.CellId = cellId;
        if (cellId == -1) {
            mnc = (short) -1;
        }
        this.Mnc = mnc;
        if (cellId != -1) {
            s = mcc;
        }
        this.Mcc = s;
    }

    public int hashCode() {
        return StrictEquality ? (this.CellId ^ this.Mnc) ^ (this.Mcc << 16) : this.CellId;
    }

    public boolean equals(Cell other) {
        boolean z = true;
        if (other == null) {
            return false;
        }
        if (StrictEquality) {
            if (!(this.CellId == other.CellId && this.Mnc == other.Mnc && this.Mcc == other.Mcc)) {
                z = false;
            }
        } else if (this.CellId != other.CellId) {
            z = false;
        }
        return z;
    }

    public boolean equals(Object other) {
        if (other != null && (other instanceof Cell)) {
            return equals((Cell) other);
        }
        return false;
    }

    public boolean IsNoSignal() {
        return this.CellId == -1;
    }

    public void ToColonSeparated(StringBuffer sb) {
        sb.append(this.CellId).append(":").append(this.Mcc).append(":").append(this.Mnc);
    }

    public static Cell CreateFromColonSeparated(String[] cellParts) {
        return new Cell(Integer.parseInt(cellParts[0]), Short.parseShort(cellParts[1]), Short.parseShort(cellParts[2]));
    }

    public String toFormattedString() {
        return ToColonSeparated();
    }

    public String getFriendlyTypeName() {
        return TYPE_CELL_SINGLE;
    }

    public String getFriendlyTypeNamePlural() {
        return TYPE_CELL_PLURAL;
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

    public boolean CanSimpleDetectArea() {
        return true;
    }

    public String GetTypeId() {
        return Beacon.CELL;
    }

    public boolean IsMapBased() {
        return false;
    }

    public static String GetDebugCellDescription(Cell cell, Context context) {
        switch (cell.CellId) {
            case CELL_ID_WIFI_SCAN_FAIL /*-15*/:
                return "DEBUG: Wifi scan fail";
            case CELL_ID_WIFI_SCAN_START /*-14*/:
                return "DEBUG: Wifi scan start";
            case CELL_ID_WIFI_OFF /*-13*/:
                return "DEBUG: Wifi off";
            case CELL_ID_WIFI_ON /*-12*/:
                return "DEBUG: Wifi on";
            case CELL_ID_WIFI_SCAN_RESULTS /*-11*/:
                return "DEBUG: Wifi scan results";
            case CELL_ID_PROXIMITY_TIMEOUT /*-10*/:
                return "DEBUG: Cell poll proximity timeout";
            case CELL_ID_PROXIMITY_REQUEST /*-9*/:
                return "DEBUG: Cell poll proximity request";
            case CELL_ID_WIFI_STILL_EMPTY /*-8*/:
                return "DEBUG: Wifi Poll still empty";
            case CELL_ID_WIFI_EMPTY /*-7*/:
                return "DEBUG: Wifi Poll empty";
            case CELL_ID_BAD_DATA /*-6*/:
                return "DEBUG: Bad Data :(";
            case CELL_ID_PHONE_SHUTDOWN /*-5*/:
                return "DEBUG: Phone shutdown";
            case CELL_ID_SERVICE_CREATED /*-4*/:
                return "DEBUG: Service created";
            case CELL_ID_LOW_MEM /*-3*/:
                return "DEBUG: Low memory";
            case CELL_ID_REBIRTH /*-2*/:
                return "DEBUG: Service recreated";
            case -1:
                return context.getString(R.string.hrNoSignalUnknown);
            default:
                return null;
        }
    }
}
