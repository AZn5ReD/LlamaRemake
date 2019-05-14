package com.kebab.ApiCompat;

import android.net.wifi.WifiInfo;

public class WifiCompat {
    public static String GetWifiName(WifiInfo __WifiInfo) {
        String probablyBrokenSsidCosAndroidIsStupidSometimes = __WifiInfo.getSSID();
        if (probablyBrokenSsidCosAndroidIsStupidSometimes == null || probablyBrokenSsidCosAndroidIsStupidSometimes.length() < 2) {
            return probablyBrokenSsidCosAndroidIsStupidSometimes;
        }
        if (probablyBrokenSsidCosAndroidIsStupidSometimes.charAt(0) == '\"' && probablyBrokenSsidCosAndroidIsStupidSometimes.charAt(probablyBrokenSsidCosAndroidIsStupidSometimes.length() - 1) == '\"') {
            return probablyBrokenSsidCosAndroidIsStupidSometimes.substring(1, probablyBrokenSsidCosAndroidIsStupidSometimes.length() - 1);
        }
        return probablyBrokenSsidCosAndroidIsStupidSometimes;
    }
}
