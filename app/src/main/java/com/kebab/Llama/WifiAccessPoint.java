package com.kebab.Llama;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import java.lang.reflect.Method;

public class WifiAccessPoint {
    public static final int WIFI_AP_STATE_DISABLED = 1;
    public static final int WIFI_AP_STATE_DISABLING = 0;
    public static final int WIFI_AP_STATE_ENABLED = 3;
    public static final int WIFI_AP_STATE_ENABLING = 2;
    public static final int WIFI_AP_STATE_FAILED = 4;
    public static boolean WaitingForWifiToTurnOff;
    static boolean _TriedInit;
    static Method _getWifiApState;
    static Method _setWifiApEnabled;

    public static void SetEnabled(LlamaService service, boolean enabled, boolean tryDisableWifi) {
        Init(service);
        try {
            boolean wifiAlreadyEnabled;
            WifiManager wifi = (WifiManager) service.getSystemService("wifi");
            if (tryDisableWifi && enabled) {
                wifiAlreadyEnabled = wifi.isWifiEnabled();
            } else {
                wifiAlreadyEnabled = false;
            }
            Logging.Report("WifiAp", "Wifi is enabled=" + wifiAlreadyEnabled, (Context) service);
            if (wifiAlreadyEnabled && tryDisableWifi) {
                Logging.Report("WifiAp", "We're allowed to disable wifi, disabling then waiting", (Context) service);
                wifi.setWifiEnabled(false);
                WaitingForWifiToTurnOff = true;
                return;
            }
            WaitingForWifiToTurnOff = false;
            Object[] args = new Object[]{null, Boolean.valueOf(enabled)};
            Logging.Report("WifiAp", "Setting state to " + enabled, (Context) service);
            _setWifiApEnabled.invoke(wifi, args);
        } catch (Exception ex) {
            Logging.Report(ex, (Context) service);
            service.HandleFriendlyError(service.getString(R.string.hrWifiAccessPointMayNotBeSupported), false);
        }
    }

    public static boolean IsEnabled(Context service) {
        Init(service);
        if (_getWifiApState == null) {
            return false;
        }
        try {
            int a = ((Integer) _getWifiApState.invoke((WifiManager) service.getSystemService("wifi"), new Object[0])).intValue();
            Logging.Report("WifiAp", "State was " + a, service);
            return a != 1;
        } catch (Exception ex) {
            Logging.Report(ex, service);
            return false;
        }
    }

    static void Init(Context context) {
        if (!_TriedInit) {
            try {
                WifiManager wifi = (WifiManager) context.getSystemService("wifi");
                _setWifiApEnabled = wifi.getClass().getMethod("setWifiApEnabled", new Class[]{WifiConfiguration.class, Boolean.TYPE});
                _getWifiApState = wifi.getClass().getMethod("getWifiApState", new Class[0]);
            } catch (Exception ex) {
                Logging.Report(ex, context);
            }
            _TriedInit = true;
        }
    }
}
