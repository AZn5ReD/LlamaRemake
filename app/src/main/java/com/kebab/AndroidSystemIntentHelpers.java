package com.kebab;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;

public class AndroidSystemIntentHelpers {
    private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";
    private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
    private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
    private static final String APP_PKG_NAME_22 = "pkg";
    private static final String SCHEME = "package";

    public static void showInstalledAppDetails(Activity context, String packageName) {
        Intent intent = new Intent();
        int apiLevel = VERSION.SDK_INT;
        if (apiLevel >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts(SCHEME, packageName, null));
        } else {
            String appPkgName = apiLevel == 8 ? APP_PKG_NAME_22 : APP_PKG_NAME_21;
            intent.setAction("android.intent.action.VIEW");
            intent.setClassName(APP_DETAILS_PACKAGE_NAME, APP_DETAILS_CLASS_NAME);
            intent.putExtra(appPkgName, packageName);
        }
        context.startActivity(intent);
    }
}
