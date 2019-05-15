package com.kebab.Llama;

import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.provider.Settings.System;
import com.kebab.Llama.EventActions.ChangeNotificationIconAction;
import com.kebab.Tuple;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

public class Profile implements Parcelable {
    public static final Creator<Profile> CREATOR = new Creator<Profile>() {
        public Profile createFromParcel(Parcel in) {
            return Profile.CreateFromPsv(in.readString());
        }

        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };
    private static final int DEFAULT_NOISY_CONTACT_VOLUME = 5;
    static String LLAMA_TONE_V1 = "1";
    static final String NOISY_CONTACT_PREFIX = "c";
    static final String NOISY_GROUP_PREFIX = "g";
    public static final Comparator<Profile> NameComparator = new Comparator<Profile>() {
        public int compare(Profile x, Profile y) {
            return String.CASE_INSENSITIVE_ORDER.compare(x.Name, y.Name);
        }
    };
    public static final int RINGERMODE_RING = 2;
    public static final int RINGERMODE_SILENT = 0;
    public static final int RINGERMODE_VIBRATE = 1;
    public static final int RINGERMODE_VIBRATE_RING = 3;
    static Boolean _UseCyanogenModLinkSetting;
    public Integer AlarmVolume;
    public Integer InCallVolume;
    public Integer LlamaNotificationIcon;
    public Integer LlamaNotificationIconDots;
    public ArrayList<Tuple<String, String>> LlamaTones = new ArrayList();
    public Integer MusicVolume;
    public String Name;
    public int NoisyContactVolume = 5;
    public ArrayList<String> NoisyContacts = new ArrayList();
    public String NotificationTone;
    public Integer NotificationVolume;
    public Integer RingVolume;
    public Integer RingerMode;
    public String Ringtone;
    public Integer SystemVolume;

    public Profile(String name) {
        this.Name = name;
    }

    public boolean equals(Profile p) {
        if (p == null) {
            return false;
        }
        return p.Name.equals(this.Name);
    }

    public int hashCode() {
        return this.Name.hashCode();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(ToPsv());
    }

    private void Validate() {
        if (this.RingerMode != null) {
            switch (this.RingerMode.intValue()) {
                case 0:
                case 1:
                    if (this.RingVolume != null) {
                        this.RingVolume = Integer.valueOf(0);
                    }
                    if (this.NotificationVolume != null) {
                        this.NotificationVolume = Integer.valueOf(0);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }

    public static Profile CreateFromPsv(String psv) {
        Integer num = null;
        String[] parts = psv.split("\\|", -1);
        Profile result = new Profile(new String(LlamaStorage.SimpleUnescape(parts[0])));
        result.RingerMode = parts[1].equals("") ? null : Integer.valueOf(Integer.parseInt(parts[1]));
        result.NotificationVolume = parts[2].equals("") ? null : Integer.valueOf(Integer.parseInt(parts[2]));
        result.RingVolume = parts[3].equals("") ? null : Integer.valueOf(Integer.parseInt(parts[3]));
        result.NotificationTone = parts[4].equals("") ? null : new String(LlamaStorage.SimpleUnescape(parts[4]));
        result.Ringtone = parts[5].equals("") ? null : new String(LlamaStorage.SimpleUnescape(parts[5]));
        if (parts.length > 6) {
            result.LlamaNotificationIcon = parts[6].equals("") ? null : Integer.valueOf(Integer.parseInt(parts[6]));
        }
        if (parts.length > 10) {
            result.AlarmVolume = parts[7].equals("") ? null : Integer.valueOf(Integer.parseInt(parts[7]));
            result.MusicVolume = parts[8].equals("") ? null : Integer.valueOf(Integer.parseInt(parts[8]));
            result.SystemVolume = parts[9].equals("") ? null : Integer.valueOf(Integer.parseInt(parts[9]));
            result.InCallVolume = parts[10].equals("") ? null : Integer.valueOf(Integer.parseInt(parts[10]));
        }
        if (parts.length > 12) {
            result.NoisyContactVolume = parts[11].equals("") ? 5 : Integer.parseInt(parts[11]);
            DeserializeNoisyContactsInto(LlamaStorage.SimpleUnescape(parts[12]), result.NoisyContacts);
        }
        if (parts.length > 13) {
            if (!parts[13].equals("")) {
                num = Integer.valueOf(Integer.parseInt(parts[13]));
            }
            result.LlamaNotificationIconDots = num;
        } else if (result.LlamaNotificationIcon != null) {
            Tuple<Integer, Integer> legacy = ChangeNotificationIconAction.ConvertLegacy(result.LlamaNotificationIcon.intValue());
            result.LlamaNotificationIcon = (Integer) legacy.Item1;
            result.LlamaNotificationIconDots = (Integer) legacy.Item2;
        }
        if (parts.length > 14) {
            DeserializeLlamaTonesInto(LlamaStorage.SimpleUnescape(parts[14]), result.LlamaTones);
        }
        result.Validate();
        return result;
    }

    private static void DeserializeLlamaTonesInto(String data, ArrayList<Tuple<String, String>> llamaTones) {
        llamaTones.clear();
        ArrayList<String> values = LlamaStorage.DeserializePsvStringArrayList(data, false);
        if (((String) values.get(0)).equals(LLAMA_TONE_V1)) {
            for (int i = 1; i < values.size(); i += 2) {
                llamaTones.add(new Tuple(values.get(i), values.get(i + 1)));
            }
        }
    }

    private String SerializeLlamaTones() {
        StringBuilder sb = new StringBuilder(this.LlamaTones.size() * 10);
        sb.append(LLAMA_TONE_V1);
        Iterator i$ = this.LlamaTones.iterator();
        while (i$.hasNext()) {
            Tuple<String, String> item = (Tuple) i$.next();
            sb.append("|");
            sb.append(LlamaStorage.SimpleEscape((String) item.Item1));
            sb.append("|");
            sb.append(LlamaStorage.SimpleEscape((String) item.Item2));
        }
        return sb.toString();
    }

    private static void DeserializeNoisyContactsInto(String data, ArrayList<String> contactList) {
        contactList.clear();
        Iterator i$ = LlamaStorage.DeserializePsvStringArrayList(data, true).iterator();
        while (i$.hasNext()) {
            String value = (String) i$.next();
            if (value.length() > 2 && value.startsWith("c")) {
                contactList.add(value.substring(2));
            }
        }
    }

    private String SerializeNoisyContacts() {
        StringBuilder sb = new StringBuilder(this.NoisyContacts.size() * 10);
        LlamaStorage.SerializePsvStringArrayList(sb, this.NoisyContacts, "c:");
        return sb.toString();
    }

    public String ToPsv() {
        StringBuffer sb = new StringBuffer();
        ToPsv(sb);
        return new String(sb);
    }

    public void ToPsv(StringBuffer sb) {
        Validate();
        sb.append(LlamaStorage.SimpleEscape(this.Name)).append("|");
        if (this.RingerMode != null) {
            sb.append(this.RingerMode);
        }
        sb.append("|");
        if (this.NotificationVolume != null) {
            sb.append(this.NotificationVolume);
        }
        sb.append("|");
        if (this.RingVolume != null) {
            sb.append(this.RingVolume);
        }
        sb.append("|");
        if (this.NotificationTone != null) {
            sb.append(LlamaStorage.SimpleEscape(this.NotificationTone));
        }
        sb.append("|");
        if (this.Ringtone != null) {
            sb.append(LlamaStorage.SimpleEscape(this.Ringtone));
        }
        sb.append("|");
        if (this.LlamaNotificationIcon != null) {
            sb.append(this.LlamaNotificationIcon);
        }
        sb.append("|");
        if (this.AlarmVolume != null) {
            sb.append(this.AlarmVolume);
        }
        sb.append("|");
        if (this.MusicVolume != null) {
            sb.append(this.MusicVolume);
        }
        sb.append("|");
        if (this.SystemVolume != null) {
            sb.append(this.SystemVolume);
        }
        sb.append("|");
        if (this.InCallVolume != null) {
            sb.append(this.InCallVolume);
        }
        sb.append("|");
        sb.append(this.NoisyContactVolume);
        sb.append("|");
        sb.append(LlamaStorage.SimpleEscape(SerializeNoisyContacts()));
        sb.append("|");
        if (this.LlamaNotificationIconDots != null) {
            sb.append(this.LlamaNotificationIconDots);
        }
        sb.append("|");
        sb.append(LlamaStorage.SimpleEscape(SerializeLlamaTones()));
    }

    public void Activate(LlamaService service, OngoingNotification ogNo, NoisyContacts noisyContacts) {
        Logging.Report("Activating profile", (Context) service);
        if (Build.ID != null && Build.ID.toLowerCase().contains("miui")) {
            service.HandleFriendlyError(Integer.valueOf(1));
        }
        AudioManager audio = (AudioManager) service.getSystemService("audio");
        if (this.SystemVolume != null) {
            OpenIntents.SendVolumeChanging(service, 1, this.SystemVolume.intValue());
            audio.setStreamVolume(1, this.SystemVolume.intValue(), 0);
        }
        if (VERSION.SDK_INT < 14) {
            SetRingerRingtoneNoficationsBelowApi14(audio, service);
        } else {
            SetRingerRingtoneNoficationsAtOrAboveApi14(audio, service);
        }
        if (this.AlarmVolume != null) {
            OpenIntents.SendVolumeChanging(service, 4, this.AlarmVolume.intValue());
            audio.setStreamVolume(4, this.AlarmVolume.intValue(), 0);
        }
        if (this.MusicVolume != null) {
            OpenIntents.SendVolumeChanging(service, 3, this.MusicVolume.intValue());
            audio.setStreamVolume(3, this.MusicVolume.intValue(), 0);
        }
        if (this.InCallVolume != null) {
            OpenIntents.SendVolumeChanging(service, 0, this.InCallVolume.intValue());
            audio.setStreamVolume(0, this.InCallVolume.intValue(), 0);
        }
        if (this.Ringtone != null) {
            Logging.Report("Profiles", "Setting ringtone. Value is[" + this.Ringtone + "]", (Context) service);
            try {
                RingtoneManager.setActualDefaultRingtoneUri(service, 1, this.Ringtone.equals(Constants.SilentRingtone) ? null : Uri.parse(this.Ringtone));
            } catch (Exception ex) {
                Logging.Report(ex, (Context) service);
                service.HandleFriendlyError(service.getString(R.string.hrRingtoneChangeError1, new Object[]{this.Name}), false);
            }
        } else {
            Logging.Report("Profiles", "Not setting ringtone. Value is NULL", (Context) service);
        }
        if (this.NotificationTone != null) {
            Logging.Report("Profiles", "Setting notiftone. Value is[" + this.NotificationTone + "]", (Context) service);
            try {
                RingtoneManager.setActualDefaultRingtoneUri(service, 2, this.NotificationTone.equals(Constants.SilentRingtone) ? null : Uri.parse(this.NotificationTone));
            } catch (Exception ex2) {
                Logging.Report(ex2, (Context) service);
                service.HandleFriendlyError(service.getString(R.string.hrRingtoneChangeError1, new Object[]{this.Name}), false);
            }
        } else {
            Logging.Report("Profiles", "Not setting notifytone. Value is NULL", (Context) service);
        }
        service.SetCurrentLlamaTones(this);
        service.SetNoisyContacts(this);
        if (noisyContacts != null) {
            noisyContacts.UpdateLastPhoneVolume(this);
        }
        service.SetNotificationIcon(this.LlamaNotificationIcon, this.LlamaNotificationIconDots);
        if (ogNo != null) {
            ogNo.SetCurrentProfileName(this.Name);
            MinimalisticTextIntegration.SetProfileName(service, this.Name);
            OpenIntents.SendProfileChange(service, this.Name);
        }
    }

    private void SetRingerRingtoneNoficationsBelowApi14(AudioManager audio, LlamaService service) {
        Logging.Report("Profiles", "Running SetRingerRingtoneNoficationsBelowApi14", (Context) service);
        if (!(this.RingVolume == null && this.NotificationVolume == null)) {
            final int notificatinVolumeIsRingVolume;
            int ringerVolume = audio.getStreamVolume(2);
            int notificationVolume = audio.getStreamVolume(5);
            int oldNotificatinVolumeIsRingVolume = GetNotificationUseRingVolume(service.getContentResolver());
            if (this.RingVolume != null) {
                ringerVolume = this.RingVolume.intValue();
            }
            if (this.NotificationVolume != null) {
                notificationVolume = this.NotificationVolume.intValue();
            }
            if (ringerVolume == notificationVolume) {
                notificatinVolumeIsRingVolume = 1;
            } else {
                notificatinVolumeIsRingVolume = 0;
                if (VERSION.SDK_INT >= 14) {
                    service.HandleFriendlyError(Integer.valueOf(2));
                }
            }
            final int capturedRingerVolume = ringerVolume;
            final int capturedNotificationVolume = notificationVolume;
            final LlamaService llamaService = service;
            final AudioManager audioManager = audio;
            Runnable changeVolumeLevels = new Runnable() {
                public void run() {
                    OpenIntents.SendVolumeChanging(llamaService, 2, capturedRingerVolume);
                    OpenIntents.SendVolumeChanging(llamaService, 5, capturedNotificationVolume);
                    Logging.Report("Setting volume for notification/volume link " + capturedRingerVolume + " and " + capturedNotificationVolume, llamaService);
                    audioManager.setStreamVolume(2, capturedRingerVolume, 0);
                    audioManager.setStreamVolume(5, capturedNotificationVolume, 0);
                    llamaService._ProfileIsChanging = false;
                    Logging.Report("Completed volume for notification/volume link", llamaService);
                    if (((Boolean) LlamaSettings.ControlRingtoneNotificationVolumeLink.GetValue(llamaService)).booleanValue() && notificatinVolumeIsRingVolume == 1) {
                        Profile.SetNotificationUseRingVolume(llamaService.getContentResolver(), notificatinVolumeIsRingVolume);
                    }
                }
            };
            SetNotificationUseRingVolume(service.getContentResolver(), 0);
            if (oldNotificatinVolumeIsRingVolume == notificatinVolumeIsRingVolume) {
                Logging.Report("Set volume is simple", (Context) service);
                changeVolumeLevels.run();
            } else {
                Logging.Report("Need to change notification/volume link to " + notificatinVolumeIsRingVolume + ". Setting volumes to " + capturedRingerVolume + "," + capturedNotificationVolume + " first", (Context) service);
                OpenIntents.SendVolumeChanging(service, 2, capturedRingerVolume);
                OpenIntents.SendVolumeChanging(service, 5, capturedNotificationVolume);
                audio.setStreamVolume(2, capturedRingerVolume, 0);
                audio.setStreamVolume(5, capturedNotificationVolume, 0);
                service._ProfileIsChanging = true;
                Logging.Report("Starting handler for notification/volume link", (Context) service);
                new Handler().postDelayed(changeVolumeLevels, 200);
            }
        }
        if (this.RingerMode != null) {
            int newRingerMode;
            int newRingerVibrate;
            int newNotificationVibrate;
            boolean ignoreRingerMode;
            switch (this.RingerMode.intValue()) {
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
            if (!ignoreRingerMode) {
                int oldRingerMode = audio.getRingerMode();
                int oldNotificationVibrate = audio.getVibrateSetting(1);
                int oldRingerVibrate = audio.getVibrateSetting(0);
                Logging.Report("RingerMode change " + oldRingerMode + "," + oldNotificationVibrate + "," + oldRingerVibrate + " to " + newRingerMode + "," + newNotificationVibrate + "," + newRingerVibrate, (Context) service);
                if (oldRingerMode != newRingerMode) {
                    audio.setRingerMode(newRingerMode);
                }
                if (!(oldNotificationVibrate == newNotificationVibrate || ((Integer) LlamaSettings.UseDeprecatedVibrateSetting.GetValue(service)).intValue() == 0)) {
                    audio.setVibrateSetting(1, newNotificationVibrate);
                }
                if (oldRingerVibrate != newRingerVibrate && ((Integer) LlamaSettings.UseDeprecatedVibrateSetting.GetValue(service)).intValue() != 0) {
                    audio.setVibrateSetting(0, newRingerVibrate);
                }
            }
        }
    }

    private void SetRingerRingtoneNoficationsAtOrAboveApi14(AudioManager audio, LlamaService service) {
        final boolean isSilent;
        final boolean isVibrate;
        Logging.Report("Profiles", "Running SetRingerRingtoneNoficationsAtOrAboveApi14", (Context) service);
        if (this.RingerMode != null && this.RingerMode.intValue() == 0) {
            isSilent = true;
            isVibrate = false;
        } else if (this.RingerMode == null || this.RingerMode.intValue() != 1) {
            isSilent = this.RingVolume != null && this.RingVolume.intValue() == 0 && this.NotificationVolume != null && this.NotificationVolume.intValue() == 0;
            isVibrate = false;
        } else {
            isSilent = false;
            isVibrate = true;
        }
        Runnable _tempchangeRingerSilentVibrate = null;
        if (this.RingerMode != null || isSilent || isVibrate) {
            final AudioManager audioManager = audio;
            final LlamaService llamaService = service;
            _tempchangeRingerSilentVibrate = new Runnable() {
                public void run() {
                    int newRingerMode;
                    int newRingerVibrate;
                    int newNotificationVibrate;
                    boolean ignoreRingerMode;
                    int fakedRingerMode = isSilent ? 0 : isVibrate ? 1 : Profile.this.RingerMode.intValue();
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
                    if (!ignoreRingerMode) {
                        int oldRingerMode = audioManager.getRingerMode();
                        int oldNotificationVibrate = audioManager.getVibrateSetting(1);
                        int oldRingerVibrate = audioManager.getVibrateSetting(0);
                        Logging.Report("RingerMode change " + oldRingerMode + "," + oldNotificationVibrate + "," + oldRingerVibrate + " to " + newRingerMode + "," + newNotificationVibrate + "," + newRingerVibrate, llamaService);
                        if (oldRingerMode != newRingerMode) {
                            audioManager.setRingerMode(newRingerMode);
                        }
                        if (!(oldNotificationVibrate == newNotificationVibrate || ((Integer) LlamaSettings.UseDeprecatedVibrateSetting.GetValue(llamaService)).intValue() == 0)) {
                            audioManager.setVibrateSetting(1, newNotificationVibrate);
                        }
                        if (!(oldRingerVibrate == newRingerVibrate || ((Integer) LlamaSettings.UseDeprecatedVibrateSetting.GetValue(llamaService)).intValue() == 0)) {
                            audioManager.setVibrateSetting(0, newRingerVibrate);
                        }
                        if (newRingerVibrate == 1) {
                            System.putInt(llamaService.getContentResolver(), "vibrate_when_ringing", 1);
                        } else {
                            System.putInt(llamaService.getContentResolver(), "vibrate_when_ringing", 0);
                        }
                    }
                }
            };
        }
        final Runnable changeRingerSilentVibrate = _tempchangeRingerSilentVibrate;
        Runnable runnable;
        if (this.RingVolume == null && this.NotificationVolume == null) {
            if (changeRingerSilentVibrate != null) {
                changeRingerSilentVibrate.run();
            }
            runnable = null;
            return;
        }
        final int notificatinVolumeIsRingVolume;
        final int oldRingerVolume = audio.getStreamVolume(2);
        final int oldNotificationVolume = audio.getStreamVolume(5);
        int oldNotificatinVolumeIsRingVolume = GetNotificationUseRingVolume(service.getContentResolver());
        int ringerVolume = oldRingerVolume;
        int notificationVolume = oldNotificationVolume;
        if (this.RingVolume != null) {
            ringerVolume = this.RingVolume.intValue();
        }
        if (this.NotificationVolume != null) {
            notificationVolume = this.NotificationVolume.intValue();
        }
        if (isSilent || isVibrate) {
            ringerVolume = 0;
            notificationVolume = 0;
        }
        if (ringerVolume == notificationVolume) {
            notificatinVolumeIsRingVolume = 1;
        } else {
            notificatinVolumeIsRingVolume = 0;
            if (VERSION.SDK_INT >= 14) {
                service.HandleFriendlyError(Integer.valueOf(2));
            }
        }
        final int capturedRingerVolume = ringerVolume;
        final int capturedNotificationVolume = notificationVolume;
        final LlamaService llamaService2 = service;
        final boolean z = isSilent;
        final boolean z2 = isVibrate;
        final AudioManager audioManager2 = audio;
        runnable = new Runnable() {
            public void run() {
                OpenIntents.SendVolumeChanging(llamaService2, 2, capturedRingerVolume);
                OpenIntents.SendVolumeChanging(llamaService2, 5, capturedNotificationVolume);
                if ((z || z2) && oldRingerVolume == 0 && oldNotificationVolume == 0) {
                    Logging.Report("Volumes were both silent and ringer mode is silent/vibrate. Not changing volumes", llamaService2);
                } else {
                    Logging.Report("Setting volume for notification/volume link " + capturedRingerVolume + " and " + capturedNotificationVolume, llamaService2);
                    audioManager2.setStreamVolume(2, capturedRingerVolume, 0);
                    audioManager2.setStreamVolume(5, capturedNotificationVolume, 0);
                }
                if (changeRingerSilentVibrate != null) {
                    changeRingerSilentVibrate.run();
                }
                llamaService2._ProfileIsChanging = false;
                Logging.Report("Completed volume for notification/volume link", llamaService2);
                if (((Boolean) LlamaSettings.ControlRingtoneNotificationVolumeLink.GetValue(llamaService2)).booleanValue() && notificatinVolumeIsRingVolume == 1) {
                    Profile.SetNotificationUseRingVolume(llamaService2.getContentResolver(), notificatinVolumeIsRingVolume);
                }
            }
        };
        boolean needToDelayAfterRingNotifyLinkRemove = oldNotificatinVolumeIsRingVolume != notificatinVolumeIsRingVolume;
        SetNotificationUseRingVolume(service.getContentResolver(), 0);
        if (needToDelayAfterRingNotifyLinkRemove) {
            Logging.Report("Need to change notification/volume link to " + notificatinVolumeIsRingVolume + ". Setting volumes to " + ringerVolume + "," + notificationVolume + " first", (Context) service);
            OpenIntents.SendVolumeChanging(service, 2, ringerVolume);
            OpenIntents.SendVolumeChanging(service, 5, notificationVolume);
            audio.setStreamVolume(2, ringerVolume, 0);
            audio.setStreamVolume(5, notificationVolume, 0);
            service._ProfileIsChanging = true;
            Logging.Report("Starting handler for notification/volume link", (Context) service);
            new Handler().postDelayed(runnable, 200);
            return;
        }
        Logging.Report("Set volume is simple", (Context) service);
        runnable.run();
    }

    private static void SetRingerRingtoneNoficationsAtOrAboveApi14version2notused(Profile profile, AudioManager audio, LlamaService service) {
        Integer profileRingerMode = profile.RingerMode;
        Integer profileRingVolume = profile.RingVolume;
        Integer profileNotifVolume = profile.NotificationVolume;
        if (profileRingerMode == null && !(profileRingVolume == null && profileNotifVolume == null)) {
            int currentAudioMode = audio.getRingerMode();
            if (currentAudioMode == 0) {
                profileRingerMode = Integer.valueOf(1);
            } else if (currentAudioMode == 1) {
                profileRingerMode = Integer.valueOf(3);
            }
        }
        if (profileRingerMode != null) {
            if (profileRingerMode.intValue() == 0) {
                audio.setRingerMode(0);
                audio.setVibrateSetting(1, 0);
                audio.setVibrateSetting(0, 0);
                profileRingVolume = null;
                profileNotifVolume = null;
            } else if (profileRingerMode.intValue() == 1) {
                System.putInt(service.getContentResolver(), "vibrate_when_ringing", 1);
                audio.setRingerMode(1);
                audio.setVibrateSetting(1, 1);
                audio.setVibrateSetting(0, 1);
                profileRingVolume = null;
                profileNotifVolume = null;
            } else if (profileRingerMode.intValue() == 2) {
                System.putInt(service.getContentResolver(), "vibrate_when_ringing", 0);
                audio.setRingerMode(2);
                audio.setVibrateSetting(1, 0);
                audio.setVibrateSetting(0, 0);
            } else if (profileRingerMode.intValue() == 3) {
                System.putInt(service.getContentResolver(), "vibrate_when_ringing", 1);
                audio.setRingerMode(2);
                audio.setVibrateSetting(1, 1);
                audio.setVibrateSetting(0, 1);
            }
        }
        if (profileRingVolume != null || profileNotifVolume != null) {
            final int notificatinVolumeIsRingVolume;
            int oldRingerVolume = audio.getStreamVolume(2);
            int oldNotificationVolume = audio.getStreamVolume(5);
            int oldNotificatinVolumeIsRingVolume = GetNotificationUseRingVolume(service.getContentResolver());
            int ringerVolume = oldRingerVolume;
            int notificationVolume = oldNotificationVolume;
            if (profileRingVolume != null) {
                ringerVolume = profileRingVolume.intValue();
            }
            if (profileNotifVolume != null) {
                notificationVolume = profileNotifVolume.intValue();
            }
            if (ringerVolume == notificationVolume) {
                notificatinVolumeIsRingVolume = 1;
            } else {
                notificatinVolumeIsRingVolume = 0;
                if (VERSION.SDK_INT >= 14) {
                    service.HandleFriendlyError(Integer.valueOf(2));
                }
            }
            final int capturedRingerVolume = ringerVolume;
            final int capturedNotificationVolume = notificationVolume;
            final LlamaService llamaService = service;
            final AudioManager audioManager = audio;
            Runnable changeVolumeLevels = new Runnable() {
                public void run() {
                    OpenIntents.SendVolumeChanging(llamaService, 2, capturedRingerVolume);
                    OpenIntents.SendVolumeChanging(llamaService, 5, capturedNotificationVolume);
                    Logging.Report("Setting volume for notification/volume link " + capturedRingerVolume + " and " + capturedNotificationVolume, llamaService);
                    audioManager.setStreamVolume(2, capturedRingerVolume, 0);
                    audioManager.setStreamVolume(5, capturedNotificationVolume, 0);
                    llamaService._ProfileIsChanging = false;
                    Logging.Report("Completed volume for notification/volume link", llamaService);
                    if (((Boolean) LlamaSettings.ControlRingtoneNotificationVolumeLink.GetValue(llamaService)).booleanValue() && notificatinVolumeIsRingVolume == 1) {
                        Profile.SetNotificationUseRingVolume(llamaService.getContentResolver(), notificatinVolumeIsRingVolume);
                    }
                }
            };
            SetNotificationUseRingVolume(service.getContentResolver(), 0);
            if (oldNotificatinVolumeIsRingVolume == notificatinVolumeIsRingVolume) {
                Logging.Report("Set volume is simple", (Context) service);
                changeVolumeLevels.run();
                return;
            }
            Logging.Report("Need to change notification/volume link to " + notificatinVolumeIsRingVolume + ". Setting volumes to " + capturedRingerVolume + "," + capturedNotificationVolume + " first", (Context) service);
            OpenIntents.SendVolumeChanging(service, 2, capturedRingerVolume);
            OpenIntents.SendVolumeChanging(service, 5, capturedNotificationVolume);
            audio.setStreamVolume(2, capturedRingerVolume, 0);
            audio.setStreamVolume(5, capturedNotificationVolume, 0);
            service._ProfileIsChanging = true;
            Logging.Report("Starting handler for notification/volume link", (Context) service);
            new Handler().postDelayed(changeVolumeLevels, 200);
        }
    }

    public static int GetNotificationUseRingVolume(ContentResolver contentResolver) {
        if (_UseCyanogenModLinkSetting == null) {
            _UseCyanogenModLinkSetting = Boolean.valueOf(System.getInt(contentResolver, "volume_link_notification", 666) != 666);
        }
        if (_UseCyanogenModLinkSetting.booleanValue()) {
            return System.getInt(contentResolver, "volume_link_notification", 0);
        }
        return System.getInt(contentResolver, "notifications_use_ring_volume", 0);
    }

    private static void SetNotificationUseRingVolume(ContentResolver contentResolver, int value) {
        if (_UseCyanogenModLinkSetting == null) {
            _UseCyanogenModLinkSetting = Boolean.valueOf(System.getInt(contentResolver, "volume_link_notification", 666) != 666);
        }
        System.putInt(contentResolver, "notifications_use_ring_volume", value);
        if (_UseCyanogenModLinkSetting.booleanValue()) {
            System.putInt(contentResolver, "volume_link_notification", value);
        }
    }

    public int Matches(Profile other) {
        int matches = 0;
        if (this.NotificationTone != null && this.NotificationTone.equals(other.NotificationTone)) {
            matches = 0 + 1;
        }
        if (this.Ringtone != null && this.Ringtone.equals(other.Ringtone)) {
            matches++;
        }
        if (this.NotificationVolume != null && this.NotificationVolume.equals(other.NotificationVolume)) {
            matches++;
        }
        if (this.RingVolume != null && this.RingVolume.equals(other.RingVolume)) {
            matches++;
        }
        if (this.RingerMode == null || other.RingerMode == null || !this.RingerMode.equals(other.RingerMode)) {
            return matches;
        }
        return matches + 1;
    }

    public static boolean ObjectEquals(Object o1, Object o2) {
        if (o1 == null) {
            return o2 == null;
        } else {
            return o1.equals(o2);
        }
    }

    public static final Profile CreateDefault(Context context, int count) {
        Profile p = new Profile(String.format(context.getString(R.string.hrNewProfile1), new Object[]{Integer.valueOf(count + 1)}));
        p.NotificationVolume = Integer.valueOf(5);
        p.RingerMode = Integer.valueOf(3);
        p.RingVolume = Integer.valueOf(5);
        return p;
    }

    public static Profile DetectProfile(Context context) {
        Uri ringToneUri;
        Uri notificationToneUri;
        Profile p = new Profile("DetectedProfile");
        AudioManager audio = (AudioManager) context.getSystemService("audio");
        p.RingVolume = Integer.valueOf(audio.getStreamVolume(2));
        p.RingVolume = Integer.valueOf(audio.getStreamVolume(5));
        int ringerMode = audio.getRingerMode();
        if (ringerMode == 0) {
            p.RingerMode = Integer.valueOf(0);
        } else if (ringerMode == 1) {
            p.RingerMode = Integer.valueOf(1);
        } else if (ringerMode == 2) {
            p.RingerMode = Integer.valueOf(3);
        } else if (ringerMode == 2) {
            p.RingerMode = Integer.valueOf(2);
        }
        try {
            ringToneUri = RingtoneManager.getActualDefaultRingtoneUri(context, 1);
        } catch (Exception ex) {
            Logging.Report(new Exception("Error reading ringtone", ex), context);
            ringToneUri = null;
        }
        try {
            notificationToneUri = RingtoneManager.getActualDefaultRingtoneUri(context, 2);
        } catch (Exception ex2) {
            Logging.Report(new Exception("Error reading notificationtone", ex2), context);
            notificationToneUri = null;
        }
        p.Ringtone = ringToneUri == null ? Constants.SilentRingtone : ringToneUri.toString();
        p.NotificationTone = notificationToneUri == null ? Constants.SilentRingtone : notificationToneUri.toString();
        return p;
    }

    public int GetTransformedIconResourceId(int existingIcon, int existingDots, Context context) {
        int icon;
        int dots;
        if (this.LlamaNotificationIcon == null || this.LlamaNotificationIcon.intValue() == -1) {
            icon = existingIcon;
        } else {
            icon = this.LlamaNotificationIcon.intValue();
        }
        if (this.LlamaNotificationIconDots == null || this.LlamaNotificationIconDots.intValue() == -1) {
            dots = existingDots;
        } else {
            dots = this.LlamaNotificationIconDots.intValue();
        }
        return NotificationIcon.GetResourceId(icon, dots, false, ((Boolean) LlamaSettings.BlackIcons.GetValue(context)).booleanValue());
    }

    public boolean HasSameRingerNotificationVolumes() {
        if (this.NotificationVolume == null) {
            if (this.RingVolume == null) {
                return true;
            }
            return false;
        } else if (this.RingVolume == null) {
            return false;
        } else {
            if (this.NotificationVolume != this.RingVolume) {
                return false;
            }
            return true;
        }
    }

    public boolean HasRingerChangeButNoVolume() {
        if (this.RingerMode == null || ((this.RingerMode.intValue() != 2 && this.RingerMode.intValue() != 3) || this.RingVolume != null || this.NotificationVolume != null)) {
            return false;
        }
        return true;
    }
}
