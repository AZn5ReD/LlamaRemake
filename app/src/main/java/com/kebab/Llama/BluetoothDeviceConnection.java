package com.kebab.Llama;

import android.os.Handler;

public class BluetoothDeviceConnection {
    public String Address;
    public long LastConnectionTime;
    public String Name;
    public Handler SmoothingRecheckHandler;
    public boolean WasConnected;

    public BluetoothDeviceConnection(String name, String address, long lastConnectionTime, boolean wasConnected) {
        this.Name = name;
        this.Address = address;
        this.LastConnectionTime = lastConnectionTime;
        this.WasConnected = wasConnected;
    }

    public boolean equals(Object o) {
        return o != null && ((BluetoothDeviceConnection) o).Address.equals(this.Address);
    }

    public int hashCode() {
        return this.Address.hashCode();
    }

    public void ToPsv(StringBuffer sb) {
        sb.append(this.Address);
        sb.append("|");
        sb.append(this.LastConnectionTime);
        sb.append("|");
        sb.append(this.WasConnected ? "1" : "0");
        sb.append("|");
        sb.append(LlamaStorage.SimpleEscape(this.Name));
    }

    public static BluetoothDeviceConnection FromPsv(String s) {
        String[] parts = s.split("\\|", -1);
        if (parts.length < 2) {
            return null;
        }
        return new BluetoothDeviceConnection(parts.length > 3 ? LlamaStorage.SimpleUnescape(parts[3]) : "Unknown", parts[0], Long.parseLong(parts[1]), parts[2].equals("1"));
    }
}
