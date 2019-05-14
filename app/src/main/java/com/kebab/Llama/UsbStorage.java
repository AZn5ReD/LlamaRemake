package com.kebab.Llama;

import java.lang.reflect.Method;

public class UsbStorage {
    Method _Disable;
    Method _Enable;
    boolean _InitTried;
    Method _IsEnabled;
    LlamaService _Service;
    Object _UsbService;

    public UsbStorage(LlamaService service) {
        this._Service = service;
    }

    /* Access modifiers changed, original: 0000 */
    public boolean init() {
        if (this._InitTried) {
            return this._Enable != null;
        } else {
            this._InitTried = true;
            try {
                this._UsbService = this._Service.getSystemService("storage");
                Class<?> c = this._UsbService.getClass();
                this._Enable = c.getMethod("enableUsbMassStorage", new Class[0]);
                this._Disable = c.getMethod("disableUsbMassStorage", new Class[0]);
                this._IsEnabled = c.getMethod("isUsbMassStorageEnabled", new Class[0]);
                return true;
            } catch (Exception ex) {
                report(ex);
                return false;
            }
        }
    }

    private void report(Exception ex) {
        Logging.Report((Throwable) ex, this._Service);
        this._Service.HandleFriendlyError(this._Service.getString(R.string.hrUsbToggleError), false);
    }

    public void ToggleUsb(boolean turnOn) {
        if (!init()) {
            return;
        }
        if (turnOn) {
            try {
                this._Enable.invoke(this._UsbService, new Object[0]);
                return;
            } catch (Exception ex) {
                report(ex);
                return;
            }
        }
        this._Disable.invoke(this._UsbService, new Object[0]);
    }

    public void ToggleUsbOnOff() {
        if (init()) {
            try {
                boolean z;
                if (((Boolean) this._IsEnabled.invoke(this._UsbService, new Object[0])).booleanValue()) {
                    z = false;
                } else {
                    z = true;
                }
                ToggleUsb(z);
            } catch (Exception ex) {
                report(ex);
            }
        }
    }
}
