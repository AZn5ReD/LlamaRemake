package com.kebab.ApiCompat;

import android.view.Display;

public class ScreenRotationApi8Plus {
    static int GetScreenRotation(Display display) {
        return display.getRotation();
    }
}
