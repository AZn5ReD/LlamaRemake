package com.kebab.Llama;

import com.kebab.IterableHelpers;
import com.kebab.Selector;
import java.util.List;

public class AppInfos {
    public String[] Names;
    public String[] Packages;

    public AppInfos() {
        List<SimplePackageInfo> appInfos = IterableHelpers.OrderBy(Instances.Service.GetInstalledApps(), SimplePackageInfo.NameComparator);
        this.Names = (String[]) IterableHelpers.ToArray(IterableHelpers.Select(appInfos, new Selector<SimplePackageInfo, String>() {
            public String Do(SimplePackageInfo value) {
                return value.getFriendlyName();
            }
        }), String.class);
        this.Packages = (String[]) IterableHelpers.ToArray(IterableHelpers.Select(appInfos, new Selector<SimplePackageInfo, String>() {
            public String Do(SimplePackageInfo value) {
                return value.getPackageName();
            }
        }), String.class);
    }
}
