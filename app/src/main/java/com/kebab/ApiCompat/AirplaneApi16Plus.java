package com.kebab.ApiCompat;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings.Global;
import com.kebab.Llama.LlamaService;

public class AirplaneApi16Plus {
    static void SetAirplaneMode(boolean turnOn, Context context) {
        LlamaService.RunWithRoot("settings put global airplane_mode_on " + (turnOn ? 1 : 0), context, true);
        Intent intent = new Intent("android.intent.action.AIRPLANE_MODE");
        intent.putExtra("state", turnOn);
        intent.putExtra("llama.sender", "llama");
        context.sendBroadcast(intent);
    }

    public static boolean GetAirplaneMode(Context context) {
        try {
            return Global.getInt(context.getContentResolver(), "airplane_mode_on", 0) == 1;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
