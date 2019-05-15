package com.kebab.Llama.EventActions;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceActivity;
import com.kebab.AppendableCharSequence;
import com.kebab.IterableHelpers;
import com.kebab.Llama.AppMultiListPreference;
import com.kebab.Llama.Event;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.LlamaStorage;
import com.kebab.Llama.R;
import com.kebab.Llama.SimplePackageInfo;
import com.kebab.PreferenceEx;
import com.kebab.ResultRegisterableActivity;
import com.kebab.Selector;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public abstract class KillAppActionBase<T extends KillAppActionBase<?>> extends EventAction<T> {
    ArrayList<SimplePackageInfo> _ApplicationInfo;

    public abstract void AppendActionDescription(Context context, AppendableCharSequence appendableCharSequence) throws IOException;

    public abstract T CreateSelf(ArrayList<SimplePackageInfo> arrayList);

    public abstract int GetTitleResource();

    public abstract void PerformAction(LlamaService llamaService, Activity activity, Event event, long j, int i);

    public abstract String getId();

    public KillAppActionBase(ArrayList<SimplePackageInfo> applicationInfo) {
        this._ApplicationInfo = applicationInfo;
    }

    public boolean RenameProfile(String oldName, String newName) {
        return false;
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 2;
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        SimplePackageInfo info;
        boolean needSeparator = false;
        Iterator i$ = this._ApplicationInfo.iterator();
        while (i$.hasNext()) {
            info = (SimplePackageInfo) i$.next();
            if (needSeparator) {
                sb.append(LlamaStorage.SimpleEscapedPipe());
            }
            sb.append(LlamaStorage.SimpleEscape(LlamaStorage.SimpleEscape(info.getFriendlyName())));
            needSeparator = true;
        }
        needSeparator = false;
        sb.append("|");
        i$ = this._ApplicationInfo.iterator();
        while (i$.hasNext()) {
            info = (SimplePackageInfo) i$.next();
            if (needSeparator) {
                sb.append(LlamaStorage.SimpleEscapedPipe());
            }
            sb.append(LlamaStorage.SimpleEscape(LlamaStorage.SimpleEscape(info.getPackageName())));
            needSeparator = true;
        }
    }

    public PreferenceEx<T> CreatePreference(PreferenceActivity context) {
        return new AppMultiListPreference<T>((ResultRegisterableActivity) context, context.getString(GetTitleResource()), (T) this, true, context.getString(R.string.hrGettingApplicationNames)) {
            /* Access modifiers changed, original: protected */
            public String GetHumanReadableValue(Context context, T value) {
                return IterableHelpers.ConcatenateString(value._ApplicationInfo, ", ", new Selector<SimplePackageInfo, String>() {
                    public String Do(SimplePackageInfo value) {
                        return value.getFriendlyName();
                    }
                });
            }

            /* Access modifiers changed, original: protected */
            public boolean IsSelectedItemEqualToListItem(T existingSelectedValue, SimplePackageInfo listItem) {
                if (existingSelectedValue._ApplicationInfo == null) {
                    return false;
                }
                Iterator i$ = existingSelectedValue._ApplicationInfo.iterator();
                while (i$.hasNext()) {
                    if (((SimplePackageInfo) i$.next()).getPackageName().equals(listItem.getPackageName())) {
                        return true;
                    }
                }
                return false;
            }

            /* Access modifiers changed, original: protected */
            public T ConvertCheckedListItemToResult(HashSet<Integer> listItems) {
                ArrayList<SimplePackageInfo> items = new ArrayList();
                Iterator i$ = listItems.iterator();
                while (i$.hasNext()) {
                    items.add(this._ListItems.get(((Integer) i$.next()).intValue()));
                }
                return KillAppActionBase.this.CreateSelf(items);
            }
        };
    }

    public String GetIsValidError(Context context) {
        if (this._ApplicationInfo.size() == 0) {
            return context.getString(R.string.hrChooseAnApplication);
        }
        Iterator i$ = this._ApplicationInfo.iterator();
        while (i$.hasNext()) {
            SimplePackageInfo info = (SimplePackageInfo) i$.next();
            if (info.getPackageName() == null || info.getPackageName().length() == 0) {
                return context.getString(R.string.hrChooseAnApplication);
            }
            if (info.getFriendlyName() != null) {
                if (info.getFriendlyName().length() == 0) {
                }
            }
            return context.getString(R.string.hrChooseAnApplicationWithAName);
        }
        return null;
    }

    public static ArrayList<SimplePackageInfo> CreateSimplePackageList(String[] parts, int currentPart) {
        ArrayList<String> names = LlamaStorage.DeserializePsvStringArrayList(LlamaStorage.SimpleUnescape(parts[currentPart + 1]), true);
        ArrayList<String> packages = LlamaStorage.DeserializePsvStringArrayList(LlamaStorage.SimpleUnescape(parts[currentPart + 2]), true);
        ArrayList<SimplePackageInfo> result = new ArrayList(names.size());
        for (int i = 0; i < names.size(); i++) {
            result.add(new SimplePackageInfo((String) names.get(i), (String) packages.get(i)));
        }
        return result;
    }
}
