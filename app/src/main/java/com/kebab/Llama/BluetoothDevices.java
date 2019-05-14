package com.kebab.Llama;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import com.kebab.IterableHelpers;
import com.kebab.Tuple;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

public class BluetoothDevices {
    public static final String BT_TOAST_CONSTANT = "BLUETOOTHDEVICES";
    final int CONNECTION_SMOOTH_MILLIS = 3000;
    Hashtable<String, BluetoothDeviceConnection> _ConnectedDevices;
    LlamaService _Owner;

    public BluetoothDevices(LlamaService owner) {
        this._Owner = owner;
    }

    private void initDeviceList() {
        if (this._ConnectedDevices == null) {
            this._ConnectedDevices = new Hashtable();
            this._Owner._Storage.LoadConnectedBluetoothDevicesInto(this._Owner, this._ConnectedDevices);
        }
    }

    public void OnConnected(Intent intent) {
        initDeviceList();
        BluetoothDevice dev = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
        String deviceName = dev.getName();
        if (deviceName == null) {
            deviceName = BluetoothBeacon.UNKNOWN;
        }
        String deviceAddress = dev.getAddress();
        Logging.Report(BT_TOAST_CONSTANT, "BT RAW: " + deviceAddress + " connected", this._Owner);
        FilterDeviceConnectionDisconnection(deviceName, deviceAddress, true);
    }

    public void OnDisconnected(Intent intent) {
        initDeviceList();
        BluetoothDevice dev = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
        String deviceName = dev.getName();
        if (deviceName == null) {
            deviceName = BluetoothBeacon.UNKNOWN;
        }
        String deviceAddress = dev.getAddress();
        Logging.Report(BT_TOAST_CONSTANT, "BT RAW: " + deviceAddress + " disconnected", this._Owner);
        FilterDeviceConnectionDisconnection(deviceName, deviceAddress, false);
    }

    private void FilterDeviceConnectionDisconnection(final String deviceName, String deviceAddress, final boolean isConnection) {
        final BluetoothDeviceConnection existingConnection = (BluetoothDeviceConnection) this._ConnectedDevices.get(deviceAddress);
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (existingConnection == null) {
            Logging.Report(BT_TOAST_CONSTANT, "First " + (isConnection ? "connection" : "disconnection") + " from " + deviceAddress, this._Owner);
            BluetoothDeviceConnection newItem = new BluetoothDeviceConnection(deviceName, deviceAddress, currentTime, isConnection);
            this._ConnectedDevices.put(newItem.Address, newItem);
            if (isConnection) {
                DeviceFilteredAndConnectedDisconnected(newItem, isConnection, deviceName);
            }
        } else if (existingConnection.WasConnected == isConnection) {
            Logging.Report(BT_TOAST_CONSTANT, deviceAddress + " already " + (isConnection ? "connected" : "disconnected"), this._Owner);
        } else if (currentTime - existingConnection.LastConnectionTime > 3000) {
            DeviceFilteredAndConnectedDisconnected(existingConnection, isConnection, deviceName);
        } else if (existingConnection.SmoothingRecheckHandler == null) {
            existingConnection.SmoothingRecheckHandler = new Handler();
            existingConnection.SmoothingRecheckHandler.postDelayed(new Runnable() {
                public void run() {
                    Logging.Report(BluetoothDevices.BT_TOAST_CONSTANT, "Delayed check for " + existingConnection.Address + ". Delayed state=" + (isConnection ? "connected" : "disconnected") + ", last seen state=" + existingConnection.WasConnected, BluetoothDevices.this._Owner);
                    if (existingConnection.WasConnected == isConnection) {
                        BluetoothDevices.this.DeviceFilteredAndConnectedDisconnected(existingConnection, isConnection, deviceName);
                    }
                    existingConnection.SmoothingRecheckHandler = null;
                }
            }, 3000);
        }
    }

    private void DeviceFilteredAndConnectedDisconnected(BluetoothDeviceConnection existingConnection, boolean isConnection, String updatedName) {
        Logging.Report(BT_TOAST_CONSTANT, "BTFILTERED: " + (isConnection ? "connected " : "disconnected ") + existingConnection.Address, this._Owner);
        existingConnection.WasConnected = isConnection;
        existingConnection.Name = updatedName;
        this._Owner._Storage.SaveConnectedBluetoothDevices(this._Owner, this._ConnectedDevices.elements());
        this._Owner.OnBluetoothDevice(existingConnection.Address, isConnection);
    }

    public boolean IsAnyDeviceConnected() {
        initDeviceList();
        for (BluetoothDeviceConnection bcd : this._ConnectedDevices.values()) {
            if (bcd.WasConnected) {
                return true;
            }
        }
        return false;
    }

    public List<String> GetConnectedDevice() {
        initDeviceList();
        ArrayList<String> connectedDevices = new ArrayList();
        Enumeration<BluetoothDeviceConnection> enummer = this._ConnectedDevices.elements();
        while (enummer.hasMoreElements()) {
            BluetoothDeviceConnection bcd = (BluetoothDeviceConnection) enummer.nextElement();
            if (bcd.WasConnected) {
                connectedDevices.add(bcd.Address);
            }
        }
        return connectedDevices;
    }

    public List<Tuple<String, String>> GetConnectedBluetoothDevices() {
        initDeviceList();
        ArrayList<Tuple<String, String>> connectedDevices = new ArrayList(this._ConnectedDevices.size());
        Enumeration<BluetoothDeviceConnection> enummer = this._ConnectedDevices.elements();
        while (enummer.hasMoreElements()) {
            BluetoothDeviceConnection bcd = (BluetoothDeviceConnection) enummer.nextElement();
            if (bcd.WasConnected) {
                connectedDevices.add(new Tuple(bcd.Name, bcd.Address));
            }
        }
        return connectedDevices;
    }

    public void OnBluetoothStateChange(Intent intent) {
        if (intent.getIntExtra("android.bluetooth.adapter.extra.STATE", -1) == 10) {
            initDeviceList();
            ArrayList<BluetoothDeviceConnection> oldDevices = IterableHelpers.ToArrayList(this._ConnectedDevices.values());
            this._ConnectedDevices.clear();
            this._Owner._Storage.SaveConnectedBluetoothDevices(this._Owner, this._ConnectedDevices.elements());
            Iterator i$ = oldDevices.iterator();
            while (i$.hasNext()) {
                BluetoothDeviceConnection device = (BluetoothDeviceConnection) i$.next();
                if (device.WasConnected) {
                    Logging.Report(BT_TOAST_CONSTANT, "BTFORCED: disconnected " + device.Address, this._Owner);
                    this._Owner.OnBluetoothDevice(device.Address, false);
                }
            }
        }
    }
}
