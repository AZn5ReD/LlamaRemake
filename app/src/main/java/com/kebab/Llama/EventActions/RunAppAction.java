package com.kebab.Llama.EventActions;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ResolveInfo.DisplayNameComparator;
import android.preference.PreferenceActivity;
import android.widget.Toast;
import com.kebab.AppendableCharSequence;
import com.kebab.DelayedRadioListPreference;
import com.kebab.Helpers;
import com.kebab.HelpersC;
import com.kebab.Llama.Event;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.LlamaStorage;
import com.kebab.Llama.Logging;
import com.kebab.Llama.R;
import com.kebab.Llama.SimplePackageInfo;
import com.kebab.PreferenceEx;
import com.kebab.ResultRegisterableActivity;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class RunAppAction extends EventAction<RunAppAction> {
    String _FriendlyName;
    String _PackageOrBase64Intent;

    public RunAppAction(String friendlyName, String packageOrBase64Intent) {
        this._FriendlyName = friendlyName;
        this._PackageOrBase64Intent = packageOrBase64Intent;
    }

    public void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        if (this._PackageOrBase64Intent.subSequence(0, 1).equals(":")) {
            Intent intent;
            try {
                intent = (Intent) Helpers.GetParcelableFromString(this._PackageOrBase64Intent.substring(1), Intent.class);
            } catch (Exception ex) {
                intent = null;
                Logging.Report(ex, (Context) service);
            }
            if (intent == null) {
                service.HandleFriendlyError(String.format(service.getString(R.string.hrFailedToDecodeIntentInEvent1), new Object[]{event.Name}), false);
                return;
            }
            service.startShortcut(intent, this._FriendlyName);
            return;
        }
        service.RunApplication(new SimplePackageInfo(this._FriendlyName, this._PackageOrBase64Intent));
    }

    public boolean RenameProfile(String oldName, String newName) {
        return false;
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 2;
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.RUN_APP_ACTION;
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(LlamaStorage.SimpleEscape(this._FriendlyName));
        sb.append("|");
        sb.append(LlamaStorage.SimpleEscape(this._PackageOrBase64Intent));
    }

    public static RunAppAction CreateFrom(String[] parts, int currentPart) {
        return new RunAppAction(LlamaStorage.SimpleUnescape(parts[currentPart + 1]), LlamaStorage.SimpleUnescape(parts[currentPart + 2]));
    }

    public PreferenceEx<RunAppAction> CreatePreference(PreferenceActivity context) {
        final PackageManager pm = context.getPackageManager();
        final PreferenceActivity preferenceActivity = context;
        return new DelayedRadioListPreference<RunAppAction, ResolveInfo>((ResultRegisterableActivity) context, context.getString(R.string.hrRunApplication), this, true, context.getString(R.string.hrGettingApplicationNames)) {
            Intent _SelectedValueIntent;

            /* Access modifiers changed, original: protected */
            public String GetHumanReadableValue(Context context, RunAppAction value) {
                return value._FriendlyName;
            }

            /* Access modifiers changed, original: protected */
            public List<ResolveInfo> GetListItems() {
                List<ResolveInfo> data = pm.queryIntentActivities(new Intent("android.intent.action.MAIN").addCategory("android.intent.category.LAUNCHER"), 0);
                Collections.sort(data, new DisplayNameComparator(pm));
                return data;
            }

            /* Access modifiers changed, original: protected */
            public RunAppAction ConvertListItemToResult(ResolveInfo listItem) {
                Intent intent = new Intent("android.intent.action.MAIN");
                ActivityInfo ai = listItem.activityInfo;
                intent.setClassName(ai.packageName, ai.name);
                intent.addCategory("android.intent.category.LAUNCHER");
                CharSequence cs = listItem.loadLabel(pm);
                String buff = Helpers.GetParcelAsString(intent);
                if (!Helpers.VerifyIntentsMatch(preferenceActivity, intent, (Intent) Helpers.GetParcelableFromString(buff, Intent.class))) {
                    Toast.makeText(preferenceActivity, "Failed to store intent. Please email the Llama developer.", 1);
                    buff = "";
                }
                return new RunAppAction(cs == null ? "" : cs.toString(), ":" + buff);
            }

            /* Access modifiers changed, original: protected */
            public CharSequence ConvertListItemToString(ResolveInfo listItem) {
                return listItem.loadLabel(pm);
            }

            /* Access modifiers changed, original: protected */
            public void PrepareForFindSelectedItem(RunAppAction existingSelectedValue) {
                this._SelectedValueIntent = existingSelectedValue._PackageOrBase64Intent.startsWith(":") ? (Intent) Helpers.GetParcelableFromString(existingSelectedValue._PackageOrBase64Intent.substring(1), Intent.class) : null;
            }

            /* Access modifiers changed, original: protected */
            public boolean IsSelectedItemEqualToListItem(RunAppAction existingSelectedValue, ResolveInfo listItem) {
                if (this._SelectedValueIntent != null) {
                    ComponentName selectedComponentName = this._SelectedValueIntent.getComponent();
                    if (!(selectedComponentName == null || listItem.activityInfo == null)) {
                        ActivityInfo activityInfo = listItem.activityInfo;
                        if (activityInfo != null) {
                            String selectedPackageName = selectedComponentName.getPackageName();
                            String selectedClassName = selectedComponentName.getClassName();
                            if (HelpersC.StringEquals(selectedPackageName, activityInfo.packageName) && HelpersC.StringEquals(selectedClassName, activityInfo.name)) {
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
        };
    }

    public void AppendActionDescription(Context context, AppendableCharSequence sb) throws IOException {
        sb.append(String.format(context.getString(R.string.hrRun1), new Object[]{this._FriendlyName}));
    }

    public String GetIsValidError(Context context) {
        if (this._PackageOrBase64Intent == null || this._PackageOrBase64Intent.length() == 0) {
            return context.getString(R.string.hrChooseAnApplication);
        }
        if (this._FriendlyName == null || this._FriendlyName.length() == 0) {
            return context.getString(R.string.hrChooseAnApplicationWithAName);
        }
        return null;
    }

    public boolean IsHarmful() {
        return false;
    }
}
