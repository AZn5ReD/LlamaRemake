package com.kebab.Llama;

import android.app.Activity;
import android.content.Context;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.text.method.PasswordTransformationMethod;
import com.kebab.DialogHandlerInterface;
import com.kebab.DialogPreference;
import com.kebab.EditTextPreference;
import com.kebab.ListPreference;
import com.kebab.ListPreferenceMultiselect;
import com.kebab.OnGetValueEx;
import com.kebab.PreferenceEx;
import com.kebab.SeekBarPreference;
import com.kebab.SeekBarPreferenceNoMaxDisplay;
import com.kebab.SimplePreference;
import com.kebab.Tuple;

public abstract class EventFragment<TSelf> {
    public static final String ACTIVE_APP_CONDITION = "aa";
    public static final String AIRPLANE_MODE_CONDITION = "am";
    public static final String ANDROID_INTENT_ACTION = "ai";
    public static final String APP_NOTIFICATION_CONDITION = "na";
    public static final String AUDIO_BECOMING_NOISY_CONDITION = "an";
    public static final String BATTERY_LEVEL_CONDITION = "cl";
    public static final String BLUETOOTH_DEVICE_CONNECTED_CONDITION = "bc";
    public static final String BLUETOOTH_DEVICE_DISCONNECTED_CONDITION = "bd";
    public static final String BLUETOOTH_DEVICE_NOT_CONNECTED = "bn";
    public static final String CALENDAR_EVENT = "ce";
    public static final String CALENDAR_EVENT2 = "c2";
    public static final String CAR_MODE_ACTION = "za";
    public static final String CAR_MODE_CONDITION = "z";
    public static final String CHANGE_BRIGHTNESS_ACTION = "i";
    public static final String CHANGE_LLAMA_BLUETOOTH_POLLING_ACTION = "lb";
    public static final String CHANGE_LLAMA_LOCATION_POLLING_ACTION = "ll";
    public static final String CHANGE_LLAMA_WIFI_POLLING_ACTION = "lw";
    public static final String CHANGE_NOTIFICATION_ICON_ACTION = "ic";
    public static final String CHANGE_NOTIFICATION_ICON_ACTION2 = "i2";
    public static final String CHANGE_PASSWORD_ACTION = "pc";
    public static final String CHANGE_PROFILE_ACTION = "p";
    public static final String CHANGE_PROFILE_ACTION2 = "p2";
    public static final String CHANGE_SCREEN_TIMEOUT_ACTION = "st";
    public static final String CHANGE_VOLUME_ACTION = "v";
    public static final String CHARGING_CONDITION = "c";
    public static final String DAY_OF_THE_WEEK_CONDITION = "y";
    public static final String DESK_DOCK_CONDITION = "dd";
    public static final String ENTER_AREA_CONDITION = "e";
    public static final String GROUP_AND_CONDITION = "nd";
    public static final String GROUP_OR_CONDITION = "or";
    public static final String HEADSET_CONNECTED_CONDITION = "h";
    public static final String KEY_GUARD_ACTION = "k";
    public static final String LEAVE_AREA_CONDITION = "x";
    public static final String LLAMA_VARIABLE_CHANGED = "vc";
    public static final String LOCALE_PLUGIN_ACTION = "lo";
    public static final String MCC_MNC_CONDITION = "mn";
    public static final String MEDIA_BUTTON_ACTION = "mb";
    public static final String MOBILE_DATA_CONNECTED = "mc";
    public static final String MOBILE_DATA_ENABLED = "md";
    public static final String MUSIC_PLAYING_CONDITION = "m";
    public static final String NEXT_ALARM_CONDITION = "al";
    public static final String NFC_DETECTED_CONDITION = "nf";
    public static final String NOTIFICATION_ACTION = "n";
    public static final String NOT_IN_AREA_CONDITION = "ni";
    public static final String PHONE_REBOOT_CONDITION = "pr";
    public static final String PHONE_STATE_CONDITION = "ps";
    public static final String QUEUE_EVENT_ACTION = "qe";
    public static final String QUIT_APP_ACTION = "q";
    public static final String QUIT_APP_ROOT_ACTION = "qr";
    public static final String REBOOT_ACTION = "o";
    public static final String ROAMING_CONDITION = "rm";
    public static final String RUN_APP_ACTION = "r";
    public static final String RUN_SHORTCUT_ACTION = "rs";
    public static final String SCREEN_BACKLIGHT_CONDITION = "sf";
    public static final String SCREEN_OFF_ACTION = "sc";
    public static final String SCREEN_ON_ACTION = "so";
    public static final String SCREEN_ROTATION_ACTION = "sr";
    public static final String SCREEN_ROTATION_CONDITION = "ro";
    public static final String SET_LLAMA_VARIABLE = "vs";
    public static final String SHUTDOWN_PHONE_ACTION = "sd";
    public static final String SIGNAL_LEVEL_CONDITION = "sl";
    public static final String SIMPLE_TRIGGER_CONFIRMATION_DENIED = "cd";
    public static final String SIMPLE_TRIGGER_DELAY_FINSIHED = "td";
    public static final String SIMPLE_TRIGGER_NAMED_EVENT = "ne";
    public static final String SIMPLE_TRIGGER_NOT_APPLICABLE = "tn";
    public static final String SIMPLE_TRIGGER_REPEAT = "tr";
    public static final String SIMPLE_TRIGGER_SET_LLAMA_VARIABLE_INTENT = "sv";
    public static final String SIMPLE_TRIGGER_SHORTCUT_CUSTOM_EVENT = "cs";
    public static final String SIMPLE_TRIGGER_SHORTCUT_NAMED_EVENT = "ns";
    public static final String SIMPLE_TRIGGER_TEST_EVENT_EDITOR = "te";
    public static final String SIMPLE_TRIGGER_TEST_EVENT_LIST = "tl";
    public static final String SIMPLE_TRIGGER_USER_CONFIRMED = "uc";
    public static final String SOUND_PLAYER = "sn";
    public static final String SPEAKERPHONE_ACTION = "sp";
    public static final String SPEAK_ACTION = "sk";
    public static final String TIME_BETWEEN_CONDITION = "t";
    public static final String TOAST_ACTION = "tt";
    public static final String TOGGLE_AIRPLANE_ACTION = "l";
    public static final String TOGGLE_APN_ACTION = "a";
    public static final String TOGGLE_BLUETOOTH_ACTION = "b";
    public static final String TOGGLE_CELL_POLLING_ACTION = "cp";
    public static final String TOGGLE_FOUR_G_ACTION = "fg";
    public static final String TOGGLE_GPS_ACTION = "g";
    public static final String TOGGLE_HAPTIC_FEEDBACK_ACTION = "ha";
    public static final String TOGGLE_MOBILE_DATA_ACTION = "d";
    public static final String TOGGLE_PROFILE_LOCK_ACTION = "pl";
    public static final String TOGGLE_SYNC_ACTION = "s";
    public static final String TOGGLE_USB_ACTION = "u";
    public static final String TOGGLE_WIFI_ACTION = "w";
    public static final String TWO_G_THREE_G_ACTION = "tg";
    public static final String USER_PRESENT_CONDITION = "up";
    public static final String VIBRATE_ACTION = "vi";
    public static final String WALLPAPER_ACTION = "wa";
    public static final String WIFI_HOTSPOT_ACTION = "wh";
    public static final String WIFI_HOTSPOT_CONDITION = "wp";
    public static final String WIFI_NETWORK_CONNECTED_CONDITION = "wc";
    public static final String WIFI_NETWORK_DISCONNECTED_CONDITION = "wd";
    public static final String WIFI_SLEEP_POLICY_ACTION = "ws";

    public abstract PreferenceEx<TSelf> CreatePreference(PreferenceActivity preferenceActivity);

    public abstract String GetIsValidError(Context context);

    public abstract int GetPartsConsumption();

    public abstract boolean IsCondition();

    public abstract void ToPsvInternal(StringBuilder stringBuilder);

    public abstract String getId();

    public String ToPsv() {
        StringBuilder sb = new StringBuilder();
        ToPsv(sb);
        return sb.toString();
    }

    public void ToPsv(StringBuilder sb) {
        sb.append(getId());
        sb.append("|");
        ToPsvInternal(sb);
    }

    public static Tuple<EventFragment<?>, Integer> CreateFromFactory(String[] parts, int currentPart) {
        String part = parts[currentPart];
        EventMeta meta = (EventMeta) EventMeta.All.get(part);
        if (meta == null) {
            throw new RuntimeException("Could not find event fragment with id " + part);
        }
        try {
            return meta.Create.CreateAndUpgrade(parts, currentPart);
        } catch (Exception ex) {
            throw new RuntimeException("Could not read fragment with id " + part, ex);
        }
    }

    /* Access modifiers changed, original: protected */
    public <TDialogResult> PreferenceEx<TSelf> CreateDialogPreference(Activity activity, String title, DialogHandlerInterface<TDialogResult> dialogHelper, TDialogResult currentValue, OnGetValueEx<TSelf> onGetValueEx) {
        DialogPreference<TSelf, TDialogResult> pref = new DialogPreference(activity, currentValue, dialogHelper);
        pref.setTitle(title);
        pref.setSummary("");
        pref.SetOnGetValueExCallback(onGetValueEx);
        return pref;
    }

    /* Access modifiers changed, original: protected */
    public PreferenceEx<TSelf> CreateSeekBarPreference(Context context, String title, int min, int max, int currentValue, OnGetValueEx<TSelf> onGetValueEx) {
        SeekBarPreference<TSelf> pref = new SeekBarPreference(context, currentValue, title, min, max, "");
        pref.setTitle(title);
        pref.setSummary("");
        pref.SetOnGetValueExCallback(onGetValueEx);
        return pref;
    }

    /* Access modifiers changed, original: protected */
    public PreferenceEx<TSelf> CreateSeekBarPreference(Context context, String title, int min, int max, String topMostValue, int currentValue, OnGetValueEx<TSelf> onGetValueEx) {
        SeekBarPreference<TSelf> pref = new SeekBarPreference(context, currentValue, title, min, max, topMostValue, " " + context.getString(R.string.hrMinutes));
        pref.setTitle(title);
        pref.setSummary("");
        pref.SetOnGetValueExCallback(onGetValueEx);
        return pref;
    }

    /* Access modifiers changed, original: protected */
    public PreferenceEx<TSelf> CreateSeekBarPreferenceNoMax(Context context, String title, int min, int max, String topMostValue, int currentValue, OnGetValueEx<TSelf> onGetValueEx) {
        SeekBarPreference<TSelf> pref = new SeekBarPreferenceNoMaxDisplay(context, currentValue, title, min, max, topMostValue, " " + context.getString(R.string.hrMinutes));
        pref.setTitle(title);
        pref.setSummary("");
        pref.SetOnGetValueExCallback(onGetValueEx);
        return pref;
    }

    /* Access modifiers changed, original: protected */
    public ListPreference<TSelf> CreateListPreference(Context context, String title, String[] values, String selectedValue, OnGetValueEx<TSelf> onGetValueEx) {
        return CreateListPreference(context, title, values, selectedValue, (OnGetValueEx) onGetValueEx, null);
    }

    /* Access modifiers changed, original: protected */
    public ListPreference<TSelf> CreateListPreference(Context context, String title, String[] values, String selectedValue, OnGetValueEx<TSelf> onGetValueEx, OnPreferenceChangeListener onPreferenceChangeListener) {
        ListPreference<TSelf> pref = new ListPreference(context);
        pref.setTitle(title);
        pref.setSummary("");
        pref.setEntries(values);
        pref.setEntryValues(values);
        pref.SetOnGetValueExCallback(onGetValueEx);
        if (onPreferenceChangeListener != null) {
            pref.setOnPreferenceChangeListener(onPreferenceChangeListener);
        }
        if (selectedValue != null) {
            pref.setValue(selectedValue);
        }
        return pref;
    }

    /* Access modifiers changed, original: protected */
    public PreferenceEx<TSelf> CreateSimplePreference(PreferenceActivity context, String title, String singleValueDescription, OnGetValueEx<TSelf> onGetValueEx) {
        SimplePreference<TSelf> pref = new SimplePreference(context);
        pref.setTitle(title);
        pref.setSummary("");
        pref.setSingletonValueDescription(singleValueDescription);
        pref.SetOnGetValueExCallback(onGetValueEx);
        return pref;
    }

    /* Access modifiers changed, original: protected */
    public PreferenceEx<TSelf> CreateListPreference(Context context, String title, String[] keys, String[] values, String selectedKey, OnGetValueEx<TSelf> onGetValueEx) {
        ListPreference<TSelf> pref = new ListPreference(context);
        pref.setTitle(title);
        pref.setSummary("");
        pref.setEntries(values);
        pref.setEntryValues(keys);
        pref.SetOnGetValueExCallback(onGetValueEx);
        if (selectedKey != null) {
            pref.setValue(selectedKey);
        }
        if (values.length <= 1) {
            pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    return true;
                }
            });
        }
        return pref;
    }

    /* Access modifiers changed, original: protected */
    public PreferenceEx<TSelf> CreateListPreferenceMultiselect(Context context, String title, String[] values, Iterable<String> selectedValues, OnGetValueEx<TSelf> onGetValueEx) {
        ListPreferenceMultiselect<TSelf> pref = new ListPreferenceMultiselect(context);
        pref.setTitle(title);
        pref.setSummary("");
        pref.setEntries(values);
        pref.setEntryValues(values);
        pref.SetOnGetValueExCallback(onGetValueEx);
        if (selectedValues != null) {
            pref.setValues(selectedValues);
        }
        return pref;
    }

    /* Access modifiers changed, original: protected */
    public PreferenceEx<TSelf> CreateListPreferenceMultiselect(Context context, String title, String[] values, String[] valueKeys, Iterable<String> selectedValueKeys, OnGetValueEx<TSelf> onGetValueEx) {
        ListPreferenceMultiselect<TSelf> pref = new ListPreferenceMultiselect(context);
        pref.setTitle(title);
        pref.setSummary("");
        pref.setEntries(values);
        pref.setEntryValues(valueKeys);
        pref.SetOnGetValueExCallback(onGetValueEx);
        if (selectedValueKeys != null) {
            pref.setValues(selectedValueKeys);
        }
        return pref;
    }

    /* Access modifiers changed, original: protected */
    public PreferenceEx<TSelf> CreateEditTextPreference(Context context, String title, String value, OnGetValueEx<TSelf> onGetValueEx) {
        return CreateEditTextPreference(context, title, value, false, onGetValueEx);
    }

    /* Access modifiers changed, original: protected */
    public PreferenceEx<TSelf> CreateEditTextPreference(Context context, String title, String value, boolean isPassword, OnGetValueEx<TSelf> onGetValueEx) {
        EditTextPreference<TSelf> pref = new EditTextPreference(context);
        pref.getEditText().setSelectAllOnFocus(true);
        pref.getEditText().setInputType(16384);
        pref.setTitle(title);
        pref.setSummary("");
        pref.setText(value);
        pref.SetOnGetValueExCallback(onGetValueEx);
        if (isPassword) {
            pref.getEditText().setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        return pref;
    }
}
