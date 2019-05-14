package com.kebab.ApiCompat;

import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;
import com.kebab.Llama.Logging;

public class DeviceCompat {
    public static boolean HasStupidMenuButtonInBottomRight(Context context) {
        boolean isPadfone = Build.MODEL.equals("PadFone 2");
        Logging.Report("DeviceCompat", "MODEL=[" + Build.MODEL + "] Apilevel=" + VERSION.SDK_INT + " is it a PadFone 2=" + isPadfone, context);
        return isPadfone;
    }

    public static boolean IsPileOfShitWhenDealingWithVibrateMode(Context context) {
        String model = Build.MODEL;
        Logging.Report("s4compat", "Build.MODEL='" + model + "'", context);
        return model.equals("GT-I9500") || model.equals("SHV-E300K") || model.equals("SHV-E300L") || model.equals("SHV-E300S") || model.equals("GT-I9505") || model.equals("SGH-I337") || model.equals("SGH-M919") || model.equals("SCH-I545") || model.equals("SPH-L720") || model.equals("SCH-R970") || model.equals("GT-I9508") || model.equals("SCH-I959") || model.equals("GT-I9502") || model.equals("SGH-N045") || model.equals("SAMSUNG-SGH-I337");
    }

    public static boolean WeirdlyReportsInvalidCellIdsBeforeRealCellIds() {
        return Build.MODEL.equals("Nexus 4");
    }
}
