package com.kebab.ApiCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.provider.Settings.System;

public class AirplaneCompat {
    public static boolean IsSecureSystemSetting() {
        return VERSION.SDK_INT >= 17;
    }

    public static boolean IsBroadcastSecured() {
        return VERSION.SDK_INT >= 18;
    }

    public static boolean GetAirplaneMode(Context context) {
        if (IsSecureSystemSetting()) {
            return GetAirplaneModeInternal(context);
        }
        if (System.getInt(context.getContentResolver(), "airplane_mode_on", 0) != 1) {
            return false;
        }
        return true;
    }

    public static void SetAirplaneMode(Context context, boolean turnOn) {
        if (!IsSecureSystemSetting()) {
            System.putInt(context.getContentResolver(), "airplane_mode_on", turnOn ? 1 : 0);
            Intent intent = new Intent("android.intent.action.AIRPLANE_MODE");
            intent.putExtra("state", turnOn);
            intent.putExtra("llama.sender", "llama");
            context.sendBroadcast(intent);
        } else if (IsBroadcastSecured()) {
            AirplaneApi18Plus.SetAirplaneMode(turnOn, context);
        } else {
            AirplaneApi16Plus.SetAirplaneMode(turnOn, context);
        }
    }

    private static boolean GetAirplaneModeInternal(Context context) {
        return AirplaneApi16Plus.GetAirplaneMode(context);
    }
}
