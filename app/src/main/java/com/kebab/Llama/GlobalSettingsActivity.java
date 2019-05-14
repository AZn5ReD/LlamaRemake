package com.kebab.Llama;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.View;
import android.widget.EditText;
import com.kebab.AlertDialogEx.Builder;
import com.kebab.CachedSetting;
import com.kebab.Helpers;
import com.kebab.IterableHelpers;
import com.kebab.Llama.EventActions.VibrateAction;
import com.kebab.Llama.Instances.HelloablePreferenceActivity;
import com.kebab.RingtonePreference;
import com.kebab.RingtonePreference.ValueChangedListener;
import com.kebab.SeekBarDialogView.ValueFormatter;
import com.kebab.SeekBarPreference;
import com.kebab.TextEntryDialog;
import com.kebab.TextEntryDialog.ButtonHandler;
import com.kebab.Tuple;
import java.util.ArrayList;
import java.util.Iterator;

public class GlobalSettingsActivity extends HelloablePreferenceActivity {
    Handler _Handler = new Handler();
    boolean _MultithreadModeChanged;

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.edit_globalsettings, false);
        addPreferencesFromResource(R.xml.edit_globalsettings);
        ((CheckBoxPreference) findPreference("BlackIcons")).setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                LlamaSettings.BlackIcons.SetValueAndCommit(GlobalSettingsActivity.this, (Boolean) newValue, new CachedSetting[0]);
                LlamaSettings.BlackIcons.Reset();
                if (Instances.Service != null) {
                    Instances.Service._OngoingNotification.Update();
                }
                return true;
            }
        });
        ((CheckBoxPreference) findPreference("MultiThreadedMode")).setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                GlobalSettingsActivity.this._MultithreadModeChanged = true;
                Helpers.ShowTip(GlobalSettingsActivity.this, "Llama will restart when you close the settings");
                return true;
            }
        });
        CheckBoxPreference watchForNtcTags = (CheckBoxPreference) findPreference("WatchForNfcTags");
        watchForNtcTags.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference arg0, Object arg1) {
                ComponentName component = new ComponentName(GlobalSettingsActivity.this, NfcDiscoverActivity.class);
                PackageManager pm = GlobalSettingsActivity.this.getPackageManager();
                if (((Boolean) arg1).booleanValue()) {
                    pm.setComponentEnabledSetting(component, 1, 1);
                } else {
                    pm.setComponentEnabledSetting(component, 2, 1);
                }
                return true;
            }
        });
        final CheckBoxPreference checkBoxPreference = watchForNtcTags;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                int nfcState = GlobalSettingsActivity.this.getPackageManager().getComponentEnabledSetting(new ComponentName(GlobalSettingsActivity.this, NfcDiscoverActivity.class));
                boolean isEnabled = (nfcState == 2 || nfcState == 3) ? false : true;
                checkBoxPreference.setChecked(isEnabled);
            }
        }, 40);
        final CheckBoxPreference dontCheckVolumeInCallCheckbox = (CheckBoxPreference) findPreference("DontCheckVolumeInCall");
        final CheckBoxPreference revertProfilesCheckbox = (CheckBoxPreference) findPreference("RevertVolumeChanges");
        final CheckBoxPreference lockProfilesCheckbox = (CheckBoxPreference) findPreference("AutoProfileLocked");
        final CheckBoxPreference changeIconCheckbox = (CheckBoxPreference) findPreference("ChangeIconIfVolumeChanges");
        revertProfilesCheckbox.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (((Boolean) newValue).booleanValue()) {
                    lockProfilesCheckbox.setChecked(false);
                    changeIconCheckbox.setChecked(false);
                    dontCheckVolumeInCallCheckbox.setEnabled(true);
                    GlobalSettingsActivity.this.showVolumeChangeInCallMessage();
                } else {
                    dontCheckVolumeInCallCheckbox.setEnabled(false);
                }
                return true;
            }
        });
        lockProfilesCheckbox.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (((Boolean) newValue).booleanValue()) {
                    revertProfilesCheckbox.setChecked(false);
                    changeIconCheckbox.setChecked(false);
                    dontCheckVolumeInCallCheckbox.setEnabled(true);
                    GlobalSettingsActivity.this.showVolumeChangeInCallMessage();
                } else {
                    dontCheckVolumeInCallCheckbox.setEnabled(false);
                }
                return true;
            }
        });
        changeIconCheckbox.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (((Boolean) newValue).booleanValue()) {
                    revertProfilesCheckbox.setChecked(false);
                    lockProfilesCheckbox.setChecked(false);
                    dontCheckVolumeInCallCheckbox.setEnabled(true);
                    GlobalSettingsActivity.this.showVolumeChangeInCallMessage();
                } else {
                    dontCheckVolumeInCallCheckbox.setEnabled(false);
                }
                return true;
            }
        });
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (((Boolean) LlamaSettings.RevertVolumeChanges.GetValue(GlobalSettingsActivity.this)).booleanValue() || ((Boolean) LlamaSettings.AutoLockProfileOnVolumeChange.GetValue(GlobalSettingsActivity.this)).booleanValue() || ((Boolean) LlamaSettings.ChangeIconIfVolumeChanges.GetValue(GlobalSettingsActivity.this)).booleanValue()) {
                    dontCheckVolumeInCallCheckbox.setEnabled(true);
                } else {
                    dontCheckVolumeInCallCheckbox.setEnabled(false);
                }
            }
        }, 40);
        SeekBarPreference<?> unlockDelay = (SeekBarPreference) findPreference("ProfileUnlockDelay");
        unlockDelay.setValueFormatter(new ValueFormatter() {
            public String FormatValue(int value, boolean isTopMostValue, String topMostValue) {
                return isTopMostValue ? topMostValue : Helpers.GetHoursMinutesSeconds(GlobalSettingsActivity.this, value * 60);
            }

            public int GetTextSize() {
                return 20;
            }
        });
        unlockDelay.setMax(((Boolean) LlamaSettings.LongerProfileLock.GetValue(this)).booleanValue() ? Constants.PROFILE_LOCK_MAX_MINUTES_LONG : 480);
        CheckBoxPreference useDeprecatedVibrateSettingTickbox = (CheckBoxPreference) findPreference("UseDeprecatedVibrateSettingTickbox");
        useDeprecatedVibrateSettingTickbox.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int newValueToSave;
                if (((Boolean) newValue).booleanValue()) {
                    newValueToSave = 1;
                } else {
                    newValueToSave = 0;
                }
                Logging.Report("s4compat", "UseDeprecatedVibrate tickbox changed to " + newValueToSave, GlobalSettingsActivity.this);
                LlamaSettings.UseDeprecatedVibrateSetting.SetValueAndCommit(GlobalSettingsActivity.this, Integer.valueOf(newValueToSave), new CachedSetting[0]);
                return true;
            }
        });
        useDeprecatedVibrateSettingTickbox.setChecked(((Integer) LlamaSettings.UseDeprecatedVibrateSetting.GetValue(this)).intValue() == 1);
        ((PreferenceScreen) findPreference("showConnectedBluetoothDevices")).setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                CharSequence[] data = IterableHelpers.ToCharSequenceArray(Instances.Service.GetConnectedBluetoothDevices());
                BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
                if (ba != null) {
                    for (int i = 0; i < data.length; i++) {
                        String name = ba.getRemoteDevice((String) data[i]).getName();
                        if (name != null) {
                            data[i] = name + " (" + data[i] + ")";
                        }
                    }
                }
                new Builder(GlobalSettingsActivity.this).setItems(data, null).create().show();
                return true;
            }
        });
        ((PreferenceScreen) findPreference("showCalendarDebug")).setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                GlobalSettingsActivity.this.startActivity(new Intent(GlobalSettingsActivity.this, CalendarDebugActivity.class));
                return true;
            }
        });
        ((PreferenceScreen) findPreference("resetDonationFlag")).setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                LlamaSettings.HasInAppDonation.SetValueAndCommit(GlobalSettingsActivity.this, Integer.valueOf(0), new CachedSetting[0]);
                return true;
            }
        });
        ((CheckBoxPreference) findPreference("LocationLogging")).setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference pref, Object newValue) {
                if (((Boolean) newValue).booleanValue()) {
                    new Builder(GlobalSettingsActivity.this).setMessage("There's no way to view this info yet, but you can start collecting it if you like").setPositiveButton(R.string.hrOkeyDoke, null).show();
                }
                return true;
            }
        });
        findPreference("VibrateWhenProfilesUnlock").setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference arg0) {
                final Tuple<View, EditText> vibrateDialog = VibrateAction.CreateView(GlobalSettingsActivity.this, (String) LlamaSettings.VibrateWhenProfilesUnlock.GetValue(GlobalSettingsActivity.this));
                Dialog dialog = new Builder(GlobalSettingsActivity.this).setView((View) vibrateDialog.Item1).setPositiveButton(R.string.hrOk, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        String value = ((EditText) vibrateDialog.Item2).getText().toString();
                        if ("0".equals(value)) {
                            value = "";
                        }
                        LlamaSettings.VibrateWhenProfilesUnlock.SetValueAndCommit(GlobalSettingsActivity.this, value, new CachedSetting[0]);
                    }
                }).setNegativeButton(R.string.hrCancel, null).create();
                dialog.setOwnerActivity(GlobalSettingsActivity.this);
                dialog.getWindow().setSoftInputMode(32);
                dialog.getWindow().setSoftInputMode(37);
                dialog.show();
                return true;
            }
        });
        ((PreferenceScreen) findPreference("resetHelpMessages")).setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                LlamaSettings.HelpAreas.SetValueAndCommit(GlobalSettingsActivity.this, Boolean.valueOf(false), LlamaSettings.HelpEvents.SetValueForCommit(Boolean.valueOf(false)), LlamaSettings.HelpProfiles.SetValueForCommit(Boolean.valueOf(false)), LlamaSettings.HelpRecent.SetValueForCommit(Boolean.valueOf(false)));
                LlamaSettings.HadFirstRunMessage.SetValueAndCommit(GlobalSettingsActivity.this, Boolean.valueOf(false), LlamaSettings.AcceptedConfirmationMessages.SetValueForCommit(""), LlamaSettings.AcceptedDisclaimerMessage.SetValueForCommit(Boolean.valueOf(false)));
                if (Instances.UiActivity != null) {
                    Instances.UiActivity.finish();
                }
                GlobalSettingsActivity.this.finish();
                return true;
            }
        });
        final PreferenceScreen llamaSecurityPassword = (PreferenceScreen) findPreference("llamaSecurityPassword");
        llamaSecurityPassword.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                GlobalSettingsActivity.this.showLlamaSecurityPassword();
                return true;
            }
        });
        if (getIntent().getBooleanExtra(Constants.EXTRA_SCROLL_TO_LLAMA_SECURITY, false)) {
            this._Handler.postDelayed(new Runnable() {
                public void run() {
                    Helpers.ScrollToPreference(GlobalSettingsActivity.this, llamaSecurityPassword);
                    GlobalSettingsActivity.this.showLlamaSecurityPassword();
                }
            }, 40);
        }
        findPreference("IgnoreOtherCells").setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                GlobalSettingsActivity.this.ShowIgnoredCellsDialog();
                return true;
            }
        });
        RingtonePreference reminderTone = (RingtonePreference) findPreference("ReminderRingtoneUri");
        String value = (String) LlamaSettings.ReminderRingtoneUri.GetValue(this);
        reminderTone.SetRingtoneValue(value == null ? null : Uri.parse(value));
        reminderTone.SetOnValueChangedCallback(new ValueChangedListener() {
            public void OnValueChange(Uri newValue) {
                LlamaSettings.ReminderRingtoneUri.SetValueAndCommit(GlobalSettingsActivity.this, newValue == null ? null : newValue.toString(), new CachedSetting[0]);
            }
        });
        ((PreferenceScreen) findPreference("deleteCellsFromAreas")).setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference paramPreference) {
                GlobalSettingsActivity.this.doDeleteAllAreas(Beacon.CELL);
                return false;
            }
        });
        ((PreferenceScreen) findPreference("deleteMapPointsFromAreas")).setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference paramPreference) {
                GlobalSettingsActivity.this.doDeleteAllAreas(Beacon.EARTH_POINT);
                return false;
            }
        });
        ((PreferenceScreen) findPreference("deleteWifiNameFromAreas")).setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference paramPreference) {
                GlobalSettingsActivity.this.doDeleteAllAreas(Beacon.WIFI_NAME);
                return false;
            }
        });
        ((PreferenceScreen) findPreference("deleteWifiMacFromAreas")).setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference paramPreference) {
                GlobalSettingsActivity.this.doDeleteAllAreas(Beacon.WIFI_MAC_ADDRESS);
                return false;
            }
        });
        ((PreferenceScreen) findPreference("deleteBluetoothFromAreas")).setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference paramPreference) {
                GlobalSettingsActivity.this.doDeleteAllAreas(Beacon.BLUETOOTH);
                return false;
            }
        });
        ((PreferenceScreen) findPreference("manageNfcTags")).setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference paramPreference) {
                GlobalSettingsActivity.this.manageNfcTags();
                return false;
            }
        });
        ListPreference language = (ListPreference) findPreference("LocaleOverride");
        TranslatorInfo infos = TranslatorInfo.GetInfo(this);
        language.setEntries(infos.Names);
        language.setEntryValues(infos.LanguageIds);
        language.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (Instances.UiActivity != null) {
                    Instances.UiActivity.finish();
                }
                GlobalSettingsActivity.this.finish();
                GlobalSettingsActivity.this.startActivity(new Intent(GlobalSettingsActivity.this, LlamaUi.class));
                return true;
            }
        });
        if (((Boolean) LlamaSettings.AutoLockProfileOnVolumeChange.GetValue(this)).booleanValue() || ((Boolean) LlamaSettings.ChangeIconIfVolumeChanges.GetValue(this)).booleanValue() || ((Boolean) LlamaSettings.RevertVolumeChanges.GetValue(this)).booleanValue()) {
            dontCheckVolumeInCallCheckbox.setEnabled(false);
        }
    }

    /* Access modifiers changed, original: protected */
    public void manageNfcTags() {
        final ArrayList<NfcFriendlyName> tags = Instances.Service.GetAllNfcTags(true);
        final boolean[] checkedItems = new boolean[tags.size()];
        String[] names = new String[tags.size()];
        for (int i = 0; i < tags.size(); i++) {
            names[i] = ((NfcFriendlyName) tags.get(i)).Name;
        }
        new Builder(this).setTitle(R.string.hrManageSavedNfcTags).setMultiChoiceItems(names, null, new OnMultiChoiceClickListener() {
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checkedItems[which] = isChecked;
            }
        }).setPositiveButton(R.string.hrDeleteTag, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < checkedItems.length; i++) {
                    if (checkedItems[i]) {
                        Instances.Service.DeleteNfcTag(((NfcFriendlyName) tags.get(i)).HexString, false);
                    }
                }
                Instances.Service.SaveNfcTagChanges();
            }
        }).setNegativeButton(R.string.hrCancel, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    private void showLlamaSecurityPassword() {
        new Builder(this).setMessage(R.string.hrLlamaSecurityPasswordDescriptionEx).setPositiveButton(R.string.hrOkeyDoke, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                TextEntryDialog.Show(GlobalSettingsActivity.this, GlobalSettingsActivity.this.getString(R.string.hrPleaseEnterALlamaSecurityPassword), new ButtonHandler() {
                    public void Do(String result) {
                        LlamaSettings.EncryptionPassword.SetValueAndCommit(GlobalSettingsActivity.this, result, new CachedSetting[0]);
                    }
                });
            }
        }).show();
    }

    /* Access modifiers changed, original: protected */
    public void ShowIgnoredCellsDialog() {
        ArrayList<Cell> cells = new ArrayList();
        Iterator i$ = Instances.Service.GetIgnoredCells().iterator();
        while (i$.hasNext()) {
            Cell cell = (Cell) i$.next();
            if (!Cell.NoSignal.equals(cell)) {
                cells.add(cell);
            }
        }
        final Cell[] ignoredCells = (Cell[]) IterableHelpers.ToArray(cells, Cell.class);
        CharSequence[] items = new CharSequence[ignoredCells.length];
        final boolean[] ticks = new boolean[ignoredCells.length];
        for (int i = 0; i < ignoredCells.length; i++) {
            items[i] = ignoredCells[i].toFormattedString();
        }
        if (ignoredCells.length == 0) {
            new Builder(this).setTitle(R.string.hrIgnoreCells).setMessage(R.string.hrNoCellsIgnoredLongTapRecentTab).show();
        } else {
            new Builder(this).setTitle(R.string.hrIgnoreCells).setMultiChoiceItems(items, ticks, new OnMultiChoiceClickListener() {
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    ticks[which] = isChecked;
                }
            }).setNeutralButton(R.string.hrRemove, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    ArrayList<Cell> cellsToUnignore = new ArrayList();
                    for (int i = 0; i < ticks.length; i++) {
                        if (ticks[i]) {
                            cellsToUnignore.add(ignoredCells[i]);
                        }
                    }
                    Instances.Service.RemoveIgnoredCells(cellsToUnignore);
                }
            }).setNegativeButton(R.string.hrCancel, null).show();
        }
    }

    public void onPause() {
        commitSettings();
        super.onPause();
        Thread.currentThread().setPriority(1);
    }

    public void onDestroy() {
        super.onDestroy();
    }

    private void commitSettings() {
        LlamaSettings.StoreRecentCells.Reset();
        LlamaSettings.ZeroRecentCells.Reset();
        LlamaSettings.DebugCellsInRecent.Reset();
        LlamaSettings.DebugToasts.Reset();
        LlamaSettings.ForcePersistant.Reset();
        LlamaSettings.WriteToLlamaLog.Reset();
        LlamaSettings.LogSensitiveData.Reset();
        LlamaSettings.BlackIcons.Reset();
        LlamaSettings.NotificationMode.Reset();
        LlamaSettings.ErrorNotificationMode.Reset();
        LlamaSettings.ControlRingtoneNotificationVolumeLink.Reset();
        LlamaSettings.IgnoreInvalidCell.Reset();
        LlamaSettings.ShowAutoEvents.Reset();
        LlamaSettings.Use12HourTimePickers.Reset();
        LlamaSettings.HideDonateMenuItem.Reset();
        LlamaSettings.ColourEventList.Reset();
        LlamaSettings.LogAllCellChanges.Reset();
        LlamaSettings.CellPollingMode.Reset();
        LlamaSettings.CellPollingInterval.Reset();
        LlamaSettings.CellPollingActiveMillis.Reset();
        LlamaSettings.CellPollingWithWakeLock.Reset();
        LlamaSettings.CellPollingWithScreenWakeLock.Reset();
        LlamaSettings.RevertVolumeChanges.Reset();
        LlamaSettings.ChangeIconIfVolumeChanges.Reset();
        LlamaSettings.AutoLockProfileOnVolumeChange.Reset();
        LlamaSettings.DontCheckVolumeInCall.Reset();
        LlamaSettings.ProfileUnlockDelay.Reset();
        LlamaSettings.VibrateWhenProfilesUnlock.Reset();
        LlamaSettings.DebugTagFilter.Reset();
        LlamaSettings.DebugAccessiblity.Reset();
        LlamaSettings.MobileDataMenuMode.Reset();
        LlamaSettings.UseDeprecatedVibrateSetting.Reset();
        LlamaSettings.ReminderRingtoneUri.Reset();
        LlamaSettings.NotificationIconForSounds.Reset();
        LlamaSettings.AndroidLocationInterval.Reset();
        LlamaSettings.AndroidLocationGpsEnabled.Reset();
        LlamaSettings.AndroidLocationEnabled.Reset();
        LlamaSettings.LongerProfileLock.Reset();
        LlamaSettings.ActiveAppWatcherMillis.Reset();
        LlamaSettings.MultiThreadedMode.Reset();
        LlamaSettings.ExtraTtsCleanup.Reset();
        LlamaSettings.InstantConfirmation.Reset();
        LlamaSettings.ResolveContentUris.Reset();
        LlamaSettings.LocationLogging.Reset();
        LlamaSettings.LocationLoggingToSdCard.Reset();
        LlamaSettings.NearbyWifiInterval.Reset();
        LlamaSettings.NearbyWifiEnabled.Reset();
        LlamaSettings.NearbyWifiDisableForHotSpot.Reset();
        LlamaSettings.NearbyBtInterval.Reset();
        LlamaSettings.NearbyBtEnabled.Reset();
        LlamaSettings.RootShutdownCommand.Reset();
        LlamaSettings.RootRebootCommand.Reset();
        LlamaSettings.LocaleOverride.Reset();
        LlamaSettings.EventRecursionLimit.Reset();
        LlamaSettings.HistoryItems.Reset();
        LlamaSettings.ShowAllActionsAndConditions.Reset();
        Logging.InitFilter((String) LlamaSettings.DebugTagFilter.GetValue(this));
        LocalisationInit.Init(this, true);
        if (((Integer) LlamaSettings.NotificationMode.GetValue(this)).intValue() == 4 && VERSION.SDK_INT >= 9) {
            LlamaSettings.NotificationMode.SetValueAndCommit(this, Integer.valueOf(1), new CachedSetting[0]);
            Instances.Service.HandleFriendlyError(getString(R.string.hrNotificationIconOnlyError), true);
        }
        if (((Boolean) LlamaSettings.ZeroRecentCells.GetValue(this)).booleanValue() && Instances.Service != null) {
            Instances.Service.ClearAllRecentCells();
        }
        if (Instances.Service != null) {
            Instances.Service._OngoingNotification.Update();
            Instances.Service.initIgnoredCells();
            Instances.Service.initCellPoller();
            Instances.Service.initLocationListener();
            Instances.Service.initWifiPoller(false, false);
            Instances.Service.initBluetoothPoller(false);
            Instances.Service.SetAppWatcherInterval(((Integer) LlamaSettings.ActiveAppWatcherMillis.GetValue(this)).intValue());
            Instances.Service.initLlamaTrailLocationLogger();
        }
        Logging.Report("s4compat", "UseDeprecatedVibrate now set to " + LlamaSettings.UseDeprecatedVibrateSetting.GetValue(this), (Context) this);
        if (this._MultithreadModeChanged) {
            stopService(new Intent(this, LlamaService.class));
            finish();
            Instances.UiActivity.finish();
            startActivity(new Intent(this, LlamaUi.class));
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void doDeleteAllAreas(final String beaconType) {
        new Builder(this).setTitle(R.string.hrBulkOperations).setMessage(R.string.hrBulkDeleteBeaconWarning).setPositiveButton(R.string.hrYes, new OnClickListener() {
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                if (Instances.HasServiceOrRestart(GlobalSettingsActivity.this)) {
                    Instances.Service.BulkDeleteBeaconType(beaconType);
                }
            }
        }).setNegativeButton(R.string.hrNo, null).show();
    }

    /* Access modifiers changed, original: 0000 */
    public void showVolumeChangeInCallMessage() {
        Helpers.ShowSimpleDialogMessage(this, getString(R.string.hrDontCheckVolumeInCallDescription));
    }

    public void onResume() {
        Thread.currentThread().setPriority(5);
        super.onResume();
    }
}
