package com.kebab;

import android.content.Context;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.Logging;
import java.lang.reflect.Method;

public class WimaxHelper {
    private static final String SERVICE_NAME = "wimax";
    private static final String SERVICE_NAME_ALT = "WiMax";
    public static final int WIMAX_DISABLED = 0;
    public static final int WIMAX_ENABLED = 1;
    public static final int WIMAX_INTERMEDIATE = 2;

    public static int GetWimaxStatus(LlamaService context) {
        try {
            Object wimaxManager = context.getSystemService(SERVICE_NAME);
            if (wimaxManager == null) {
                wimaxManager = context.getSystemService(SERVICE_NAME_ALT);
            }
            if (((Integer) wimaxManager.getClass().getMethod("getWimaxState", (Class[]) null).invoke(wimaxManager, (Object[]) null)).intValue() == 1) {
                return 1;
            }
            return 2;
        } catch (Exception e) {
            Logging.Report(e, (Context) context);
            context.HandleFriendlyError("Failed to get WiMax status", false);
            return 0;
        }
    }

    public static void ChangeWimax(LlamaService context, boolean newState) {
        try {
            Object wimaxManager = context.getSystemService(SERVICE_NAME);
            if (wimaxManager == null) {
                wimaxManager = context.getSystemService(SERVICE_NAME_ALT);
            }
            Method setWimaxEnabled = wimaxManager.getClass().getMethod("setWimaxEnabled", new Class[]{Boolean.TYPE});
            if (newState) {
                setWimaxEnabled.invoke(wimaxManager, new Object[]{Boolean.TRUE});
                return;
            }
            setWimaxEnabled.invoke(wimaxManager, new Object[]{Boolean.FALSE});
        } catch (Exception e) {
            Logging.Report(e, (Context) context);
            context.HandleFriendlyError("Failed to toggle WiMax", false);
        }
    }
}
