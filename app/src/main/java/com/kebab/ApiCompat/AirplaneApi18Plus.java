package com.kebab.ApiCompat;

import android.content.Context;
import com.kebab.Llama.LlamaService;

public class AirplaneApi18Plus {
    static void SetAirplaneMode(boolean turnOn, Context context) {
        LlamaService.RunWithRoot("settings put global airplane_mode_on " + (turnOn ? 1 : 0), context, true);
        LlamaService.RunWithRoot("am broadcast -a android.intent.action.AIRPLANE_MODE -e llama.sender llama -e state " + (turnOn ? "true" : "false"), context, true);
    }
}
