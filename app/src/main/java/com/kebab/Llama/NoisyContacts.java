package com.kebab.Llama;

import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.telephony.TelephonyManager;
import java.util.HashSet;
import java.util.List;

public class NoisyContacts {
    AudioManager _AudioManager;
    HashSet<String> _ContactLookupKeys = new HashSet();
    Integer _LastNotificationVolume;
    Integer _LastPhoneVolume;
    Integer _LastRingingType;
    int _LoudVolume;
    Ringtone _Ringtone;
    LlamaService _Service;

    public NoisyContacts(LlamaService service) {
        this._Service = service;
        this._AudioManager = (AudioManager) service.getSystemService("audio");
    }

    public boolean IsRingingForNoisyContact() {
        return this._LastPhoneVolume != null;
    }

    public void HandlePhoneIntent(String phoneStateString, List<String> contactLookupKeys) {
        if (TelephonyManager.EXTRA_STATE_IDLE.equals(phoneStateString)) {
            Logging.Report("NoisyContacts", "State changed to idle", this._Service);
            StateIsNowIdle();
        } else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(phoneStateString)) {
            Logging.Report("NoisyContacts", "State changed to offhook", this._Service);
            StateIsNowIncall();
        } else if (TelephonyManager.EXTRA_STATE_RINGING.equals(phoneStateString)) {
            Logging.Report("NoisyContacts", "State changed to ringing", this._Service);
            if (IsNoisyContact(contactLookupKeys)) {
                SavePhoneVolumes();
                StartRinging();
            }
        }
    }

    private void StartRinging() {
        SetPhoneVolumesLoud();
        if (((Boolean) LlamaSettings.ForceNoisyContactRingtone.GetValue(this._Service)).booleanValue()) {
            Uri ringtoneUri = RingtoneManager.getDefaultUri(1);
            Ringtone ringtone = RingtoneManager.getRingtone(this._Service, ringtoneUri);
            if (this._Ringtone != null) {
                this._Ringtone.stop();
            }
            this._Ringtone = ringtone;
            Logging.Report("NoisyContacts", "Playing ringtone " + ringtoneUri.toString(), this._Service);
            this._Ringtone.play();
        }
    }

    private void StateIsNowIdle() {
        if (this._Ringtone != null) {
            this._Ringtone.stop();
            this._Ringtone = null;
            Logging.Report("NoisyContacts", "Llama ringtone stopped", this._Service);
        }
        ResetPhoneVolumes();
    }

    private void StateIsNowIncall() {
        if (this._Ringtone != null) {
            this._Ringtone.stop();
            this._Ringtone = null;
            Logging.Report("NoisyContacts", "Llama ringtone stopped", this._Service);
        }
        ResetPhoneVolumes();
    }

    private boolean IsNoisyContact(List<String> peopleIds) {
        boolean foundPerson = false;
        for (String peopleId : peopleIds) {
            if (this._ContactLookupKeys.contains(peopleId)) {
                foundPerson = true;
            }
        }
        if (!foundPerson) {
            return false;
        }
        Logging.Report("NoisyContacts", "Found person is a Noisy Contact", this._Service);
        return true;
    }

    private void SavePhoneVolumes() {
        if (this._LastRingingType == null && this._LastPhoneVolume == null) {
            this._LastRingingType = Integer.valueOf(this._AudioManager.getRingerMode());
            this._LastPhoneVolume = Integer.valueOf(this._AudioManager.getStreamVolume(2));
            this._LastNotificationVolume = Integer.valueOf(this._AudioManager.getStreamVolume(5));
        }
    }

    private void ResetPhoneVolumes() {
        if (this._LastRingingType != null) {
            this._AudioManager.setRingerMode(this._LastRingingType.intValue());
        }
        if (this._LastPhoneVolume != null) {
            OpenIntents.SendVolumeChanging(this._Service, 2, this._LastPhoneVolume.intValue());
            this._AudioManager.setStreamVolume(2, this._LastPhoneVolume.intValue(), 0);
            Logging.Report("NoisyContacts", "Setting ring volume back to " + this._LastPhoneVolume, this._Service);
        }
        if (this._LastNotificationVolume != null) {
            OpenIntents.SendVolumeChanging(this._Service, 5, this._LastNotificationVolume.intValue());
            this._AudioManager.setStreamVolume(5, this._LastNotificationVolume.intValue(), 0);
            Logging.Report("NoisyContacts", "Setting notification volume back to " + this._LastNotificationVolume, this._Service);
        }
        this._LastRingingType = null;
        this._LastPhoneVolume = null;
        this._LastNotificationVolume = null;
    }

    private void SetPhoneVolumesLoud() {
        OpenIntents.SendVolumeChanging(this._Service, 2, this._LoudVolume);
        OpenIntents.SendVolumeChanging(this._Service, 5, this._LoudVolume);
        Logging.Report("NoisyContacts", "Setting volume to " + this._LoudVolume, this._Service);
        this._AudioManager.setRingerMode(2);
        this._AudioManager.setStreamVolume(2, this._LoudVolume, 0);
        this._AudioManager.setStreamVolume(5, this._LoudVolume, 0);
    }

    public void SetNoisyContacts(Profile p) {
        this._ContactLookupKeys = new HashSet();
        this._ContactLookupKeys.addAll(p.NoisyContacts);
        this._LoudVolume = p.NoisyContactVolume;
        Logging.Report("NoisyContacts", "Got " + this._ContactLookupKeys.size() + " noisy contacts, with volume " + this._LoudVolume, this._Service);
    }

    public void UpdateLastPhoneVolume(Profile profile) {
        if (this._LastPhoneVolume != null) {
            Logging.Report("NoisyContacts", "Volume change while we're dealing with a noisy contact", this._Service);
            if (profile.RingerMode != null) {
                Integer newRingerMode;
                switch (profile.RingerMode.intValue()) {
                    case 0:
                        newRingerMode = Integer.valueOf(0);
                        break;
                    case 1:
                        newRingerMode = Integer.valueOf(1);
                        break;
                    case 2:
                    case 3:
                        newRingerMode = Integer.valueOf(2);
                        break;
                    default:
                        newRingerMode = null;
                        break;
                }
                if (newRingerMode != null) {
                    Logging.Report("NoisyContacts", "Setting previous ringermode from " + this._LastRingingType + " to " + newRingerMode, this._Service);
                    this._LastRingingType = newRingerMode;
                }
            }
            if (profile.RingVolume != null) {
                Logging.Report("NoisyContacts", "Setting previous ring volume from " + this._LastPhoneVolume + " to " + profile.RingVolume, this._Service);
                this._LastPhoneVolume = profile.RingVolume;
            }
            if (profile.NotificationVolume != null) {
                Logging.Report("NoisyContacts", "Setting previous notification volume from " + this._LastNotificationVolume + " to " + profile.NotificationVolume, this._Service);
                this._LastNotificationVolume = profile.NotificationVolume;
            }
        }
    }
}
