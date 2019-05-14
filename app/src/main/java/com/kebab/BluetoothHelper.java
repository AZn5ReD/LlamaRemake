package com.kebab;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Handler;
import com.kebab.Llama.BluetoothEnableWakeLock;
import com.kebab.Llama.Logging;

public class BluetoothHelper {
    public static final int BLUETOOTH_FAILED = 2;
    public static final int BLUETOOTH_SUCCESS = 1;
    public static final int NO_BLUETOOTH = 0;

    public static int ToggleBluetooth(Context context, boolean turnOn) {
        BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
        final Context appContext = context.getApplicationContext();
        if (mAdapter == null) {
            return 0;
        }
        if (!turnOn) {
            mAdapter.disable();
        } else if (!mAdapter.isEnabled()) {
            if (!mAdapter.enable()) {
                return 2;
            }
            if (!mAdapter.isEnabled()) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        if (BluetoothEnableWakeLock.IsAcquired(appContext)) {
                            Logging.Report("Bluetooth wake lock was still acquired. Bluetooth should have turned on by now :'(", appContext);
                        }
                        BluetoothEnableWakeLock.ReleaseLock(appContext);
                    }
                }, 60000);
                BluetoothEnableWakeLock.AcquireLock(context, "Turning on Bluetooth");
            }
        }
        return 1;
    }
}
