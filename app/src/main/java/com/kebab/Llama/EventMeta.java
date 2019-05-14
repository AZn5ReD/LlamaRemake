package com.kebab.Llama;

import android.content.Context;
import com.kebab.Llama.EventActions.AndroidIntentAction;
import com.kebab.Llama.EventActions.ChangeBrightnessAction;
import com.kebab.Llama.EventActions.ChangeLlamaBluetoothPollingAction;
import com.kebab.Llama.EventActions.ChangeLlamaLocationPollingAction;
import com.kebab.Llama.EventActions.ChangeLlamaWifiPollingAction;
import com.kebab.Llama.EventActions.ChangeNotificationIconAction;
import com.kebab.Llama.EventActions.ChangeNotificationIconAction2;
import com.kebab.Llama.EventActions.ChangePasswordAction;
import com.kebab.Llama.EventActions.ChangeProfileAction;
import com.kebab.Llama.EventActions.ChangeProfileAction2;
import com.kebab.Llama.EventActions.ChangeScreenTimeoutAction;
import com.kebab.Llama.EventActions.KeyGuardAction;
import com.kebab.Llama.EventActions.KillAppRestartPackageAction;
import com.kebab.Llama.EventActions.KillAppRootAction;
import com.kebab.Llama.EventActions.LocalePluginAction;
import com.kebab.Llama.EventActions.LockProfileChangesAction;
import com.kebab.Llama.EventActions.MediaButtonAction;
import com.kebab.Llama.EventActions.MusicVolumeAction;
import com.kebab.Llama.EventActions.NotificationAction;
import com.kebab.Llama.EventActions.QueueEventAction;
import com.kebab.Llama.EventActions.RebootAction;
import com.kebab.Llama.EventActions.RunAppAction;
import com.kebab.Llama.EventActions.RunShortcutAction;
import com.kebab.Llama.EventActions.ScreenOffAction;
import com.kebab.Llama.EventActions.ScreenOnAction;
import com.kebab.Llama.EventActions.ScreenRotationAction;
import com.kebab.Llama.EventActions.SetLlamaVariableAction;
import com.kebab.Llama.EventActions.ShutdownPhoneAction;
import com.kebab.Llama.EventActions.SoundPlayerAction;
import com.kebab.Llama.EventActions.SpeakAction;
import com.kebab.Llama.EventActions.SpeakerphoneAction;
import com.kebab.Llama.EventActions.ToastAction;
import com.kebab.Llama.EventActions.Toggle4GAction;
import com.kebab.Llama.EventActions.ToggleAirplaneAction;
import com.kebab.Llama.EventActions.ToggleApnAction;
import com.kebab.Llama.EventActions.ToggleBluetoothAction;
import com.kebab.Llama.EventActions.ToggleCarDockAction;
import com.kebab.Llama.EventActions.ToggleCellPollingAction;
import com.kebab.Llama.EventActions.ToggleGpsAction;
import com.kebab.Llama.EventActions.ToggleHapticFeedbackAction;
import com.kebab.Llama.EventActions.ToggleMobileDataAction;
import com.kebab.Llama.EventActions.ToggleSyncAction;
import com.kebab.Llama.EventActions.ToggleWifiAction;
import com.kebab.Llama.EventActions.TwoGThreeGAction;
import com.kebab.Llama.EventActions.UsbStorageAction;
import com.kebab.Llama.EventActions.VibrateAction;
import com.kebab.Llama.EventActions.WallpaperAction;
import com.kebab.Llama.EventActions.WifiHotspotAction;
import com.kebab.Llama.EventActions.WifiSleepPolicyAction;
import com.kebab.Llama.EventConditions.ActiveAppCondition;
import com.kebab.Llama.EventConditions.AirplaneModeCondition;
import com.kebab.Llama.EventConditions.AppNotificationCondition;
import com.kebab.Llama.EventConditions.AudioBecomingNoisyCondition;
import com.kebab.Llama.EventConditions.BatteryLevelCondition;
import com.kebab.Llama.EventConditions.BluetoothDeviceConnectedCondition;
import com.kebab.Llama.EventConditions.BluetoothDeviceDisconnectedCondition;
import com.kebab.Llama.EventConditions.CalendarEventCondition;
import com.kebab.Llama.EventConditions.CalendarEventCondition2;
import com.kebab.Llama.EventConditions.CarModeCondition;
import com.kebab.Llama.EventConditions.ChargingCondition;
import com.kebab.Llama.EventConditions.DayOfTheWeekCondition;
import com.kebab.Llama.EventConditions.DeskDockCondition;
import com.kebab.Llama.EventConditions.EnterAreaCondition;
import com.kebab.Llama.EventConditions.GroupAndCondition;
import com.kebab.Llama.EventConditions.GroupOrCondition;
import com.kebab.Llama.EventConditions.HeadsetConnectedCondition;
import com.kebab.Llama.EventConditions.HourMinute;
import com.kebab.Llama.EventConditions.LeaveAreaCondition;
import com.kebab.Llama.EventConditions.LlamaVariableCondition;
import com.kebab.Llama.EventConditions.MccMncCondition;
import com.kebab.Llama.EventConditions.MobileDataConnectedCondition;
import com.kebab.Llama.EventConditions.MobileDataEnabledCondition;
import com.kebab.Llama.EventConditions.MusicPlayingCondition;
import com.kebab.Llama.EventConditions.NextAlarmCondition;
import com.kebab.Llama.EventConditions.NfcDetectedCondition;
import com.kebab.Llama.EventConditions.NotConnectedToBluetoothDeviceCondition;
import com.kebab.Llama.EventConditions.NotInAreaCondition;
import com.kebab.Llama.EventConditions.PhoneRebootCondition;
import com.kebab.Llama.EventConditions.PhoneStateCondition;
import com.kebab.Llama.EventConditions.RoamingCondition;
import com.kebab.Llama.EventConditions.ScreenBacklightCondition;
import com.kebab.Llama.EventConditions.ScreenRotationCondition;
import com.kebab.Llama.EventConditions.SignalLevelCondition;
import com.kebab.Llama.EventConditions.TimeBetweenCondition;
import com.kebab.Llama.EventConditions.UserPresentCondition;
import com.kebab.Llama.EventConditions.WifiHotspotCondition;
import com.kebab.Llama.EventConditions.WifiNetworkConnectedCondition;
import com.kebab.Llama.EventConditions.WifiNetworkDisconnectedCondition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class EventMeta {
    public static HashMap<String, EventMeta> All;
    public static HashMap<String, SimpleEventTrigger> AllTriggers;
    public static SimpleEventTrigger Confirmed = new SimpleEventTrigger(EventFragment.SIMPLE_TRIGGER_USER_CONFIRMED, R.string.hrConfirmationTriggerName);
    public static SimpleEventTrigger Delayed = new SimpleEventTrigger(EventFragment.SIMPLE_TRIGGER_DELAY_FINSIHED, R.string.hrAfterDelayTriggerName);
    private static int[] EMPTY_TRIGGERS = new int[0];
    public static SimpleEventTrigger EventEditorTest = new SimpleEventTrigger(EventFragment.SIMPLE_TRIGGER_TEST_EVENT_EDITOR, R.string.hrEventNameTriggerEventEditor);
    public static SimpleEventTrigger EventListTest = new SimpleEventTrigger(EventFragment.SIMPLE_TRIGGER_TEST_EVENT_LIST, R.string.hrEventNameTriggerEventList);
    private static final String[] MULTIPLE_FRAGMENT = null;
    public static final Comparator<? super EventMeta> NameComparator = new Comparator<EventMeta>() {
        public int compare(EventMeta x, EventMeta y) {
            return x.Name.compareToIgnoreCase(y.Name);
        }
    };
    public static SimpleEventTrigger NamedEvent = new SimpleEventTrigger(EventFragment.SIMPLE_TRIGGER_NAMED_EVENT, R.string.hrEventNameTriggerName);
    public static SimpleEventTrigger Repeated = new SimpleEventTrigger(EventFragment.SIMPLE_TRIGGER_REPEAT, R.string.hrRepeatTriggerName);
    public static SimpleEventTrigger SetLlamaVariableIntent = new SimpleEventTrigger(EventFragment.SIMPLE_TRIGGER_SET_LLAMA_VARIABLE_INTENT, R.string.hrEventNameTriggerSetLlamaVariableIntent);
    public static SimpleEventTrigger ShortcutCustomEvent = new SimpleEventTrigger(EventFragment.SIMPLE_TRIGGER_SHORTCUT_CUSTOM_EVENT, R.string.hrEventNameTriggerShortcutCustom);
    public static SimpleEventTrigger ShortcutNamedEvent = new SimpleEventTrigger(EventFragment.SIMPLE_TRIGGER_SHORTCUT_NAMED_EVENT, R.string.hrEventNameTriggerShortcutName);
    public static Comparator<? super EventMeta> TypeThenNameComparer = new Comparator<EventMeta>() {
        public int compare(EventMeta x, EventMeta y) {
            if (x.IsCondition) {
                if (y.IsCondition) {
                    return x.Name.compareToIgnoreCase(y.Name);
                }
                return -1;
            } else if (y.IsCondition) {
                return 1;
            } else {
                return x.Name.compareToIgnoreCase(y.Name);
            }
        }
    };
    private static final String[] UNIQUE_FRAGMENT = new String[0];
    public WrappedCreator<?, ?> Create;
    public String[] DisallowedCoFragments;
    public int HelpDescriptionResourceId;
    public String Id;
    public boolean IsCompatibility;
    public boolean IsCondition;
    public String Name;
    private int[] Triggers;

    public interface ConditionStaticInitter2 {
        void UpdateStatics(String str, int[] iArr, int i, int i2);
    }

    public interface ConditionStaticInitter1 {
        void UpdateStatics(String str, int[] iArr, int i);
    }

    public interface ConditionStaticInitter3 {
        void UpdateStatics(String str, int[] iArr, int i, int i2, int i3);
    }

    public interface ConditionStaticInitterCustomTriggers {
        void UpdateStatics(String str);
    }

    public interface ConditionStaticInitter4 {
        void UpdateStatics(String str, int[] iArr, int i, int i2, int i3, int i4);
    }

    public interface ConditionStaticInitterNoTriggers {
        void UpdateStatics(String str, int[] iArr);
    }

    private EventMeta(String id, String name, int helpDescriptionResourceId, boolean isCondition, WrappedCreatorFull<?> create, String[] disallowedCoFragments) {
        this.Id = id;
        this.Name = name;
        this.Create = create;
        this.IsCondition = isCondition;
        this.HelpDescriptionResourceId = helpDescriptionResourceId;
        this.IsCompatibility = false;
        this.DisallowedCoFragments = disallowedCoFragments;
        if (isCondition) {
            this.Triggers = EMPTY_TRIGGERS;
        }
    }

    private EventMeta(String id, String name, int helpDescriptionResourceId, boolean isCondition, int triggerId, WrappedCreatorFull<?> create, String[] disallowedCoFragments) {
        this(id, name, helpDescriptionResourceId, isCondition, create, disallowedCoFragments);
        this.Triggers = new int[]{triggerId};
        if (!isCondition) {
            throw new RuntimeException("You ballsed the metadata for " + id);
        }
    }

    private EventMeta(String id, String name, int helpDescriptionResourceId, boolean isCondition, int triggerIdOn, int triggerIdOff, WrappedCreatorFull<?> create, String[] disallowedCoFragments) {
        this(id, name, helpDescriptionResourceId, isCondition, create, disallowedCoFragments);
        this.Triggers = new int[]{triggerIdOn, triggerIdOff};
        if (!isCondition) {
            throw new RuntimeException("You ballsed the metadata for " + id);
        }
    }

    private EventMeta(String id, String name, int helpDescriptionResourceId, boolean isCondition, int triggerIdOn, int triggerIdOff, int triggerIdOther, WrappedCreatorFull<?> create, String[] disallowedCoFragments) {
        this(id, name, helpDescriptionResourceId, isCondition, create, disallowedCoFragments);
        this.Triggers = new int[]{triggerIdOn, triggerIdOff, triggerIdOther};
        if (!isCondition) {
            throw new RuntimeException("You ballsed the metadata for " + id);
        }
    }

    private EventMeta(String id, String name, int helpDescriptionResourceId, boolean isCondition, int triggerIdOn, int triggerIdOff, int triggerIdOther, int triggerIdOther2, WrappedCreatorFull<?> create, String[] disallowedCoFragments) {
        this(id, name, helpDescriptionResourceId, isCondition, create, disallowedCoFragments);
        this.Triggers = new int[]{triggerIdOn, triggerIdOff, triggerIdOther, triggerIdOther2};
        if (!isCondition) {
            throw new RuntimeException("You ballsed the metadata for " + id);
        }
    }

    public EventMeta(String id, WrappedCreatorCompat<?, ?> create) {
        this.Id = id;
        this.Create = create;
        this.IsCompatibility = true;
    }

    public static void Init(Context c) {
        AllTriggers = new HashMap();
        AllTriggers.put(Repeated.Id, Repeated);
        AllTriggers.put(Delayed.Id, Delayed);
        AllTriggers.put(Confirmed.Id, Confirmed);
        AllTriggers.put(NamedEvent.Id, NamedEvent);
        AllTriggers.put(ShortcutNamedEvent.Id, ShortcutNamedEvent);
        AllTriggers.put(ShortcutCustomEvent.Id, ShortcutCustomEvent);
        AllTriggers.put(EventEditorTest.Id, EventEditorTest);
        AllTriggers.put(EventListTest.Id, EventListTest);
        AllTriggers.put(SetLlamaVariableIntent.Id, SetLlamaVariableIntent);
        EventMeta[] all = new EventMeta[]{new EventMeta(EventFragment.CHANGE_PROFILE_ACTION, new WrappedCreatorCompat<ChangeProfileAction, ChangeProfileAction2>() {
            public ChangeProfileAction Create(String[] parts, int currentPart) {
                return ChangeProfileAction.CreateFrom(parts, currentPart);
            }

            public ChangeProfileAction2 TryUpgrade(ChangeProfileAction action) {
                return new ChangeProfileAction2(action);
            }
        }), new EventMeta(EventFragment.CHANGE_PROFILE_ACTION2, c.getString(R.string.hrActionChangeProfile), R.string.hrActionChangeProfileDescription, false, new WrappedCreatorFull<ChangeProfileAction2>() {
            public ChangeProfileAction2 Create(String[] parts, int currentPart) {
                return ChangeProfileAction2.CreateFrom(parts, currentPart);
            }

            public ChangeProfileAction2 Create() {
                return new ChangeProfileAction2("", 0);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.CHANGE_NOTIFICATION_ICON_ACTION, new WrappedCreatorCompat<ChangeNotificationIconAction, ChangeNotificationIconAction2>() {
            public ChangeNotificationIconAction Create(String[] parts, int currentPart) {
                return ChangeNotificationIconAction.CreateFrom(parts, currentPart);
            }

            public ChangeNotificationIconAction2 TryUpgrade(ChangeNotificationIconAction action) {
                return new ChangeNotificationIconAction2(action);
            }
        }), new EventMeta(EventFragment.CHANGE_NOTIFICATION_ICON_ACTION2, c.getString(R.string.hrActionNotificationIcon), R.string.hrActionNotificationIconDescription, false, new WrappedCreatorFull<ChangeNotificationIconAction2>() {
            public ChangeNotificationIconAction2 Create(String[] parts, int currentPart) {
                return ChangeNotificationIconAction2.CreateFrom(parts, currentPart);
            }

            public ChangeNotificationIconAction2 Create() {
                return new ChangeNotificationIconAction2(0, -1);
            }
        }, MULTIPLE_FRAGMENT), new EventMeta(EventFragment.TOGGLE_USB_ACTION, c.getString(R.string.hrActionUsbStorage), R.string.hrActionUsbStorageDescription, false, new WrappedCreatorFull<UsbStorageAction>() {
            public UsbStorageAction Create(String[] parts, int currentPart) {
                return UsbStorageAction.CreateFrom(parts, currentPart);
            }

            public UsbStorageAction Create() {
                return new UsbStorageAction(1);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.SET_LLAMA_VARIABLE, c.getString(R.string.hrActionSetLlamaVariable), R.string.hrActionSetLlamaVariableDescription, false, new WrappedCreatorFull<SetLlamaVariableAction>() {
            public SetLlamaVariableAction Create(String[] parts, int currentPart) {
                return SetLlamaVariableAction.CreateFrom(parts, currentPart);
            }

            public SetLlamaVariableAction Create() {
                return new SetLlamaVariableAction("", "");
            }
        }, MULTIPLE_FRAGMENT), new EventMeta(EventFragment.SCREEN_ROTATION_ACTION, c.getString(R.string.hrActionScreenRotation), R.string.hrActionScreenRotationDescription, false, new WrappedCreatorFull<ScreenRotationAction>() {
            public ScreenRotationAction Create(String[] parts, int currentPart) {
                return ScreenRotationAction.CreateFrom(parts, currentPart);
            }

            public ScreenRotationAction Create() {
                return new ScreenRotationAction(0);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.TOGGLE_HAPTIC_FEEDBACK_ACTION, c.getString(R.string.hrActionHapticFeedback), R.string.hrActionHapticFeedbackDescription, false, new WrappedCreatorFull<ToggleHapticFeedbackAction>() {
            public ToggleHapticFeedbackAction Create(String[] parts, int currentPart) {
                return ToggleHapticFeedbackAction.CreateFrom(parts, currentPart);
            }

            public ToggleHapticFeedbackAction Create() {
                return new ToggleHapticFeedbackAction(true);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.TOAST_ACTION, c.getString(R.string.hrToastAction), R.string.hrToastActionDescription, false, new WrappedCreatorFull<ToastAction>() {
            public ToastAction Create(String[] parts, int currentPart) {
                return ToastAction.CreateFrom(parts, currentPart);
            }

            public ToastAction Create() {
                return new ToastAction("");
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.ANDROID_INTENT_ACTION, c.getString(R.string.hrActionAndroidIntent), R.string.hrActionAndroidIntentDescription, false, new WrappedCreatorFull<AndroidIntentAction>() {
            public AndroidIntentAction Create(String[] parts, int currentPart) {
                return AndroidIntentAction.CreateFrom(parts, currentPart);
            }

            public AndroidIntentAction Create() {
                return new AndroidIntentAction("", "", 0);
            }
        }, MULTIPLE_FRAGMENT), new EventMeta(EventFragment.MEDIA_BUTTON_ACTION, c.getString(R.string.hrActionMediaPlayer), R.string.hrActionMediaPlayerDescription, false, new WrappedCreatorFull<MediaButtonAction>() {
            public MediaButtonAction Create(String[] parts, int currentPart) {
                return MediaButtonAction.CreateFrom(parts, currentPart);
            }

            public MediaButtonAction Create() {
                return new MediaButtonAction(85);
            }
        }, MULTIPLE_FRAGMENT), new EventMeta(EventFragment.CHANGE_BRIGHTNESS_ACTION, c.getString(R.string.hrActionScreenBrightness), R.string.hrActionScreenBrightnessDescription, false, new WrappedCreatorFull<ChangeBrightnessAction>() {
            public ChangeBrightnessAction Create(String[] parts, int currentPart) {
                return ChangeBrightnessAction.CreateFrom(parts, currentPart);
            }

            public ChangeBrightnessAction Create() {
                return new ChangeBrightnessAction(50);
            }

            public String GetWarningMessage(Context c) {
                return c.getString(R.string.hrActionScreenBrightnessWarning);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.SCREEN_ON_ACTION, c.getString(R.string.hrActionScreenOn), R.string.hrActionScreenOnDescription, false, new WrappedCreatorFull<ScreenOnAction>() {
            public ScreenOnAction Create(String[] parts, int currentPart) {
                return ScreenOnAction.CreateFrom(parts, currentPart);
            }

            public ScreenOnAction Create() {
                return new ScreenOnAction(2);
            }

            public String GetWarningMessage(Context c) {
                return c.getString(R.string.hrActionScreenOnWarning);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.SCREEN_OFF_ACTION, c.getString(R.string.hrActionScreenOff), R.string.hrActionScreenOffDescription, false, new WrappedCreatorFull<ScreenOffAction>() {
            public ScreenOffAction Create(String[] parts, int currentPart) {
                return ScreenOffAction.CreateFrom(parts, currentPart);
            }

            public ScreenOffAction Create() {
                return new ScreenOffAction(0);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.QUEUE_EVENT_ACTION, c.getString(R.string.hrActionQueueEvent), R.string.hrActionQueueEventDescription, false, new WrappedCreatorFull<QueueEventAction>() {
            public QueueEventAction Create(String[] parts, int currentPart) {
                return QueueEventAction.CreateFrom(parts, currentPart);
            }

            public QueueEventAction Create() {
                return new QueueEventAction(null, 0);
            }
        }, MULTIPLE_FRAGMENT), new EventMeta(EventFragment.CHANGE_PASSWORD_ACTION, c.getString(R.string.hrActionChangePassword), R.string.hrActionChangePasswordDescription, false, new WrappedCreatorFull<ChangePasswordAction>() {
            public ChangePasswordAction Create(String[] parts, int currentPart) {
                return ChangePasswordAction.CreateFrom(parts, currentPart);
            }

            public ChangePasswordAction Create() {
                return new ChangePasswordAction("");
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.WIFI_HOTSPOT_ACTION, c.getString(R.string.hrActionWifiHotSpot), R.string.hrActionWifiHotSpotDescription, false, new WrappedCreatorFull<WifiHotspotAction>() {
            public WifiHotspotAction Create(String[] parts, int currentPart) {
                return WifiHotspotAction.CreateFrom(parts, currentPart);
            }

            public WifiHotspotAction Create() {
                return new WifiHotspotAction(1);
            }

            public String GetWarningMessage(Context c) {
                return c.getString(R.string.hrAndroid22Only);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.SPEAKERPHONE_ACTION, c.getString(R.string.hrActionSpeakerphone), R.string.hrActionSpeakerphoneDescription, false, new WrappedCreatorFull<SpeakerphoneAction>() {
            public SpeakerphoneAction Create(String[] parts, int currentPart) {
                return SpeakerphoneAction.CreateFrom(parts, currentPart);
            }

            public SpeakerphoneAction Create() {
                return new SpeakerphoneAction(1);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.CHANGE_VOLUME_ACTION, c.getString(R.string.hrActionMusicVolume), R.string.hrActionMusicVolumeDescription, false, new WrappedCreatorFull<MusicVolumeAction>() {
            public MusicVolumeAction Create(String[] parts, int currentPart) {
                return MusicVolumeAction.CreateFrom(parts, currentPart);
            }

            public MusicVolumeAction Create() {
                return new MusicVolumeAction(0);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.WALLPAPER_ACTION, c.getString(R.string.hrActionWallpaper), R.string.hrActionWallpaperDescription, false, new WrappedCreatorFull<WallpaperAction>() {
            public WallpaperAction Create(String[] parts, int currentPart) {
                return WallpaperAction.CreateFrom(parts, currentPart);
            }

            public WallpaperAction Create() {
                return new WallpaperAction("", "");
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.VIBRATE_ACTION, c.getString(R.string.hrActionVibrate), R.string.hrActionVibrateDescription, false, new WrappedCreatorFull<VibrateAction>() {
            public VibrateAction Create(String[] parts, int currentPart) {
                return VibrateAction.CreateFrom(parts, currentPart);
            }

            public VibrateAction Create() {
                return new VibrateAction("0,500");
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.TOGGLE_APN_ACTION, c.getString(R.string.hrActionApn), R.string.hrActionApnDescription, false, new WrappedCreatorFull<ToggleApnAction>() {
            public ToggleApnAction Create(String[] parts, int currentPart) {
                return ToggleApnAction.CreateFrom(parts, currentPart);
            }

            public ToggleApnAction Create() {
                return new ToggleApnAction(true);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.TWO_G_THREE_G_ACTION, c.getString(R.string.hrActionTwoGThreeG), R.string.hrActionTwoGThreeGDescription, false, new WrappedCreatorFull<TwoGThreeGAction>() {
            public TwoGThreeGAction Create(String[] parts, int currentPart) {
                return TwoGThreeGAction.CreateFrom(parts, currentPart);
            }

            public TwoGThreeGAction Create() {
                return new TwoGThreeGAction(0);
            }

            public String GetWarningMessage(Context c) {
                return c.getString(R.string.hrActionTwoGThreeGDescription);
            }

            public boolean IsHeftyWarningMessage() {
                return true;
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.CAR_MODE_ACTION, c.getString(R.string.hrActionCarMode), R.string.hrActionCarModeDescription, false, new WrappedCreatorFull<ToggleCarDockAction>() {
            public ToggleCarDockAction Create(String[] parts, int currentPart) {
                return ToggleCarDockAction.CreateFrom(parts, currentPart);
            }

            public ToggleCarDockAction Create() {
                return new ToggleCarDockAction(1);
            }

            public String GetWarningMessage(Context c) {
                return c.getString(R.string.hrAndroid22Only);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.WIFI_SLEEP_POLICY_ACTION, c.getString(R.string.hrActionWifiSleepPolicy), R.string.hrActionWifiSleepPolicyDescription, false, new WrappedCreatorFull<WifiSleepPolicyAction>() {
            public WifiSleepPolicyAction Create(String[] parts, int currentPart) {
                return WifiSleepPolicyAction.CreateFrom(parts, currentPart);
            }

            public WifiSleepPolicyAction Create() {
                return new WifiSleepPolicyAction(0);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.TOGGLE_BLUETOOTH_ACTION, c.getString(R.string.hrActionBluetooth), R.string.hrActionBluetoothDescription, false, new WrappedCreatorFull<ToggleBluetoothAction>() {
            public ToggleBluetoothAction Create(String[] parts, int currentPart) {
                return ToggleBluetoothAction.CreateFrom(parts, currentPart);
            }

            public ToggleBluetoothAction Create() {
                return new ToggleBluetoothAction(1, 0);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.TOGGLE_AIRPLANE_ACTION, c.getString(R.string.hrActionAirplane), R.string.hrActionAirplaneDescription, false, new WrappedCreatorFull<ToggleAirplaneAction>() {
            public ToggleAirplaneAction Create(String[] parts, int currentPart) {
                return ToggleAirplaneAction.CreateFrom(parts, currentPart);
            }

            public ToggleAirplaneAction Create() {
                return new ToggleAirplaneAction(true);
            }

            public String GetWarningMessage(Context c) {
                return c.getString(R.string.hrAirplaneModeEnterAreaWarning);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.TOGGLE_WIFI_ACTION, c.getString(R.string.hrActionWifi), R.string.hrActionWifiDescription, false, new WrappedCreatorFull<ToggleWifiAction>() {
            public ToggleWifiAction Create(String[] parts, int currentPart) {
                return ToggleWifiAction.CreateFrom(parts, currentPart);
            }

            public ToggleWifiAction Create() {
                return new ToggleWifiAction(1, 0);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.TOGGLE_GPS_ACTION, c.getString(R.string.hrActionGps), R.string.hrActionGpsDescription, false, new WrappedCreatorFull<ToggleGpsAction>() {
            public ToggleGpsAction Create(String[] parts, int currentPart) {
                return ToggleGpsAction.CreateFrom(parts, currentPart);
            }

            public ToggleGpsAction Create() {
                return new ToggleGpsAction(true);
            }

            public String GetWarningMessage(Context c) {
                return c.getString(R.string.hrGpsEnableWarning);
            }

            public boolean IsHeftyWarningMessage() {
                return true;
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.REBOOT_ACTION, c.getString(R.string.hrActionReboot), R.string.hrActionRebootDescription, false, new WrappedCreatorFull<RebootAction>() {
            public RebootAction Create(String[] parts, int currentPart) {
                return RebootAction.CreateFrom(parts, currentPart);
            }

            public RebootAction Create() {
                return new RebootAction(0);
            }

            public String GetWarningMessage(Context c) {
                return c.getString(R.string.hrRebootSafetyMessage);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.SHUTDOWN_PHONE_ACTION, c.getString(R.string.hrActionShutdown), R.string.hrActionShutdownDescription, false, new WrappedCreatorFull<ShutdownPhoneAction>() {
            public ShutdownPhoneAction Create(String[] parts, int currentPart) {
                return ShutdownPhoneAction.CreateFrom(parts, currentPart);
            }

            public ShutdownPhoneAction Create() {
                return new ShutdownPhoneAction(0);
            }

            public String GetWarningMessage(Context c) {
                return c.getString(R.string.hrShutdownSafetyMessage);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.TOGGLE_SYNC_ACTION, c.getString(R.string.hrActionSync), R.string.hrActionSyncDescription, false, new WrappedCreatorFull<ToggleSyncAction>() {
            public ToggleSyncAction Create(String[] parts, int currentPart) {
                return ToggleSyncAction.CreateFrom(parts, currentPart);
            }

            public ToggleSyncAction Create() {
                return new ToggleSyncAction(2);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.TOGGLE_CELL_POLLING_ACTION, c.getString(R.string.hrActionLlamaCellPolling), R.string.hrActionLlamaCellPollingDescription, false, new WrappedCreatorFull<ToggleCellPollingAction>() {
            public ToggleCellPollingAction Create(String[] parts, int currentPart) {
                return ToggleCellPollingAction.CreateFrom(parts, currentPart);
            }

            public ToggleCellPollingAction Create() {
                return new ToggleCellPollingAction(true);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.CHANGE_LLAMA_WIFI_POLLING_ACTION, c.getString(R.string.hrActionLlamaWifiPolling), R.string.hrActionLlamaWifiPollingDescription, false, new WrappedCreatorFull<ChangeLlamaWifiPollingAction>() {
            public ChangeLlamaWifiPollingAction Create(String[] parts, int currentPart) {
                return ChangeLlamaWifiPollingAction.CreateFrom(parts, currentPart);
            }

            public ChangeLlamaWifiPollingAction Create() {
                return new ChangeLlamaWifiPollingAction(5);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.CHANGE_LLAMA_BLUETOOTH_POLLING_ACTION, c.getString(R.string.hrActionLlamaBluetoothPolling), R.string.hrActionLlamaBluetoothPollingDescription, false, new WrappedCreatorFull<ChangeLlamaBluetoothPollingAction>() {
            public ChangeLlamaBluetoothPollingAction Create(String[] parts, int currentPart) {
                return ChangeLlamaBluetoothPollingAction.CreateFrom(parts, currentPart);
            }

            public ChangeLlamaBluetoothPollingAction Create() {
                return new ChangeLlamaBluetoothPollingAction(5);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.CHANGE_LLAMA_LOCATION_POLLING_ACTION, c.getString(R.string.hrActionAndroidLocationPolling), R.string.hrActionAndroidLocationPollingDescription, false, new WrappedCreatorFull<ChangeLlamaLocationPollingAction>() {
            public ChangeLlamaLocationPollingAction Create(String[] parts, int currentPart) {
                return ChangeLlamaLocationPollingAction.CreateFrom(parts, currentPart);
            }

            public ChangeLlamaLocationPollingAction Create() {
                return new ChangeLlamaLocationPollingAction(5);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.CHANGE_SCREEN_TIMEOUT_ACTION, c.getString(R.string.hrActionScreenTimeout), R.string.hrActionScreenTimeoutDescription, false, new WrappedCreatorFull<ChangeScreenTimeoutAction>() {
            public ChangeScreenTimeoutAction Create(String[] parts, int currentPart) {
                return ChangeScreenTimeoutAction.CreateFrom(parts, currentPart);
            }

            public ChangeScreenTimeoutAction Create() {
                return new ChangeScreenTimeoutAction(30000);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.TOGGLE_MOBILE_DATA_ACTION, c.getString(R.string.hrActionMobileData), R.string.hrActionMobileDataDescription, false, new WrappedCreatorFull<ToggleMobileDataAction>() {
            public ToggleMobileDataAction Create(String[] parts, int currentPart) {
                return ToggleMobileDataAction.CreateFrom(parts, currentPart);
            }

            public ToggleMobileDataAction Create() {
                return new ToggleMobileDataAction(true);
            }

            public String GetWarningMessage(Context c) {
                return c.getString(R.string.hrMobileDataWarning);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.TOGGLE_FOUR_G_ACTION, c.getString(R.string.hrActionToggle4G), R.string.hrActionToggle4GDescription, false, new WrappedCreatorFull<Toggle4GAction>() {
            public Toggle4GAction Create(String[] parts, int currentPart) {
                return Toggle4GAction.CreateFrom(parts, currentPart);
            }

            public Toggle4GAction Create() {
                return new Toggle4GAction(true);
            }

            public String GetWarningMessage(Context c) {
                return c.getString(R.string.hr4GWarning);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.NOTIFICATION_ACTION, c.getString(R.string.hrActionReminder), R.string.hrActionReminderDescription, false, new WrappedCreatorFull<NotificationAction>() {
            public NotificationAction Create(String[] parts, int currentPart) {
                return NotificationAction.CreateFrom(parts, currentPart);
            }

            public NotificationAction Create() {
                return new NotificationAction("");
            }
        }, MULTIPLE_FRAGMENT), new EventMeta(EventFragment.SPEAK_ACTION, c.getString(R.string.hrActionSpeak), R.string.hrActionSpeakDescription, false, new WrappedCreatorFull<SpeakAction>() {
            public SpeakAction Create(String[] parts, int currentPart) {
                return SpeakAction.CreateFrom(parts, currentPart);
            }

            public SpeakAction Create() {
                return new SpeakAction("", 5);
            }
        }, MULTIPLE_FRAGMENT), new EventMeta(EventFragment.SOUND_PLAYER, c.getString(R.string.hrActionSoundPlayer), R.string.hrActionSoundPlayerDescription, false, new WrappedCreatorFull<SoundPlayerAction>() {
            public SoundPlayerAction Create(String[] parts, int currentPart) {
                return SoundPlayerAction.CreateFrom(parts, currentPart);
            }

            public SoundPlayerAction Create() {
                return new SoundPlayerAction(null, null, 5);
            }
        }, MULTIPLE_FRAGMENT), new EventMeta(EventFragment.KEY_GUARD_ACTION, c.getString(R.string.hrActionScreenLock), R.string.hrActionScreenLockDescription2, false, new WrappedCreatorFull<KeyGuardAction>() {
            public KeyGuardAction Create(String[] parts, int currentPart) {
                return KeyGuardAction.CreateFrom(parts, currentPart);
            }

            public KeyGuardAction Create() {
                return new KeyGuardAction(1);
            }

            public String GetWarningMessage(Context c) {
                return c.getString(R.string.hrKeyGuardBuggyWarning);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.TOGGLE_PROFILE_LOCK_ACTION, c.getString(R.string.hrActionProfileLock), R.string.hrActionProfileLockDescription, false, new WrappedCreatorFull<LockProfileChangesAction>() {
            public LockProfileChangesAction Create(String[] parts, int currentPart) {
                return LockProfileChangesAction.CreateFrom(parts, currentPart);
            }

            public LockProfileChangesAction Create() {
                return new LockProfileChangesAction(true);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.RUN_SHORTCUT_ACTION, c.getString(R.string.hrActionRunShortcut), R.string.hrActionRunShortcutDescription, false, new WrappedCreatorFull<RunShortcutAction>() {
            public RunShortcutAction Create(String[] parts, int currentPart) {
                return RunShortcutAction.CreateFrom(parts, currentPart);
            }

            public RunShortcutAction Create() {
                return new RunShortcutAction(null, null);
            }
        }, MULTIPLE_FRAGMENT), new EventMeta(EventFragment.LOCALE_PLUGIN_ACTION, c.getString(R.string.hrActionLocalePlugin), R.string.hrActionLocalePluginDescription, false, new WrappedCreatorFull<LocalePluginAction>() {
            public LocalePluginAction Create(String[] parts, int currentPart) {
                return LocalePluginAction.CreateFrom(parts, currentPart);
            }

            public LocalePluginAction Create() {
                return new LocalePluginAction("", "", "", "");
            }

            public String GetWarningMessage(Context c) {
                return c.getString(R.string.hrActionLocalePluginDescription);
            }

            public boolean IsHeftyWarningMessage() {
                return true;
            }
        }, MULTIPLE_FRAGMENT), new EventMeta(EventFragment.RUN_APP_ACTION, c.getString(R.string.hrActionRunApp), R.string.hrActionRunAppDescription, false, new WrappedCreatorFull<RunAppAction>() {
            public RunAppAction Create(String[] parts, int currentPart) {
                return RunAppAction.CreateFrom(parts, currentPart);
            }

            public RunAppAction Create() {
                return new RunAppAction("", "");
            }
        }, MULTIPLE_FRAGMENT), new EventMeta(EventFragment.QUIT_APP_ACTION, c.getString(R.string.hrActionKillApp), R.string.hrActionKillAppDescription, false, new WrappedCreatorFull<KillAppRestartPackageAction>() {
            public KillAppRestartPackageAction Create(String[] parts, int currentPart) {
                return KillAppRestartPackageAction.CreateFrom(parts, currentPart);
            }

            public KillAppRestartPackageAction Create() {
                return new KillAppRestartPackageAction(new ArrayList());
            }

            public String GetWarningMessage(Context c) {
                return c.getString(R.string.hrActionKillAppWarning);
            }

            public boolean IsHeftyWarningMessage() {
                return true;
            }
        }, MULTIPLE_FRAGMENT), new EventMeta(EventFragment.QUIT_APP_ROOT_ACTION, c.getString(R.string.hrActionKillWithRootApp), R.string.hrActionKillWithRootAppDescription, false, new WrappedCreatorFull<KillAppRootAction>() {
            public KillAppRootAction Create(String[] parts, int currentPart) {
                return KillAppRootAction.CreateFrom(parts, currentPart);
            }

            public KillAppRootAction Create() {
                return new KillAppRootAction(new ArrayList());
            }
        }, MULTIPLE_FRAGMENT), new EventMeta(EventFragment.GROUP_OR_CONDITION, c.getString(R.string.hrConditionGroupOr), R.string.hrConditionGroupOrDescription, true, new WrappedCreatorFull<GroupOrCondition>() {
            public GroupOrCondition Create(String[] parts, int currentPart) {
                return GroupOrCondition.CreateFrom(parts, currentPart);
            }

            public GroupOrCondition Create() {
                return new GroupOrCondition(new ArrayList(), false, false);
            }
        }, MULTIPLE_FRAGMENT), new EventMeta(EventFragment.GROUP_AND_CONDITION, c.getString(R.string.hrConditionGroupAnd), R.string.hrConditionGroupAndDescription, true, new WrappedCreatorFull<GroupAndCondition>() {
            public GroupAndCondition Create(String[] parts, int currentPart) {
                return GroupAndCondition.CreateFrom(parts, currentPart);
            }

            public GroupAndCondition Create() {
                return new GroupAndCondition(new ArrayList(), false, false);
            }
        }, MULTIPLE_FRAGMENT), new EventMeta(EventFragment.ENTER_AREA_CONDITION, c.getString(R.string.hrConditionEnterInArea), R.string.hrConditionEnterInAreaDescription, true, 0, 1, new WrappedCreatorFull<EnterAreaCondition>() {
            public EnterAreaCondition Create(String[] parts, int currentPart) {
                return EnterAreaCondition.CreateFrom(parts, currentPart);
            }

            public EnterAreaCondition Create() {
                return new EnterAreaCondition(new String[0]);
            }
        }, MULTIPLE_FRAGMENT), new EventMeta(EventFragment.LEAVE_AREA_CONDITION, c.getString(R.string.hrConditionLeaveArea), R.string.hrConditionLeaveAreaDescription, true, 1, 0, new WrappedCreatorFull<LeaveAreaCondition>() {
            public LeaveAreaCondition Create(String[] parts, int currentPart) {
                return LeaveAreaCondition.CreateFrom(parts, currentPart);
            }

            public LeaveAreaCondition Create() {
                return new LeaveAreaCondition(new String[0]);
            }
        }, MULTIPLE_FRAGMENT), new EventMeta(EventFragment.NOT_IN_AREA_CONDITION, c.getString(R.string.hrConditionNotInAreas), R.string.hrConditionNotInAreasDescription, true, 1, 0, new WrappedCreatorFull<NotInAreaCondition>() {
            public NotInAreaCondition Create(String[] parts, int currentPart) {
                return NotInAreaCondition.CreateFrom(parts, currentPart);
            }

            public NotInAreaCondition Create() {
                return new NotInAreaCondition(new String[0]);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.LLAMA_VARIABLE_CHANGED, c.getString(R.string.hrConditionLlamaVariable), R.string.hrConditionLlamaVariableDescription, true, 23, new WrappedCreatorFull<LlamaVariableCondition>() {
            public LlamaVariableCondition Create(String[] parts, int currentPart) {
                return LlamaVariableCondition.CreateFrom(parts, currentPart);
            }

            public LlamaVariableCondition Create() {
                return new LlamaVariableCondition(1, null, null);
            }
        }, MULTIPLE_FRAGMENT), new EventMeta(EventFragment.NFC_DETECTED_CONDITION, c.getString(R.string.hrConditionNfcTagDetected), R.string.hrConditionNfcTagDetectedDescription, true, 38, new WrappedCreatorFull<NfcDetectedCondition>() {
            public NfcDetectedCondition Create(String[] parts, int currentPart) {
                return NfcDetectedCondition.CreateFrom(parts, currentPart);
            }

            public NfcDetectedCondition Create() {
                return new NfcDetectedCondition(null, false);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.CALENDAR_EVENT, new WrappedCreatorCompat<CalendarEventCondition, CalendarEventCondition2>() {
            public CalendarEventCondition Create(String[] parts, int currentPart) {
                return CalendarEventCondition.CreateFrom(parts, currentPart);
            }

            public CalendarEventCondition2 TryUpgrade(CalendarEventCondition condition) {
                return new CalendarEventCondition2(condition);
            }
        }), new EventMeta(EventFragment.CALENDAR_EVENT2, c.getString(R.string.hrConditionCalendarEvent), R.string.hrConditionCalendarEventDescription, true, 2, 29, new WrappedCreatorFull<CalendarEventCondition2>() {
            public CalendarEventCondition2 Create(String[] parts, int currentPart) {
                return CalendarEventCondition2.CreateFrom(parts, currentPart);
            }

            public CalendarEventCondition2 Create() {
                return new CalendarEventCondition2(new ArrayList(), true, new ArrayList(), null, null);
            }
        }, MULTIPLE_FRAGMENT), new EventMeta(EventFragment.NEXT_ALARM_CONDITION, c.getString(R.string.hrConditionNextAlarm), R.string.hrConditionNextAlarmDescription, true, 2, 14, 3, 35, new WrappedCreatorFull<NextAlarmCondition>() {
            public NextAlarmCondition Create(String[] parts, int currentPart) {
                return NextAlarmCondition.CreateFrom(parts, currentPart);
            }

            public NextAlarmCondition Create() {
                return new NextAlarmCondition(true);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.DAY_OF_THE_WEEK_CONDITION, c.getString(R.string.hrConditionDayOfTheWeek), R.string.hrConditionDayOfTheWeek_Description, true, 2, new WrappedCreatorFull<DayOfTheWeekCondition>() {
            public DayOfTheWeekCondition Create(String[] parts, int currentPart) {
                return DayOfTheWeekCondition.CreateFrom(parts, currentPart);
            }

            public DayOfTheWeekCondition Create() {
                return new DayOfTheWeekCondition(0);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.AUDIO_BECOMING_NOISY_CONDITION, c.getString(R.string.hrConditionAudioBecomingNoisy), R.string.hrConditionAudioBecomingNoisyDescription, true, 24, new WrappedCreatorFull<AudioBecomingNoisyCondition>() {
            public AudioBecomingNoisyCondition Create(String[] parts, int currentPart) {
                return AudioBecomingNoisyCondition.CreateFrom(parts, currentPart);
            }

            public AudioBecomingNoisyCondition Create() {
                return new AudioBecomingNoisyCondition(false);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.USER_PRESENT_CONDITION, c.getString(R.string.hrConditionUserPresent), R.string.hrConditionUserPresentDescription, true, 36, new WrappedCreatorFull<UserPresentCondition>() {
            public UserPresentCondition Create(String[] parts, int currentPart) {
                return UserPresentCondition.CreateFrom(parts, currentPart);
            }

            public UserPresentCondition Create() {
                return new UserPresentCondition(false);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.HEADSET_CONNECTED_CONDITION, c.getString(R.string.hrConditionHeadset), R.string.hrConditionHeadsetDescription, true, 10, 11, new WrappedCreatorFull<HeadsetConnectedCondition>() {
            public HeadsetConnectedCondition Create(String[] parts, int currentPart) {
                return HeadsetConnectedCondition.CreateFrom(parts, currentPart);
            }

            public HeadsetConnectedCondition Create() {
                return new HeadsetConnectedCondition(1);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.WIFI_HOTSPOT_CONDITION, c.getString(R.string.hrConditionWifiHotspot), R.string.hrConditionWifiHotspotDescription, true, new WrappedCreatorFull<WifiHotspotCondition>() {
            public WifiHotspotCondition Create(String[] parts, int currentPart) {
                return WifiHotspotCondition.CreateFrom(parts, currentPart);
            }

            public WifiHotspotCondition Create() {
                return new WifiHotspotCondition(true);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.SCREEN_BACKLIGHT_CONDITION, c.getString(R.string.hrScreenOnOff), R.string.hrConditionScreenOnOffDescription, true, 13, 14, new WrappedCreatorFull<ScreenBacklightCondition>() {
            public ScreenBacklightCondition Create(String[] parts, int currentPart) {
                return ScreenBacklightCondition.CreateFrom(parts, currentPart);
            }

            public ScreenBacklightCondition Create() {
                return new ScreenBacklightCondition(true);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.PHONE_STATE_CONDITION, c.getString(R.string.hrConditionCallState), R.string.hrConditionCallStateDescription, true, 33, 32, 37, new WrappedCreatorFull<PhoneStateCondition>() {
            public PhoneStateCondition Create(String[] parts, int currentPart) {
                return PhoneStateCondition.CreateFrom(parts, currentPart);
            }

            public PhoneStateCondition Create() {
                return new PhoneStateCondition(12, "");
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.PHONE_REBOOT_CONDITION, c.getString(R.string.hrConditionPhoneReboot), R.string.hrConditionPhoneRebootDescription, true, 21, 20, new WrappedCreatorFull<PhoneRebootCondition>() {
            public PhoneRebootCondition Create(String[] parts, int currentPart) {
                return PhoneRebootCondition.CreateFrom(parts, currentPart);
            }

            public PhoneRebootCondition Create() {
                return new PhoneRebootCondition(true);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.CHARGING_CONDITION, c.getString(R.string.hrConditionCharging), R.string.hrConditionChargingDescription, true, 3, 4, new WrappedCreatorFull<ChargingCondition>() {
            public ChargingCondition Create(String[] parts, int currentPart) {
                return ChargingCondition.CreateFrom(parts, currentPart);
            }

            public ChargingCondition Create() {
                return new ChargingCondition(1);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.BATTERY_LEVEL_CONDITION, c.getString(R.string.hrConditionBatteryLevel), R.string.hrConditionBatteryLevelDescription, true, 12, 3, 4, new WrappedCreatorFull<BatteryLevelCondition>() {
            public BatteryLevelCondition Create(String[] parts, int currentPart) {
                return BatteryLevelCondition.CreateFrom(parts, currentPart);
            }

            public BatteryLevelCondition Create() {
                return new BatteryLevelCondition(50, true, false);
            }
        }, MULTIPLE_FRAGMENT), new EventMeta(EventFragment.SIGNAL_LEVEL_CONDITION, c.getString(R.string.hrConditionSignalLevel), R.string.hrConditionSignalLevelDescription, true, 45, new WrappedCreatorFull<SignalLevelCondition>() {
            public SignalLevelCondition Create(String[] parts, int currentPart) {
                return SignalLevelCondition.CreateFrom(parts, currentPart);
            }

            public SignalLevelCondition Create() {
                return new SignalLevelCondition(50, true, false);
            }
        }, MULTIPLE_FRAGMENT), new EventMeta(EventFragment.CAR_MODE_CONDITION, c.getString(R.string.hrConditionCarMode), R.string.hrConditionCarModeDescription, true, 27, 28, new WrappedCreatorFull<CarModeCondition>() {
            public CarModeCondition Create(String[] parts, int currentPart) {
                return CarModeCondition.CreateFrom(parts, currentPart);
            }

            public CarModeCondition Create() {
                return new CarModeCondition(true);
            }

            public String GetWarningMessage(Context c) {
                return c.getString(R.string.hrAndroid22Only);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.DESK_DOCK_CONDITION, c.getString(R.string.hrConditionDeskDock), R.string.hrConditionDeskDockDescription, true, 25, 26, new WrappedCreatorFull<DeskDockCondition>() {
            public DeskDockCondition Create(String[] parts, int currentPart) {
                return DeskDockCondition.CreateFrom(parts, currentPart);
            }

            public DeskDockCondition Create() {
                return new DeskDockCondition(true);
            }

            public String GetWarningMessage(Context c) {
                return c.getString(R.string.hrAndroid22Only);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.SCREEN_ROTATION_CONDITION, c.getString(R.string.hrConditionScreenRotation), R.string.hrConditionScreenRotationDescription, true, 34, new WrappedCreatorFull<ScreenRotationCondition>() {
            public ScreenRotationCondition Create(String[] parts, int currentPart) {
                return ScreenRotationCondition.CreateFrom(parts, currentPart);
            }

            public ScreenRotationCondition Create() {
                return new ScreenRotationCondition(8);
            }

            public String GetWarningMessage(Context c) {
                return c.getString(R.string.hrAndroid22Only);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.APP_NOTIFICATION_CONDITION, c.getString(R.string.hrConditionAppNotification), R.string.hrConditionAppNotificationDescription, true, 19, new WrappedCreatorFull<AppNotificationCondition>() {
            public AppNotificationCondition Create(String[] parts, int currentPart) {
                return AppNotificationCondition.CreateFrom(parts, currentPart);
            }

            public AppNotificationCondition Create() {
                return new AppNotificationCondition("", "", "");
            }

            public String GetWarningMessage(Context c) {
                return c.getString(R.string.hrConditionAppNotificationWarning);
            }
        }, MULTIPLE_FRAGMENT), new EventMeta(EventFragment.ACTIVE_APP_CONDITION, c.getString(R.string.hrConditionActiveApp), R.string.hrConditionActiveAppDescription, true, 30, 31, new WrappedCreatorFull<ActiveAppCondition>() {
            public ActiveAppCondition Create(String[] parts, int currentPart) {
                return ActiveAppCondition.CreateFrom(parts, currentPart);
            }

            public ActiveAppCondition Create() {
                return new ActiveAppCondition(true, "", "", "");
            }
        }, MULTIPLE_FRAGMENT), new EventMeta(EventFragment.AIRPLANE_MODE_CONDITION, c.getString(R.string.hrConditionAirplaneMode), R.string.hrConditionAirplaneModeDescription, true, 17, 18, new WrappedCreatorFull<AirplaneModeCondition>() {
            public AirplaneModeCondition Create(String[] parts, int currentPart) {
                return AirplaneModeCondition.CreateFrom(parts, currentPart);
            }

            public AirplaneModeCondition Create() {
                return new AirplaneModeCondition(true);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.MOBILE_DATA_ENABLED, c.getString(R.string.hrConditionMobileData), R.string.hrConditionMobileDataDescription, true, 41, 42, new WrappedCreatorFull<MobileDataEnabledCondition>() {
            public MobileDataEnabledCondition Create(String[] parts, int currentPart) {
                return MobileDataEnabledCondition.CreateFrom(parts, currentPart);
            }

            public MobileDataEnabledCondition Create() {
                return new MobileDataEnabledCondition(true);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.MOBILE_DATA_CONNECTED, c.getString(R.string.hrConditionMobileDataConnected), R.string.hrConditionMobileDataConnectedDescription, true, 43, 44, new WrappedCreatorFull<MobileDataConnectedCondition>() {
            public MobileDataConnectedCondition Create(String[] parts, int currentPart) {
                return MobileDataConnectedCondition.CreateFrom(parts, currentPart);
            }

            public MobileDataConnectedCondition Create() {
                return new MobileDataConnectedCondition(true);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.ROAMING_CONDITION, c.getString(R.string.hrConditionRoaming), R.string.hrConditionRoamingDescription, true, 39, 40, new WrappedCreatorFull<RoamingCondition>() {
            public RoamingCondition Create(String[] parts, int currentPart) {
                return RoamingCondition.CreateFrom(parts, currentPart);
            }

            public RoamingCondition Create() {
                return new RoamingCondition(true);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.BLUETOOTH_DEVICE_CONNECTED_CONDITION, c.getString(R.string.hrConditionBluetoothConnected), R.string.hrConditionBluetoothConnectedDescription, true, 8, 9, new WrappedCreatorFull<BluetoothDeviceConnectedCondition>() {
            public BluetoothDeviceConnectedCondition Create(String[] parts, int currentPart) {
                return BluetoothDeviceConnectedCondition.CreateFrom(parts, currentPart);
            }

            public BluetoothDeviceConnectedCondition Create() {
                return new BluetoothDeviceConnectedCondition(new String[0], new String[0]);
            }

            public String GetWarningMessage(Context c) {
                return c.getString(R.string.hrBluetoothParingInfo);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.BLUETOOTH_DEVICE_NOT_CONNECTED, c.getString(R.string.hrConditionBluetootDeviceNotConnected), R.string.hrConditionBluetootDeviceNotConnectedDescription, true, 9, 8, new WrappedCreatorFull<NotConnectedToBluetoothDeviceCondition>() {
            public NotConnectedToBluetoothDeviceCondition Create(String[] parts, int currentPart) {
                return NotConnectedToBluetoothDeviceCondition.CreateFrom(parts, currentPart);
            }

            public NotConnectedToBluetoothDeviceCondition Create() {
                return new NotConnectedToBluetoothDeviceCondition(new String[0], new String[0]);
            }

            public String GetWarningMessage(Context c) {
                return c.getString(R.string.hrBluetoothParingInfo);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.BLUETOOTH_DEVICE_DISCONNECTED_CONDITION, c.getString(R.string.hrConditionBluetoothDisconnected), R.string.hrConditionBluetoothDisconnectedDescription, true, 9, 8, new WrappedCreatorFull<BluetoothDeviceDisconnectedCondition>() {
            public BluetoothDeviceDisconnectedCondition Create(String[] parts, int currentPart) {
                return BluetoothDeviceDisconnectedCondition.CreateFrom(parts, currentPart);
            }

            public BluetoothDeviceDisconnectedCondition Create() {
                return new BluetoothDeviceDisconnectedCondition(new String[0], new String[0]);
            }

            public String GetWarningMessage(Context c) {
                return c.getString(R.string.hrBluetoothParingInfo);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.WIFI_NETWORK_DISCONNECTED_CONDITION, c.getString(R.string.hrConditionWifiDisconnected), R.string.hrConditionWifiDisconnectedDescription, true, 16, 15, new WrappedCreatorFull<WifiNetworkDisconnectedCondition>() {
            public WifiNetworkDisconnectedCondition Create(String[] parts, int currentPart) {
                return WifiNetworkDisconnectedCondition.CreateFrom(parts, currentPart);
            }

            public WifiNetworkDisconnectedCondition Create() {
                return new WifiNetworkDisconnectedCondition(new String[0]);
            }

            public String GetWarningMessage(Context c) {
                return c.getString(R.string.hrWifiNetworkInfo);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.WIFI_NETWORK_CONNECTED_CONDITION, c.getString(R.string.hrConditionWifiConnected), R.string.hrConditionWifiConnectedDescription, true, 15, 16, new WrappedCreatorFull<WifiNetworkConnectedCondition>() {
            public WifiNetworkConnectedCondition Create(String[] parts, int currentPart) {
                return WifiNetworkConnectedCondition.CreateFrom(parts, currentPart);
            }

            public WifiNetworkConnectedCondition Create() {
                return new WifiNetworkConnectedCondition(new String[0]);
            }

            public String GetWarningMessage(Context c) {
                return c.getString(R.string.hrWifiNetworkInfo);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.MUSIC_PLAYING_CONDITION, c.getString(R.string.hrConditionMusicPlayback), R.string.hrConditionMusicPlaybackDescription, true, 5, 6, new WrappedCreatorFull<MusicPlayingCondition>() {
            public MusicPlayingCondition Create(String[] parts, int currentPart) {
                return MusicPlayingCondition.CreateFrom(parts, currentPart);
            }

            public MusicPlayingCondition Create() {
                return new MusicPlayingCondition(true);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.MCC_MNC_CONDITION, c.getString(R.string.hrConditionMobileNetworkId), R.string.hrConditionMobileNetworkIdDescription, true, 46, new WrappedCreatorFull<MccMncCondition>() {
            public MccMncCondition Create(String[] parts, int currentPart) {
                return MccMncCondition.CreateFrom(parts, currentPart);
            }

            public MccMncCondition Create() {
                return new MccMncCondition(-100, -100, true);
            }
        }, UNIQUE_FRAGMENT), new EventMeta(EventFragment.TIME_BETWEEN_CONDITION, c.getString(R.string.hrConditionTimeBetween), R.string.hrConditionTimeBetweenDescription, true, 2, new WrappedCreatorFull<TimeBetweenCondition>() {
            public TimeBetweenCondition Create(String[] parts, int currentPart) {
                return TimeBetweenCondition.CreateFrom(parts, currentPart);
            }

            public TimeBetweenCondition Create() {
                return new TimeBetweenCondition(HourMinute.HoursMinutesToInt(9, 0), HourMinute.HoursMinutesToInt(17, 0));
            }

            public String GetWarningMessage(Context c) {
                return c.getString(R.string.hrTimeBetweenWarning);
            }
        }, MULTIPLE_FRAGMENT)};
        All = new HashMap();
        for (EventMeta meta : all) {
            if (All.containsKey(meta.Id)) {
                throw new RuntimeException("EventFragment map already contains a value for id " + meta.Id);
            }
            All.put(meta.Id, meta);
        }
    }

    public static ArrayList<EventMeta> GetConditions() {
        ArrayList<EventMeta> list = new ArrayList();
        for (EventMeta meta : All.values()) {
            if (!meta.IsCompatibility && meta.IsCondition) {
                list.add(meta);
            }
        }
        Collections.sort(list, NameComparator);
        return list;
    }

    public static ArrayList<EventMeta> GetActions() {
        ArrayList<EventMeta> list = new ArrayList();
        for (EventMeta meta : All.values()) {
            if (!(meta.IsCompatibility || meta.IsCondition)) {
                list.add(meta);
            }
        }
        Collections.sort(list, NameComparator);
        return list;
    }

    public static void InitCondition(String id, ConditionStaticInitterCustomTriggers conditionStaticInitter) {
        conditionStaticInitter.UpdateStatics(id);
    }

    public static void InitCondition(String id, ConditionStaticInitterNoTriggers conditionStaticInitter) {
        EventMeta ef = (EventMeta) All.get(id);
        if (ef.Triggers == null) {
            throw new RuntimeException("You've ballsed up the config for " + id);
        }
        conditionStaticInitter.UpdateStatics(id, ef.Triggers);
    }

    public static void InitCondition(String id, ConditionStaticInitter1 conditionStaticInitter) {
        EventMeta ef = (EventMeta) All.get(id);
        if (ef.Triggers.length != 1) {
            throw new RuntimeException("You've ballsed up the config for " + id);
        }
        conditionStaticInitter.UpdateStatics(id, ef.Triggers, ef.Triggers[0]);
    }

    public static void InitCondition(String id, ConditionStaticInitter2 conditionStaticInitter) {
        EventMeta ef = (EventMeta) All.get(id);
        if (ef.Triggers.length != 2) {
            throw new RuntimeException("You've ballsed up the config for " + id);
        }
        conditionStaticInitter.UpdateStatics(id, ef.Triggers, ef.Triggers[0], ef.Triggers[1]);
    }

    public static void InitCondition(String id, ConditionStaticInitter3 conditionStaticInitter) {
        EventMeta ef = (EventMeta) All.get(id);
        if (ef.Triggers.length != 3) {
            throw new RuntimeException("You've ballsed up the config for " + id);
        }
        conditionStaticInitter.UpdateStatics(id, ef.Triggers, ef.Triggers[0], ef.Triggers[1], ef.Triggers[2]);
    }

    public static void InitCondition(String id, ConditionStaticInitter4 conditionStaticInitter) {
        EventMeta ef = (EventMeta) All.get(id);
        if (ef.Triggers.length != 4) {
            throw new RuntimeException("You've ballsed up the config for " + id);
        }
        conditionStaticInitter.UpdateStatics(id, ef.Triggers, ef.Triggers[0], ef.Triggers[1], ef.Triggers[2], ef.Triggers[3]);
    }
}
