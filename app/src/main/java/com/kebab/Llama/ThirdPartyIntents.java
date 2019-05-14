package com.kebab.Llama;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import com.kebab.Tuple3;

public class ThirdPartyIntents {
    public static final int BROADCAST = 2;
    public static final int START_ACTIVITY = 0;
    public static final int START_SERVICE = 1;

    public static Tuple3<Integer, Integer, Intent>[] GetAll() {
        return new Tuple3[]{Tuple3.Create(Integer.valueOf(R.string.hrTpiWidgetLockerEnable), Integer.valueOf(1), new Intent().setAction("com.teslacoilsw.widgetlocker.ENABLE")), Tuple3.Create(Integer.valueOf(R.string.hrTpiWidgetLockerDisable), Integer.valueOf(1), new Intent().setAction("com.teslacoilsw.widgetlocker.DISABLE")), Tuple3.Create(Integer.valueOf(R.string.hrTpiWidgetLockerSuspend), Integer.valueOf(1), new Intent().setAction("com.teslacoilsw.widgetlocker.SUSPEND")), Tuple3.Create(Integer.valueOf(R.string.hrTpiWidgetLockerResume), Integer.valueOf(1), new Intent().setAction("com.teslacoilsw.widgetlocker.RESUME")), Tuple3.Create(Integer.valueOf(R.string.hrTpiWidgetLockerActivate), Integer.valueOf(0), new Intent().setAction("com.teslacoilsw.widgetlocker.ACTIVATE")), Tuple3.Create(Integer.valueOf(R.string.hrTpiWidgetLockerUnlock), Integer.valueOf(0), new Intent().setAction("com.teslacoilsw.widgetlocker.UNLOCK")), Tuple3.Create(Integer.valueOf(R.string.hrTpiBeautifulWidgetsRefresh), Integer.valueOf(2), new Intent().setAction("com.levelup.beautifulwidgets.ACTION_UPDATEWEATHER")), Tuple3.Create(Integer.valueOf(R.string.hrTpiAndroidStartHome), Integer.valueOf(0), new Intent().setAction("android.intent.action.MAIN").addCategory("android.intent.category.HOME")), Tuple3.Create(Integer.valueOf(R.string.hrTpiHoursBankStartStop), Integer.valueOf(2), new Intent().setAction("br.com.passeionaweb.android.hoursbank.widget.CLICK")), Tuple3.Create(Integer.valueOf(R.string.hrTpiDrivingModeWidgetOn), Integer.valueOf(2), new Intent().setAction("br.com.passeionaweb.android.hoursbank.widget.CLICK").setComponent(new ComponentName("com.arnab.drivingmode", "com.arnab.drivingmode.DWidget")).setAction("_drv_on")), Tuple3.Create(Integer.valueOf(R.string.hrTpiDrivingModeWidgetOff), Integer.valueOf(2), new Intent().setAction("br.com.passeionaweb.android.hoursbank.widget.CLICK").setComponent(new ComponentName("com.arnab.drivingmode", "com.arnab.drivingmode.DWidget")).setAction("_drv_off"))};
    }

    public static String[] GetNames(Context context, Tuple3<Integer, Integer, Intent>[] values, int prePad) {
        String[] result = new String[(values.length + prePad)];
        for (int i = 0; i < values.length; i++) {
            result[i + prePad] = context.getString(((Integer) values[i].Item1).intValue());
        }
        return result;
    }
}
