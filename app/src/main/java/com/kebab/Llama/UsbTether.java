package com.kebab.Llama;

import android.content.Context;
import android.net.ConnectivityManager;
import java.lang.reflect.Method;

public class UsbTether {
    public static final int TETHER_SUCCESS = 0;
    public static boolean WaitingForWifiToTurnOff;
    static boolean _TriedInit;
    static Method _getTetherState;
    static String _ifaceName;
    static Method _setTetherEnabled;

    public static void SetEnabled(LlamaService service, boolean enabled, boolean tryDisableWifi) {
        Init(service);
        ConnectivityManager cm = (ConnectivityManager) service.getSystemService("connectivity");
        try {
            if (((Integer) _setTetherEnabled.invoke(cm, new Object[]{_ifaceName})).intValue() != 0) {
            }
        } catch (Exception e) {
            Logging.Report(e, (Context) service);
        }
    }

    public static boolean IsEnabled(Context service) {
        Init(service);
        return false;
    }

    static void Init(Context context) {
        if (!_TriedInit) {
            _TriedInit = true;
            try {
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService("connectivity");
                Method[] wmMethods = cm.getClass().getDeclaredMethods();
                for (Method method : wmMethods) {
                    if (method.getName().equals("getTetherableIfaces")) {
                        try {
                            _ifaceName = ((String[]) method.invoke(cm, new Object[0]))[0];
                            break;
                        } catch (Exception e) {
                            Logging.Report(e, context);
                            return;
                        }
                    }
                }
                for (Method method2 : wmMethods) {
                    if (method2.getName().equals("tether")) {
                        _setTetherEnabled = method2;
                        return;
                    }
                }
            } catch (Exception ex) {
                Logging.Report(ex, context);
            }
        }
    }
}
