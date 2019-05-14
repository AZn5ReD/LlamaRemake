package com.kebab.Llama;

import android.graphics.Color;

public class Constants {
    public static final String ACTION_CONFIRM_EVENT = "com.kebab.Llama.ConfirmEvent";
    public static final String ACTION_NOTIFICATION_BUTTON = "com.kebab.Llama.NotificationButton";
    public static final String ACTION_NOTIFICATION_CLEAR = "com.kebab.Llama.NotificationClear";
    public static final String ACTION_PROXI_CHANGED = "com.kebab.Llama.ProxiChanged";
    public static final String ACTION_RTC_BT_POLL = "com.kebab.Llama.RtcBtPoll";
    public static final String ACTION_RTC_WIFI_POLL = "com.kebab.Llama.RtcWifiPoll";
    public static final String ACTION_RUN_SHORTCUT = "com.kebab.Llama.RunShortcut";
    public static final String ACTION_SET_LLAMA_VARIABLE = "com.kebab.Llama.SetLlamaVariable";
    public static final String ACTION_STOP_ALL_SOUNDS = "com.kebab.Llama.StopAllSounds";
    public static final String ACTION_UI_NOTIFICATION = "com.kebab.Llama.UiNotification";
    public static final int BUILD_VERSION_FROYO = 8;
    public static final int BUILD_VERSION_ICS = 14;
    public static final int BUILD_VERSION_JELLYBEAN16 = 16;
    public static final int BUILD_VERSION_JELLYBEAN17 = 17;
    public static final int BUILD_VERSION_JELLYBEAN18 = 18;
    public static final int COLOUR_BLUE = Color.rgb(87, 87, 235);
    public static final int COLOUR_CYAN = Color.rgb(87, 235, 235);
    public static final int COLOUR_GREEN = Color.rgb(60, 213, 60);
    public static final int COLOUR_MAGENTA = Color.rgb(235, 87, 235);
    public static final int COLOUR_RED = Color.rgb(220, 30, 30);
    public static final int COLOUR_YELLOW = Color.rgb(235, 235, 87);
    public static final int CPU_WAKER = 1001;
    public static final int CURRENT_VERSION = 2;
    public static final int DONATE_DAYS = 14;
    public static final int DONATE_RUNS = 10;
    public static final String EVENT = "Event";
    public static final String EXTRA_ANONYMOUS_EVENT = "LlamaAnonymousEvent";
    public static final String EXTRA_CONDITION_EDIT_MODE = "ConditionEdit";
    public static final String EXTRA_CONDITION_OR_MODE = "ConditionOr";
    public static final String EXTRA_EVENT_OR_SHOULD_TRIGGER_EVERY_TIME = "TriggerEveryTime";
    public static final String EXTRA_EVENT_QUEUE_DELAY = "EventQueueDelay";
    public static final String EXTRA_EVENT_QUEUE_DELAY_SECONDS = "EventQueueDelaySeconds";
    public static final String EXTRA_INITIAL_STICKY = "LlamaInitialStickyB";
    public static final String EXTRA_IS_EDIT = "IsEdit";
    public static final String EXTRA_LLAMA_SHORTCUT_DATA = "LlamaData";
    public static final String EXTRA_LLAMA_SHORTCUT_TYPE = "LlamaType";
    public static final String EXTRA_NOTIFICATION_BUTTON_INDEX = "NotificationButtonIndex";
    public static final String EXTRA_NOTIFICATION_CONFIRMATION_MESSAGE_ID = "LlamaConfirmationId";
    public static final String EXTRA_NOTIFICATION_EVENT_NAME = "LlamaEventName";
    public static final String EXTRA_NOTIFICATION_ID_TO_CLEAR = "LlamaNotificationId";
    public static final String EXTRA_NOTIFICATION_MESSAGE = "LlamaMessage";
    public static final String EXTRA_NOTIFICATION_TITLE = "LlamaTitle";
    public static final String EXTRA_PACKAGE_NAME = "PackageName";
    public static final String EXTRA_QUEUED_EVENT = "QueuedEvent";
    public static final String EXTRA_SCROLL_TO_LLAMA_SECURITY = "scrollToLlamaSecurity";
    public static final String EXTRA_THANKS_PEOPLES = "RAMSES\nmassimo2001\nKaBudokan";
    public static final String EXTRA_TICKER_TEXT = "TickerText";
    public static final String EXTRA_VARIABLE_NAME = "VariableName";
    public static final String EXTRA_VARIABLE_VALUE = "VariableValue";
    public static final String INTENT_FAKE_PERSIST_CALLBACK = "fakePersist";
    public static final String INTENT_FROM_UI = "fromUi";
    public static final String INTENT_RTC_CALLBACK = "hmTime";
    public static final boolean IsTestVersion = false;
    public static final int LLAMAP_INTENT = 65432;
    public static final String LLAMA_DONATION_PACKAGE_NAME = "com.kebab.LlamaDonation";
    public static final String LLAMA_EXTERNAL_STORAGE_ROOT = "Llama";
    public static final int LLAMA_ICON_0_DOTS = 0;
    public static final int LLAMA_ICON_1_DOT = 1;
    public static final int LLAMA_ICON_2_DOTS = 2;
    public static final int LLAMA_ICON_3_DOTS = 3;
    public static final int LLAMA_ICON_4_DOTS = 4;
    public static final int LLAMA_ICON_BLACK = 58;
    public static final int LLAMA_ICON_BLUE = 55;
    public static final int LLAMA_ICON_GREEN = 54;
    public static final int LLAMA_ICON_ORANGE = 52;
    public static final int LLAMA_ICON_PINK = 57;
    public static final int LLAMA_ICON_PURPLE = 56;
    public static final int LLAMA_ICON_RED = 51;
    public static final int LLAMA_ICON_WARNING = 20;
    public static final int LLAMA_ICON_WHITE = 59;
    public static final int LLAMA_ICON_YELLOW = 53;
    public static final String LLAMA_LOCATION_DIR_NAME = "LocationTrail";
    public static final String LLAMA_MAP_ACTIVITY_NAME = "com.kebab.LlamaMap.LlamaMapActivity";
    public static final String LLAMA_MAP_PACKAGE_NAME = "com.kebab.LlamaMap";
    public static final int LOCATION_PROXIMITY = 1004;
    public static final int MAX_POLLING_INTERVAL_MINS = 480;
    public static final int MENU_ABOUT = 102;
    public static final int MENU_ACTIVATE = 8;
    public static final int MENU_ACTIVATE_AND_LOCK = 118;
    public static final int MENU_ADD_ACTION = 16;
    public static final int MENU_ADD_CELL_TO_AREA = 107;
    public static final int MENU_ADD_CONDITION = 17;
    public static final int MENU_APN_TOGGLE = 114;
    public static final int MENU_CANCEL_REMOVE = 15;
    public static final int MENU_COPY_EVENT = 18;
    public static final int MENU_COPY_PROFILE = 19;
    public static final int MENU_CREATE_REMINDER = 2;
    public static final int MENU_DELETE_AREA = 5;
    public static final int MENU_DELETE_EVENT = 13;
    public static final int MENU_DELETE_PROFILE = 10;
    public static final int MENU_DISABLE_ALL_ITEM = 24;
    public static final int MENU_DISABLE_ITEM = 123;
    public static final int MENU_DONATE = 115;
    public static final int MENU_EDIT_AREA = 3;
    public static final int MENU_EDIT_EVENT = 12;
    public static final int MENU_EDIT_PROFILE = 9;
    public static final int MENU_ENABLE_ALL_ITEM = 23;
    public static final int MENU_ENABLE_ITEM = 122;
    public static final int MENU_EVENT_HISTORY = 116;
    public static final int MENU_EXPORT = 103;
    public static final int MENU_HELP = 112;
    public static final int MENU_IGNORE_CELL = 125;
    public static final int MENU_IMPORT = 104;
    public static final int MENU_IMPORT_EXPORT = 113;
    public static final int MENU_LOCK_PROFILES = 120;
    public static final int MENU_MAX = 1000;
    public static final int MENU_MOBILE_DATA_TOGGLE = 121;
    public static final int MENU_NEW_AREA = 1;
    public static final int MENU_NEW_EVENT = 108;
    public static final int MENU_NEW_PROFILE = 11;
    public static final int MENU_QUIT = 100;
    public static final int MENU_RECHECK_DONATION = 22;
    public static final int MENU_REMOVE = 14;
    public static final int MENU_REMOVE_CELL_FROM_AREA = 111;
    public static final int MENU_RENAME_GROUP = 25;
    public static final int MENU_RUN_ALL_TRUE_EVENTS = 128;
    public static final int MENU_SETTINGS = 101;
    public static final int MENU_SET_FROM_MAP = 20;
    public static final int MENU_SHARE_EVENT = 26;
    public static final int MENU_START_LEARNING = 6;
    public static final int MENU_STOP_LEARNING = 7;
    public static final int MENU_TEST_ACTIONS = 117;
    public static final int MENU_UNIGNORE_CELL = 126;
    public static final int MENU_UNLOCK_PROFILES = 119;
    public static final int MENU_VARIABLES = 127;
    public static final int MENU_VIEW_ALL_ON_MAP = 21;
    public static final int MENU_VIEW_AREA_CELLS = 4;
    public static final int MENU_VIEW_ON_MAP = 124;
    public static int NON_ONGOING_NOTIFICATION_ID = 7654;
    public static final String OLD_NAME = "OldName";
    public static int ONGOING_NOTIFICATION_ID = 7653;
    public static int OTHER_NOTIFICATION_STARTID = 8000;
    public static final String PROFILE = "Profile";
    public static final int PROFILE_LOCK_MAX_MINUTES = 480;
    public static final int PROFILE_LOCK_MAX_MINUTES_LONG = 1440;
    public static final String PROFILE_NEVER_UNLOCK = "never";
    public static final int REQUEST_CODE_ADD_EVENT_ACTION = 207;
    public static final int REQUEST_CODE_ADD_PROFILE_ACTION = 206;
    public static final int REQUEST_CODE_CUSTOM_START_OFFSET = 10000;
    public static final int REQUEST_CODE_DEVICE_ADMIN = 9000;
    public static final int REQUEST_CODE_EDIT_EVENT_ACTION = 205;
    public static final int REQUEST_CODE_EDIT_PROFILE_ACTION = 204;
    public static final int REQUEST_CODE_NEW_PROFILE = 201;
    public static final int REQUEST_CODE_RENAME_ACTION = 202;
    public static final int RTC_BT_POLL = 1003;
    public static final int RTC_PROXIMITY_TIMEOUT = 1005;
    public static int RTC_WAKE = 1000;
    public static final int RTC_WAKE_FAKE_PERSIST = 1006;
    public static final int RTC_WIFI_POLL = 1002;
    public static final String SCREEN_BRIGHTNESS_MODE_KEY = "screen_brightness_mode";
    public static final int SCREEN_ON_BRIGHT = 1;
    public static final int SCREEN_ON_DIM = 0;
    public static final int SCREEN_ON_FULL = 2;
    public static final String SHORTCUT_TYPE_ANONYMOUS_EVENT = "AnonEvent";
    public static final String SHORTCUT_TYPE_EVENT = "Event";
    public static final String SHORTCUT_TYPE_PROFILE = "Profile";
    public static final int SOUND_PLAYER_NOTIFICATION_ID = 7655;
    public static final String SilentRingtone = "*S*";
    public static String TAG = "LlamaDroid";
    public static final boolean UseBilling = true;
    public static final String VIBRATE_SPLIT_CHARS = "\\s\\x20:;\\.\\-,";
    public static final String VIBRATE_SPLIT_CHARS_NO_COMMA = "\\s\\x20:;\\.\\-";
}