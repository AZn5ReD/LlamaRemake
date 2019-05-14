package com.kebab.Llama.EventActions;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;
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
import com.kebab.Locale.LocaleActionPlugin;
import com.kebab.Locale.LocaleHelper;
import com.kebab.NotSupportedException;
import com.kebab.PreferenceEx;
import com.kebab.ResultRegisterableActivity;
import com.kebab.ResultRegisterableActivity.ResultCallback;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class LocalePluginAction extends EventAction<LocalePluginAction> {
    String _Base64Bundle;
    String _FriendlyName;
    String _FriendlyText;
    String _PackageName;

    public LocalePluginAction(String friendlyName, String friendlyText, String packageName, String asciiIntent) {
        this._FriendlyName = friendlyName;
        this._FriendlyText = friendlyText;
        this._PackageName = packageName;
        this._Base64Bundle = asciiIntent;
    }

    public void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        try {
            new LocaleHelper(service).FireAction(this._PackageName, Helpers.GetBundleFromString(this._Base64Bundle));
        } catch (Exception e) {
            Logging.Report(e, (Context) service, false);
            service.ShowFriendlyInfo(true, service.getString(R.string.hrFailedToRunLocalePlugin, new Object[]{this._FriendlyName, this._FriendlyText, event.Name}), false);
        }
    }

    public boolean RenameProfile(String oldName, String newName) {
        return false;
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 4;
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.LOCALE_PLUGIN_ACTION;
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(LlamaStorage.SimpleEscape(this._FriendlyName)).append("|");
        sb.append(LlamaStorage.SimpleEscape(this._FriendlyText == null ? "" : this._FriendlyText)).append("|");
        sb.append(LlamaStorage.SimpleEscape(this._PackageName)).append("|");
        sb.append(this._Base64Bundle);
    }

    public static LocalePluginAction CreateFrom(String[] parts, int currentPart) {
        return new LocalePluginAction(LlamaStorage.SimpleUnescape(parts[currentPart + 1]), LlamaStorage.SimpleUnescape(parts[currentPart + 2]), LlamaStorage.SimpleUnescape(parts[currentPart + 3]), parts[currentPart + 4]);
    }

    public PreferenceEx<LocalePluginAction> CreatePreference(PreferenceActivity context) {
        PackageManager pm = context.getPackageManager();
        final LocaleHelper localeHelper = new LocaleHelper(context);
        final PreferenceActivity preferenceActivity = context;
        return new DelayedSimpleListPreference<LocalePluginAction, LocaleActionPlugin>((ResultRegisterableActivity) context, context.getString(R.string.hrActionLocalePlugin), this, true, context.getString(R.string.hrGettingLocaleActionPlugins)) {
            /* Access modifiers changed, original: protected */
            public LocalePluginAction ConvertListItemToResult(LocaleActionPlugin listItem) {
                throw new NotSupportedException("This call is not used by LocalePluginActions preference.");
            }

            /* Access modifiers changed, original: protected */
            public void FillDialogBuilder(final LocalePluginAction existingSelectedItem, Builder dialog, final GotResultHandler<LocalePluginAction> gotResultHandler) {
                dialog.setItems(this._ListItemStrings, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final LocaleActionPlugin pluginInfo = (LocaleActionPlugin) _ListItems.get(which);
                        localeHelper.StartSettingsActivity(_Host, pluginInfo, pluginInfo.PackageName.equals(existingSelectedItem._PackageName) ? Helpers.GetBundleFromString(existingSelectedItem._Base64Bundle) : null, new ResultCallback() {
                            public void HandleResult(int result, Intent data, Object extraStateInfo) {
                                if (result == -1) {
                                    Bundle bundle = localeHelper.GetBundleFromResultIntent(data);
                                    gotResultHandler.HandleResult(new LocalePluginAction(pluginInfo.FriendlyName, localeHelper.GetFriendlyTextFromResultIntent(data), pluginInfo.PackageName, Helpers.GetStringFromBundle(bundle)));
                                }
                            }
                        });
                        Helpers.ShowTip(preferenceActivity, preferenceActivity.getString(R.string.hrLocalePluginConfigMessage));
                        dialog.dismiss();
                    }
                });
            }

            /* Access modifiers changed, original: protected */
            public CharSequence ConvertListItemToString(LocaleActionPlugin listItem) {
                return listItem.FriendlyName;
            }

            /* Access modifiers changed, original: protected */
            public String GetHumanReadableValue(Context context, LocalePluginAction value) {
                if (value._FriendlyName == null || value._FriendlyName.length() == 0) {
                    return "";
                }
                if (value._FriendlyText == null || value._FriendlyText.length() == 0) {
                    return value._FriendlyName;
                }
                return value._FriendlyName + ": " + value._FriendlyText;
            }

            /* Access modifiers changed, original: protected */
            public List<LocaleActionPlugin> GetListItems() {
                List<LocaleActionPlugin> infos = localeHelper.GetAllPlugins();
                Collections.sort(infos, LocaleActionPlugin.FriendlyNameComparator);
                return infos;
            }

            /* Access modifiers changed, original: protected */
            public boolean IsSelectedItemEqualToListItem(LocalePluginAction existingSelectedValue, LocaleActionPlugin listItem) {
                throw new NotSupportedException("This call is not used by LocalePluginAction's preference.");
            }
        };
    }

    public void AppendActionDescription(Context context, AppendableCharSequence sb) throws IOException {
        sb.append(String.format(context.getString(R.string.hrRunLocalePlugin1Quotes2), new Object[]{this._FriendlyName, this._FriendlyText}));
    }

    public String GetIsValidError(Context context) {
        if (this._PackageName == null || this._PackageName.length() == 0) {
            return context.getString(R.string.hrChooseALocalePluginToRun);
        }
        return null;
    }

    public boolean IsHarmful() {
        return true;
    }
}
