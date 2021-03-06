package com.kebab.Llama;

import android.content.Context;
import com.kebab.DelayedRadioListPreference;
import com.kebab.IterableHelpers;
import com.kebab.ResultRegisterableActivity;
import java.util.List;

public abstract class AppListPreference<T> extends DelayedRadioListPreference<T, SimplePackageInfo> {
    public abstract T ConvertListItemToResult(SimplePackageInfo simplePackageInfo);

    public abstract String GetHumanReadableValue(Context context, T t);

    public abstract boolean IsSelectedItemEqualToListItem(T t, SimplePackageInfo simplePackageInfo);

    public AppListPreference(ResultRegisterableActivity activity, String title, T currentValue, boolean runAsynchronously, String asyncMessage) {
        super(activity, title, currentValue, runAsynchronously, asyncMessage);
    }

    /* Access modifiers changed, original: protected */
    public CharSequence ConvertListItemToString(SimplePackageInfo listItem) {
        return listItem.getFriendlyName();
    }

    /* Access modifiers changed, original: protected */
    public List<SimplePackageInfo> GetListItems() {
        return IterableHelpers.OrderBy(Instances.Service.GetInstalledApps(), SimplePackageInfo.NameComparator);
    }
}
