package com.kebab.Llama;

import android.content.Context;
import com.kebab.CachedBooleanSetting;
import com.kebab.CachedIntSetting;
import com.kebab.CachedLongSetting;
import com.kebab.CachedStringBackedDateSetting;
import com.kebab.CachedStringBackedIntSetting;
import com.kebab.CachedStringSetting;

public class LlamaSettings {
    public static CachedStringSetting AcceptedConfirmationMessages = new CachedStringSetting(null, "AcceptedConfirmationMessages", "");
    public static CachedBooleanSetting AcceptedDisclaimerMessage = new CachedBooleanSetting(null, "AcceptedDisclaimerMessage", Boolean.valueOf(false));
    public static CachedIntSetting ActiveAppWatcherMillis = new CachedIntSetting(null, "ActiveAppWatcherMillis", 1000);
    public static CachedBooleanSetting AndroidLocationEnabled = new CachedBooleanSetting(null, "AndroidLocationEnabled", Boolean.valueOf(false));
    public static CachedBooleanSetting AndroidLocationGpsEnabled = new CachedBooleanSetting(null, "AndroidLocationGpsEnabled", Boolean.valueOf(false));
    public static CachedIntSetting AndroidLocationInterval = new CachedIntSetting(null, "AndroidLocationInterval", 5);
    public static CachedBooleanSetting AutoLockProfileOnVolumeChange = new CachedBooleanSetting(null, "AutoProfileLocked", Boolean.valueOf(false));
    public static CachedBooleanSetting BlackIcons = new CachedBooleanSetting(null, "BlackIcons", Boolean.valueOf(false));
    public static final int CELL_POLLING_MODE_LEARNING = 0;
    public static final int CELL_POLLING_MODE_OFF = 2;
    public static final int CELL_POLLING_MODE_ON = 1;
    public static CachedIntSetting CellPollingActiveMillis = new CachedIntSetting(null, "CellPollingActiveMillis", 1000);
    public static CachedIntSetting CellPollingInterval = new CachedIntSetting(null, "CellPollingInterval", 5);
    public static CachedStringBackedIntSetting CellPollingMode = new CachedStringBackedIntSetting(null, "CellPollingMode", Integer.valueOf(2));
    public static CachedBooleanSetting CellPollingWithScreenWakeLock = new CachedBooleanSetting(null, "CellPollingWithScreenWakeLock", Boolean.valueOf(false));
    public static CachedBooleanSetting CellPollingWithWakeLock = new CachedBooleanSetting(null, "CellPollingWithWakeLock", Boolean.valueOf(false));
    public static CachedBooleanSetting ChangeIconIfVolumeChanges = new CachedBooleanSetting(null, "ChangeIconIfVolumeChanges", Boolean.valueOf(false));
    public static CachedStringBackedIntSetting ColourEventList = new CachedStringBackedIntSetting(null, "ColourEventList", Integer.valueOf(1));
    public static CachedBooleanSetting ControlRingtoneNotificationVolumeLink = new CachedBooleanSetting(null, "RingtoneVolumeLink", Boolean.valueOf(true));
    public static CachedBooleanSetting DebugAccessiblity = new CachedBooleanSetting(null, "DebugAccessiblity", Boolean.valueOf(false));
    public static CachedBooleanSetting DebugCellsInRecent = new CachedBooleanSetting(null, "DebugCellsInRecent", Boolean.valueOf(false));
    public static CachedStringSetting DebugTagFilter = new CachedStringSetting(null, "DebugTagFilter", null);
    public static CachedBooleanSetting DebugToasts = new CachedBooleanSetting(null, "DebugToasts", Boolean.valueOf(false));
    public static CachedBooleanSetting DontCheckVolumeInCall = new CachedBooleanSetting(null, "DontCheckVolumeInCall", Boolean.valueOf(false));
    public static final int ERROR_NOTIFICATION_ICON = 2;
    public static final int ERROR_NOTIFICATION_OFF = 0;
    public static final int ERROR_NOTIFICATION_TOAST = 1;
    public static final int EVENT_COLOURING_COLOUR_BLIND = 2;
    public static final int EVENT_COLOURING_COLOUR_BLIND_BG = 3;
    public static final int EVENT_COLOURING_OFF = 0;
    public static final int EVENT_COLOURING_ON = 1;
    public static CachedStringSetting EncryptionPassword = new CachedStringSetting("Shhh", "EncryptionPassword", "");
    public static CachedStringBackedIntSetting ErrorNotificationMode = new CachedStringBackedIntSetting(null, "ErrorNotificationMode", Integer.valueOf(1));
    public static CachedIntSetting EventRecursionLimit = new CachedIntSetting(null, "EventRecursionLimit", 5);
    public static CachedStringBackedIntSetting EventRuns = new CachedStringBackedIntSetting(null, "EventRuns", Integer.valueOf(0));
    public static CachedBooleanSetting ExtraTtsCleanup = new CachedBooleanSetting(null, "ExtraTtsCleanup", Boolean.valueOf(false));
    public static CachedStringSetting ExtraTtsCleanupText = new CachedStringSetting(null, "ExtraTtsCleanupText", ".");
    public static CachedBooleanSetting ForceNoisyContactRingtone = new CachedBooleanSetting(null, "ForceNoisyContactRingtone", Boolean.valueOf(false));
    public static CachedBooleanSetting ForcePersistant = new CachedBooleanSetting(null, "ForcePersistant", Boolean.valueOf(false));
    public static CachedBooleanSetting HadFirstRunMessage = new CachedBooleanSetting(null, "HadFirstRunMessage", Boolean.valueOf(false));
    public static CachedIntSetting HasInAppDonation = new CachedIntSetting(null, "HasInAppDonation", 0);
    public static CachedBooleanSetting HelpAreas = new CachedBooleanSetting("Help", "SeenAreas", Boolean.valueOf(false));
    public static CachedBooleanSetting HelpEvents = new CachedBooleanSetting("Help", "SeenEvents", Boolean.valueOf(false));
    public static CachedBooleanSetting HelpProfiles = new CachedBooleanSetting("Help", "SeenProfiles", Boolean.valueOf(false));
    public static CachedBooleanSetting HelpRecent = new CachedBooleanSetting("Help", "SeenRecent", Boolean.valueOf(false));
    public static CachedBooleanSetting HideDonateMenuItem = new CachedBooleanSetting(null, "HideDonateMenuItem", Boolean.valueOf(false));
    public static CachedIntSetting HistoryItems = new CachedIntSetting(null, "HistoryItems", 30);
    public static final int INAPP_DONATED = 1;
    public static final int INAPP_NOT_DONATED = 2;
    public static final int INAPP_UNKNOWN = 0;
    public static CachedBooleanSetting IgnoreInvalidCell = new CachedBooleanSetting(null, "IgnoreInvalidCell2", Boolean.valueOf(true));
    public static CachedStringBackedDateSetting InstallDate = new CachedStringBackedDateSetting(null, "InstallDate", null);
    public static CachedBooleanSetting InstantConfirmation = new CachedBooleanSetting(null, "InstantConfirmation", Boolean.valueOf(false));
    public static CachedLongSetting LastAlarmTimeMillis = new CachedLongSetting("Alarm", "LastAlarmTimeMillis", 0);
    public static CachedStringSetting LastAlarmTimeRaw = new CachedStringSetting("Alarm", "LastAlarmTimeRaw", "");
    public static CachedStringSetting LastAreaNames = new CachedStringSetting(null, "LastAreaNames", null);
    public static CachedIntSetting LastBatteryPercent = new CachedIntSetting("Battery", "LastBatteryPercent", 100);
    public static CachedIntSetting LastMessageVersion = new CachedIntSetting(null, "LastMessageVersion", 0);
    public static CachedLongSetting LastNearbyBluetoothPollTicks = new CachedLongSetting("LastTickTimes", "LastNearbyBluetoothPollTicks", 0);
    public static CachedLongSetting LastNearbyWifiPollTicks = new CachedLongSetting("LastTickTimes", "LastNearbyWifiPollTicks", 0);
    public static CachedIntSetting LastNotificationIcon = new CachedIntSetting(null, "LastNotificationIcon", -1);
    public static CachedIntSetting LastNotificationIconDots = new CachedIntSetting(null, "LastNotificationIconDots", -1);
    public static CachedBooleanSetting LastNotificationIconIsWarning = new CachedBooleanSetting(null, "LastNotificationIconIsWarning", Boolean.valueOf(false));
    public static CachedStringSetting LastProfileName = new CachedStringSetting(null, "LastProfileName", null);
    public static CachedStringSetting LastWifiAddress = new CachedStringSetting("LastWifi", "LastWifiAddress", null);
    public static CachedStringSetting LastWifiName = new CachedStringSetting("LastWifi", "LastWifiName", null);
    public static CachedBooleanSetting LlamaWasExitted = new CachedBooleanSetting(null, "LlamaWasExitted", Boolean.valueOf(false));
    public static CachedStringSetting LocaleOverride = new CachedStringSetting(null, "LocaleOverride", null);
    public static CachedBooleanSetting LocationLogging = new CachedBooleanSetting(null, "LocationLogging", Boolean.valueOf(false));
    public static CachedBooleanSetting LocationLoggingToSdCard = new CachedBooleanSetting(null, "LocationLoggingToSdCard", Boolean.valueOf(true));
    public static CachedBooleanSetting LogAllCellChanges = new CachedBooleanSetting(null, "LogAllCellChanges", Boolean.valueOf(false));
    public static CachedBooleanSetting LogSensitiveData = new CachedBooleanSetting(null, "LogSensitiveData", Boolean.valueOf(false));
    public static CachedBooleanSetting LongerProfileLock = new CachedBooleanSetting(null, "LongerProfileLock", Boolean.valueOf(false));
    public static final int MOBILE_DATA_MENU_APN = 2;
    public static final int MOBILE_DATA_MENU_BOTH = 3;
    public static final int MOBILE_DATA_MENU_DATA = 1;
    public static final int MOBILE_DATA_MENU_NONE = 0;
    public static CachedIntSetting MobileData = new CachedIntSetting("MobileData", "MobileData", -1);
    public static CachedIntSetting MobileDataConnected = new CachedIntSetting("MobileData", "MobileDataConnected", -1);
    public static CachedStringBackedIntSetting MobileDataMenuMode = new CachedStringBackedIntSetting(null, "MobileDataMenuMode", Integer.valueOf(0));
    public static CachedBooleanSetting MultiThreadedMode = new CachedBooleanSetting(null, "MultiThreadedMode", Boolean.valueOf(false));
    public static final int NOTIFICATION_MODE_OFF = 0;
    public static final int NOTIFICATION_MODE_ONGOING = 1;
    public static final int NOTIFICATION_MODE_ONGOING_HIGH_PRIORITY = 7;
    public static final int NOTIFICATION_MODE_ONGOING_ICON_ONLY = 4;
    public static final int NOTIFICATION_MODE_ONGOING_ICON_ONLY_CLEARABLE = 5;
    public static final int NOTIFICATION_MODE_ONGOING_NORMAL_PRIORITY = 6;
    public static final int NOTIFICATION_MODE_ONGOING_NO_ICON = 3;
    public static final int NOTIFICATION_MODE_TOAST = 2;
    public static CachedBooleanSetting NearbyBtEnabled = new CachedBooleanSetting(null, "NearbyBtEnabled", Boolean.valueOf(false));
    public static CachedIntSetting NearbyBtInterval = new CachedIntSetting(null, "NearbyBtInterval", 5);
    public static CachedBooleanSetting NearbyWifiDisableForHotSpot = new CachedBooleanSetting(null, "NearbyWifiDisableForHotSpot", Boolean.valueOf(false));
    public static CachedBooleanSetting NearbyWifiEnabled = new CachedBooleanSetting(null, "NearbyWifiEnabled", Boolean.valueOf(false));
    public static CachedIntSetting NearbyWifiInterval = new CachedIntSetting(null, "NearbyWifiInterval", 5);
    public static CachedBooleanSetting NotificationIconForSounds = new CachedBooleanSetting(null, "NotificationIconForSounds", Boolean.valueOf(true));
    public static CachedStringBackedIntSetting NotificationMode = new CachedStringBackedIntSetting(null, "NotificationMode", Integer.valueOf(1));
    public static CachedStringSetting ProfileAfterLockName = new CachedStringSetting(null, "ProfileAfterLockName", null);
    public static CachedBooleanSetting ProfileLocked = new CachedBooleanSetting(null, "ProfileLocked", Boolean.valueOf(false));
    public static CachedStringSetting ProfileLockedUntilTimeString = new CachedStringSetting(null, "ProfileLockedUntilTimeString", null);
    public static CachedIntSetting ProfileUnlockDelay = new CachedIntSetting(null, "ProfileUnlockDelay", 30);
    public static CachedIntSetting RecentItems = new CachedIntSetting(null, "RecentItems", 40);
    public static CachedStringSetting ReminderRingtoneUri = new CachedStringSetting(null, "ReminderRingtoneUri", null);
    public static CachedBooleanSetting ResolveContentUris = new CachedBooleanSetting(null, "ResolveContentUris", Boolean.valueOf(false));
    public static CachedBooleanSetting RevertVolumeChanges = new CachedBooleanSetting(null, "RevertVolumeChanges", Boolean.valueOf(false));
    public static CachedStringBackedIntSetting RootRebootCommand = new CachedStringBackedIntSetting(null, "RootRebootCommandType", Integer.valueOf(0));
    public static CachedStringBackedIntSetting RootShutdownCommand = new CachedStringBackedIntSetting(null, "RootShutdownCommandType", Integer.valueOf(0));
    public static CachedBooleanSetting ShowAllActionsAndConditions = new CachedBooleanSetting(null, "ShowAllActionsAndConditions", Boolean.valueOf(false));
    public static CachedBooleanSetting ShowAutoEvents = new CachedBooleanSetting(null, "ShowAutoEvents", Boolean.valueOf(false));
    public static CachedBooleanSetting StoreRecentCells = new CachedBooleanSetting(null, "StoreRecentCells", Boolean.valueOf(true));
    public static CachedBooleanSetting Use12HourTimePickers = new CachedBooleanSetting(null, "Use12HourTimePickers", Boolean.valueOf(true));
    public static CachedIntSetting UseDeprecatedVibrateSetting = new CachedIntSetting(null, "UseDeprecatedVibrateSetting", 666);
    public static CachedStringSetting VibrateWhenProfilesUnlock = new CachedStringSetting(null, "VibrateWhenProfilesUnlock", "");
    public static CachedBooleanSetting WriteToLlamaLog = new CachedBooleanSetting(null, "WriteToLlamaLog", Boolean.valueOf(false));
    public static CachedBooleanSetting ZeroRecentCells = new CachedBooleanSetting(null, "ZeroRecentCells", Boolean.valueOf(false));

    public static int GetColourNegative(Context context) {
        switch (((Integer) ColourEventList.GetValue(context)).intValue()) {
            case 2:
                return Constants.COLOUR_RED;
            case 3:
                return Constants.COLOUR_BLUE;
            default:
                return Constants.COLOUR_RED;
        }
    }

    public static int GetColourPositive(Context context) {
        switch (((Integer) ColourEventList.GetValue(context)).intValue()) {
            case 2:
                return Constants.COLOUR_BLUE;
            case 3:
                return Constants.COLOUR_GREEN;
            default:
                return Constants.COLOUR_GREEN;
        }
    }

    public static int GetColourAltPositive(Context context) {
        switch (((Integer) ColourEventList.GetValue(context)).intValue()) {
            case 2:
                return Constants.COLOUR_GREEN;
            case 3:
                return Constants.COLOUR_BLUE;
            default:
                return Constants.COLOUR_BLUE;
        }
    }
}
