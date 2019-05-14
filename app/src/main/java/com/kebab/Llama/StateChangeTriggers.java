package com.kebab.Llama;

public abstract class StateChangeTriggers {
    public static final int DEPRECATED_TRIGGER_CAR_MODE_CHANGED = 7;
    public static final int TRIGGER_AIRPLANE_MODE_DISABLED = 18;
    public static final int TRIGGER_AIRPLANE_MODE_ENABLED = 17;
    public static final int TRIGGER_APP_NOTIFICATION = 19;
    public static final int TRIGGER_APP_TO_BACKGROUND = 31;
    public static final int TRIGGER_APP_TO_FOREGROUND = 30;
    public static final int TRIGGER_AUDIO_BECOMING_NOISY = 24;
    public static final int TRIGGER_BATTERY_LEVEL = 12;
    public static final int TRIGGER_BLUETOOTH_CONNECTED = 8;
    public static final int TRIGGER_BLUETOOTH_DISCONNECTED = 9;
    public static final int TRIGGER_CALENDAR = 29;
    public static final int TRIGGER_CAR_MODE_OFF = 28;
    public static final int TRIGGER_CAR_MODE_ON = 27;
    public static final int TRIGGER_DESK_MODE_OFF = 26;
    public static final int TRIGGER_DESK_MODE_ON = 25;
    public static final int TRIGGER_ENTER_CURRENT_AREA = 0;
    public static final int TRIGGER_EVENT_NAME = 22;
    public static final int TRIGGER_HEADSET_CONNECTED = 10;
    public static final int TRIGGER_HEADSET_DISCONNECTED = 11;
    public static final int TRIGGER_IS_ROAMING = 39;
    public static final int TRIGGER_LEAVE_CURRENT_AREA = 1;
    public static final int TRIGGER_MCC_MNC_CHANGE = 46;
    public static final int TRIGGER_MOBILE_DATA_CONNECTED = 43;
    public static final int TRIGGER_MOBILE_DATA_DISABLED = 42;
    public static final int TRIGGER_MOBILE_DATA_ENABLED = 41;
    public static final int TRIGGER_MOBILE_DATA_NOT_CONNECTED = 44;
    public static final int TRIGGER_MUSIC_STARTED = 5;
    public static final int TRIGGER_MUSIC_STOPPED = 6;
    public static final int TRIGGER_NEXT_ALARM = 35;
    public static final int TRIGGER_NFC_DETECTED = 38;
    public static final int TRIGGER_NONE = -1;
    public static final int TRIGGER_NOT_ROAMING = 40;
    public static final int TRIGGER_PHONE_IS_IDLE = 32;
    public static final int TRIGGER_PHONE_IS_IN_CALL = 33;
    public static final int TRIGGER_PHONE_IS_RINGING = 37;
    public static final int TRIGGER_PHONE_SHUTDOWN = 20;
    public static final int TRIGGER_PHONE_START_UP = 21;
    public static final int TRIGGER_POWER_CONNECTED = 3;
    public static final int TRIGGER_POWER_DISCONNECTED = 4;
    public static final int TRIGGER_SCREEN_OFF = 14;
    public static final int TRIGGER_SCREEN_ON = 13;
    public static final int TRIGGER_SCREEN_ROTATED = 34;
    public static final int TRIGGER_SIGNAL_LEVEL = 45;
    public static final int TRIGGER_TIME_CHANGED = 2;
    public static final int TRIGGER_USER_PRESENT = 36;
    public static final int TRIGGER_VARIABLE_CHANGE = 23;
    public static final int TRIGGER_WIFI_CONNECTED = 15;
    public static final int TRIGGER_WIFI_DISCONNECTED = 16;

    private StateChangeTriggers() {
    }
}
