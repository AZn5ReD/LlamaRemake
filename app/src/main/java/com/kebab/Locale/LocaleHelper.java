package com.kebab.Locale;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import com.kebab.Llama.R;
import com.kebab.ResultRegisterableActivity;
import com.kebab.ResultRegisterableActivity.ResultCallback;
import java.util.ArrayList;
import java.util.List;

public class LocaleHelper {
    public static final String ACTION_EDIT_SETTING = "com.twofortyfouram.locale.intent.action.EDIT_SETTING";
    public static final String ACTION_FIRE_SETTING = "com.twofortyfouram.locale.intent.action.FIRE_SETTING";
    public static final String EXTRA_BUNDLE = "com.twofortyfouram.locale.intent.extra.BUNDLE";
    public static final String EXTRA_STRING_BLURB = "com.twofortyfouram.locale.intent.extra.BLURB";
    public static final String EXTRA_STRING_BREADCRUMB = "com.twofortyfouram.locale.intent.extra.BREADCRUMB";
    Context _Context;

    public LocaleHelper(Context context) {
        this._Context = context.getApplicationContext();
    }

    public List<LocaleActionPlugin> GetAllPlugins() {
        List<ResolveInfo> actionEditors = this._Context.getPackageManager().queryIntentActivities(new Intent(ACTION_EDIT_SETTING), 0);
        List<LocaleActionPlugin> names = new ArrayList();
        for (ResolveInfo r : actionEditors) {
            String packageName;
            String activityName;
            String label = (String) r.loadLabel(this._Context.getPackageManager());
            if (r.activityInfo != null) {
                packageName = r.activityInfo.packageName;
            } else {
                packageName = null;
            }
            if (r.activityInfo != null) {
                activityName = r.activityInfo.name;
            } else {
                activityName = null;
            }
            if (!(label == null || packageName == null || activityName == null)) {
                names.add(new LocaleActionPlugin(label, packageName, activityName));
            }
        }
        return names;
    }

    public void StartSettingsActivity(ResultRegisterableActivity activity, LocaleActionPlugin plugin, Bundle bundle, ResultCallback resultCallback) {
        Intent intent = new Intent(ACTION_EDIT_SETTING);
        intent.setComponent(new ComponentName(plugin.PackageName, plugin.ActivityName));
        if (bundle != null) {
            intent.putExtra(EXTRA_BUNDLE, bundle);
        }
        intent.putExtra(EXTRA_STRING_BREADCRUMB, activity.GetActivity().getString(R.string.hr3rdPartyActionCrumb));
        activity.RegisterActivityResult(intent, resultCallback, null);
    }

    public void FireAction(LocaleActionPlugin plugin, Bundle bundle) {
        FireAction(plugin.PackageName, bundle);
    }

    public void FireAction(String pluginPackageName, Bundle bundle) {
        Intent intent = new Intent(ACTION_FIRE_SETTING);
        intent.setPackage(pluginPackageName);
        intent.putExtra(EXTRA_BUNDLE, bundle);
        intent.putExtras(bundle);
        this._Context.sendBroadcast(intent);
    }

    public Bundle GetBundleFromResultIntent(Intent data) {
        if (data.hasExtra(EXTRA_BUNDLE)) {
            return data.getBundleExtra(EXTRA_BUNDLE);
        }
        Bundle b = new Bundle();
        b.putAll(data.getExtras());
        return b;
    }

    public String GetFriendlyTextFromResultIntent(Intent data) {
        return data.getStringExtra(EXTRA_STRING_BLURB);
    }
}
