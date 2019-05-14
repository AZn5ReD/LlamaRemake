package com.kebab.Llama;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.util.Log;
import com.kebab.HelpersC;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

public class LlamaStorage {
    public static final int STORAGE_AREAS = 1;
    public static final int STORAGE_EVENTS = 2;
    public static final int STORAGE_IGNOREDCELLS = 8;
    public static final int STORAGE_NFCNAMES = 16;
    public static final int STORAGE_PROFILES = 4;

    public HashMap<String, NfcFriendlyName> LoadNfcNames(Context context) {
        String[] nfcArray = context.getApplicationContext().getSharedPreferences("NFCNAMES", 0).getString("NFCNAMES", "").split("\n");
        HashMap<String, NfcFriendlyName> result = new HashMap(nfcArray.length);
        for (String s : nfcArray) {
            if (s.length() != 0) {
                NfcFriendlyName a = NfcFriendlyName.CreateFromPsv(s);
                result.put(a.HexString, a);
            }
        }
        return result;
    }

    public void SaveNfcNames(Context context, Iterable<NfcFriendlyName> nfcs) {
        Editor editor = context.getApplicationContext().getSharedPreferences("NFCNAMES", 0).edit();
        StringBuffer sb = new StringBuffer();
        for (NfcFriendlyName nfc : nfcs) {
            nfc.ToPsv(sb);
            sb.append("\n");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        editor.putString("NFCNAMES", sb.toString());
        HelpersC.CommitPrefs(editor, context);
    }

    public ArrayList<Area> LoadAreas(Context context) {
        String[] areaArray = context.getApplicationContext().getSharedPreferences("AREAS", 0).getString("AREAS", "").split("\n");
        ArrayList<Area> result = new ArrayList(areaArray.length);
        for (String s : areaArray) {
            if (s.length() != 0) {
                result.add(Area.CreateFromPsv(s));
            }
        }
        return result;
    }

    public void SaveAreas(Context context, Iterable<Area> areas) {
        Editor editor = context.getApplicationContext().getSharedPreferences("AREAS", 0).edit();
        StringBuffer sb = new StringBuffer();
        for (Area a : areas) {
            a.ToPsv(sb);
            sb.append("\n");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        editor.putString("AREAS", sb.toString());
        HelpersC.CommitPrefs(editor, context);
    }

    public ArrayList<Event> LoadEvents(Context context) {
        String[] eventArray = context.getApplicationContext().getSharedPreferences("EVENTS", 0).getString("EVENTS", "").split("\n");
        ArrayList<Event> result = new ArrayList(eventArray.length);
        for (String s : eventArray) {
            if (s.length() != 0) {
                result.add(Event.CreateFromPsv(s));
            }
        }
        return result;
    }

    public void SaveEvents(Context context, Iterable<Event> events) {
        Editor editor = context.getApplicationContext().getSharedPreferences("EVENTS", 0).edit();
        StringBuilder sb = new StringBuilder();
        for (Event e : events) {
            e.ToPsv(sb);
            sb.append("\n");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        editor.putString("EVENTS", sb.toString());
        HelpersC.CommitPrefs(editor, context);
    }

    public void SaveProfiles(Context context, Iterable<Profile> profiles) {
        Editor editor = context.getApplicationContext().getSharedPreferences("PROFILES", 0).edit();
        StringBuffer sb = new StringBuffer();
        for (Profile p : profiles) {
            p.ToPsv(sb);
            sb.append("\n");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        editor.putString("PROFILES", sb.toString());
        HelpersC.CommitPrefs(editor, context);
    }

    public ArrayList<Profile> LoadProfiles(Context context) {
        String[] profilesArray = context.getApplicationContext().getSharedPreferences("PROFILES", 0).getString("PROFILES", "").split("\n");
        ArrayList<Profile> result = new ArrayList(profilesArray.length);
        for (String s : profilesArray) {
            if (s.length() != 0) {
                result.add(Profile.CreateFromPsv(s));
            }
        }
        return result;
    }

    public boolean SaveSharedPrefsToSd(Context context, String debugTag) {
        File llamaDir = new File(Environment.getExternalStorageDirectory(), Constants.LLAMA_EXTERNAL_STORAGE_ROOT);
        Logging.Report("ImportExport", "Llama folder is: " + llamaDir.getAbsolutePath() + " fileTag: " + debugTag, context);
        try {
            llamaDir.mkdirs();
            String data = context.getApplicationContext().getSharedPreferences("AREAS", 0).getString("AREAS", "");
            Logging.Report("ImportExport", "Writing areas with " + data.length() + " chars", context);
            if (!WriteAll(new File(llamaDir, debugTag + "Llama_Areas.txt").getAbsolutePath(), data)) {
                return false;
            }
            data = context.getApplicationContext().getSharedPreferences("EVENTS", 0).getString("EVENTS", "");
            Logging.Report("ImportExport", "Writing events with " + data.length() + " chars", context);
            if (!WriteAll(new File(llamaDir, debugTag + "Llama_Events.txt").getAbsolutePath(), data)) {
                return false;
            }
            data = context.getApplicationContext().getSharedPreferences("PROFILES", 0).getString("PROFILES", "");
            Logging.Report("ImportExport", "Writing profiles with " + data.length() + " chars", context);
            if (!WriteAll(new File(llamaDir, debugTag + "Llama_Profiles.txt").getAbsolutePath(), data)) {
                return false;
            }
            data = context.getApplicationContext().getSharedPreferences("IGNOREDCELLS", 0).getString("IGNOREDCELLS", "");
            Logging.Report("ImportExport", "Writing ignoredCells with " + data.length() + " chars", context);
            if (!WriteAll(new File(llamaDir, debugTag + "Llama_IgnoredCells.txt").getAbsolutePath(), data)) {
                return false;
            }
            data = context.getApplicationContext().getSharedPreferences("NFCNAMES", 0).getString("NFCNAMES", "");
            Logging.Report("ImportExport", "Writing nfc names with " + data.length() + " chars", context);
            if (WriteAll(new File(llamaDir, debugTag + "Llama_NfcNames.txt").getAbsolutePath(), data)) {
                return true;
            }
            return false;
        } catch (SecurityException ex) {
            Logging.Report(ex, context, true);
            return false;
        }
    }

    public int LoadSharedPrefsFromSd(Context context, String debugTag) {
        Editor editor;
        File llamaDir = new File(Environment.getExternalStorageDirectory(), Constants.LLAMA_EXTERNAL_STORAGE_ROOT);
        String resultAreas = ReadAll(new File(llamaDir, debugTag + "Llama_Areas.txt").getAbsolutePath());
        String resultEvents = ReadAll(new File(llamaDir, debugTag + "Llama_Events.txt").getAbsolutePath());
        String resultProfiles = ReadAll(new File(llamaDir, debugTag + "Llama_Profiles.txt").getAbsolutePath());
        String resultIgnoredCells = ReadAll(new File(llamaDir, debugTag + "Llama_IgnoredCells.txt").getAbsolutePath());
        String resultNfcNames = ReadAll(new File(llamaDir, debugTag + "Llama_NfcNames.txt").getAbsolutePath());
        int result = 0;
        if (resultAreas != null) {
            editor = context.getApplicationContext().getSharedPreferences("AREAS", 0).edit();
            editor.putString("AREAS", resultAreas);
            editor.commit();
            result = 0 + 1;
        }
        if (resultEvents != null) {
            editor = context.getApplicationContext().getSharedPreferences("EVENTS", 0).edit();
            editor.putString("EVENTS", resultEvents);
            editor.commit();
            result += 2;
        }
        if (resultProfiles != null) {
            editor = context.getApplicationContext().getSharedPreferences("PROFILES", 0).edit();
            editor.putString("PROFILES", resultProfiles);
            editor.commit();
            result += 4;
        }
        if (resultIgnoredCells != null) {
            editor = context.getApplicationContext().getSharedPreferences("IGNOREDCELLS", 0).edit();
            editor.putString("IGNOREDCELLS", resultIgnoredCells);
            editor.commit();
            result += 8;
        }
        if (resultNfcNames == null) {
            return result;
        }
        editor = context.getApplicationContext().getSharedPreferences("NFCNAMES", 0).edit();
        editor.putString("NFCNAMES", resultNfcNames);
        editor.commit();
        return result + 16;
    }

    /* JADX WARNING: Removed duplicated region for block: B:39:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0036 A:{SYNTHETIC, Splitter:B:19:0x0036} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static boolean WriteAll(String path, String value) {
        Throwable ex;
        Throwable th;
        BufferedWriter bos = null;
        try {
            BufferedWriter bos2 = new BufferedWriter(new FileWriter(path));
            try {
                bos2.write(value);
                bos2.flush();
                bos2.close();
                bos = null;
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException ex2) {
                        Log.e(Constants.TAG, "Failed to close", ex2);
                        return false;
                    }
                }
                return true;
            } catch (IOException e) {
                ex = e;
                bos = bos2;
                try {
                    Log.e(Constants.TAG, "Failed to write all", ex);
                    Logging.Report(ex, null, true);
                } catch (Exception ex22) {
                    Log.e(Constants.TAG, "Failed to log", ex22);
                } catch (Throwable th2) {
                    th = th2;
                }
                if (bos != null) {
                }
            } catch (Throwable th3) {
                th = th3;
                bos = bos2;
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException ex23) {
                        Log.e(Constants.TAG, "Failed to close", ex23);
                        return false;
                    }
                }
                throw th;
            }
        } catch (IOException e2) {
            ex = e2;
            Log.e(Constants.TAG, "Failed to write all", ex);
            Logging.Report(ex, null, true);
            if (bos != null) {
                return false;
            }
            try {
                bos.close();
                return false;
            } catch (IOException ex232) {
                Log.e(Constants.TAG, "Failed to close", ex232);
                return false;
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:24:0x0038 A:{SYNTHETIC, Splitter:B:24:0x0038} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static String ReadAll(String path) {
        String stringBuffer;
        Throwable th;
        BufferedReader reader = null;
        try {
            BufferedReader reader2 = new BufferedReader(new FileReader(path));
            try {
                StringBuffer sb = new StringBuffer();
                char[] chars = new char[1024];
                while (true) {
                    int numRead = reader2.read(chars);
                    if (numRead <= -1) {
                        break;
                    }
                    sb.append(chars, 0, numRead);
                }
                stringBuffer = sb.toString();
                if (reader2 != null) {
                    try {
                        reader2.close();
                    } catch (Exception e) {
                    }
                }
                reader = reader2;
            } catch (Exception e2) {
                reader = reader2;
            } catch (Throwable th2) {
                th = th2;
                reader = reader2;
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception e3) {
                    }
                }
                throw th;
            }
        } catch (Exception e4) {
            stringBuffer = null;
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e5) {
                }
            }
            return stringBuffer;
        } catch (Throwable th3) {
            th = th3;
            if (reader != null) {
            }
            throw th;
        }
        return stringBuffer;
    }

    public static String SimpleEscape(String value) {
        if (value == null) {
            return "";
        }
        if (value.indexOf(Constants.MENU_VIEW_ON_MAP) >= 0 || value.indexOf(10) >= 0 || value.indexOf(45) >= 0 || value.indexOf(92) >= 0) {
            return value.replace("\\", "\\b").replace("|", "\\p").replace("-", "\\d").replace("\n", "\\n");
        }
        return value;
    }

    public static String SimpleEscapedPipe() {
        return "\\p";
    }

    public static String SimpleUnescape(String value) {
        if (value == null) {
            return "";
        }
        if (value.indexOf(92) >= 0) {
            return value.replace("\\p", "|").replace("\\n", "\n").replace("\\d", "-").replace("\\b", "\\");
        }
        return value;
    }

    public void LoadRecentInto(Context context, LinkedList<BeaconAndCalendar> recentCells) {
        for (String s : context.getApplicationContext().getSharedPreferences("RECENTCELLS", 0).getString("RECENTCELLS", "").split("\n")) {
            if (s.length() != 0) {
                recentCells.add(BeaconAndCalendar.CreateFromPsv(s));
            }
        }
    }

    public void SaveRecent(Context context, LinkedList<BeaconAndCalendar> recentCells) {
        Editor editor = context.getApplicationContext().getSharedPreferences("RECENTCELLS", 0).edit();
        StringBuffer sb = new StringBuffer();
        Iterator i$ = recentCells.iterator();
        while (i$.hasNext()) {
            ((BeaconAndCalendar) i$.next()).ToPsv(sb);
            sb.append("\n");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        editor.putString("RECENTCELLS", sb.toString());
        HelpersC.CommitPrefs(editor, context);
    }

    public Collection<Beacon> GetLastBeacons(Context context, String beaconTypeId) {
        ArrayList<Beacon> beacons = new ArrayList();
        for (String part : context.getApplicationContext().getSharedPreferences("LASTAREAS", 0).getString("LastBeacons" + beaconTypeId, "").split("\\|", -1)) {
            if (part.length() != 0) {
                beacons.add(Beacon.CreateFromColonSeparated(part));
            }
        }
        return beacons;
    }

    public void ResetLastBeacons(Context context) {
        Editor editor = context.getApplicationContext().getSharedPreferences("LASTAREAS", 0).edit();
        editor.putString("LastBeacons" + Beacon.BLUETOOTH, "");
        editor.putString("LastBeacons" + Beacon.WIFI_NAME, "");
        editor.putString("LastBeacons" + Beacon.EARTH_POINT, "");
        editor.putString("LastBeacons" + Beacon.CELL, "");
        editor.commit();
    }

    public void SetLastBeacons(Context context, Iterable<Beacon> bts, Iterable<Beacon> cells, Iterable<Beacon> earthPoints, Iterable<Beacon> wifis) {
        Editor editor = context.getApplicationContext().getSharedPreferences("LASTAREAS", 0).edit();
        SetLastAreasHelper(editor, cells, "LastBeacons" + Beacon.CELL);
        SetLastAreasHelper(editor, wifis, "LastBeacons" + Beacon.WIFI_NAME);
        SetLastAreasHelper(editor, bts, "LastBeacons" + Beacon.BLUETOOTH);
        SetLastAreasHelper(editor, earthPoints, "LastBeacons" + Beacon.EARTH_POINT);
        HelpersC.CommitPrefs(editor, context);
    }

    private void SetLastAreasHelper(Editor editor, Iterable<Beacon> values, String settingName) {
        StringBuffer sb = new StringBuffer();
        for (Beacon b : values) {
            sb.append(b.ToColonSeparated()).append("|");
        }
        editor.putString(settingName, sb.toString());
    }

    public void SaveEventHistory(Context context, LinkedList<EventHistory> eventHistory) {
        Editor editor = context.getApplicationContext().getSharedPreferences("EVENTHISTORY", 0).edit();
        StringBuffer sb = new StringBuffer();
        Iterator i$ = eventHistory.iterator();
        while (i$.hasNext()) {
            ((EventHistory) i$.next()).ToPsv(sb);
            sb.append("\n");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        editor.putString("EVENTHISTORY", sb.toString());
        HelpersC.CommitPrefs(editor, context);
    }

    public void LoadEventHistoryInto(Context context, LinkedList<EventHistory> eventHistory) {
        for (String s : context.getApplicationContext().getSharedPreferences("EVENTHISTORY", 0).getString("EVENTHISTORY", "").split("\n")) {
            if (s.length() != 0) {
                eventHistory.addLast(EventHistory.CreateFromPsv(s));
            }
        }
    }

    public void SaveConnectedBluetoothDevices(Context context, Enumeration<BluetoothDeviceConnection> connectedDevices) {
        Editor editor = context.getApplicationContext().getSharedPreferences("CONNECTEDBTDEVICES", 0).edit();
        StringBuffer sb = new StringBuffer();
        while (connectedDevices.hasMoreElements()) {
            ((BluetoothDeviceConnection) connectedDevices.nextElement()).ToPsv(sb);
            sb.append("\n");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        editor.putString("CONNECTEDBTDEVICES", sb.toString());
        HelpersC.CommitPrefs(editor, context);
    }

    public void LoadConnectedBluetoothDevicesInto(Context context, Hashtable<String, BluetoothDeviceConnection> connectedDevices) {
        String all = context.getApplicationContext().getSharedPreferences("CONNECTEDBTDEVICES", 0).getString("CONNECTEDBTDEVICES", "");
        Logging.Report("LlamaStorage", all, context);
        for (String s : all.split("\n")) {
            if (s.length() != 0) {
                BluetoothDeviceConnection connection = BluetoothDeviceConnection.FromPsv(s);
                if (connection != null) {
                    connectedDevices.put(connection.Address, connection);
                }
            }
        }
    }

    public static ArrayList<String> DeserializePsvStringArrayList(String data, boolean removeEmpties) {
        String[] stuff = data.split("\\|", -1);
        ArrayList<String> result = new ArrayList(stuff.length);
        if (removeEmpties) {
            for (String s : stuff) {
                if (s.length() > 0) {
                    result.add(SimpleUnescape(s));
                }
            }
        } else {
            for (String s2 : stuff) {
                result.add(SimpleUnescape(s2));
            }
        }
        return result;
    }

    public static void SerializePsvStringArrayList(StringBuilder sb, ArrayList<String> data, String prefix) {
        boolean first = true;
        Iterator i$ = data.iterator();
        while (i$.hasNext()) {
            String d = (String) i$.next();
            if (first) {
                first = false;
            } else {
                sb.append("|");
            }
            sb.append(SimpleEscape(prefix + d));
        }
    }

    public HashSet<Cell> LoadIgnoredCells(Context context) {
        String[] array = context.getApplicationContext().getSharedPreferences("IGNOREDCELLS", 0).getString("IGNOREDCELLS", "").split("\n");
        HashSet<Cell> cells = new HashSet();
        for (String s : array) {
            if (s.length() != 0) {
                cells.add(Cell.CreateFromColonSeparated(s.split(":", -1)));
            }
        }
        return cells;
    }

    public void SaveIgnoredCells(Context context, Iterable<Cell> ignoredCells) {
        Editor editor = context.getApplicationContext().getSharedPreferences("IGNOREDCELLS", 0).edit();
        StringBuffer sb = new StringBuffer();
        for (Cell c : ignoredCells) {
            if (!Cell.NoSignal.equals(c)) {
                c.ToColonSeparated(sb);
                sb.append("\n");
            }
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        editor.putString("IGNOREDCELLS", sb.toString());
        HelpersC.CommitPrefs(editor, context);
    }

    public void SaveVariables(Context context, HashMap<String, String> variables) {
        Editor editor = context.getApplicationContext().getSharedPreferences("VARIABLES", 0).edit();
        StringBuffer sb = new StringBuffer();
        for (Entry<String, String> entry : variables.entrySet()) {
            sb.append(SimpleEscape((String) entry.getKey()));
            sb.append("|");
            sb.append(SimpleEscape((String) entry.getValue()));
            sb.append("\n");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        editor.putString("VARIABLES", sb.toString());
        HelpersC.CommitPrefs(editor, context);
    }

    public void LoadVariables(Context context, HashMap<String, String> variables) {
        variables.clear();
        for (String s : context.getApplicationContext().getSharedPreferences("VARIABLES", 0).getString("VARIABLES", "").split("\n")) {
            if (s.length() != 0) {
                String[] parts = s.split("\\|", -1);
                variables.put(SimpleUnescape(parts[0]), SimpleUnescape(parts[1]));
            }
        }
    }

    public void LoadLlamaTones(Context context, HashMap<String, String> map) {
        map.clear();
        for (String s : context.getApplicationContext().getSharedPreferences("TONES", 0).getString("TONES", "").split("\n")) {
            if (s.length() != 0) {
                String[] parts = s.split("\\|", -1);
                map.put(SimpleUnescape(parts[0]), SimpleUnescape(parts[1]));
            }
        }
    }

    public void SaveLlamaTones(Context context, HashMap<String, String> map) {
        Editor editor = context.getApplicationContext().getSharedPreferences("TONES", 0).edit();
        StringBuffer sb = new StringBuffer();
        for (Entry<String, String> entry : map.entrySet()) {
            sb.append(SimpleEscape((String) entry.getKey()));
            sb.append("|");
            sb.append(SimpleEscape((String) entry.getValue()));
            sb.append("\n");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        editor.putString("TONES", sb.toString());
        HelpersC.CommitPrefs(editor, context);
    }
}
