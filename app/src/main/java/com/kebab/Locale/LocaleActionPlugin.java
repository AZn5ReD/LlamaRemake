package com.kebab.Locale;

import java.util.Comparator;

public class LocaleActionPlugin {
    public static final Comparator<? super LocaleActionPlugin> FriendlyNameComparator = new Comparator<LocaleActionPlugin>() {
        public int compare(LocaleActionPlugin lhs, LocaleActionPlugin rhs) {
            return lhs.FriendlyName.compareToIgnoreCase(rhs.FriendlyName);
        }
    };
    public String ActivityName;
    public String FriendlyName;
    public String PackageName;

    public LocaleActionPlugin(String friendly, String packageName, String activityName) {
        this.FriendlyName = friendly;
        this.PackageName = packageName;
        this.ActivityName = activityName;
    }
}
