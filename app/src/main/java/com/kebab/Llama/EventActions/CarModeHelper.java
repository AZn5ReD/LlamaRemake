package com.kebab.Llama.EventActions;

import android.content.Context;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.Logging;
import com.kebab.Llama.R;
import java.lang.reflect.Method;

public class CarModeHelper {
    static final String UI_MODE_SERVICE = "uimode";
    static final int UI_MODE_TYPE_CAR = 3;
    static Method _IsEnabled;
    static Method _SetDisabled;
    static Method _SetEnabled;
    static boolean triedToGetMethods;

    public static boolean IsOn(LlamaService service) {
        if (initHelper(service)) {
            try {
                boolean z;
                if (((Integer) _IsEnabled.invoke(service.getSystemService(UI_MODE_SERVICE), new Object[0])).intValue() == 3) {
                    z = true;
                } else {
                    z = false;
                }
                return z;
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        service.HandleFriendlyError(service.getString(R.string.hrCarModeNotSupportedByPhone), false);
        return false;
    }

    public static void SetEnabled(LlamaService service, boolean enabled) {
        if (initHelper(service)) {
            Object uiservice = service.getSystemService(UI_MODE_SERVICE);
            if (enabled) {
                try {
                    _SetEnabled.invoke(uiservice, new Object[]{Integer.valueOf(0)});
                    return;
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
            _SetDisabled.invoke(uiservice, new Object[]{Integer.valueOf(0)});
            return;
        }
        service.HandleFriendlyError(service.getString(R.string.hrCarModeNotSupportedByPhone), false);
    }

    private static boolean initHelper(Context context) {
        if (!triedToGetMethods) {
            Object service = context.getSystemService(UI_MODE_SERVICE);
            if (service != null) {
                Class<?> uiClass = service.getClass();
                try {
                    _SetEnabled = uiClass.getMethod("enableCarMode", new Class[]{Integer.TYPE});
                    _SetDisabled = uiClass.getMethod("disableCarMode", new Class[]{Integer.TYPE});
                    _IsEnabled = uiClass.getMethod("getCurrentModeType", new Class[0]);
                } catch (Exception e) {
                    Logging.Report("CarMode", "Failed to reflect methods for car mode", context);
                }
                if (_SetEnabled == null || _SetDisabled == null || _IsEnabled == null) {
                    _IsEnabled = null;
                    _SetDisabled = null;
                    _SetEnabled = null;
                }
            }
            triedToGetMethods = true;
        }
        if (_SetEnabled != null) {
            return true;
        }
        return false;
    }
}
