package com.kebab.Llama.EventConditions;

import android.content.Context;
import android.preference.PreferenceActivity;
import com.kebab.Llama.AppListPreference;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.EventMeta;
import com.kebab.Llama.EventMeta.ConditionStaticInitter1;
import com.kebab.Llama.EventTrigger;
import com.kebab.Llama.LlamaStorage;
import com.kebab.Llama.R;
import com.kebab.Llama.SimplePackageInfo;
import com.kebab.Llama.StateChange;
import com.kebab.PreferenceEx;
import com.kebab.Ref;
import com.kebab.ResultRegisterableActivity;
import java.io.IOException;

public class AppNotificationCondition extends EventCondition<AppNotificationCondition> {
    public static String MY_ID;
    public static int MY_TRIGGER;
    public static int[] MY_TRIGGERS;
    String _Filters;
    String _FriendlyName;
    String _PackageName;

    static {
        EventMeta.InitCondition(EventFragment.APP_NOTIFICATION_CONDITION, new ConditionStaticInitter1() {
            public void UpdateStatics(String id, int[] triggers, int trigger) {
                AppNotificationCondition.MY_ID = id;
                AppNotificationCondition.MY_TRIGGERS = triggers;
                AppNotificationCondition.MY_TRIGGER = trigger;
            }
        });
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return MY_ID;
    }

    public int[] getEventTriggers() {
        return MY_TRIGGERS;
    }

    public AppNotificationCondition(String packageName, String friendlyName, String filters) {
        this._PackageName = packageName;
        this._FriendlyName = friendlyName;
        this._Filters = filters;
    }

    public int TestCondition(StateChange state, Context context, Ref<EventTrigger> ref) {
        if (state.TriggerType == MY_TRIGGER && state.NotificationPackageName.equalsIgnoreCase(this._PackageName)) {
            return 2;
        }
        return 0;
    }

    public boolean RenameArea(String oldName, String newName) {
        return false;
    }

    public void AppendConditionSimple(Context context, Appendable sb) throws IOException {
        sb.append(String.format(context.getString(R.string.hrWhen1ShowsANotification), new Object[]{this._FriendlyName}));
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 3;
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(LlamaStorage.SimpleEscape(this._PackageName)).append("|");
        sb.append(LlamaStorage.SimpleEscape(this._FriendlyName)).append("|");
        sb.append(LlamaStorage.SimpleEscape(this._Filters));
    }

    public static AppNotificationCondition CreateFrom(String[] parts, int currentPart) {
        return new AppNotificationCondition(LlamaStorage.SimpleUnescape(parts[currentPart + 1]), LlamaStorage.SimpleUnescape(parts[currentPart + 2]), LlamaStorage.SimpleUnescape(parts[currentPart + 3]));
    }

    public PreferenceEx<AppNotificationCondition> CreatePreference(PreferenceActivity context) {
        return new AppListPreference<AppNotificationCondition>((ResultRegisterableActivity) context, context.getString(R.string.hrConditionAppNotification), this, true, context.getString(R.string.hrGettingApplicationNames)) {
            /* Access modifiers changed, original: protected */
            public AppNotificationCondition ConvertListItemToResult(SimplePackageInfo listItem) {
                return new AppNotificationCondition(listItem.getPackageName(), listItem.getFriendlyName(), "");
            }

            /* Access modifiers changed, original: protected */
            public String GetHumanReadableValue(Context context, AppNotificationCondition value) {
                return value._FriendlyName;
            }

            /* Access modifiers changed, original: protected */
            public boolean IsSelectedItemEqualToListItem(AppNotificationCondition existingSelectedValue, SimplePackageInfo listItem) {
                if (existingSelectedValue._PackageName == null) {
                    return false;
                }
                return existingSelectedValue._PackageName.equals(listItem.getPackageName());
            }
        };
    }

    public String GetIsValidError(Context context) {
        if (this._PackageName == null) {
            return context.getString(R.string.hrChooseAnApplication);
        }
        return null;
    }
}
