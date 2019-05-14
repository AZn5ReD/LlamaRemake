package com.kebab.Llama.EventActions;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ResolveInfo.DisplayNameComparator;
import android.preference.PreferenceActivity;
import android.widget.Toast;
import com.kebab.AppendableCharSequence;
import com.kebab.ClickablePreferenceEx.GotResultHandler;
import com.kebab.DelayedSimpleListPreference;
import com.kebab.Helpers;
import com.kebab.Llama.Event;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.LlamaStorage;
import com.kebab.Llama.Logging;
import com.kebab.Llama.R;
import com.kebab.NotSupportedException;
import com.kebab.PreferenceEx;
import com.kebab.ResultRegisterableActivity;
import com.kebab.ResultRegisterableActivity.ResultCallback;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class RunShortcutAction extends EventAction<RunShortcutAction> {
    String _Base64Intent;
    String _FriendlyName;

    public RunShortcutAction(String friendlyName, String asciiIntent) {
        this._FriendlyName = friendlyName;
        this._Base64Intent = asciiIntent;
    }

    public void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        Intent intent;
        try {
            intent = (Intent) Helpers.GetParcelableFromString(this._Base64Intent, Intent.class);
        } catch (Exception ex) {
            intent = null;
            Logging.Report(ex, (Context) service);
        }
        if (intent != null) {
            service.startShortcut(intent, this._FriendlyName);
        }
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
        return EventFragment.RUN_SHORTCUT_ACTION;
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(LlamaStorage.SimpleEscape(this._FriendlyName));
        sb.append("|");
        sb.append(this._Base64Intent);
    }

    public static RunShortcutAction CreateFrom(String[] parts, int currentPart) {
        return new RunShortcutAction(LlamaStorage.SimpleUnescape(parts[currentPart + 1]), parts[currentPart + 2]);
    }

    public PreferenceEx<RunShortcutAction> CreatePreference(PreferenceActivity context) {
        final PackageManager pm = context.getPackageManager();
        final PreferenceActivity preferenceActivity = context;
        return new DelayedSimpleListPreference<RunShortcutAction, ResolveInfo>((ResultRegisterableActivity) context, context.getString(R.string.hrActionRunShortcut), this, true, context.getString(R.string.hrGettingShortcuts)) {
            /* Access modifiers changed, original: protected */
            public RunShortcutAction ConvertListItemToResult(ResolveInfo listItem) {
                throw new NotSupportedException("This call is not used by RunShortcutAction's preference.");
            }

            /* Access modifiers changed, original: protected */
            public void FillDialogBuilder(RunShortcutAction existingSelectedItem, Builder dialog, final GotResultHandler<RunShortcutAction> gotResultHandler) {
                dialog.setItems(this._ListItemStrings, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent("android.intent.action.CREATE_SHORTCUT");
                        ActivityInfo ai = ((ResolveInfo) _ListItems.get(which)).activityInfo;
                        intent.setClassName(ai.packageName, ai.name);
                        _Host.RegisterActivityResult(intent, new ResultCallback() {
                            public void HandleResult(int result, Intent data, Object extraStateInfo) {
                                if (result == -1) {
                                    Intent shortcutIntent = (Intent) data.getParcelableExtra("android.intent.extra.shortcut.INTENT");
                                    String friendlyName = data.getStringExtra("android.intent.extra.shortcut.NAME");
                                    String buff = Helpers.GetParcelAsString(shortcutIntent);
                                    if (!Helpers.VerifyIntentsMatch(preferenceActivity, shortcutIntent, (Intent) Helpers.GetParcelableFromString(buff, Intent.class))) {
                                        Toast.makeText(preferenceActivity, "Failed to store intent. Please email the Llama developer.", 1);
                                        buff = "";
                                    }
                                    if (friendlyName == null || friendlyName.length() == 0) {
                                        friendlyName = preferenceActivity.getString(17039374);
                                    }
                                    gotResultHandler.HandleResult(new RunShortcutAction(friendlyName, buff));
                                }
                            }
                        }, null);
                        dialog.dismiss();
                    }
                });
            }

            /* Access modifiers changed, original: protected */
            public CharSequence ConvertListItemToString(ResolveInfo listItem) {
                return listItem.loadLabel(pm);
            }

            /* Access modifiers changed, original: protected */
            public String GetHumanReadableValue(Context context, RunShortcutAction value) {
                return value._FriendlyName;
            }

            /* Access modifiers changed, original: protected */
            public List<ResolveInfo> GetListItems() {
                List<ResolveInfo> infos = pm.queryIntentActivities(new Intent("android.intent.action.CREATE_SHORTCUT"), 0);
                Collections.sort(infos, new DisplayNameComparator(pm));
                return infos;
            }

            /* Access modifiers changed, original: protected */
            public boolean IsSelectedItemEqualToListItem(RunShortcutAction existingSelectedValue, ResolveInfo listItem) {
                throw new NotSupportedException("This call is not used by RunShortcutAction's preference.");
            }
        };
    }

    public void AppendActionDescription(Context context, AppendableCharSequence sb) throws IOException {
        sb.append(String.format(context.getString(R.string.hrRunAShortcut), new Object[]{this._FriendlyName}));
    }

    public String GetIsValidError(Context context) {
        if (this._Base64Intent == null || this._Base64Intent.length() == 0) {
            return context.getString(R.string.hrChooseAShortcutToRun);
        }
        return null;
    }

    public boolean IsHarmful() {
        return true;
    }
}
