package com.kebab.Llama;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceScreen;
import android.view.KeyEvent;
import com.kebab.Activities.PeoplePickerActivity;
import com.kebab.AlertDialogEx.Builder;
import com.kebab.Helpers;
import com.kebab.Llama.Content.LlamaMainContentProvider;
import com.kebab.Llama.Instances.HelloablePreferenceActivity;
import com.kebab.LlamaToneEditorDialog;
import com.kebab.ResultRegisterableActivity;
import com.kebab.ResultRegisterableActivity.Helper;
import com.kebab.ResultRegisterableActivity.ResultCallback;
import com.kebab.RingtonePreference;
import com.kebab.RunnableArg;
import com.kebab.SeekBarPreference;
import com.kebab.Tuple;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ProfileEditActivity extends HelloablePreferenceActivity implements ResultRegisterableActivity {
    static final int PEOPLE_CHOOSER = 123400;
    HashMap<Integer, Tuple<ResultCallback, Object>> _ActivityRequests = new HashMap();
    private SeekBarPreference<?> _AlarmVolume;
    private CheckBoxPreference _ChangeAlarmVolume;
    private CheckBoxPreference _ChangeInCallVolume;
    private CheckBoxPreference _ChangeMusicVolume;
    private CheckBoxPreference _ChangeNotificationIcon;
    private CheckBoxPreference _ChangeNotificationTone;
    private CheckBoxPreference _ChangeNotificationVolume;
    CheckBoxPreference _ChangeRingerVolume;
    private CheckBoxPreference _ChangeRingtone;
    private CheckBoxPreference _ChangeSystemVolume;
    private CheckBoxPreference _ChangeVibrateMode;
    private SeekBarPreference<?> _InCallVolume;
    private boolean _IsEdit;
    private SeekBarPreference<?> _MusicVolume;
    private PreferenceScreen _NoisyContactChooser;
    private SeekBarPreference<?> _NoisyContactVolume;
    private ListPreference _NotificationIcon;
    private SeekBarPreference<?> _NotificationIconDots;
    private RingtonePreference _NotificationTone;
    private SeekBarPreference<?> _NotificationVolume;
    String _OldName;
    HashSet<Runnable> _OnDestoryRunnables = new HashSet();
    EditTextPreference _ProfileName;
    Profile _ProfileToEdit;
    private SeekBarPreference<?> _RingerVolume;
    private RingtonePreference _Ringtone;
    boolean _ShownAlarmWarning;
    private SeekBarPreference<?> _SystemVolume;
    private ListPreference _VibrateMode;
    AudioManager am;
    int requestCode = Constants.REQUEST_CODE_CUSTOM_START_OFFSET;

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.am = (AudioManager) getSystemService("audio");
        addPreferencesFromResource(R.xml.edit_profile);
        this._ProfileToEdit = (Profile) getIntent().getExtras().get("Profile");
        this._IsEdit = getIntent().getBooleanExtra(Constants.EXTRA_IS_EDIT, false);
        Profile savedProfile = savedInstanceState == null ? null : (Profile) savedInstanceState.getParcelable("Profile");
        if (savedProfile != null) {
            this._ProfileToEdit = savedProfile;
        }
        setTitle(this._IsEdit ? R.string.hrLlamaDashEditingProfile : R.string.hrLlamaDashNewProfile);
        this._OldName = this._ProfileToEdit.Name;
        this._ProfileName = (EditTextPreference) findPreference(LlamaMainContentProvider.COLUMN_PROFILE_NAME);
        this._ChangeRingerVolume = (CheckBoxPreference) findPreference("changeRingerVolume");
        this._RingerVolume = (SeekBarPreference) findPreference("ringerVolume");
        this._ChangeNotificationVolume = (CheckBoxPreference) findPreference("changeNotificationVolume");
        this._NotificationVolume = (SeekBarPreference) findPreference("notificationVolume");
        this._ChangeMusicVolume = (CheckBoxPreference) findPreference("changeMusicVolume");
        this._MusicVolume = (SeekBarPreference) findPreference("musicVolume");
        this._ChangeSystemVolume = (CheckBoxPreference) findPreference("changeSystemVolume");
        this._SystemVolume = (SeekBarPreference) findPreference("systemVolume");
        this._ChangeAlarmVolume = (CheckBoxPreference) findPreference("changeAlarmVolume");
        this._AlarmVolume = (SeekBarPreference) findPreference("alarmVolume");
        this._ChangeInCallVolume = (CheckBoxPreference) findPreference("changeInCallVolume");
        this._InCallVolume = (SeekBarPreference) findPreference("inCallVolume");
        this._ChangeVibrateMode = (CheckBoxPreference) findPreference("changeVibrateMode");
        this._VibrateMode = (ListPreference) findPreference("vibrateMode");
        this._ChangeRingtone = (CheckBoxPreference) findPreference("changeRingtone");
        this._Ringtone = (RingtonePreference) findPreference("ringtone");
        this._ChangeNotificationTone = (CheckBoxPreference) findPreference("changeNotificationTone");
        this._NotificationTone = (RingtonePreference) findPreference("notificationTone");
        this._ChangeNotificationIcon = (CheckBoxPreference) findPreference("changeNotificationIcon");
        this._NotificationIcon = (ListPreference) findPreference("notificationIcon");
        this._NotificationIconDots = (SeekBarPreference) findPreference("notificationIconDots");
        this._NoisyContactChooser = (PreferenceScreen) findPreference("noisyContactChooser");
        this._NoisyContactChooser.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                ProfileEditActivity.this.ShowContactChooser();
                return false;
            }
        });
        this._NoisyContactVolume = (SeekBarPreference) findPreference("noisyContactRingerVolume");
        ((PreferenceScreen) findPreference("llamaTonesEditor")).setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                ProfileEditActivity.this.ShowLlamaToneChooser();
                return false;
            }
        });
        findPreference("llamaTonesHelp").setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                ProfileEditActivity.this.ShowLlamaToneHelp();
                return false;
            }
        });
        this._RingerVolume.setMax(this.am.getStreamMaxVolume(2));
        this._NoisyContactVolume.setMax(this.am.getStreamMaxVolume(2));
        this._NotificationVolume.setMax(this.am.getStreamMaxVolume(5));
        this._AlarmVolume.setMax(this.am.getStreamMaxVolume(4));
        this._MusicVolume.setMax(this.am.getStreamMaxVolume(3));
        this._SystemVolume.setMax(this.am.getStreamMaxVolume(1));
        this._InCallVolume.setMax(this.am.getStreamMaxVolume(0));
        this._VibrateMode.setEntries(new String[]{getString(R.string.hrSilent), getString(R.string.hrVibrate), getString(R.string.hrRing), getString(R.string.hrRingAndVibrate)});
        this._VibrateMode.setEntryValues(new String[]{"0", "1", "2", "3"});
        this._ChangeAlarmVolume.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                if (!ProfileEditActivity.this._ShownAlarmWarning) {
                    ProfileEditActivity.this._ShownAlarmWarning = true;
                    new Builder(ProfileEditActivity.this).setMessage("Beware of alarm clock apps that reset the alarm volume for each defined alarm. e.g. some Samsung phones. Please install another alarm app from the market that does not do this.").setPositiveButton(R.string.hrOkeyDoke, null).show();
                }
                return false;
            }
        });
        this._NotificationIcon.setEntries(getResources().getStringArray(R.array.notificationColourNames));
        this._NotificationIcon.setEntryValues(getResources().getStringArray(R.array.notificationColourValues));
        FillDialogFromProfile();
    }

    /* Access modifiers changed, original: protected */
    public void ShowLlamaToneChooser() {
        LlamaToneEditorDialog.Show(this, this._ProfileToEdit.LlamaTones, new RunnableArg<List<Tuple<String, String>>>() {
            public void Run(List<Tuple<String, String>> value) {
                ProfileEditActivity.this._ProfileToEdit.LlamaTones.clear();
                ProfileEditActivity.this._ProfileToEdit.LlamaTones.addAll(value);
            }
        });
    }

    /* Access modifiers changed, original: protected */
    public void ShowLlamaToneHelp() {
        Helpers.ShowSimpleDialogMessage(this, getString(R.string.hrLlamaTonesDetailedDescription));
    }

    private void FillDialogFromProfile() {
        Uri uri = null;
        this._ProfileName.setText(this._ProfileToEdit.Name);
        if (this._ProfileToEdit.RingVolume != null) {
            this._ChangeRingerVolume.setChecked(true);
            this._RingerVolume.setValue(this._ProfileToEdit.RingVolume.intValue());
        } else {
            this._RingerVolume.setValue(this.am.getStreamVolume(2));
            this._ChangeRingerVolume.setChecked(false);
        }
        if (this._ProfileToEdit.NotificationVolume != null) {
            this._ChangeNotificationVolume.setChecked(true);
            this._NotificationVolume.setValue(this._ProfileToEdit.NotificationVolume.intValue());
        } else {
            this._NotificationVolume.setValue(this.am.getStreamVolume(5));
            this._ChangeNotificationVolume.setChecked(false);
        }
        if (this._ProfileToEdit.Ringtone != null) {
            this._Ringtone.SetRingtoneValue(this._ProfileToEdit.Ringtone.equals(Constants.SilentRingtone) ? null : Uri.parse(this._ProfileToEdit.Ringtone));
            this._ChangeRingtone.setChecked(true);
        } else {
            this._ChangeRingtone.setChecked(false);
        }
        if (this._ProfileToEdit.NotificationTone != null) {
            RingtonePreference ringtonePreference = this._NotificationTone;
            if (!this._ProfileToEdit.NotificationTone.equals(Constants.SilentRingtone)) {
                uri = Uri.parse(this._ProfileToEdit.NotificationTone);
            }
            ringtonePreference.SetRingtoneValue(uri);
            this._ChangeNotificationTone.setChecked(true);
        } else {
            this._ChangeNotificationTone.setChecked(false);
        }
        if (this._ProfileToEdit.AlarmVolume != null) {
            this._ChangeAlarmVolume.setChecked(true);
            this._AlarmVolume.setValue(this._ProfileToEdit.AlarmVolume.intValue());
        } else {
            this._AlarmVolume.setValue(this.am.getStreamVolume(4));
            this._ChangeAlarmVolume.setChecked(false);
        }
        if (this._ProfileToEdit.MusicVolume != null) {
            this._ChangeMusicVolume.setChecked(true);
            this._MusicVolume.setValue(this._ProfileToEdit.MusicVolume.intValue());
        } else {
            this._MusicVolume.setValue(this.am.getStreamVolume(3));
            this._ChangeMusicVolume.setChecked(false);
        }
        if (this._ProfileToEdit.SystemVolume != null) {
            this._ChangeSystemVolume.setChecked(true);
            this._SystemVolume.setValue(this._ProfileToEdit.SystemVolume.intValue());
        } else {
            this._SystemVolume.setValue(this.am.getStreamVolume(1));
            this._ChangeSystemVolume.setChecked(false);
        }
        if (this._ProfileToEdit.InCallVolume != null) {
            this._ChangeInCallVolume.setChecked(true);
            this._InCallVolume.setValue(this._ProfileToEdit.InCallVolume.intValue());
        } else {
            this._InCallVolume.setValue(this.am.getStreamVolume(0));
            this._ChangeInCallVolume.setChecked(false);
        }
        if (this._ProfileToEdit.RingerMode != null) {
            this._ChangeVibrateMode.setChecked(true);
            this._VibrateMode.setValue(this._ProfileToEdit.RingerMode.intValue() + "");
        } else {
            this._ChangeVibrateMode.setChecked(false);
        }
        if (this._ProfileToEdit.LlamaNotificationIcon != null) {
            this._ChangeNotificationIcon.setChecked(true);
            this._NotificationIcon.setValue(this._ProfileToEdit.LlamaNotificationIcon.intValue() + "");
            SeekBarPreference seekBarPreference = this._NotificationIconDots;
            int intValue = (this._ProfileToEdit.LlamaNotificationIconDots == null || this._ProfileToEdit.LlamaNotificationIconDots.intValue() == -1) ? Integer.MAX_VALUE : this._ProfileToEdit.LlamaNotificationIconDots.intValue();
            seekBarPreference.setValue(intValue);
        } else {
            this._ChangeNotificationIcon.setChecked(false);
        }
        this._NoisyContactVolume.setValue(this._ProfileToEdit.NoisyContactVolume);
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Helper.HandleOnDestroy(this._OnDestoryRunnables);
        SaveDialogToProfile();
        outState.putParcelable("Profile", this._ProfileToEdit);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode != 4) {
            return super.onKeyUp(keyCode, event);
        }
        executeDone(true);
        return true;
    }

    private void executeDone(boolean success) {
        if (success) {
            SaveDialogToProfile();
            if (VERSION.SDK_INT >= 14) {
                Logging.Report("Showing ICS warning", (Context) this);
                if (this._ProfileToEdit.HasSameRingerNotificationVolumes()) {
                    executeDone2();
                    return;
                } else {
                    new Builder(this).setMessage(R.string.hrIcsRingerNotificationVolumeMessage).setPositiveButton(R.string.hrMakeSettingsTheSame, new OnClickListener() {
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            if (ProfileEditActivity.this._ProfileToEdit.RingVolume != null) {
                                Logging.Report("Set NotificationVolume to RingVolume", ProfileEditActivity.this);
                                ProfileEditActivity.this._ProfileToEdit.NotificationVolume = ProfileEditActivity.this._ProfileToEdit.RingVolume;
                            } else {
                                Logging.Report("Set RingVolumet to NotificationVolume", ProfileEditActivity.this);
                                ProfileEditActivity.this._ProfileToEdit.RingVolume = ProfileEditActivity.this._ProfileToEdit.NotificationVolume;
                            }
                            ProfileEditActivity.this.executeDone2();
                        }
                    }).setNegativeButton(R.string.hrLeaveSettingsAlone, new OnClickListener() {
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            Logging.Report("Not changed volumes", ProfileEditActivity.this);
                            ProfileEditActivity.this.executeDone2();
                        }
                    }).setNeutralButton(R.string.hrMoanToGoogle, new OnClickListener() {
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            Intent i = new Intent("android.intent.action.VIEW");
                            i.addFlags(268435456);
                            i.setData(Uri.parse("http://code.google.com/p/android/issues/detail?id=23117"));
                            ProfileEditActivity.this.startActivity(i);
                            ProfileEditActivity.this.executeDone2();
                        }
                    }).show();
                    return;
                }
            }
            executeDone2();
            return;
        }
        setResult(0, new Intent());
        finish();
    }

    private void executeDone2() {
        if (VERSION.SDK_INT < 14) {
            executeDone3();
        } else if (this._ProfileToEdit.SystemVolume != null) {
            new Builder(this).setMessage(R.string.hrIcsSystemVolumeMessage).setPositiveButton(R.string.hrSave, new OnClickListener() {
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    ProfileEditActivity.this.executeDone3();
                }
            }).setNegativeButton(R.string.hrCancel, new OnClickListener() {
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    paramDialogInterface.dismiss();
                }
            }).show();
        } else {
            executeDone3();
        }
    }

    private void executeDone3() {
        boolean i = true;
        int i2 = (this._ProfileToEdit.RingerMode == null || (!(this._ProfileToEdit.RingerMode.intValue() == 0 || this._ProfileToEdit.RingerMode.intValue() == 1) || this._ProfileToEdit.RingVolume == null || this._ProfileToEdit.RingVolume.intValue() <= 0)) ? 0 : 1;
        boolean ringerProblem = i2 != 0 ? false : true;
        if (this._ProfileToEdit.RingerMode == null || (!(this._ProfileToEdit.RingerMode.intValue() == 0 || this._ProfileToEdit.RingerMode.intValue() == 1) || this._ProfileToEdit.NotificationVolume == null || this._ProfileToEdit.NotificationVolume.intValue() <= 0)) {
            i = false;
        }
        if (i | ringerProblem) {
            new Builder(this).setMessage(R.string.hrRingerSilentButHasVolumes).setPositiveButton(R.string.hrOkeyDoke, new OnClickListener() {
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    paramDialogInterface.dismiss();
                }
            }).show();
        } else {
            executeDoneForReal();
        }
    }

    private void executeDoneForReal() {
        setResultAndFinish();
    }

    /* Access modifiers changed, original: 0000 */
    public void setResultAndFinish() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("Profile", this._ProfileToEdit);
        resultIntent.putExtra(Constants.OLD_NAME, this._OldName);
        setResult(-1, resultIntent);
        finish();
    }

    private void SaveDialogToProfile() {
        this._ProfileToEdit.Name = this._ProfileName.getText();
        if (this._ChangeRingerVolume.isChecked()) {
            Logging.Report("Change Ringer value is " + this._RingerVolume.getValue(), (Context) this);
            this._ProfileToEdit.RingVolume = Integer.valueOf(this._RingerVolume.getValue());
        } else {
            Logging.Report("Change Ringer not ticked", (Context) this);
            this._ProfileToEdit.RingVolume = null;
        }
        if (this._ChangeNotificationVolume.isChecked()) {
            this._ProfileToEdit.NotificationVolume = Integer.valueOf(this._NotificationVolume.getValue());
        } else {
            this._ProfileToEdit.NotificationVolume = null;
        }
        if (this._ChangeMusicVolume.isChecked()) {
            this._ProfileToEdit.MusicVolume = Integer.valueOf(this._MusicVolume.getValue());
        } else {
            this._ProfileToEdit.MusicVolume = null;
        }
        if (this._ChangeRingtone.isChecked()) {
            this._ProfileToEdit.Ringtone = this._Ringtone.GetRingtoneValue() == null ? Constants.SilentRingtone : this._Ringtone.GetRingtoneValue().toString();
        } else {
            this._ProfileToEdit.Ringtone = null;
        }
        if (this._ChangeNotificationTone.isChecked()) {
            this._ProfileToEdit.NotificationTone = this._NotificationTone.GetRingtoneValue() == null ? Constants.SilentRingtone : this._NotificationTone.GetRingtoneValue().toString();
        } else {
            this._ProfileToEdit.NotificationTone = null;
        }
        if (this._ChangeSystemVolume.isChecked()) {
            this._ProfileToEdit.SystemVolume = Integer.valueOf(this._SystemVolume.getValue());
        } else {
            this._ProfileToEdit.SystemVolume = null;
        }
        if (this._ChangeInCallVolume.isChecked()) {
            this._ProfileToEdit.InCallVolume = Integer.valueOf(this._InCallVolume.getValue());
        } else {
            this._ProfileToEdit.InCallVolume = null;
        }
        if (this._ChangeAlarmVolume.isChecked()) {
            this._ProfileToEdit.AlarmVolume = Integer.valueOf(this._AlarmVolume.getValue());
        } else {
            this._ProfileToEdit.AlarmVolume = null;
        }
        if (this._ChangeVibrateMode.isChecked()) {
            this._ProfileToEdit.RingerMode = Helpers.ParseIntOrNull(this._VibrateMode.getValue());
        } else {
            this._ProfileToEdit.RingerMode = null;
        }
        if (this._ChangeNotificationIcon.isChecked()) {
            this._ProfileToEdit.LlamaNotificationIcon = Helpers.ParseIntOrNull(this._NotificationIcon.getValue());
            int value = this._NotificationIconDots.getValue();
            Profile profile = this._ProfileToEdit;
            if (value == Integer.MAX_VALUE) {
                value = -1;
            }
            profile.LlamaNotificationIconDots = Integer.valueOf(value);
        } else {
            this._ProfileToEdit.LlamaNotificationIcon = null;
            this._ProfileToEdit.LlamaNotificationIconDots = null;
        }
        this._ProfileToEdit.NoisyContactVolume = this._NoisyContactVolume.getValue();
    }

    private void ShowContactChooser() {
        PeoplePickerActivity.StartPeopleIds(this, PEOPLE_CHOOSER, this._ProfileToEdit.NoisyContacts);
    }

    /* Access modifiers changed, original: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PEOPLE_CHOOSER /*123400*/:
                ArrayList<String> result = PeoplePickerActivity.ResultForPeopleIds(resultCode, data);
                if (result != null) {
                    this._ProfileToEdit.NoisyContacts = result;
                    break;
                }
                break;
        }
        Tuple<ResultCallback, Object> handler = (Tuple) this._ActivityRequests.get(Integer.valueOf(requestCode));
        if (!(handler == null || handler.Item1 == null)) {
            ((ResultCallback) handler.Item1).HandleResult(resultCode, data, handler.Item2);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onResume() {
        Thread.currentThread().setPriority(5);
        super.onResume();
    }

    public void onPause() {
        super.onPause();
        Thread.currentThread().setPriority(1);
    }

    public void RegisterActivityResult(Intent intent, ResultCallback runnable, Object extraStateInfo) {
        int thisRequestCode = this.requestCode;
        this.requestCode = thisRequestCode + 1;
        this._ActivityRequests.put(Integer.valueOf(thisRequestCode), Tuple.Create(runnable, extraStateInfo));
        startActivityForResult(intent, thisRequestCode);
    }

    public Activity GetActivity() {
        return this;
    }

    public void AddBeforeOnDestroyHandler(Runnable runnable) {
        Helper.AddBeforeOnDestroyHandler(this._OnDestoryRunnables, runnable);
    }

    public void RemoveBeforeOnDestroyHandler(Runnable runnable) {
        Helper.ClearOnDestroyHandler(this._OnDestoryRunnables, runnable);
    }
}
