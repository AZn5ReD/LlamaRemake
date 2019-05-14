package com.kebab.Llama;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings.System;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.kebab.AlertDialogEx.Builder;
import com.kebab.DateHelpers;
import com.kebab.Helpers;
import com.kebab.IterableHelpers;
import com.kebab.Llama.LlamaListTabBase.LlamaListTabBaseImpl;
import com.kebab.SeekBarDialog;
import com.kebab.SeekBarDialog.ButtonHandler;
import com.kebab.SeekBarDialogView.ValueFormatter;
import com.kebab.Tuple;
import com.kebab.Tuple3;
import java.util.ArrayList;
import java.util.Calendar;

public class ProfilesActivity extends LlamaListTabBase {
    public static String[] _RandomTips;
    BaseAdapter _Adapter;
    ArrayList<Tuple3<String, String, Integer>> _Data = new ArrayList();
    TextView _DebugText;
    ImageButton _LockButton;

    public ProfilesActivity() {
        SetImpl(new LlamaListTabBaseImpl(R.layout.tab_profiles, LlamaSettings.HelpProfiles, R.string.hrHelpProfiles) {
            public void Update() {
                ProfilesActivity.this.Update();
            }

            /* Access modifiers changed, original: protected */
            public String[] InitAndGetTabRandomTips() {
                return ProfilesActivity.this.InitAndGetTabRandomTips();
            }

            /* Access modifiers changed, original: protected */
            public CharSequence[] getContextSensitiveMenuItems() {
                return new CharSequence[0];
            }

            /* Access modifiers changed, original: protected */
            public boolean handleContextSensitiveItem(CharSequence menu) {
                return false;
            }
        });
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Instances.ProfilesActivity = this;
        this._Adapter = new BaseAdapter() {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v;
                if (convertView == null) {
                    v = View.inflate(ProfilesActivity.this, R.layout.profile_list_item, null);
                } else {
                    v = convertView;
                }
                final Tuple3<String, String, Integer> map = (Tuple3) getItem(position);
                ((ImageView) v.findViewById(R.id.iconImage)).setImageResource(((Integer) map.Item3).intValue());
                ((TextView) v.findViewById(R.id.text1)).setText((CharSequence) map.Item1);
                TextView text2 = (TextView) v.findViewById(R.id.text2);
                if (((String) map.Item2).length() > 0) {
                    text2.setVisibility(0);
                    text2.setText((CharSequence) map.Item2);
                } else {
                    text2.setVisibility(8);
                }
                ((ImageView) v.findViewById(R.id.image1)).setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        ProfilesActivity.this.EnableProfileLock((String) map.Item1);
                    }
                });
                return v;
            }

            public int getCount() {
                return ProfilesActivity.this._Data.size();
            }

            public Object getItem(int position) {
                return ProfilesActivity.this._Data.get(position);
            }

            public long getItemId(int position) {
                return (long) position;
            }
        };
        getListView().setAdapter(this._Adapter);
        registerForContextMenu(getListView());
        ((ImageButton) findViewById(R.id.addButton)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ProfilesActivity.this.AddNewProfile();
            }
        });
        this._LockButton = (ImageButton) findViewById(R.id.lockButton);
        this._LockButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ProfilesActivity.this.ToggleProfileLock();
            }
        });
        this._DebugText = (TextView) findViewById(R.id.text);
    }

    /* Access modifiers changed, original: 0000 */
    public void ToggleProfileLock() {
        if (((Boolean) LlamaSettings.ProfileLocked.GetValue(this)).booleanValue()) {
            Instances.Service.DisableProfileLock(true, true);
            Helpers.ShowTip((Context) this, getString(R.string.hrProfilesUnlocked));
            return;
        }
        EnableProfileLock(null);
    }

    public void onDestroy() {
        if (Instances.ProfilesActivity == this) {
            Instances.ProfilesActivity = null;
        }
        this._Data = null;
        this._Adapter = null;
        getListView().setAdapter(null);
        super.onDestroy();
    }

    /* Access modifiers changed, original: protected */
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Instances.Service.SetProfile((String) ((Tuple3) this._Data.get(position)).Item1, false, null, true);
    }

    public void onResume() {
        super.onResume();
        if (Instances.Service != null) {
            Update();
        }
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, Constants.MENU_ACTIVATE_AND_LOCK, 0, R.string.hrActivateAndLock);
        menu.add(0, 9, 0, R.string.hrEditProfile);
        menu.add(0, 19, 0, R.string.hrCopyProfile);
        menu.add(0, 10, 0, R.string.hrDeleteProfile);
    }

    public boolean onContextItemSelected(MenuItem item) {
        final String selectedProfileName = ((Tuple3) this._Data.get(((AdapterContextMenuInfo) item.getMenuInfo()).position)).Item1;
        switch (item.getItemId()) {
            case 8:
                Instances.Service.SetProfile(selectedProfileName, false, null, true);
                break;
            case 9:
                EditProfile(selectedProfileName);
                break;
            case 10:
                new Builder(this).setTitle(R.string.hrDeleteProfile).setCancelable(true).setMessage(String.format(getString(R.string.hrAreYouSureYouWantToDelete1), new Object[]{selectedProfileName})).setPositiveButton(R.string.hrYes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        LlamaService service = Instances.GetServiceOrRestart(ProfilesActivity.this);
                        if (service != null) {
                            service.DeleteProfileByName(selectedProfileName);
                        }
                    }
                }).setNegativeButton(R.string.hrNo, null).show();
                break;
            case 19:
                CopyProfile(selectedProfileName);
                break;
            case Constants.MENU_ACTIVATE_AND_LOCK /*118*/:
                EnableProfileLock(selectedProfileName);
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void EnableProfileLock(final String selectedProfileName) {
        int i;
        int initialValue = ((Integer) LlamaSettings.ProfileUnlockDelay.GetValue(this)).intValue();
        if (((Boolean) LlamaSettings.LongerProfileLock.GetValue(this)).booleanValue()) {
            i = Constants.PROFILE_LOCK_MAX_MINUTES_LONG;
        } else {
            i = 480;
        }
        SeekBarDialog.Show(this, initialValue, 1, i, getString(R.string.hrNever), getString(R.string.hrAutomaticallyUnlockProfileChangesAfter), getString(R.string.hrAutomaticallyUnlockProfileDescription), new ValueFormatter() {
            public String FormatValue(int value, boolean isTopMostValue, String topMostValue) {
                if (isTopMostValue) {
                    return ProfilesActivity.this.getString(R.string.hrNever) + "\n" + ProfilesActivity.this.getString(R.string.hrTheApocalypse);
                }
                String timeSpan = Helpers.GetHoursMinutesSeconds(ProfilesActivity.this, value * 60);
                Calendar time = Calendar.getInstance();
                time.add(12, value);
                String fireAtTime = DateHelpers.formatTimeNoSeconds(time);
                return timeSpan + "\n" + ProfilesActivity.this.getString(R.string.hrAtTime1, new Object[]{fireAtTime});
            }

            public int GetTextSize() {
                return 20;
            }
        }, new ButtonHandler() {
            public void Do(int lockMinutes) {
                if (selectedProfileName != null) {
                    Instances.Service.SetProfile(selectedProfileName, false, Integer.valueOf(lockMinutes), true);
                    return;
                }
                Instances.Service.DisableProfileLock(false, true);
                Instances.Service.EnableProfileLock(lockMinutes, Instances.Service.GetLastProfileName(), false);
            }
        }, null);
    }

    private void CopyProfile(String selectedProfileName) {
        Profile clonedProfile = Profile.CreateFromPsv(Instances.Service.GetProfileByName(selectedProfileName).ToPsv());
        clonedProfile.Name += " - " + getString(R.string.hrCopy);
        while (Instances.Service.GetProfileByName(clonedProfile.Name) != null) {
            clonedProfile.Name += " 2";
        }
        Instances.Service.AddProfile(clonedProfile);
        EditProfile(clonedProfile.Name);
    }

    private void EditProfile(String selectedProfileName) {
        Intent settingsActivity = new Intent(getBaseContext(), ProfileEditActivity.class);
        settingsActivity.putExtra("Profile", Instances.Service.GetProfileByName(selectedProfileName));
        settingsActivity.putExtra(Constants.EXTRA_IS_EDIT, true);
        startActivityForResult(settingsActivity, Constants.REQUEST_CODE_EDIT_PROFILE_ACTION);
    }

    /* Access modifiers changed, original: 0000 */
    public void AddNewProfile() {
        Intent settingsActivity = new Intent(getBaseContext(), ProfileEditActivity.class);
        settingsActivity.putExtra("Profile", Profile.CreateDefault(this, Instances.Service.GetProfiles().size()));
        settingsActivity.putExtra(Constants.EXTRA_IS_EDIT, false);
        startActivityForResult(settingsActivity, Constants.REQUEST_CODE_ADD_PROFILE_ACTION);
    }

    /* Access modifiers changed, original: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Profile editedProfile;
        switch (requestCode) {
            case Constants.REQUEST_CODE_EDIT_PROFILE_ACTION /*204*/:
                if (resultCode == -1) {
                    editedProfile = (Profile) data.getParcelableExtra("Profile");
                    String oldName = data.getStringExtra(Constants.OLD_NAME);
                    if (!oldName.equals(editedProfile.Name)) {
                        while (Instances.Service.GetProfileByName(editedProfile.Name) != null) {
                            editedProfile.Name += " 2";
                        }
                    }
                    Instances.Service.UpdateProfile(oldName, editedProfile);
                    return;
                }
                return;
            case Constants.REQUEST_CODE_ADD_PROFILE_ACTION /*206*/:
                if (resultCode == -1) {
                    editedProfile = (Profile) data.getParcelableExtra("Profile");
                    while (Instances.Service.GetProfileByName(editedProfile.Name) != null) {
                        editedProfile.Name += " 2";
                    }
                    Instances.Service.AddProfile(editedProfile);
                    return;
                }
                return;
            default:
                return;
        }
    }

    public void UpdateDebugInfo() {
        if (Instances.HasServiceOrRestart(getApplicationContext())) {
            String llamaLinkRingNotification;
            String llamaVibrateWhenRinging;
            String llamaVibrateNotification;
            String llamaVibrateRing;
            String llamaRingerMode;
            AudioManager am = (AudioManager) getSystemService("audio");
            String lastProfile = Instances.Service.GetLastProfileName();
            Profile p = lastProfile == null ? null : Instances.Service.GetProfileByName(lastProfile);
            String llamaRinger = p == null ? "unk" : p.RingVolume == null ? "n/a" : String.valueOf(p.RingVolume);
            String llamaNotication = p == null ? "unk" : p.NotificationVolume == null ? "n/a" : String.valueOf(p.NotificationVolume);
            String llamaAlarm = p == null ? "unk" : p.AlarmVolume == null ? "n/a" : String.valueOf(p.AlarmVolume);
            String llamaMusic = p == null ? "unk" : p.MusicVolume == null ? "n/a" : String.valueOf(p.MusicVolume);
            String llamaIncall = p == null ? "unk" : p.InCallVolume == null ? "n/a" : String.valueOf(p.InCallVolume);
            String llamaSystem = p == null ? "unk" : p.SystemVolume == null ? "n/a" : String.valueOf(p.SystemVolume);
            if (p == null) {
                llamaLinkRingNotification = "unk";
            } else if (p.RingVolume == null || p.NotificationVolume == null) {
                llamaLinkRingNotification = "n/a";
            } else {
                if (p.RingVolume == p.NotificationVolume) {
                    if (((Boolean) LlamaSettings.ControlRingtoneNotificationVolumeLink.GetValue(this)).booleanValue()) {
                        String str = "1";
                    }
                    llamaLinkRingNotification = "d/c";
                } else {
                    llamaLinkRingNotification = "0";
                }
            }
            if (p != null) {
                boolean isSilent;
                boolean isVibrate;
                if (p.RingerMode != null && p.RingerMode.intValue() == 0) {
                    isSilent = true;
                    isVibrate = false;
                } else if (p.RingerMode == null || p.RingerMode.intValue() != 1) {
                    isSilent = p.RingVolume != null && p.RingVolume.intValue() == 0 && p.NotificationVolume != null && p.NotificationVolume.intValue() == 0;
                    isVibrate = false;
                } else {
                    isSilent = false;
                    isVibrate = true;
                }
                if (p.RingerMode != null || isSilent || isVibrate) {
                    int newRingerMode;
                    int newRingerVibrate;
                    int newNotificationVibrate;
                    boolean ignoreRingerMode;
                    int fakedRingerMode = isSilent ? 0 : isVibrate ? 1 : p.RingerMode.intValue();
                    switch (fakedRingerMode) {
                        case 0:
                            newRingerMode = 0;
                            newRingerVibrate = 0;
                            newNotificationVibrate = 0;
                            ignoreRingerMode = false;
                            break;
                        case 1:
                            newRingerMode = 1;
                            newRingerVibrate = 1;
                            newNotificationVibrate = 1;
                            ignoreRingerMode = false;
                            break;
                        case 2:
                            newRingerMode = 2;
                            newRingerVibrate = 0;
                            newNotificationVibrate = 0;
                            ignoreRingerMode = false;
                            break;
                        case 3:
                            newRingerMode = 2;
                            newRingerVibrate = 1;
                            newNotificationVibrate = 1;
                            ignoreRingerMode = false;
                            break;
                        default:
                            ignoreRingerMode = true;
                            newRingerMode = 0;
                            newNotificationVibrate = 0;
                            newRingerVibrate = 0;
                            break;
                    }
                    if (ignoreRingerMode) {
                        llamaVibrateWhenRinging = "n/a";
                        llamaVibrateNotification = llamaVibrateWhenRinging;
                        llamaVibrateRing = llamaVibrateWhenRinging;
                        llamaRingerMode = llamaVibrateWhenRinging;
                    } else {
                        llamaRingerMode = String.valueOf(newRingerMode);
                        llamaVibrateRing = String.valueOf(newRingerVibrate);
                        llamaVibrateNotification = String.valueOf(newNotificationVibrate);
                        if (newRingerVibrate == 1) {
                            llamaVibrateWhenRinging = "1";
                        } else {
                            llamaVibrateWhenRinging = "0";
                        }
                    }
                } else {
                    llamaVibrateWhenRinging = "n/a";
                    llamaVibrateNotification = llamaVibrateWhenRinging;
                    llamaVibrateRing = llamaVibrateWhenRinging;
                    llamaRingerMode = llamaVibrateWhenRinging;
                }
            } else {
                llamaVibrateWhenRinging = "n/a";
                llamaVibrateNotification = llamaVibrateWhenRinging;
                llamaVibrateRing = llamaVibrateWhenRinging;
                llamaRingerMode = llamaVibrateWhenRinging;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Item         Llama  OS");
            sb.append("\nRinger       ").append(llamaRinger).append("    ").append(am.getStreamVolume(2));
            sb.append("\nNotif        ").append(llamaNotication).append("    ").append(am.getStreamVolume(5));
            sb.append("\nLinkRingNoti ").append(llamaLinkRingNotification).append("    ").append(Profile.GetNotificationUseRingVolume(getContentResolver()));
            sb.append("\nMusic        ").append(llamaMusic).append("    ").append(am.getStreamVolume(3));
            sb.append("\nAlarm        ").append(llamaAlarm).append("    ").append(am.getStreamVolume(4));
            sb.append("\nIn-call      ").append(llamaIncall).append("    ").append(am.getStreamVolume(0));
            sb.append("\nSystem       ").append(llamaSystem).append("    ").append(am.getStreamVolume(1));
            sb.append("\nRingMode     ").append(llamaRingerMode).append("    ").append(am.getRingerMode());
            sb.append("\nRingVib      ").append(llamaVibrateRing).append("    ").append(am.getVibrateSetting(0));
            sb.append("\nNotifVib     ").append(llamaVibrateNotification).append("    ").append(am.getVibrateSetting(1));
            sb.append("\nVibWhenRing  ").append(llamaVibrateWhenRinging).append("    ").append(System.getInt(getContentResolver(), "vibrate_when_ringing", -66));
            Logging.Report("VolumeChanges", sb.toString(), (Context) this);
            this._DebugText.setText(sb.toString());
            this._DebugText.setVisibility(8);
        }
    }

    public void Update() {
        if (!Instances.HasServiceOrRestart(getApplicationContext())) {
            return;
        }
        if (LlamaService.IsOnUiThread()) {
            String unlockMessage;
            UpdateDebugInfo();
            Iterable<Profile> profilesCopy = IterableHelpers.OrderBy(Instances.Service.GetProfiles(), Profile.NameComparator);
            String currentProfile = Instances.Service.GetLastProfileName();
            Tuple<Integer, Integer> currentIcon = Instances.Service.GetCurrentNotificationIcon();
            String lockedProfile = (String) LlamaSettings.ProfileAfterLockName.GetValue(this);
            String lockedUntil = (String) LlamaSettings.ProfileLockedUntilTimeString.GetValue(this);
            if (lockedUntil == null) {
                lockedProfile = null;
            }
            if (Constants.PROFILE_NEVER_UNLOCK.equals(lockedUntil)) {
                unlockMessage = getString(R.string.hrWillActivateWhenProfilesAreManuallyUnlocked);
            } else {
                unlockMessage = String.format(getString(R.string.hrWillActivateAt1), new Object[]{lockedUntil});
            }
            Calendar changeDateTime = Instances.Service.GetLastProfileDateTime();
            this._Data.clear();
            for (Profile p : profilesCopy) {
                Tuple3<String, String, Integer> map = new Tuple3();
                map.Item1 = p.Name;
                if (p.Name.equals(currentProfile)) {
                    String format;
                    if (changeDateTime != null) {
                        format = String.format(getString(R.string.hrActivatedAt1), new Object[]{DateHelpers.FormatDate(changeDateTime)});
                    } else {
                        format = getString(R.string.hrCurrentNoDash);
                    }
                    map.Item2 = Helpers.CapitaliseFirstLetter(format);
                } else if (p.Name.equals(lockedProfile)) {
                    map.Item2 = unlockMessage;
                } else {
                    map.Item2 = "";
                }
                map.Item3 = Integer.valueOf(p.GetTransformedIconResourceId(((Integer) currentIcon.Item1).intValue(), ((Integer) currentIcon.Item2).intValue(), this));
                this._Data.add(map);
            }
            this._Adapter.notifyDataSetChanged();
            if (lockedProfile != null) {
                this._LockButton.setImageDrawable(getResources().getDrawable(R.drawable.tb_locked));
                this._LockButton.setContentDescription(getString(R.string.hrProfileUnlock));
                return;
            }
            this._LockButton.setImageDrawable(getResources().getDrawable(R.drawable.tb_unlocked));
            this._LockButton.setContentDescription(getString(R.string.hrProfileLock));
            return;
        }
        runOnUiThread(new X() {
            /* Access modifiers changed, original: 0000 */
            public void R() {
                ProfilesActivity.this.Update();
            }
        });
    }

    /* Access modifiers changed, original: protected */
    public String[] InitAndGetTabRandomTips() {
        if (_RandomTips == null) {
            _RandomTips = new String[]{getString(R.string.hrProfilesTip1), getString(R.string.hrProfilesTip2), getString(R.string.hrProfilesTip3)};
        }
        return _RandomTips;
    }
}
