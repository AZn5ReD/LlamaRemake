package com.kebab;

import android.content.Context;
import com.kebab.Llama.Logging;
import java.lang.reflect.Method;

public class UiModeManagerCompat {
    public static String ACTION_ENTER_CAR_MODE = "android.app.action.ENTER_CAR_MODE";
    public static String ACTION_ENTER_DESK_MODE = "android.app.action.ENTER_DESK_MODE";
    public static String ACTION_EXIT_CAR_MODE = "android.app.action.EXIT_CAR_MODE";
    public static String ACTION_EXIT_DESK_MODE = "android.app.action.EXIT_DESK_MODE";
    public static final int UI_MODE_TYPE_CAR = 3;
    public static final int UI_MODE_TYPE_DESK = 2;
    static boolean failed;
    static Method getCurrentModeTypeMethod;

    public static Integer GetDockMode(Context context) {
        Object uiModeService = context.getSystemService("uimode");
        if (failed || uiModeService == null) {
            return null;
        }
        if (getCurrentModeTypeMethod == null) {
            try {
                getCurrentModeTypeMethod = uiModeService.getClass().getMethod("getCurrentModeType", new Class[0]);
            } catch (Exception e) {
                Logging.Report(e, context);
                failed = true;
            }
        }
        try {
            return (Integer) getCurrentModeTypeMethod.invoke(uiModeService, new Object[0]);
        } catch (Exception e2) {
            Logging.Report(e2, context);
            failed = true;
            return null;
        }
    }
}
