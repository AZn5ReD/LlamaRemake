package com.kebab.Llama;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import java.util.HashSet;

public class BluetoothDiscoverer {
    HashSet<BluetoothDevice> _Devices = new HashSet();
    OnDiscoveryCompletedListener _DiscoveryCompletedListener;

    public interface OnDiscoveryCompletedListener {
        void OnDiscoveryCompleted(Iterable<BluetoothDevice> iterable);
    }

    public BluetoothDiscoverer(OnDiscoveryCompletedListener discoveryCompletedListener) {
        this._DiscoveryCompletedListener = discoveryCompletedListener;
    }

    public void HandleDiscoveryStartedIntent(Intent intent) {
        this._Devices.clear();
    }

    public void HandleDiscoveryFinishedIntent(Intent intent) {
        if (this._DiscoveryCompletedListener != null) {
            this._DiscoveryCompletedListener.OnDiscoveryCompleted(this._Devices);
        }
    }

    public void HandleDiscoveredDeviceIntent(Intent intent) {
        this._Devices.add((BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE"));
    }
}
