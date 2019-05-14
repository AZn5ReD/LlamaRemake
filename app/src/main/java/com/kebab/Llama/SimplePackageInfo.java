package com.kebab.Llama;

import java.util.Comparator;

public class SimplePackageInfo {
    protected static final SimplePackageInfo Empty = new SimplePackageInfo("", "");
    public static final Comparator<SimplePackageInfo> NameComparator = new Comparator<SimplePackageInfo>() {
        public int compare(SimplePackageInfo x, SimplePackageInfo y) {
            return x.getFriendlyName().compareToIgnoreCase(y.getFriendlyName());
        }
    };
    String FriendlyName;
    String PackageName;

    public SimplePackageInfo(String friendlyName, String packageName) {
        this.FriendlyName = friendlyName;
        this.PackageName = packageName;
    }

    public String getPackageName() {
        return this.PackageName;
    }

    public String getFriendlyName() {
        return this.FriendlyName;
    }
}
