package com.kebab.ApiCompat;

import android.os.Build.VERSION;
import android.view.Display;

public class ScreenRotationCompat {
    public static boolean IsSupportedProperly() {
        return VERSION.SDK_INT >= 8;
    }

    public static int GetScreenRotation(Display display) {
        if (IsSupportedProperly()) {
            return GetScreenRotationProperly(display);
        }
        return display.getOrientation();
    }

    private static int GetScreenRotationProperly(Display display) {
        return ScreenRotationApi8Plus.GetScreenRotation(display);
    }
}
