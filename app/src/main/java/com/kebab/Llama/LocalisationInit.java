package com.kebab.Llama;

import android.content.Context;
import android.content.res.Configuration;
import com.kebab.DateHelpers;
import com.kebab.HelpersC;
import com.kebab.Llama.EventActions.VibrateAction;
import com.kebab.Llama.LlamaListTabBase.LlamaListTabBaseImpl;
import java.util.Locale;

public class LocalisationInit {
    public static boolean _Initted = false;

    public static void Init(Context context, boolean force) {
        ContextConfigInit(context);
        if (!_Initted || force) {
            EventMeta.Init(context);
            Beacon.InitLocalisation(context);
            BluetoothBeacon.InitLocalisation(context);
            VibrateAction.InitLocalisation(context);
            DateHelpers.InitLocalisation(context);
            AreasActivity._RandomTips = null;
            CellsActivity._RandomTips = null;
            ProfilesActivity._RandomTips = null;
            EventsActivity._RandomTips = null;
            LlamaListTabBaseImpl._CommonTips = null;
            _Initted = true;
        }
    }

    public static void ContextConfigInit(Context context) {
        String localisation = (String) LlamaSettings.LocaleOverride.GetValue(context);
        if (localisation != null && !localisation.equals("")) {
            Locale newLocale;
            Configuration oldConfig = context.getApplicationContext().getResources().getConfiguration();
            String[] parts = localisation.split("-r", -1);
            if (parts.length == 1) {
                newLocale = new Locale(parts[0]);
            } else {
                newLocale = new Locale(parts[0], parts[1]);
            }
            if (!HelpersC.StringEquals(oldConfig.locale.getLanguage(), newLocale.getLanguage()) || !HelpersC.StringEquals(oldConfig.locale.getCountry(), newLocale.getCountry())) {
                Locale.setDefault(newLocale);
                Configuration config = new Configuration();
                config.locale = newLocale;
                context.getApplicationContext().getResources().updateConfiguration(config, context.getApplicationContext().getResources().getDisplayMetrics());
            }
        }
    }
}
