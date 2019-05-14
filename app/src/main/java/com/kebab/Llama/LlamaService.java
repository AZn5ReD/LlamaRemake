package com.kebab.Llama;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.KeyguardManager.OnKeyguardExitResult;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SyncAdapterType;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Rect;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.Settings.System;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;
import com.kebab.AlertDialogEx;
import com.kebab.ApiCompat.AirplaneCompat;
import com.kebab.ApiCompat.DeviceCompat;
import com.kebab.ApiCompat.ScreenRotationCompat;
import com.kebab.ApiCompat.WifiCompat;
import com.kebab.BluetoothHelper;
import com.kebab.CachedBooleanSetting;
import com.kebab.CachedIntSetting;
import com.kebab.CachedSetting;
import com.kebab.DateHelpers;
import com.kebab.GpsNetworkProvider;
import com.kebab.GpsNetworkProvider.CombinedLocationListener;
import com.kebab.Helpers;
import com.kebab.HelpersC;
import com.kebab.IterableHelpers;
import com.kebab.Llama.BluetoothDiscoverer.OnDiscoveryCompletedListener;
import com.kebab.Llama.Content.LlamaToneContentProvider;
import com.kebab.Llama.DeviceAdmin.DeviceAdminCompat;
import com.kebab.Llama.EventActions.ChangeNotificationIconAction;
import com.kebab.Llama.EventActions.ChangeProfileAction2;
import com.kebab.Llama.EventActions.EventAction;
import com.kebab.Llama.EventActions.LockProfileChangesAction;
import com.kebab.Llama.EventActions.NotificationAction;
import com.kebab.Llama.EventActions.SetLlamaVariableAction;
import com.kebab.Llama.EventActions.VibrateAction;
import com.kebab.Llama.EventConditions.EnterAreaCondition;
import com.kebab.Llama.EventConditions.EventCondition;
import com.kebab.Llama.EventConditions.HourMinute;
import com.kebab.Llama.EventConditions.LeaveAreaCondition;
import com.kebab.Llama.EventConditions.LlamaVariableCondition;
import com.kebab.Llama.EventConditions.NextAlarmCondition;
import com.kebab.Llama.EventConditions.SignalLevelCondition;
import com.kebab.Llama.EventConditions.TimeBetweenCondition;
import com.kebab.Llama.LocLogging.LocationLogging;
import com.kebab.Nfc.NfcWatcher;
import com.kebab.Ref;
import com.kebab.Selector;
import com.kebab.Tuple;
import com.kebab.Tuple3;
import com.kebab.UiModeManagerCompat;
import com.kebab.WimaxHelper;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class LlamaService extends Service implements OnKeyguardExitResult {
    static final int ANTI_VOLUME_CHANGE_TIMEOUT = 4000;
    static final int ANTI_VOLUME_CHANGE_TIMEOUT_3 = 2000;
    static final int ANTI_VOLUME_COUNT_LIMIT = 6;
    public static final int CALLSTATE_IN_CALL = 8;
    public static final int CALLSTATE_NOT_IN_CALL = 2;
    public static final int CALLSTATE_RINGING = 4;
    static final Date CUT_OFF_DATE = new Date(150, 0, 1);
    private static final int MINIMUM_DELAY_SECONDS = 2;
    public static final String MULTITHREAD_UI_NAME = "MultithreadUi";
    public static final String MULTITHREAD_WORKER_NAME = "MultithreadWorker";
    static SmartWakeLock _ForceCellCheck = new SmartWakeLock("FOCECH");
    static SmartWakeLock _VibrateWakeLock = new SmartWakeLock("VibrateWakeLock");
    static final String invalidPackageNameRegex = "[^]";
    public int ChargingFrom;
    public HashSet<String> CurrentAreas = new HashSet();
    public Boolean IsCharging;
    Cell LastCell;
    private Calendar LastProfileNameDateTime;
    public Area LearningArea;
    public Date LearningUntilDateTime;
    ArrayList<Area> _Areas;
    Hashtable<String, HashSet<String>> _AreasForBeaconTypeLookup = new Hashtable();
    HashSet<String> _AreasForBluetooth = new HashSet();
    HashSet<String> _AreasForCell = new HashSet();
    HashSet<String> _AreasForEarthPoint = new HashSet();
    HashSet<String> _AreasForWifi = new HashSet();
    AudioManager _AudioManager;
    public BluetoothDevices _BluetoothDevices = new BluetoothDevices(this);
    BluetoothDiscoverer _BluetoothDiscoverer = new BluetoothDiscoverer(new OnDiscoveryCompletedListener() {
        public void OnDiscoveryCompleted(Iterable<BluetoothDevice> devices) {
            if (((Boolean) LlamaSettings.NearbyBtEnabled.GetValue(LlamaService.this)).booleanValue()) {
                LlamaService.this._NearbyBtPoller.OnScanningCompleted(devices);
            }
        }
    });
    CalendarReader _CalendarReader;
    PhoneStateListener _CellListener;
    HashMap<Beacon, ArrayList<String>> _CellToAreaMap = new HashMap();
    Runnable _ChargingSourceHysterisis;
    GpsNetworkProvider _CombinedLocation;
    Object _ConnectivityManager;
    PendingIntent _CpuWakerPendingIntent;
    ArrayList<Event> _EnqueuedEvents = new ArrayList();
    boolean _EventActionCountChanged;
    LinkedList<EventHistory> _EventHistory = new LinkedList();
    boolean _EventHistoryChanged;
    boolean _EventRtcPotentiallyChanged;
    EventList _Events;
    boolean _EventsChanged;
    ForegroundAppWatcher _ForegroundAppWatcher;
    Ref<Method> _GetMobileDataEnabledMethod;
    Runnable _HandleVolumeChangeRunnable = new Runnable() {
        public void run() {
            LlamaService.this.CheckIfVolumeChanged();
        }
    };
    Handler _Handler = new Handler();
    boolean _HeadsetHasMicrophone;
    HashSet<Cell> _IgnoredCells;
    boolean _InittedBatteryListener = false;
    IntentReceiver _IntentReceiver;
    boolean _IsVibrateWait;
    CachedBooleanSetting _KeyguardDisableWaitingForUser = new CachedBooleanSetting("LsVars", "KeyguardDisableWaitingForUser", Boolean.valueOf(false));
    KeyguardLock _KeyguardLock;
    Boolean _LastAirplaneMode;
    Hashtable<String, Collection<Beacon>> _LastBeaconsForTypeLookup = new Hashtable();
    ArrayList<Beacon> _LastBluetooth = new ArrayList();
    ArrayList<Beacon> _LastCell = new ArrayList();
    ArrayList<Beacon> _LastEarthPoint = new ArrayList();
    Integer _LastInCallState;
    CachedIntSetting _LastOrientation = new CachedIntSetting("LsVars", "LastOrientation", -666);
    Boolean _LastRoamingState;
    Integer _LastServiceState_State;
    Integer _LastSignalStrength;
    StateChange _LastStateChange;
    long _LastVolumeChangeMillis;
    ArrayList<Beacon> _LastWifi = new ArrayList();
    HashMap<String, String> _LlamaTones = new HashMap();
    LocationLogging _LocationLogger;
    LocationManager _LocationManager;
    BroadcastReceiver _MusicPlaybackReceiver;
    NearbyBluetoothPoller _NearbyBtPoller = new NearbyBluetoothPoller(this);
    NearbyWifiPoller _NearbyWifiPoller = new NearbyWifiPoller(this);
    String _NetworkProviderName = "";
    HashMap<String, NfcFriendlyName> _NfcNames;
    NfcWatcher _NfcWatcher;
    NoisyContacts _NoisyContacts;
    NotificationManager _NotificationManager;
    public OngoingNotification _OngoingNotification;
    public boolean _ProfileIsChanging;
    ArrayList<Profile> _Profiles;
    PendingIntent _ProximityPendingIntent;
    boolean _QueuedFirstRtcWake;
    QueuedSoundPlayer _QueuedSoundPlayer;
    boolean _Quitting = false;
    ArrayList<String> _RadioLogCat = new ArrayList();
    LinkedList<BeaconAndCalendar> _RecentCells = new LinkedList();
    Runnable _ReleaseRtcReceiverLockRunnable = new Runnable() {
        public void run() {
            RtcReceiver.ReleaseLock(LlamaService.this.getApplicationContext());
        }
    };
    PhoneStateListener _RoamingListener;
    PendingIntent _RtcPendingIntent;
    private boolean _ScreenIsOn;
    LlamaStorage _Storage = new LlamaStorage();
    TelephonyManager _TelephonyManager;
    int _TestEventCounter;
    Runnable _TurnOffProximityAlertRunnable = new Runnable() {
        public void run() {
            LlamaService.this.CancelProximityAlert(true, "Removing proximity alert because location didn't change soon enough");
        }
    };
    Handler _UiThreadHandler;
    public UsbStorage _UsbStorage = new UsbStorage(this);
    HashMap<String, String> _VariableChanges = null;
    HashMap<String, String> _Variables = new HashMap();
    boolean _VariablesHaveChanged;
    int _VibratePosition;
    Runnable _VibrateRunnable;
    Vibrator _Vibrator;
    int _VolumeChangeCounts;
    WifiManager _Wifi;
    boolean _WonkyIcsClock;
    Thread _WorkerThread;
    Handler _WorkerThreadHandler;
    Looper _WorkerThreadLooper;
    int fakeCellCount = 0;
    private Handler fakeCellHandler = new Handler();
    private Runnable fakeCellRunnable = new Runnable() {
        public void run() {
            LlamaService llamaService = LlamaService.this;
            llamaService.fakeCellCount++;
            if (LlamaService.this.fakeCellCount > 30) {
                LlamaService.this.fakeCellCount = 0;
            }
            LlamaService.this.cellLocationChanged(new Cell(LlamaService.this.fakeCellCount, (short) 11, (short) 22));
            LlamaService.this.fakeCellHandler.postDelayed(LlamaService.this.fakeCellRunnable, 10000);
        }
    };
    ProcessReader pr;
    final String profilesLockedPrefix = "(Auto) profiles were locked at ";
    Random r = new Random();

    /* renamed from: com.kebab.Llama.LlamaService$33 */
    static /* synthetic */ class AnonymousClass33 {
        static final /* synthetic */ int[] $SwitchMap$android$net$wifi$SupplicantState = new int[SupplicantState.values().length];

        static {
            try {
                $SwitchMap$android$net$wifi$SupplicantState[SupplicantState.COMPLETED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$net$wifi$SupplicantState[SupplicantState.DISCONNECTED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$net$wifi$SupplicantState[SupplicantState.DORMANT.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$android$net$wifi$SupplicantState[SupplicantState.INACTIVE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$android$net$wifi$SupplicantState[SupplicantState.UNINITIALIZED.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$android$net$wifi$SupplicantState[SupplicantState.SCANNING.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
        }
    }

    private static class Worker extends Thread {
        public boolean finished;
        private final Process process;


        private Worker(Process process) {
            this.process = process;
        }

        public void run() {
            try {
                this.process.waitFor();
                this.finished = true;
            } catch (InterruptedException e) {
            }
        }
    }

    public LlamaService() {
        this._AreasForBeaconTypeLookup.put(Beacon.BLUETOOTH, this._AreasForBluetooth);
        this._AreasForBeaconTypeLookup.put(Beacon.WIFI_MAC_ADDRESS, this._AreasForWifi);
        this._AreasForBeaconTypeLookup.put(Beacon.WIFI_NAME, this._AreasForWifi);
        this._AreasForBeaconTypeLookup.put(Beacon.EARTH_POINT, this._AreasForEarthPoint);
        this._AreasForBeaconTypeLookup.put(Beacon.CELL, this._AreasForCell);
        this._LastBeaconsForTypeLookup.put(Beacon.BLUETOOTH, this._LastBluetooth);
        this._LastBeaconsForTypeLookup.put(Beacon.WIFI_MAC_ADDRESS, this._LastWifi);
        this._LastBeaconsForTypeLookup.put(Beacon.WIFI_NAME, this._LastWifi);
        this._LastBeaconsForTypeLookup.put(Beacon.EARTH_POINT, this._LastEarthPoint);
        this._LastBeaconsForTypeLookup.put(Beacon.CELL, this._LastCell);
    }

    public void onCreate() {
        if (Helpers.IsOnMasterLlamasPhone(this)) {
            Logging.WriteToLogCat = true;
        }
        Logging.Report("service oncreate", getApplicationContext());
        Thread.currentThread().setName(MULTITHREAD_UI_NAME);
        this._UiThreadHandler = new Handler(Looper.getMainLooper());
        if (((Boolean) LlamaSettings.MultiThreadedMode.GetValue(this)).booleanValue()) {
            initWorkerThread();
        }
        if (!(Instances.Service == null || Instances.Service == this)) {
            Logging.Report("service already created", getApplicationContext());
        }
        Instances.Service = this;
        Logging.Init(this);
        LocalisationInit.Init(this, false);
        super.onCreate();
        DebugTesting();
        DateHelpers.Init(this);
        if (LlamaSettings.InstallDate.GetValue(this) == null) {
            LlamaSettings.InstallDate.SetValueAndCommit(this, Calendar.getInstance().getTime(), new CachedSetting[0]);
        }
        Logging.Report("s4compat", "UseDeprecatedVibrate is " + LlamaSettings.UseDeprecatedVibrateSetting.GetValue(this), (Context) this);
        if (((Integer) LlamaSettings.UseDeprecatedVibrateSetting.GetValue(this)).intValue() == 666) {
            if (DeviceCompat.IsPileOfShitWhenDealingWithVibrateMode(this)) {
                LlamaSettings.UseDeprecatedVibrateSetting.SetValueAndCommit(this, Integer.valueOf(0), new CachedSetting[0]);
                Logging.Report("s4compat", "UseDeprecatedVibrate set to 0", (Context) this);
            } else {
                LlamaSettings.UseDeprecatedVibrateSetting.SetValueAndCommit(this, Integer.valueOf(1), new CachedSetting[0]);
                Logging.Report("s4compat", "UseDeprecatedVibrate set to 1", (Context) this);
            }
        }
        if (((Boolean) LlamaSettings.LlamaWasExitted.GetValue(this)).booleanValue()) {
            stopSelf();
            return;
        }
        if (((Boolean) LlamaSettings.DebugCellsInRecent.GetValue(getApplicationContext())).booleanValue()) {
            ThreadComplainMustBeWorker();
            this._RecentCells.addFirst(new BeaconAndCalendar(Cell.ServiceCreated, Calendar.getInstance()));
        }
        this._Storage.LoadRecentInto(getApplicationContext(), this._RecentCells);
        this._Storage.LoadEventHistoryInto(getApplicationContext(), this._EventHistory);
        if (this._OngoingNotification == null) {
            boolean isLocked;
            String lastProfile = (String) LlamaSettings.LastProfileName.GetValue(this);
            String lastAreaNames = (String) LlamaSettings.LastAreaNames.GetValue(this);
            int lastIcon = ((Integer) LlamaSettings.LastNotificationIcon.GetValue(this)).intValue();
            int lastIconDots = ((Integer) LlamaSettings.LastNotificationIconDots.GetValue(this)).intValue();
            if (lastIconDots == -1) {
                Tuple<Integer, Integer> newValues = ChangeNotificationIconAction.ConvertLegacy(lastIcon);
                lastIcon = ((Integer) newValues.Item1).intValue();
                lastIconDots = ((Integer) newValues.Item2).intValue();
                LlamaSettings.LastNotificationIcon.SetValueAndCommit(this, Integer.valueOf(lastIcon), LlamaSettings.LastNotificationIconDots.SetValueForCommit(Integer.valueOf(lastIconDots)));
            }
            if (((Boolean) LlamaSettings.ProfileLocked.GetValue(this)).booleanValue()) {
                if (((String) LlamaSettings.ProfileLockedUntilTimeString.GetValue(this)) != null) {
                    lastProfile = String.format(getString(R.string.hrLockedUntil1), new Object[]{(String) LlamaSettings.ProfileLockedUntilTimeString.GetValue(this)});
                } else {
                    lastProfile = getString(R.string.hrProfileLocked);
                }
                isLocked = true;
            } else {
                isLocked = false;
            }
            this._OngoingNotification = new OngoingNotification(this, lastProfile, lastAreaNames, lastIcon == -1 ? null : Integer.valueOf(lastIcon), lastIconDots == -1 ? null : Integer.valueOf(lastIconDots), Boolean.valueOf(isLocked), Boolean.valueOf(((Boolean) LlamaSettings.LastNotificationIconIsWarning.GetValue(this)).booleanValue()));
            this._OngoingNotification.Update();
        }
        loadData(-1);
        initIgnoredCells();
        this._Storage.LoadVariables(this, this._Variables);
        this._LastBluetooth.addAll(this._Storage.GetLastBeacons(getApplicationContext(), Beacon.BLUETOOTH));
        this._LastCell.addAll(this._Storage.GetLastBeacons(getApplicationContext(), Beacon.CELL));
        this._LastEarthPoint.addAll(this._Storage.GetLastBeacons(getApplicationContext(), Beacon.EARTH_POINT));
        this._LastWifi.addAll(this._Storage.GetLastBeacons(getApplicationContext(), Beacon.WIFI_NAME));
        updateAreasBasedOnLastBeacons(Beacon.BLUETOOTH);
        updateAreasBasedOnLastBeacons(Beacon.CELL);
        updateAreasBasedOnLastBeacons(Beacon.EARTH_POINT);
        updateAreasBasedOnLastBeacons(Beacon.WIFI_NAME);
        this.CurrentAreas.addAll(this._AreasForCell);
        this.CurrentAreas.addAll(this._AreasForBluetooth);
        this.CurrentAreas.addAll(this._AreasForWifi);
        this.CurrentAreas.addAll(this._AreasForEarthPoint);
        if (Calendar.getInstance().getTime().compareTo(CUT_OFF_DATE) > 0) {
            HandleFriendlyError("Version expired! :(", true);
        }
        initNoisyContacts();
        initLlamaTrailLocationLogger();
        if (this._Wifi == null) {
            this._Wifi = (WifiManager) getSystemService("wifi");
        }
        if (this._IntentReceiver != null) {
            unregisterReceiver(this._IntentReceiver);
        }
        this._IntentReceiver = new IntentReceiver();
        ReinitEventTriggers();
        initAudioFocusListener();
        initCellListener();
        initLocationListener();
        initWifiPoller(false, false);
        initBluetoothPoller(false);
        initRotationListener();
        initHeadsetListener();
        initScreenOnOffListener();
        initMobileDataState();
        Logging.Report("Service created", getApplicationContext());
        if (Instances.ProfilesActivity != null) {
            Instances.ProfilesActivity.Update();
        }
        if (Instances.CurrentTab != null) {
            Instances.CurrentTab.UpdateRandomTip();
        }
        runInitialCellListen();
        recreateNotifications();
    }

    private void initMobileDataState() {
        if (((Integer) LlamaSettings.MobileData.GetValue(this)).intValue() == -1) {
            Boolean mobileDataState = getMobileDataState();
            if (mobileDataState == null) {
                Logging.Report("MobileData", "Failed to get mobile data state", (Context) this);
                LlamaSettings.MobileData.SetValueAndCommit(this, Integer.valueOf(0), new CachedSetting[0]);
                return;
            }
            Logging.Report("MobileData", "Mobile data state innited to " + mobileDataState, (Context) this);
            LlamaSettings.MobileData.SetValueAndCommit(this, Integer.valueOf(mobileDataState.booleanValue() ? 1 : 0), new CachedSetting[0]);
        }
    }

    private void ReinitEventTriggers() {
        if (this._CalendarReader == null && this._Events.HasEventsForTriggerId(29, null)) {
            this._CalendarReader = new CalendarReader(this);
        }
        if (this._ForegroundAppWatcher == null && (this._Events.HasEventsForTriggerId(30, null) || this._Events.HasEventsForTriggerId(31, null))) {
            this._ForegroundAppWatcher = new ForegroundAppWatcher(((Integer) LlamaSettings.ActiveAppWatcherMillis.GetValue(this)).intValue(), this);
            this._ForegroundAppWatcher.StartWatching();
        }
        if (!this._InittedBatteryListener && (this._Events.HasEventsForTriggerId(12, null) || this._Events.HasEventsForTriggerId(3, null) || this._Events.HasEventsForTriggerId(4, null))) {
            initBatteryListener();
        }
        if (this._RoamingListener != null) {
            return;
        }
        if (this._Events.HasEventsForTriggerId(39, null) || this._Events.HasEventsForTriggerId(40, null) || this._Events.HasEventsForTriggerId(45, null)) {
            initRoamingAndSignalListener();
        }
    }

    public void initLlamaTrailLocationLogger() {
        if (((Boolean) LlamaSettings.LocationLogging.GetValue(this)).booleanValue()) {
            if (this._LocationLogger == null) {
                this._LocationLogger = new LocationLogging(this);
                this._LocationLogger.LogLlamaStart();
            }
        } else if (this._LocationLogger != null) {
            this._LocationLogger.LogLlamaEnd();
            this._LocationLogger.onFinishing();
            this._LocationLogger = null;
        }
    }

    private void recreateNotifications() {
        Iterator i$ = this._Events.iterator();
        while (i$.hasNext()) {
            Event e = (Event) i$.next();
            if (e.ConfirmationStatus == 2) {
                createNotificationForEvent(e, false);
            }
        }
    }

    private void initNoisyContacts() {
        this._NoisyContacts = new NoisyContacts(this);
        String lastProfileName = (String) LlamaSettings.LastProfileName.GetValue(this);
        if (lastProfileName == null) {
            Logging.Report("NoisyContacts", "Init: LastProfileName was null", (Context) this);
            lastProfileName = (String) LlamaSettings.ProfileAfterLockName.GetValue(this);
        }
        if (lastProfileName != null) {
            Profile p = GetProfileByName(lastProfileName);
            if (p != null) {
                Logging.Report("NoisyContacts", "Init: Setting NoisyContacts for " + p.Name + ", with count " + p.NoisyContacts.size(), (Context) this);
                this._NoisyContacts.SetNoisyContacts(p);
                return;
            }
            Logging.Report("NoisyContacts", "Init: last profile " + lastProfileName + " did not exist.", (Context) this);
            return;
        }
        Logging.Report("NoisyContacts", "Init: ProfileAfterLockName was null", (Context) this);
    }

    private void initScreenOnOffListener() {
        IntentFilter filter = new IntentFilter("android.intent.action.SCREEN_ON");
        filter.addAction("android.intent.action.SCREEN_OFF");
        registerReceiver(this._IntentReceiver, filter);
    }

    private void initHeadsetListener() {
        registerReceiver(this._IntentReceiver, new IntentFilter("android.intent.action.HEADSET_PLUG"));
    }

    public void initWifiPoller(boolean forcePoll, boolean forceStoreCurrentState) {
        if (((Boolean) LlamaSettings.NearbyWifiEnabled.GetValue(this)).booleanValue()) {
            this._NearbyWifiPoller.Init(((Integer) LlamaSettings.NearbyWifiInterval.GetValue(this)).intValue(), forcePoll, forceStoreCurrentState);
            return;
        }
        this._NearbyWifiPoller.Cancel();
        if (this._LastWifi.size() > 0) {
            this._LastWifi.clear();
            evaluateAreasForBeaconTypeAndOtherAreas(Beacon.WIFI_NAME);
        }
    }

    public void initBluetoothPoller(boolean forcePoll) {
        if (((Boolean) LlamaSettings.NearbyBtEnabled.GetValue(this)).booleanValue()) {
            this._NearbyBtPoller.Init(((Integer) LlamaSettings.NearbyBtInterval.GetValue(this)).intValue(), forcePoll);
            return;
        }
        this._NearbyBtPoller.Cancel();
        if (this._LastBluetooth.size() > 0) {
            this._LastBluetooth.clear();
            evaluateAreasForBeaconTypeAndOtherAreas(Beacon.BLUETOOTH);
        }
    }

    public void initLocationListener() {
        if (this._LocationManager == null) {
            this._LocationManager = (LocationManager) getSystemService("location");
        }
        if (this._CombinedLocation == null) {
            this._CombinedLocation = new GpsNetworkProvider(this._LocationManager, new CombinedLocationListener() {
                public void LocationAvailabilityChanged(boolean isAvailable, boolean withGps) {
                }

                public void LocationChanged(boolean isGps, Location location) {
                    LlamaService.this.EarthPointChanged(new EarthPoint(location));
                }
            }, this);
        }
        if (((Boolean) LlamaSettings.AndroidLocationEnabled.GetValue(this)).booleanValue()) {
            this._CombinedLocation.StopTracking();
            this._CombinedLocation.StartTracking(((Boolean) LlamaSettings.AndroidLocationGpsEnabled.GetValue(this)).booleanValue(), (((Integer) LlamaSettings.AndroidLocationInterval.GetValue(this)).intValue() * 60) * 1000);
            return;
        }
        this._CombinedLocation.StopTracking();
        if (this._LastEarthPoint.size() > 0) {
            this._LastEarthPoint.clear();
            evaluateAreasForBeaconTypeAndOtherAreas(Beacon.EARTH_POINT);
        }
    }

    private void DebugTesting() {
        if (!Helpers.IsOnMasterLlamasPhone(this)) {
        }
    }

    public void ToggleGps(boolean setEnabled) {
        if (((LocationManager) getSystemService("location")).isProviderEnabled("gps") != setEnabled) {
            Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory("android.intent.category.ALTERNATIVE");
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
    }

    private void initAudioFocusListener() {
        if (this._AudioManager == null) {
            this._AudioManager = (AudioManager) getSystemService("audio");
        }
        String[] prefixes = new String[]{"com.samsung.sec.android.MusicPlayer", "com.samsung.sec.android", "com.samsung.music", "com.samsung.MusicPlayer", "com.sec.android.app.music", "com.adam.aslfms.service", "com.doubleTwist.androidPlayer", "com.sonyericsson.android.mediascape", "com.sonyericsson.android.mediascape.music", "fm.last.android", "com.htc.music", "com.android.music"};
        this._MusicPlaybackReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (isInitialStickyBroadcast()) {
                    Logging.Report("Ignoring initial sticky music broadcast " + action, context);
                    return;
                }
                Logging.Report("Music state change " + action, context);
                Instances.Service.OnMusicPlaybackStateNearlyChanged();
            }
        };
        for (String prefix : prefixes) {
            registerReceiver(this._MusicPlaybackReceiver, new IntentFilter(prefix + ".playstatechanged"));
            registerReceiver(this._MusicPlaybackReceiver, new IntentFilter(prefix + ".playbackcomplete"));
        }
    }

    public void onStart(final Intent intent, int startId) {
        if (!((Boolean) LlamaSettings.LlamaWasExitted.GetValue(this)).booleanValue()) {
            Handler handler;
            Instances.Service = this;
            if (startId == 1) {
                Logging.Report("First onStart", getApplicationContext());
            } else {
                Logging.Report("Service already started", getApplicationContext());
            }
            if (((Boolean) LlamaSettings.MultiThreadedMode.GetValue(this)).booleanValue()) {
                handler = this._WorkerThreadHandler;
            } else {
                handler = this._Handler;
            }
            final boolean queuedFirstWake = this._QueuedFirstRtcWake;
            if (!queuedFirstWake) {
                this._QueuedFirstRtcWake = true;
            }
            handler.postDelayed(new Runnable() {
                public void run() {
                    LlamaService.this.HandleIntent(intent, true, handler);
                    if (!queuedFirstWake) {
                        LlamaService.this.QueueRtcWake(null);
                    }
                }
            }, 1);
        }
    }

    public static void ThreadComplainMustBeUi() {
        if (Helpers.IsOnMasterLlamasPhone(Instances.Service) && ((Boolean) LlamaSettings.MultiThreadedMode.GetValue(Instances.Service)).booleanValue() && IsOnWorkerThread()) {
            Instances.Service.ShowNotification("On worker > ui: " + GetStack("ThreadComplainMustBeUi"), true, null);
        }
    }

    public static void ThreadComplainMustNotBeUi() {
        if (Helpers.IsOnMasterLlamasPhone(Instances.Service) && ((Boolean) LlamaSettings.MultiThreadedMode.GetValue(Instances.Service)).booleanValue() && IsOnUiThread()) {
            Instances.Service.ShowNotification("On ui > anyother: " + GetStack("ThreadComplainMustNotBeUi"), true, null);
        }
    }

    public static void ThreadComplainMustBeWorker() {
        if (Helpers.IsOnMasterLlamasPhone(Instances.Service) && ((Boolean) LlamaSettings.MultiThreadedMode.GetValue(Instances.Service)).booleanValue() && !IsOnWorkerThread()) {
            Instances.Service.ShowNotification("On ui > worker: " + GetStack("ThreadComplainMustBeWorker"), true, null);
        }
    }

    private static String GetStack(String callingMethodName) {
        int i;
        StackTraceElement e;
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        boolean foundMethodName = false;
        int appendedNames = 0;
        for (StackTraceElement e2 : stack) {
            if (e2.getMethodName().equals(callingMethodName)) {
                foundMethodName = true;
            } else if (foundMethodName) {
                if (appendedNames > 0) {
                    sb.append(", ");
                }
                sb.append(e2.getClassName() + "." + e2.getMethodName());
                appendedNames++;
                if (appendedNames == 4) {
                    break;
                }
            } else {
                continue;
            }
        }
        if (sb.length() == 0) {
            for (i = 0; i < 6; i++) {
                e2 = stack[i];
                String methodName = e2.getMethodName();
                if (appendedNames > 0) {
                    sb.append(", ");
                }
                sb.append(e2.getClassName() + "." + methodName);
                appendedNames++;
            }
        }
        return sb.toString();
    }

    private void initWorkerThread() {
        if (this._WorkerThread == null) {
            Logging.Report("Multithread", "Starting worker thread", (Context) this);
            final CountDownLatch latch = new CountDownLatch(1);
            this._WorkerThread = new Thread(new Runnable() {
                public void run() {
                    Looper.prepare();
                    LlamaService.this._WorkerThreadLooper = Looper.myLooper();
                    LlamaService.this._WorkerThreadHandler = new Handler();
                    LlamaService.this._WorkerThreadHandler.post(new Runnable() {
                        public void run() {
                            Logging.Report("Multithread", "Worker thread started looping, notifying main thread", LlamaService.this);
                            latch.countDown();
                        }
                    });
                    Logging.Report("Multithread", "Worker thread starting looper", LlamaService.this);
                    Looper.loop();
                    Logging.Report("Multithread", "Worker thread looper ended", LlamaService.this);
                }
            });
            this._WorkerThread.setName(MULTITHREAD_WORKER_NAME);
            this._WorkerThread.start();
            try {
                Logging.Report("Multithread", "Main thread waiting for worker to start", (Context) this);
                latch.await();
                Logging.Report("Multithread", "Main thread ready to send work to worker", (Context) this);
            } catch (InterruptedException e) {
                Logging.Report(e, (Context) this);
                throw new RuntimeException(e);
            }
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void shutdownWorkerThread() {
        final CountDownLatch latch = new CountDownLatch(1);
        if (this._WorkerThread != null) {
            this._WorkerThreadHandler.postAtFrontOfQueue(new Runnable() {
                public void run() {
                    Logging.Report("Multithread", "Worker thread quitting looper", LlamaService.this);
                    LlamaService.this._WorkerThreadLooper.quit();
                    latch.countDown();
                }
            });
            try {
                Logging.Report("Multithread", "Main thread waiting for worker to end", (Context) this);
                latch.await();
                Logging.Report("Multithread", "Main thread finished waiting for worker to end", (Context) this);
            } catch (InterruptedException e) {
                Logging.Report(e, (Context) this);
                throw new RuntimeException(e);
            }
        }
    }

    static boolean IsOnWorkerThread() {
        return Thread.currentThread().getName() == MULTITHREAD_WORKER_NAME;
    }

    static boolean IsOnUiThread() {
        return Thread.currentThread().getName() == MULTITHREAD_UI_NAME;
    }

    public void HandleIntent(final Intent intent, boolean releaseLock, Handler handler) {
        if (intent == null) {
            Logging.Report("Intent was unknown in HandleIntent", (Context) this);
            return;
        }
        try {
            long millis = System.currentTimeMillis();
            String intentAction = intent.getAction();
            Logging.Report("Intent Action is " + intentAction, (Context) this);
            if (Constants.ACTION_RUN_SHORTCUT.equals(intentAction)) {
                HandleLlamaShortcut(intent, null);
            } else if (Constants.ACTION_PROXI_CHANGED.equals(intentAction)) {
                HandleProxiChanged(intent);
            } else if ("android.intent.action.SCREEN_ON".equals(intentAction)) {
                HandleScreenIntent(true);
            } else if ("android.intent.action.SCREEN_OFF".equals(intentAction)) {
                HandleScreenIntent(false);
            } else if ("android.intent.action.USER_PRESENT".equals(intentAction)) {
                HandleUserPresent();
            } else if ("android.intent.action.BATTERY_CHANGED".equals(intentAction)) {
                batteryListener(intent);
            } else if ("android.intent.action.HEADSET_PLUG".equals(intentAction)) {
                handleHeadsetPlug(intent);
            } else if ("android.intent.action.CONFIGURATION_CHANGED".equals(intentAction)) {
                handleConfigChange(intent);
            } else if (UiModeManagerCompat.ACTION_ENTER_CAR_MODE.equals(intentAction)) {
                OnCarMode(true);
            } else if (UiModeManagerCompat.ACTION_EXIT_CAR_MODE.equals(intentAction)) {
                OnCarMode(false);
            } else if (UiModeManagerCompat.ACTION_ENTER_DESK_MODE.equals(intentAction)) {
                OnDeskMode(true);
            } else if (UiModeManagerCompat.ACTION_EXIT_DESK_MODE.equals(intentAction)) {
                OnDeskMode(false);
            } else if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intentAction)) {
                HandleConnectivityAction(intent);
            } else if ("android.bluetooth.device.action.ACL_CONNECTED".equals(intentAction)) {
                this._BluetoothDevices.OnConnected(intent);
            } else if ("android.bluetooth.device.action.ACL_DISCONNECTED".equals(intentAction)) {
                this._BluetoothDevices.OnDisconnected(intent);
            } else if ("android.bluetooth.adapter.action.STATE_CHANGED".equals(intentAction)) {
                HandleBluetoothStateChange(intent);
            } else if ("android.media.VOLUME_CHANGED_ACTION".equals(intentAction)) {
                handleVolumeChangeAction();
            } else if (Constants.ACTION_UI_NOTIFICATION.equals(intentAction)) {
                HandleUiNotification(intent);
            } else if (intent.hasExtra(Constants.INTENT_FAKE_PERSIST_CALLBACK)) {
                handleFakePersist();
            } else if (intent.hasExtra(Constants.INTENT_RTC_CALLBACK)) {
                handleHmTime(intent);
            } else if (intent.hasExtra("cpuWakerCell")) {
                cpuWakerCell();
            } else if (intent.hasExtra("proximity")) {
                this._TurnOffProximityAlertRunnable.run();
            } else if ("android.net.wifi.WIFI_STATE_CHANGED".equals(intentAction)) {
                this._Handler.postDelayed(new Runnable() {
                    public void run() {
                        LlamaService.this.handleWifiStateChange(intent);
                    }
                }, 1000);
            } else if ("android.net.wifi.supplicant.STATE_CHANGE".equals(intentAction)) {
                handleWifiConnectionChange(intent);
            } else if ("android.net.wifi.supplicant.CONNECTION_CHANGE".equals(intentAction)) {
                handleWifiConnectionChange(intent);
            } else if ("android.net.wifi.SCAN_RESULTS".equals(intentAction)) {
                if (((Boolean) LlamaSettings.NearbyWifiEnabled.GetValue(this)).booleanValue()) {
                    this._Handler.postDelayed(new Runnable() {
                        public void run() {
                            LlamaService.this._NearbyWifiPoller.OnScanningCompleted();
                        }
                    }, 250);
                }
            } else if (Constants.ACTION_RTC_WIFI_POLL.equals(intentAction)) {
                if (((Boolean) LlamaSettings.NearbyWifiEnabled.GetValue(this)).booleanValue()) {
                    this._NearbyWifiPoller.StartPoll(true);
                }
            } else if (Constants.ACTION_RTC_BT_POLL.equals(intentAction)) {
                if (((Boolean) LlamaSettings.NearbyBtEnabled.GetValue(this)).booleanValue()) {
                    this._NearbyBtPoller.StartPoll();
                }
            } else if ("android.bluetooth.device.action.FOUND".equals(intentAction)) {
                this._BluetoothDiscoverer.HandleDiscoveredDeviceIntent(intent);
            } else if ("android.bluetooth.adapter.action.DISCOVERY_STARTED".equals(intentAction)) {
                this._BluetoothDiscoverer.HandleDiscoveryStartedIntent(intent);
            } else if ("android.bluetooth.adapter.action.DISCOVERY_FINISHED".equals(intentAction)) {
                this._BluetoothDiscoverer.HandleDiscoveryFinishedIntent(intent);
            } else if ("android.intent.action.PHONE_STATE".equals(intentAction)) {
                HandlePhoneStateChange(intent);
            } else if ("android.intent.action.AIRPLANE_MODE".equals(intentAction)) {
                HandleAirplaneModeChange(intent);
            } else if ("android.intent.action.BOOT_COMPLETED".equals(intentAction)) {
                HandlePhoneStartUp(intent);
            } else if (Constants.ACTION_NOTIFICATION_CLEAR.equals(intentAction)) {
                HandleNotificationClear(intent);
            } else if (intent.hasExtra(Constants.INTENT_FROM_UI)) {
                if (Instances.UiActivity != null) {
                    Instances.UiActivity.OnServiceStarted();
                }
            } else if ("android.nfc.action.TAG_DISCOVERED".equals(intentAction)) {
                HandleNfcIntent(intent);
            } else if ("android.nfc.action.NDEF_DISCOVERED".equals(intentAction)) {
                HandleNfcIntent(intent);
            } else if ("android.nfc.action.TECH_DISCOVERED".equals(intentAction)) {
                HandleNfcIntent(intent);
            } else if (Constants.ACTION_STOP_ALL_SOUNDS.equals(intentAction)) {
                StopNoise();
            } else if ("android.media.AUDIO_BECOMING_NOISY".equals(intentAction)) {
                HandleAudioBecomingNoisy();
            } else if (Constants.ACTION_CONFIRM_EVENT.equals(intentAction)) {
                InstantlyAcceptConfirmationIntent(intent);
            } else if (Constants.ACTION_SET_LLAMA_VARIABLE.equals(intentAction)) {
                SetLlamaVariableViaIntent(intent);
            } else if (intent.getExtras() == null) {
                Logging.Report("HandleIntent receive had an unknown intent. No extras,", (Context) this);
            } else {
                Logging.Report("HandleIntent receive had an unknown intent. With extras " + IterableHelpers.ConcatenateString(intent.getExtras().keySet(), ","), (Context) this);
            }
            Logging.Report("IntentProfile", intentAction + " = " + (System.currentTimeMillis() - millis) + "ms", (Context) this);
            if (releaseLock) {
                handler.removeCallbacks(this._ReleaseRtcReceiverLockRunnable);
                handler.post(this._ReleaseRtcReceiverLockRunnable);
            }
        } catch (Exception ex) {
            Logging.Report(ex, (Context) this);
            throw new RuntimeException("Exception caught while handling intent. ", ex);
        } catch (Throwable th) {
            if (releaseLock) {
                handler.removeCallbacks(this._ReleaseRtcReceiverLockRunnable);
                handler.post(this._ReleaseRtcReceiverLockRunnable);
            }
        }
    }

    private void SetLlamaVariableViaIntent(Intent intent) {
        SetLlamaVariableAction action = new SetLlamaVariableAction(intent.getStringExtra(Constants.EXTRA_VARIABLE_NAME), intent.getStringExtra(Constants.EXTRA_VARIABLE_VALUE));
        Event event = new Event(getString(R.string.hrEventNameExternalSetLlamaVariable));
        event._Actions.add(action);
        RunSingleEvent(event, false, null, EventMeta.SetLlamaVariableIntent, 4);
    }

    private void HandleConnectivityAction(Intent intent) {
        NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
        if (networkInfo == null) {
            Logging.Report("Connectivity", "networkInfo is null", (Context) this);
            return;
        }
        Logging.Report("Connectivity", "NetworkType is " + networkInfo.getType(), (Context) this);
        if (networkInfo.getType() == 0) {
            int i;
            CachedIntSetting cachedIntSetting;
            boolean lastConnected;
            boolean shouldCommitSettings = false;
            boolean lastState = ((Integer) LlamaSettings.MobileData.GetValue(this)).intValue();
            Boolean newState = getMobileDataState();
            if (!(lastState || newState == null)) {
                boolean z;
                if (newState.booleanValue()) {
                    z = true;
                } else {
                    z = false;
                }
                if (z != lastState) {
                    String str = "MobileData";
                    StringBuilder append = new StringBuilder().append("Mobile data was ").append(lastState).append(" now ");
                    if (newState.booleanValue()) {
                        i = 1;
                    } else {
                        i = 0;
                    }
                    Logging.Report(str, append.append(i).toString(), (Context) this);
                    cachedIntSetting = LlamaSettings.MobileData;
                    if (newState.booleanValue()) {
                        i = 1;
                    } else {
                        i = 0;
                    }
                    cachedIntSetting.SetValueForCommit(Integer.valueOf(i));
                    shouldCommitSettings = true;
                    if (newState.booleanValue()) {
                        testEvents(StateChange.CreateMobileData(this, true));
                    } else {
                        testEvents(StateChange.CreateMobileData(this, false));
                    }
                }
            }
            if (((Integer) LlamaSettings.MobileDataConnected.GetValue(this)).intValue() == 1) {
                lastConnected = true;
            } else {
                lastConnected = false;
            }
            boolean isConnected = networkInfo.isConnected();
            if (lastConnected != isConnected) {
                testEvents(StateChange.CreateMobileDataConnected(this, isConnected));
                shouldCommitSettings = true;
            }
            if (shouldCommitSettings) {
                cachedIntSetting = LlamaSettings.MobileDataConnected;
                if (isConnected) {
                    i = 1;
                } else {
                    i = 0;
                }
                cachedIntSetting.SetValueAndCommit(this, Integer.valueOf(i), LlamaSettings.MobileData);
            }
        }
    }

    private Boolean getMobileDataState() {
        if (this._GetMobileDataEnabledMethod == null) {
            try {
                ConnectivityManager conman = (ConnectivityManager) getSystemService("connectivity");
                Field iConnectivityManagerField = Class.forName(conman.getClass().getName()).getDeclaredField("mService");
                iConnectivityManagerField.setAccessible(true);
                this._ConnectivityManager = iConnectivityManagerField.get(conman);
                Method getMobileDataEnabledMethod = this._ConnectivityManager.getClass().getDeclaredMethod("getMobileDataEnabled", new Class[0]);
                getMobileDataEnabledMethod.setAccessible(true);
                this._GetMobileDataEnabledMethod = new Ref();
                this._GetMobileDataEnabledMethod.Value = getMobileDataEnabledMethod;
            } catch (Exception ex) {
                Logging.Report("MobileData", "Failed to grab mobile data enabled method", (Context) this);
                Logging.Report(ex, (Context) this);
                this._GetMobileDataEnabledMethod = new Ref();
                this._GetMobileDataEnabledMethod.Value = null;
            }
        }
        if (this._GetMobileDataEnabledMethod.Value == null) {
            return null;
        }
        try {
            return (Boolean) ((Method) this._GetMobileDataEnabledMethod.Value).invoke(this._ConnectivityManager, new Object[0]);
        } catch (Exception e) {
            return null;
        }
    }

    private void InstantlyAcceptConfirmationIntent(Intent intent) {
        Instances.Service.SetEventConfirmationGranted(intent.getStringExtra(Constants.EXTRA_NOTIFICATION_EVENT_NAME), intent.getIntExtra(Constants.EXTRA_NOTIFICATION_ID_TO_CLEAR, 0));
    }

    private void handleVolumeChangeAction() {
        if (Instances.ProfilesActivity != null) {
            Instances.ProfilesActivity.UpdateDebugInfo();
        }
        if (this._LastVolumeChangeMillis == 0) {
            this._LastVolumeChangeMillis = System.currentTimeMillis();
        } else if (this._VolumeChangeCounts > 6) {
            if (System.currentTimeMillis() - this._LastVolumeChangeMillis < 4000) {
                Logging.Report("Too many volume changes! Llama will stop trying to reset your profile", (Context) this);
                this._Handler.removeCallbacks(this._HandleVolumeChangeRunnable);
                this._Handler.postDelayed(this._HandleVolumeChangeRunnable, 2000);
                return;
            }
            this._VolumeChangeCounts = 0;
            this._LastVolumeChangeMillis = 0;
        }
        this._VolumeChangeCounts++;
        CheckIfVolumeChanged();
    }

    private void handleConfigChange(Intent intent) {
        int rotation = ScreenRotationCompat.GetScreenRotation(((WindowManager) getSystemService("window")).getDefaultDisplay());
        if (rotation != ((Integer) this._LastOrientation.GetValue(this)).intValue()) {
            Logging.Report("Rotation", "Screen rotation change from " + this._LastOrientation.GetValue(this) + " to " + rotation, (Context) this);
            this._LastOrientation.SetValueAndCommit(this, Integer.valueOf(rotation), new CachedSetting[0]);
            testEvents(StateChange.CreateScreenRotation(this, rotation));
        }
    }

    private void HandleUserPresent() {
        if (((Boolean) this._KeyguardDisableWaitingForUser.GetValue(this)).booleanValue()) {
            KeyguardManager km = (KeyguardManager) getSystemService("keyguard");
            if (this._KeyguardLock == null) {
                Logging.Report("Keyguard lock token was null. Grrr.", getApplicationContext());
                this._KeyguardLock = km.newKeyguardLock(Constants.TAG);
            }
            this._KeyguardLock.disableKeyguard();
        }
        testEvents(StateChange.CreateUserPresent(this));
    }

    private void HandlePhoneStateChange(Intent intent) {
        String incomingNumber = intent.getStringExtra("incoming_number");
        String phoneStateString = intent.getStringExtra("state");
        ArrayList<String> peopleIds = new ArrayList();
        if (this._NoisyContacts._ContactLookupKeys.size() > 0) {
            Cursor cursor = null;
            try {
                cursor = getContentResolver().query(Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(incomingNumber)), new String[]{"lookup"}, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        peopleIds.add(cursor.getString(0));
                    }
                } else {
                    Logging.Report("NoisyContacts", "Couldnt read contacts for incomingNumber", (Context) this);
                }
                if (cursor != null) {
                    cursor.close();
                }
                Logging.Report("Found " + peopleIds.size() + " people with the ringing phone number", (Context) this);
            } catch (Throwable th) {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        this._NoisyContacts.HandlePhoneIntent(phoneStateString, peopleIds);
        int lastCallState = GetIsInCall();
        if (TelephonyManager.EXTRA_STATE_IDLE.equals(phoneStateString)) {
            this._LastInCallState = Integer.valueOf(2);
            if (lastCallState != this._LastInCallState.intValue()) {
                testEvents(StateChange.CreatePhoneState(this, lastCallState));
            }
        } else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(phoneStateString)) {
            this._LastInCallState = Integer.valueOf(8);
            if (lastCallState != this._LastInCallState.intValue()) {
                testEvents(StateChange.CreatePhoneState(this, lastCallState));
            }
        } else if (TelephonyManager.EXTRA_STATE_RINGING.equals(phoneStateString)) {
            this._LastInCallState = Integer.valueOf(4);
            if (lastCallState != this._LastInCallState.intValue()) {
                testEvents(StateChange.CreatePhoneState(this, lastCallState));
            }
        }
    }

    public int GetIsInCall() {
        return this._LastInCallState == null ? 2 : this._LastInCallState.intValue();
    }

    private void HandleNotificationClear(Intent notificationClearIntent) {
        SetEventConfirmationDenied(notificationClearIntent.getStringExtra(Constants.EXTRA_NOTIFICATION_EVENT_NAME), notificationClearIntent.getIntExtra(Constants.EXTRA_NOTIFICATION_ID_TO_CLEAR, 0));
    }

    private void HandlePhoneStartUp(Intent intent) {
        String lastProfile = (String) LlamaSettings.LastProfileName.GetValue(this);
        if (lastProfile != null) {
            MinimalisticTextIntegration.SetProfileName(this, lastProfile);
        }
        for (Entry<String, String> s : this._Variables.entrySet()) {
            MinimalisticTextIntegration.SetVariableValue(this, (String) s.getKey(), (String) s.getValue());
        }
        if (!testEvents(StateChange.CreatePhoneReboot(this, true)) && this._WonkyIcsClock) {
            Logging.Report("Had a wonky ICS clock and we've just rebooted. Checking next RTC time", (Context) this);
            QueueRtcWake(null);
        }
    }

    private void HandleAudioBecomingNoisy() {
        testEvents(StateChange.CreateAudioBecomingNoisy(this));
    }

    private void HandleUiNotification(Intent intent) {
        String packageName = intent.getStringExtra(Constants.EXTRA_PACKAGE_NAME);
        String tickerText = intent.getStringExtra(Constants.EXTRA_TICKER_TEXT);
        Logging.Report("Notification", packageName + " with " + tickerText, (Context) this);
        testEvents(StateChange.CreateAppNotification(this, packageName, tickerText));
    }

    private void HandleAirplaneModeChange(Intent intent) {
        boolean active = intent.getBooleanExtra("state", false);
        String sender = intent.getStringExtra("llama.sender");
        if (sender == null || !sender.equals("llama")) {
            Logging.Report("Airplane", "Airplane mode change, state=" + active, (Context) this);
        } else {
            Logging.Report("Airplane", "Airplane mode change sent by Llama, state=" + active, (Context) this);
        }
        if (this._LastAirplaneMode == null || this._LastAirplaneMode.booleanValue() != active) {
            this._LastAirplaneMode = Boolean.valueOf(active);
            testEvents(StateChange.CreateAirplaneMode(this, active));
            return;
        }
        Logging.Report("Airplane", "Ignoring airplane intent, because last was" + this._LastAirplaneMode, (Context) this);
    }

    public WifiInfo GetWifiInfo() {
        return this._Wifi.getConnectionInfo();
    }

    private void handleWifiConnectionChange(Intent intent) {
        Boolean isConnected = null;
        if (!intent.hasExtra("connected")) {
            SupplicantState newState = (SupplicantState) intent.getParcelableExtra("newState");
            Logging.Report("WifiCond", "Wifi state is " + newState, (Context) this);
            switch (AnonymousClass33.$SwitchMap$android$net$wifi$SupplicantState[newState.ordinal()]) {
                case 1:
                    isConnected = Boolean.valueOf(true);
                    break;
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                    isConnected = Boolean.valueOf(false);
                    break;
            }
        }
        String str;
        isConnected = Boolean.valueOf(intent.getBooleanExtra("connected", false));
        String str2 = "WifiCond";
        StringBuilder append = new StringBuilder().append("Wifi is ");
        if (isConnected.booleanValue()) {
            str = "connected";
        } else {
            str = "not connected";
        }
        Logging.Report(str2, append.append(str).toString(), (Context) this);
        if (isConnected != null) {
            if (isConnected.booleanValue()) {
                WifiInfo info = GetWifiInfo();
                String currentWifiName = null;
                String currentWifiAddress = null;
                if (info != null) {
                    currentWifiName = WifiCompat.GetWifiName(info);
                    currentWifiAddress = info.getBSSID();
                }
                if (currentWifiName == null || currentWifiAddress == null) {
                    Logging.Report("WifiCond", "Not triggering wifi connect, because of nulls: " + currentWifiName + " with " + currentWifiAddress, (Context) this);
                    return;
                } else if (currentWifiAddress.equals(LlamaSettings.LastWifiAddress.GetValue(this)) && currentWifiAddress.equals(LlamaSettings.LastWifiAddress.GetValue(this))) {
                    Logging.Report("WifiCond", "Not triggering wifi connect, it's the same network as last time", (Context) this);
                    return;
                } else {
                    handleOldWifiDisconnect();
                    StateChange sc = StateChange.CreateWifiConnect(this, info);
                    Logging.Report("WifiCond", "New Wifi network is " + ((String) sc.CurrentWifiName.Get()) + " with " + ((String) sc.CurrentWifiAddress.Get()), (Context) this);
                    LlamaSettings.LastWifiName.SetValueAndCommit(this, sc.CurrentWifiName.Get(), LlamaSettings.LastWifiAddress.SetValueForCommit(sc.CurrentWifiAddress.Get()));
                    testEvents(sc);
                    return;
                }
            }
            handleOldWifiDisconnect();
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void handleOldWifiDisconnect() {
        Logging.Report("WifiDebug", "Handling wifi disconnect, seeing if we had a previous network", (Context) this);
        String oldWifiNetworkName = (String) LlamaSettings.LastWifiName.GetValue(this);
        String oldWifiNetworkBssid = (String) LlamaSettings.LastWifiAddress.GetValue(this);
        if (oldWifiNetworkName == null && oldWifiNetworkBssid == null) {
            Logging.Report("WifiCond", "Last Wifi network was null, not running disconnect events", (Context) this);
            return;
        }
        LlamaSettings.LastWifiName.SetValueAndCommit(this, null, LlamaSettings.LastWifiAddress.SetValueForCommit(null));
        Logging.Report("WifiCond", "Last Wifi network was " + oldWifiNetworkName + " with " + oldWifiNetworkBssid + ", running disconnect events", (Context) this);
        testEvents(StateChange.CreateWifiDisconnect(this, oldWifiNetworkName, oldWifiNetworkBssid));
    }

    private void HandleScreenIntent(boolean screenIsOn) {
        this._ScreenIsOn = screenIsOn;
        if (this._ScreenIsOn) {
            if (((Integer) LlamaSettings.CellPollingMode.GetValue(this)).intValue() != 2) {
                if (((Boolean) LlamaSettings.CellPollingWithScreenWakeLock.GetValue(this)).booleanValue()) {
                    Logging.Report("CellProd", "screen was turned on. Not cancelling proximity alert because we force the screen on", (Context) this);
                } else {
                    CancelProximityAlert(false, "screen was turned on. No longer need proximity alert");
                    CancelCellPoller();
                }
            }
            testEvents(StateChange.CreateScreenOnOff(this, true));
        } else {
            testEvents(StateChange.CreateScreenOnOff(this, false));
            Logging.Report("CellProd", "Screen off, reinitting cell poller", (Context) this);
            initCellPoller();
        }
        this._Handler.postDelayed(new X() {
            /* Access modifiers changed, original: 0000 */
            public void R() {
                LlamaService.this.CheckIfVolumeChanged();
            }
        }, 2000);
    }

    private void HandleProxiChanged(Intent intent) {
        CancelProximityAlert(true, "We reached our fake desination!");
    }

    private void handleHeadsetPlug(Intent intent) {
        this._HeadsetHasMicrophone = intent.getIntExtra("microphone", 0) == 1;
        if (intent.getBooleanExtra(Constants.EXTRA_INITIAL_STICKY, false)) {
            Logging.Report("Headset", "Ignoring sticky headset broadcast", (Context) this);
            return;
        }
        int pluggedState = intent.getIntExtra("state", -1);
        Logging.Report("Headset", Helpers.DumpIntent(intent), (Context) this);
        if (pluggedState != 0) {
            testEvents(StateChange.CreateHeadsetPlugged(this, true, this._HeadsetHasMicrophone));
        } else {
            testEvents(StateChange.CreateHeadsetPlugged(this, false, this._HeadsetHasMicrophone));
        }
    }

    private void handleWifiStateChange(Intent intent) {
        int wifiState = intent.getIntExtra("wifi_state", -666);
        if (wifiState == 3) {
            if (((Boolean) LlamaSettings.NearbyWifiEnabled.GetValue(this)).booleanValue()) {
                this._NearbyWifiPoller.OnAdapterHasBeenEnabled();
            }
        } else if (wifiState == 1) {
            handleOldWifiDisconnect();
            if (WifiAccessPoint.WaitingForWifiToTurnOff) {
                Logging.Report("WifiAp", "Wifi has been disabled, and we were waiting for it to turn off", (Context) this);
                WifiAccessPoint.SetEnabled(this, true, false);
            }
        }
    }

    private void HandleBluetoothStateChange(Intent intent) {
        this._BluetoothDevices.OnBluetoothStateChange(intent);
        switch (intent.getIntExtra("android.bluetooth.adapter.extra.STATE", -1)) {
            case 10:
                BluetoothEnableWakeLock.ReleaseLock(this);
                return;
            case 12:
                if (((Boolean) LlamaSettings.NearbyBtEnabled.GetValue(this)).booleanValue()) {
                    this._NearbyBtPoller.OnAdapterEnabled();
                }
                BluetoothEnableWakeLock.ReleaseLock(this);
                return;
            default:
                return;
        }
    }

    public void HandleLlamaShortcut(Intent intent, Activity activity) {
        String shortcutTarget = intent.getStringExtra(Constants.EXTRA_LLAMA_SHORTCUT_DATA);
        String shortcutType = intent.getStringExtra(Constants.EXTRA_LLAMA_SHORTCUT_TYPE);
        Logging.Report("Got shortcut intent for " + shortcutTarget + " - " + shortcutType, (Context) this);
        if (shortcutType.equals("Event")) {
            Event event = GetEventByName(shortcutTarget);
            if (event == null) {
                HandleFriendlyError(String.format(activity.getString(R.string.hrTheShortcutRefersToAnEventNamed1WhichNoLongerExists), new Object[]{shortcutTarget}), true);
                return;
            }
            RunSingleEvent(event, true, activity, EventMeta.ShortcutNamedEvent, 1);
        } else if (shortcutType.equals(Constants.SHORTCUT_TYPE_ANONYMOUS_EVENT)) {
            RunSingleEvent(Event.CreateFromPsv(shortcutTarget), false, activity, EventMeta.ShortcutCustomEvent, 2);
        } else if (!shortcutType.equals("Profile")) {
        } else {
            if (GetProfileByName(shortcutTarget) == null) {
                HandleFriendlyError(String.format(activity.getString(R.string.hrTheShortcutRefersToAProfileNamed1WhichNoLongerExists), new Object[]{shortcutTarget}), true);
                return;
            }
            DisableProfileLock(false, true);
            SetProfile(shortcutTarget, false, null, true);
        }
    }

    public void CheckIfVolumeChanged() {
        if (!this._ProfileIsChanging && !this._NoisyContacts.IsRingingForNoisyContact()) {
            if (((Boolean) LlamaSettings.DontCheckVolumeInCall.GetValue(this)).booleanValue()) {
                Logging.Report("Checking if volume changed, LastInCallState=" + this._LastInCallState, (Context) this);
                if (this._LastInCallState != null && (this._LastInCallState.intValue() == 4 || this._LastInCallState.intValue() == 8)) {
                    return;
                }
            }
            if (((Boolean) LlamaSettings.AutoLockProfileOnVolumeChange.GetValue(this)).booleanValue() || ((Boolean) LlamaSettings.RevertVolumeChanges.GetValue(this)).booleanValue() || ((Boolean) LlamaSettings.ChangeIconIfVolumeChanges.GetValue(this)).booleanValue()) {
                String lastProfileName = (String) LlamaSettings.LastProfileName.GetValue(this);
                Boolean boxedProfileLocked = (Boolean) LlamaSettings.ProfileLocked.GetValue(this);
                boolean profileLocked = boxedProfileLocked == null ? false : boxedProfileLocked.booleanValue();
                if (lastProfileName != null || !profileLocked) {
                    Profile profile = GetProfileByName((String) LlamaSettings.LastProfileName.GetValue(this));
                    if (profile != null) {
                        int notificatinVolumeIsRingVolume;
                        if (this._AudioManager == null) {
                            this._AudioManager = (AudioManager) getSystemService("audio");
                        }
                        boolean volumeChanged = false;
                        int ringStream = this._AudioManager.getStreamVolume(2);
                        int notifyStream = this._AudioManager.getStreamVolume(5);
                        int ringerVolume = ringStream;
                        int notificationVolume = notifyStream;
                        if (profile.RingVolume != null) {
                            ringerVolume = profile.RingVolume.intValue();
                        }
                        if (profile.NotificationVolume != null) {
                            notificationVolume = profile.NotificationVolume.intValue();
                        }
                        if (ringerVolume == notificationVolume) {
                            notificatinVolumeIsRingVolume = 1;
                        } else {
                            notificatinVolumeIsRingVolume = 0;
                        }
                        if (profile.RingVolume != null && profile.RingVolume.intValue() != ringStream) {
                            Logging.Report("RING profile: " + profile.RingVolume + " audiomanager:" + ringStream, (Context) this);
                            volumeChanged = true;
                        } else if (!(notificatinVolumeIsRingVolume != 0 || profile.NotificationVolume == null || profile.NotificationVolume.intValue() == notifyStream)) {
                            Logging.Report("NOTIFY profile: " + profile.NotificationVolume + " audiomanager:" + notifyStream, (Context) this);
                            volumeChanged = true;
                        }
                        if (profile.RingerMode != null) {
                            int audioRinger = this._AudioManager.getRingerMode();
                            int audioVibrateNoify = this._AudioManager.getVibrateSetting(1);
                            int audioVibrateRinger = this._AudioManager.getVibrateSetting(0);
                            switch (profile.RingerMode.intValue()) {
                                case 0:
                                    if (!(audioRinger == 0 || audioRinger == 1)) {
                                        volumeChanged = true;
                                    }
                                    if (!(audioVibrateNoify == 0 || ((Integer) LlamaSettings.UseDeprecatedVibrateSetting.GetValue(this)).intValue() == 0)) {
                                        volumeChanged = true;
                                    }
                                    if (!(audioVibrateRinger == 0 || ((Integer) LlamaSettings.UseDeprecatedVibrateSetting.GetValue(this)).intValue() == 0)) {
                                        volumeChanged = true;
                                    }
                                    if (volumeChanged) {
                                        Logging.Report("RINGER profile: " + profile.RingerMode + " audiomanager:" + audioRinger + " " + audioVibrateNoify + " " + audioVibrateRinger, (Context) this);
                                        break;
                                    }
                                    break;
                                case 1:
                                    if (audioRinger != 1) {
                                        volumeChanged = true;
                                    }
                                    if (!(audioVibrateNoify == 1 || ((Integer) LlamaSettings.UseDeprecatedVibrateSetting.GetValue(this)).intValue() == 0)) {
                                        volumeChanged = true;
                                    }
                                    if (!(audioVibrateRinger == 1 || ((Integer) LlamaSettings.UseDeprecatedVibrateSetting.GetValue(this)).intValue() == 0)) {
                                        volumeChanged = true;
                                    }
                                    if (volumeChanged) {
                                        Logging.Report("RINGER profile: " + profile.RingerMode + " audiomanager:" + audioRinger + " " + audioVibrateNoify + " " + audioVibrateRinger, (Context) this);
                                        break;
                                    }
                                    break;
                                case 2:
                                    if (audioRinger != 2) {
                                        volumeChanged = true;
                                    }
                                    if (!(audioVibrateNoify == 0 || ((Integer) LlamaSettings.UseDeprecatedVibrateSetting.GetValue(this)).intValue() == 0)) {
                                        volumeChanged = true;
                                    }
                                    if (audioVibrateRinger == 1 && ((Integer) LlamaSettings.UseDeprecatedVibrateSetting.GetValue(this)).intValue() != 0) {
                                        volumeChanged = true;
                                    }
                                    if (volumeChanged) {
                                        Logging.Report("RINGER profile: " + profile.RingerMode + " audiomanager:" + audioRinger + " " + audioVibrateNoify + " " + audioVibrateRinger, (Context) this);
                                        break;
                                    }
                                    break;
                                case 3:
                                    if (audioRinger != 2) {
                                        volumeChanged = true;
                                    }
                                    if (!(audioVibrateNoify == 1 || ((Integer) LlamaSettings.UseDeprecatedVibrateSetting.GetValue(this)).intValue() == 0)) {
                                        volumeChanged = true;
                                    }
                                    if (!(audioVibrateRinger == 1 || ((Integer) LlamaSettings.UseDeprecatedVibrateSetting.GetValue(this)).intValue() == 0)) {
                                        volumeChanged = true;
                                    }
                                    if (volumeChanged) {
                                        Logging.Report("RINGER profile: " + profile.RingerMode + " audiomanager:" + audioRinger + " " + audioVibrateNoify + " " + audioVibrateRinger, (Context) this);
                                        break;
                                    }
                                    break;
                            }
                        }
                        if (volumeChanged) {
                            Logging.Report("Volume has changed. Last profile was " + profile.Name, (Context) this);
                            if (((Boolean) LlamaSettings.RevertVolumeChanges.GetValue(this)).booleanValue()) {
                                profile.Activate(this, this._OngoingNotification, this._NoisyContacts);
                            } else if (((Boolean) LlamaSettings.ChangeIconIfVolumeChanges.GetValue(this)).booleanValue()) {
                                LlamaSettings.LastNotificationIconIsWarning.SetValueAndCommit(this, Boolean.valueOf(true), new CachedSetting[0]);
                                this._OngoingNotification.SetIconAsWarningAndUpdate();
                            } else if (profileLocked) {
                                LlamaSettings.LastProfileName.SetValueAndCommit(this, null, new CachedSetting[0]);
                                if (Instances.ProfilesActivity != null) {
                                    Instances.ProfilesActivity.Update();
                                }
                            } else {
                                EnableProfileLock(((Integer) LlamaSettings.ProfileUnlockDelay.GetValue(this)).intValue(), profile.Name, true);
                            }
                        }
                    }
                }
            }
        }
    }

    private void batteryListener(Intent intent) {
        Logging.Report("Service exists, preparing for OnBatteryEvent", (Context) this);
        int pluggedValue = intent.getIntExtra("plugged", -666);
        int statusValue = intent.getIntExtra("status", -666);
        int batteryLevel = intent.getIntExtra("level", -1);
        boolean testedForBatteryEvents = false;
        if (batteryLevel != -1) {
            LlamaSettings.LastBatteryPercent.SetValueAndCommit(this, Integer.valueOf(batteryLevel), new CachedSetting[0]);
        }
        Logging.Report("BatteryReceiver.onReceive Extra_plugged was " + pluggedValue + ", extraStatus was " + statusValue, (Context) this);
        if (pluggedValue == -666) {
            this.IsCharging = null;
        } else {
            Boolean oldIsCharging = this.IsCharging;
            if (pluggedValue == 0) {
                this.IsCharging = Boolean.valueOf(false);
                if (this._ChargingSourceHysterisis != null) {
                    Logging.Report("We were already waiting for a charging change. Cancelling... no longer charging", (Context) this);
                    this._Handler.removeCallbacks(this._ChargingSourceHysterisis);
                }
                if (oldIsCharging == null) {
                    Logging.Report("No longer charging, but previous state was not known", (Context) this);
                } else if (oldIsCharging.booleanValue()) {
                    Logging.Report("No longer charging", (Context) this);
                    testEvents(StateChange.CreateBattery(this, false, 0));
                    testedForBatteryEvents = true;
                }
            } else if (oldIsCharging != null) {
                int newChargingFrom;
                switch (pluggedValue) {
                    case 1:
                        newChargingFrom = 2;
                        break;
                    case 4:
                        newChargingFrom = 4;
                        break;
                    default:
                        newChargingFrom = 3;
                        break;
                }
                if (!(oldIsCharging.booleanValue() && newChargingFrom == this.ChargingFrom)) {
                    if (this._ChargingSourceHysterisis != null) {
                        Logging.Report("We were already waiting for a charging change. Cancelling... now charging off " + pluggedValue + "=" + newChargingFrom + ", waiting a bit", (Context) this);
                        this._Handler.removeCallbacks(this._ChargingSourceHysterisis);
                    } else {
                        Logging.Report("Now charging off " + pluggedValue + "=" + newChargingFrom + ", waiting a bit", (Context) this);
                    }
                    this._ChargingSourceHysterisis = new Runnable() {
                        public void run() {
                            LlamaService.this._ChargingSourceHysterisis = null;
                            LlamaService.this.IsCharging = Boolean.valueOf(true);
                            LlamaService.this.ChargingFrom = newChargingFrom;
                            LlamaService.this.testEvents(StateChange.CreateBattery(LlamaService.this, true, newChargingFrom));
                        }
                    };
                    this._Handler.postDelayed(this._ChargingSourceHysterisis, 1000);
                    testedForBatteryEvents = true;
                }
            } else {
                Logging.Report("Now charging, but previous state was not known", (Context) this);
            }
        }
        if (!testedForBatteryEvents) {
            testEvents(StateChange.CreateBatteryLevel(this, batteryLevel));
        }
    }

    private void cpuWakerCell() {
        Logging.Report("CPU woken", (Context) this);
        if (Instances.Service != null) {
            Instances.Service.ForceCellCheck();
        } else {
            Logging.Report("Service was not running", (Context) this);
        }
    }

    private void handleHmTime(Intent intent) {
        int hmTime = intent.getIntExtra(Constants.INTENT_RTC_CALLBACK, -1);
        long ticks = intent.getLongExtra("ticks", -1);
        HourMinute hm = HourMinute.IntToHoursMinutesTo(hmTime);
        Logging.Report("RTC at " + hm.Hours + ":" + hm.Minutes + ", Calling OnRtcEvent", (Context) this);
        Calendar currentTime = Calendar.getInstance();
        if (!(currentTime.get(12) == hm.Minutes && currentTime.get(11) == hm.Hours)) {
            Logging.Report("HmTime/Calendar mismatch. " + hm.Hours + ":" + hm.Minutes + " vs " + DateHelpers.FormatDate(currentTime), getApplicationContext());
        }
        Calendar tickedTime = Calendar.getInstance();
        tickedTime.setTimeInMillis(ticks);
        OnRtcEvent(hmTime, tickedTime);
    }

    private void handleFakePersist() {
        Logging.Report("rebirth rtc", (Context) this);
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    private void loadData(int hmTimeRtcCallback) {
        boolean allowedToResetData;
        boolean errors = false;
        if (this._Areas == null) {
            try {
                this._Areas = this._Storage.LoadAreas(getApplicationContext());
            } catch (Exception ex) {
                this._Areas = new ArrayList();
                Log.e(Constants.TAG, "Error loading areas", ex);
                Logging.Report(ex, getApplicationContext(), true);
                errors = true;
            }
            buildCellToAreaMap();
        } else {
            Logging.Report("Areas was already loaded", getApplicationContext());
        }
        if (this._Profiles == null) {
            try {
                this._Profiles = this._Storage.LoadProfiles(getApplicationContext());
            } catch (Exception ex2) {
                this._Profiles = new ArrayList();
                Log.e(Constants.TAG, "Error loading profiles", ex2);
                Logging.Report(ex2, getApplicationContext(), true);
                errors = true;
            }
        } else {
            Logging.Report("Profiles was already loaded", getApplicationContext());
        }
        if (this._Events == null) {
            try {
                this._Events = new EventList(this._Storage.LoadEvents(getApplicationContext()));
            } catch (Exception ex22) {
                this._Events = new EventList();
                Log.e(Constants.TAG, "Error loading events", ex22);
                Logging.Report(ex22, getApplicationContext(), true);
                errors = true;
            }
        } else {
            Logging.Report("Events was already loaded", getApplicationContext());
        }
        if (this._IgnoredCells == null) {
            try {
                this._IgnoredCells = this._Storage.LoadIgnoredCells(getApplicationContext());
            } catch (Exception ex222) {
                this._IgnoredCells = new HashSet();
                Log.e(Constants.TAG, "Error loading cells", ex222);
                Logging.Report(ex222, getApplicationContext(), true);
                errors = true;
            }
        } else {
            Logging.Report("IgnoredCells was already loaded", getApplicationContext());
        }
        if (this._NfcNames == null) {
            try {
                this._NfcNames = this._Storage.LoadNfcNames(getApplicationContext());
            } catch (Exception ex2222) {
                this._NfcNames = new HashMap();
                Log.e(Constants.TAG, "Error loading NFC names", ex2222);
                Logging.Report(ex2222, getApplicationContext(), true);
                errors = true;
            }
        } else {
            Logging.Report("IgnoredCells was already loaded", getApplicationContext());
        }
        try {
            this._Storage.LoadLlamaTones(getApplicationContext(), this._LlamaTones);
        } catch (Exception ex22222) {
            Log.e(Constants.TAG, "Error loading tones", ex22222);
            Logging.Report(ex22222, getApplicationContext(), true);
        }
        if (!errors) {
            allowedToResetData = true;
        } else if (this._Storage.SaveSharedPrefsToSd(getApplicationContext(), "debug" + DateHelpers.FormatIsoDate(Calendar.getInstance().getTime()) + "_")) {
            HandleFriendlyError(getString(R.string.hrLoadSettingsError), true);
            this._Storage.SaveAreas(getApplicationContext(), this._Areas);
            this._Storage.SaveEvents(getApplicationContext(), this._Events);
            this._Storage.SaveProfiles(getApplicationContext(), this._Profiles);
            allowedToResetData = true;
        } else {
            HandleFriendlyError(getString(R.string.hrCriticalLoadSettingsError), true);
            throw new RuntimeException("Llama died because it failed to read SharedPreferences and couldn't back up data.");
        }
        if (allowedToResetData) {
            if (this._Profiles.size() == 0) {
                CreateDummyProfiles();
            }
            if (this._Events.size() == 0) {
                CreateDummyEvents(hmTimeRtcCallback);
            }
            if (this._Areas.size() == 0) {
                CreateDummyAreas();
            }
        }
        new EventList().AddEvents(this._Events);
    }

    public void AddIgnoredCell(Cell cell) {
        if (this._IgnoredCells.add(cell)) {
            this._Storage.SaveIgnoredCells(getApplicationContext(), this._IgnoredCells);
            if (this._LastCell.size() > 0 && ((Beacon) this._LastCell.get(0)).equals(cell)) {
                this._LastCell.remove(0);
            }
            if (Cell.NoSignal.equals(cell)) {
                LlamaSettings.IgnoreInvalidCell.SetValueAndCommit(this, Boolean.valueOf(true), new CachedSetting[0]);
            }
            evaluateAreasForBeaconTypeAndOtherAreas(cell.GetTypeId());
            if (Instances.CellsActivity != null) {
                Instances.CellsActivity.Update();
            }
        }
    }

    public void RemoveIgnoredCell(Cell cell) {
        RemoveIgnoredCell(cell, true, false);
    }

    private void RemoveIgnoredCell(Cell cell, boolean allowSaving, boolean forceSaving) {
        if (this._IgnoredCells.remove(cell) || forceSaving) {
            if (allowSaving) {
                this._Storage.SaveIgnoredCells(getApplicationContext(), this._IgnoredCells);
            }
            if (Cell.NoSignal.equals(cell)) {
                LlamaSettings.IgnoreInvalidCell.SetValueAndCommit(this, Boolean.valueOf(false), new CachedSetting[0]);
            }
            if (allowSaving) {
                if (this._LastCell.size() == 0) {
                    this._LastCell.add(this.LastCell);
                } else {
                    this._LastCell.set(0, this.LastCell);
                }
                evaluateAreasForBeaconTypeAndOtherAreas(cell.GetTypeId());
                if (Instances.CellsActivity != null) {
                    Instances.CellsActivity.Update();
                }
            }
        }
    }

    public void RemoveIgnoredCells(Iterable<Cell> cells) {
        Cell prev = null;
        for (Cell c : cells) {
            if (prev != null) {
                RemoveIgnoredCell(prev, false, false);
            }
            prev = c;
        }
        if (prev != null) {
            RemoveIgnoredCell(prev, true, true);
        }
    }

    private void CreateDummyEvents(int hmTimeRtcCallback) {
        Event event = new Event(getString(R.string.hrQuietAtNight));
        event._Conditions.add(new EnterAreaCondition(new String[]{getString(R.string.hrHome)}));
        event._Conditions.add(new TimeBetweenCondition(HourMinute.HoursMinutesToInt(22, 0), HourMinute.HoursMinutesToInt(6, 30)));
        event._Actions.add(new ChangeProfileAction2(getString(R.string.hrQuiet)));
        this._Events.Add(event);
        event = new Event(getString(R.string.hrNormalAtHome));
        event._Conditions.add(new EnterAreaCondition(new String[]{getString(R.string.hrHome)}));
        event._Conditions.add(new TimeBetweenCondition(HourMinute.HoursMinutesToInt(6, 30), HourMinute.HoursMinutesToInt(22, 0)));
        event._Actions.add(new ChangeProfileAction2(getString(R.string.hrNormal)));
        this._Events.Add(event);
        event = new Event(getString(R.string.hrLeftHome));
        event._Conditions.add(new LeaveAreaCondition(new String[]{getString(R.string.hrHome)}));
        event._Actions.add(new ChangeProfileAction2(getString(R.string.hrNormal)));
        this._Events.Add(event);
        event = new Event(getString(R.string.hrQuietAtWork));
        event._Conditions.add(new EnterAreaCondition(new String[]{getString(R.string.hrWork)}));
        event._Conditions.add(new TimeBetweenCondition(HourMinute.HoursMinutesToInt(8, 30), HourMinute.HoursMinutesToInt(18, 45)));
        event._Actions.add(new ChangeProfileAction2(getString(R.string.hrQuiet)));
        this._Events.Add(event);
        event = new Event(getString(R.string.hrNormalOutsideWork));
        event._Conditions.add(new EnterAreaCondition(new String[]{getString(R.string.hrWork)}));
        event._Conditions.add(new TimeBetweenCondition(HourMinute.HoursMinutesToInt(18, 45), HourMinute.HoursMinutesToInt(8, 30)));
        event._Actions.add(new ChangeProfileAction2(getString(R.string.hrNormal)));
        this._Events.Add(event);
        event = new Event(getString(R.string.hrLeftWork));
        event._Conditions.add(new LeaveAreaCondition(new String[]{getString(R.string.hrWork)}));
        event._Actions.add(new ChangeProfileAction2(getString(R.string.hrNormal)));
        this._Events.Add(event);
    }

    private void CreateDummyAreas() {
        this._Areas.add(new Area(getString(R.string.hrHome)));
        this._Areas.add(new Area(getString(R.string.hrWork)));
    }

    private void CreateDummyProfiles() {
        if (this._AudioManager == null) {
            this._AudioManager = (AudioManager) getSystemService("audio");
        }
        Profile p = new Profile(getString(R.string.hrSilent));
        p.RingerMode = Integer.valueOf(0);
        Integer valueOf = Integer.valueOf(0);
        p.RingVolume = valueOf;
        p.NotificationVolume = valueOf;
        p.LlamaNotificationIcon = Integer.valueOf(0);
        p.LlamaNotificationIconDots = Integer.valueOf(1);
        this._Profiles.add(p);
        p = new Profile(getString(R.string.hrQuiet));
        p.RingerMode = Integer.valueOf(2);
        valueOf = Integer.valueOf(1);
        p.RingVolume = valueOf;
        p.NotificationVolume = valueOf;
        p.LlamaNotificationIcon = Integer.valueOf(0);
        p.LlamaNotificationIconDots = Integer.valueOf(2);
        this._Profiles.add(p);
        p = new Profile(getString(R.string.hrNormal));
        p.RingerMode = Integer.valueOf(3);
        valueOf = Integer.valueOf((this._AudioManager.getStreamMaxVolume(2) * 5) / 7);
        p.RingVolume = valueOf;
        p.NotificationVolume = valueOf;
        p.LlamaNotificationIcon = Integer.valueOf(0);
        p.LlamaNotificationIconDots = Integer.valueOf(3);
        this._Profiles.add(p);
        p = new Profile(getString(R.string.hrLoud));
        p.RingerMode = Integer.valueOf(3);
        valueOf = Integer.valueOf(this._AudioManager.getStreamMaxVolume(2));
        p.RingVolume = valueOf;
        p.NotificationVolume = valueOf;
        p.LlamaNotificationIcon = Integer.valueOf(0);
        p.LlamaNotificationIconDots = Integer.valueOf(4);
        this._Profiles.add(p);
    }

    private void buildCellToAreaMap() {
        Iterator it = this._Areas.iterator();
        while (it.hasNext()) {
            Area a = (Area) it.next();
            Iterator i$ = a._Cells.iterator();
            while (i$.hasNext()) {
                addCellToAreaMap((Beacon) i$.next(), a);
            }
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void cancelCellListener() {
        if (this._CellListener != null) {
            ((TelephonyManager) getSystemService("phone")).listen(this._CellListener, 0);
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void cancelLocationListener() {
        if (this._CombinedLocation != null) {
            this._CombinedLocation.StopTracking();
        }
    }

    private void shutdownService() {
        cancelCellListener();
        cancelLocationListener();
        if (this._Quitting) {
            if (this._RtcPendingIntent != null) {
                this._RtcPendingIntent.cancel();
            }
            if (this._CpuWakerPendingIntent != null) {
                this._CpuWakerPendingIntent.cancel();
            }
            if (this._LocationManager == null) {
                this._LocationManager = (LocationManager) getSystemService("location");
            }
            if (this._ProximityPendingIntent != null) {
                this._LocationManager.removeProximityAlert(this._ProximityPendingIntent);
            }
            if (this._ForegroundAppWatcher != null) {
                this._ForegroundAppWatcher.StopWatching();
                this._ForegroundAppWatcher = null;
            }
            this._Events.RemoveEventType(1);
            this._Storage.ResetLastBeacons(this);
            if (this._OngoingNotification != null) {
                this._OngoingNotification.ClearOngoing();
            }
        }
        if (this._IntentReceiver != null) {
            unregisterReceiver(this._IntentReceiver);
        }
        if (this._MusicPlaybackReceiver != null) {
            unregisterReceiver(this._MusicPlaybackReceiver);
        }
        SaveRecent();
        if (this._LocationLogger != null) {
            this._LocationLogger.LogLlamaEnd();
            this._LocationLogger.onFinishing();
            this._LocationLogger = null;
        }
        EnableKeyGuardForLeakingStupidBinderTokenGrr();
        shutdownWorkerThread();
        Instances.Service = null;
        Logging.Report("Service shutdown", getApplicationContext());
        if (Instances.UiActivity != null) {
            Instances.UiActivity.finish();
        }
        RtcReceiver.ReleaseLock(getApplicationContext());
    }

    /* Access modifiers changed, original: 0000 */
    public void EnableKeyGuardForLeakingStupidBinderTokenGrr() {
        Logging.Report("Pessimistically reenabling key guard :'(", getApplicationContext());
        EnableKeyGuard(true, false, false);
    }

    public void onDestroy() {
        Logging.Report("ondestroy", getApplicationContext());
        super.onDestroy();
        shutdownService();
    }

    public void AddDebugCell(Cell cell) {
        if (((Boolean) LlamaSettings.DebugCellsInRecent.GetValue(getApplicationContext())).booleanValue()) {
            ThreadComplainMustBeWorker();
            this._RecentCells.addFirst(new BeaconAndCalendar(cell, Calendar.getInstance()));
            if (Instances.CellsActivity != null) {
                Instances.CellsActivity.Update();
            }
        }
    }

    public void onLowMemory() {
        Logging.Report("onlowmem", getApplicationContext());
        if (((Boolean) LlamaSettings.DebugCellsInRecent.GetValue(getApplicationContext())).booleanValue()) {
            ThreadComplainMustBeWorker();
            this._RecentCells.addFirst(new BeaconAndCalendar(Cell.LowMem, Calendar.getInstance()));
        }
        CancelProximityAlert(false, "LowMemory, cancelling just in case Android kills Llama.");
        if (!this._Quitting && ((Boolean) LlamaSettings.ForcePersistant.GetValue(getApplicationContext())).booleanValue()) {
            EnableKeyGuardForLeakingStupidBinderTokenGrr();
            Intent intent = new Intent(getApplicationContext(), RtcReceiver.class);
            intent.putExtra(Constants.INTENT_FAKE_PERSIST_CALLBACK, "Yep");
            Calendar time = Calendar.getInstance();
            time.add(13, 15);
            Logging.Report("starting timer for rebirth", getApplicationContext());
            this._RtcPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), Constants.RTC_WAKE_FAKE_PERSIST, intent, 134217728);
            ((AlarmManager) getSystemService("alarm")).set(0, time.getTimeInMillis(), this._RtcPendingIntent);
        }
        SaveRecent();
        if (this._LocationLogger != null) {
            this._LocationLogger.onLowMemory();
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void SaveRecent() {
        ThreadComplainMustBeWorker();
        if (((Boolean) LlamaSettings.ZeroRecentCells.GetValue(this)).booleanValue()) {
            this._RecentCells.clear();
        }
        while (this._RecentCells.size() > ((Integer) LlamaSettings.RecentItems.GetValue(this)).intValue()) {
            this._RecentCells.removeLast();
        }
        if (((Boolean) LlamaSettings.StoreRecentCells.GetValue(this)).booleanValue()) {
            this._Storage.SaveRecent(getApplicationContext(), this._RecentCells);
        }
    }

    private void initBatteryListener() {
        registerReceiver(this._IntentReceiver, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        Log.i(Constants.TAG, "Battery Receiver registered");
        this._InittedBatteryListener = true;
    }

    private void initRotationListener() {
        registerReceiver(this._IntentReceiver, new IntentFilter("android.intent.action.CONFIGURATION_CHANGED"));
    }

    /* Access modifiers changed, original: 0000 */
    public void initRoamingAndSignalListener() {
        if (this._RoamingListener == null) {
            this._RoamingListener = new PhoneStateListener() {
                public void onServiceStateChanged(ServiceState state) {
                    final boolean roaming = state.getRoaming();
                    final int status = state.getState();
                    LlamaService.this.IdeallyRunOnWorkerThread(new X() {
                        /* Access modifiers changed, original: 0000 */
                        public void R() {
                            Logging.Report("Roaming", "Service state change..." + status + " " + "roaming=" + roaming, LlamaService.this.getApplicationContext());
                            if (status == 0) {
                                if (LlamaService.this._LastRoamingState == null) {
                                    Logging.Report("Roaming", "Set roaming state to " + roaming + "for the first time", LlamaService.this.getApplicationContext());
                                    LlamaService.this._LastRoamingState = Boolean.valueOf(roaming);
                                }
                                if (LlamaService.this._LastRoamingState.booleanValue() != roaming) {
                                    Logging.Report("Roaming", "Roaming changed from " + LlamaService.this._LastRoamingState + " to " + roaming, LlamaService.this.getApplicationContext());
                                    LlamaService.this._LastRoamingState = Boolean.valueOf(roaming);
                                    LlamaService.this.testEvents(StateChange.CreateRoaming(LlamaService.this, LlamaService.this._LastRoamingState.booleanValue()));
                                }
                            }
                        }
                    });
                    LlamaService.this.IdeallyRunOnWorkerThread(new X() {
                        /* Access modifiers changed, original: 0000 */
                        public void R() {
                            if (!(LlamaService.this._LastServiceState_State == null || status == LlamaService.this._LastServiceState_State.intValue() || (status != 1 && status != 3))) {
                                LlamaService.this._LastSignalStrength = Integer.valueOf(SignalLevelCondition.MIN_VALUE_NO_SIGNAL);
                                LlamaService.this.testEvents(StateChange.CreateSignalStrength(LlamaService.this));
                            }
                            LlamaService.this._LastServiceState_State = Integer.valueOf(status);
                        }
                    });
                }

                public void onSignalStrengthsChanged(final SignalStrength strength) {
                    LlamaService.this.IdeallyRunOnWorkerThread(new X() {
                        /* Access modifiers changed, original: 0000 */
                        public void R() {
                            if (!LlamaService.this.GetIsAirplaneModeEnabled()) {
                                if (strength.isGsm()) {
                                    LlamaService.this._LastSignalStrength = Integer.valueOf((strength.getGsmSignalStrength() * 2) - 113);
                                } else {
                                    LlamaService.this._LastSignalStrength = Integer.valueOf(strength.getCdmaDbm());
                                }
                                Logging.Report("SignalStrength", "Signal strength is " + LlamaService.this._LastSignalStrength + " (" + strength.getGsmSignalStrength() + "," + strength.getCdmaDbm() + "," + strength.getEvdoDbm() + ")", LlamaService.this);
                                LlamaService.this.testEvents(StateChange.CreateSignalStrength(LlamaService.this));
                            }
                        }
                    });
                }
            };
            Logging.Report("starting to listen for telephony changes", getApplicationContext());
            if (this._TelephonyManager == null) {
                this._TelephonyManager = (TelephonyManager) getSystemService("phone");
            }
            this._TelephonyManager.listen(this._RoamingListener, 257);
        }
    }

    public boolean getRoamingStatus() {
        if (this._LastRoamingState == null) {
            this._LastRoamingState = Boolean.valueOf(this._TelephonyManager.isNetworkRoaming());
        }
        return this._LastRoamingState.booleanValue();
    }

    /* Access modifiers changed, original: 0000 */
    public void initCellListener() {
        Log.i(Constants.TAG, "InitCellListener");
        if (this._TelephonyManager == null) {
            this._TelephonyManager = (TelephonyManager) getSystemService("phone");
        }
        if (this._CellListener == null) {
            this._CellListener = new PhoneStateListener() {
                public void onCellLocationChanged(CellLocation loc) {
                    int cellId = -1;
                    short mcc = (short) -1;
                    short mnc = (short) -1;
                    Integer cdmaNetworkId = null;
                    if (loc == null) {
                        Logging.Report("cell was null", LlamaService.this);
                    } else if (loc instanceof GsmCellLocation) {
                        GsmCellLocation gsmCell = (GsmCellLocation) loc;
                        if (gsmCell != null) {
                            cellId = gsmCell.getCid();
                        }
                        Logging.Report("got GSM cellID " + cellId, LlamaService.this);
                    } else if (loc instanceof CdmaCellLocation) {
                        CdmaCellLocation cdmaCell = (CdmaCellLocation) loc;
                        if (cdmaCell != null) {
                            cellId = cdmaCell.getBaseStationId();
                            cdmaNetworkId = Integer.valueOf(cdmaCell.getNetworkId());
                            StringBuffer sb = new StringBuffer();
                            sb.append("CDMA cell change\n");
                            sb.append("getBaseStationId=").append(cdmaCell.getBaseStationId()).append("\n");
                            sb.append("getBaseStationLatitude=").append(cdmaCell.getBaseStationLatitude()).append("\n");
                            sb.append("getBaseStationLongitude=").append(cdmaCell.getBaseStationLongitude()).append("\n");
                            sb.append("getNetworkId=").append(cdmaCell.getNetworkId()).append("\n");
                            sb.append("getSystemId=").append(cdmaCell.getSystemId()).append("\n");
                            Logging.Report(sb.toString(), LlamaService.this.getApplicationContext());
                        }
                    } else {
                        LlamaService.this.HandleFriendlyError("Couldn't read cell ID", false);
                    }
                    if (cellId == 0 || cellId == Integer.MAX_VALUE) {
                        cellId = -1;
                    } else {
                        String networkOperator = ((TelephonyManager) LlamaService.this.getSystemService("phone")).getNetworkOperator();
                        Logging.Report("got operator [" + networkOperator + "]", LlamaService.this);
                        if (networkOperator != null && networkOperator.length() > 3) {
                            try {
                                mcc = Short.parseShort(networkOperator.substring(0, 3));
                                mnc = Short.parseShort(networkOperator.substring(3));
                            } catch (NumberFormatException e) {
                                Logging.Report("Mcc/Mnc parse failed: " + networkOperator, LlamaService.this);
                            }
                            if (mcc == (short) 0 && mnc == (short) 0 && cdmaNetworkId != null) {
                                mcc = (short) -1;
                                mnc = (short) -1;
                            } else {
                                if (mcc == (short) 0) {
                                    mcc = (short) -1;
                                }
                                if (mcc == (short) -1 && mnc == (short) 0) {
                                    mnc = (short) -1;
                                }
                            }
                        }
                    }
                    Logging.Report("Cell change " + cellId + " " + mcc + " " + mnc, LlamaService.this);
                    if (DeviceCompat.WeirdlyReportsInvalidCellIdsBeforeRealCellIds() && cellId == -1 && mcc != (short) -1 && mnc != (short) -1) {
                        return;
                    }
                    if (cellId == -1 || mcc != (short) -1 || mnc != (short) -1 || cdmaNetworkId != null) {
                        final Cell cell = new Cell(cellId, mcc, mnc);
                        LlamaService.this.IdeallyRunOnWorkerThread(new X() {
                            /* Access modifiers changed, original: 0000 */
                            public void R() {
                                LlamaService.this.cellLocationChanged(cell);
                            }
                        });
                    }
                }
            };
            Logging.Report("starting to listen", getApplicationContext());
            this._TelephonyManager.listen(this._CellListener, 16);
        }
        Logging.Report("requesting update", getApplicationContext());
        Log.i(Constants.TAG, "requesting update");
        initCellPoller();
    }

    public void initIgnoredCells() {
        if (((Boolean) LlamaSettings.IgnoreInvalidCell.GetValue(this)).booleanValue()) {
            this._IgnoredCells.add(Cell.NoSignal);
        } else {
            this._IgnoredCells.remove(Cell.NoSignal);
        }
    }

    public void runInitialCellListen() {
        this._CellListener.onCellLocationChanged(this._TelephonyManager.getCellLocation());
    }

    public void initCellPoller() {
        boolean allowCellPolling;
        Intent intent = new Intent(this, RtcReceiver.class);
        intent.putExtra("cpuWakerCell", "cpuWakerCell");
        if (this._CpuWakerPendingIntent == null) {
            this._CpuWakerPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), Constants.CPU_WAKER, intent, 134217728);
        }
        AlarmManager alarmManager = (AlarmManager) getSystemService("alarm");
        switch (((Integer) LlamaSettings.CellPollingMode.GetValue(this)).intValue()) {
            case 0:
                if (this.LearningArea == null) {
                    allowCellPolling = false;
                    break;
                } else {
                    allowCellPolling = true;
                    break;
                }
            case 1:
                allowCellPolling = true;
                break;
            default:
                allowCellPolling = false;
                break;
        }
        if (allowCellPolling) {
            int minutes = ((Integer) LlamaSettings.CellPollingInterval.GetValue(this)).intValue();
            Calendar time = Calendar.getInstance();
            time.add(12, minutes);
            alarmManager.setInexactRepeating(0, time.getTimeInMillis(), (long) ((minutes * 60) * 1000), this._CpuWakerPendingIntent);
            return;
        }
        CancelProximityAlert(false, "Proximity alert no longer required");
        CancelCellPoller();
    }

    private void CancelCellPoller() {
        if (this._CpuWakerPendingIntent != null) {
            this._CpuWakerPendingIntent.cancel();
            ((AlarmManager) getSystemService("alarm")).cancel(this._CpuWakerPendingIntent);
            this._CpuWakerPendingIntent = null;
        }
        CellPollWakeLock.ReleaseLock(this);
        CellPollWakeLock.ReleaseScreenLock(this);
    }

    /* Access modifiers changed, original: 0000 */
    public void CancelProximityAlert(boolean isTimeout, String loggingComment) {
        if (!(this._LocationManager == null || this._ProximityPendingIntent == null)) {
            this._LocationManager.removeProximityAlert(this._ProximityPendingIntent);
            this._ProximityPendingIntent = null;
            Logging.Report("CellProd", "CancelledProximityAlert - " + loggingComment, (Context) this);
            if (((Boolean) LlamaSettings.DebugCellsInRecent.GetValue(getApplicationContext())).booleanValue()) {
                ThreadComplainMustBeWorker();
                this._RecentCells.addFirst(new BeaconAndCalendar(Cell.ProximityTimeout, Calendar.getInstance()));
            }
        }
        CellPollWakeLock.ReleaseLock(this);
        CellPollWakeLock.ReleaseScreenLock(this);
    }

    public void ForceCellCheck() {
        if (!this._ScreenIsOn) {
            Intent intent;
            if (this._ProximityPendingIntent == null) {
                intent = new Intent(this, IntentReceiver.class);
                intent.setAction(Constants.ACTION_PROXI_CHANGED);
                this._ProximityPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), Constants.LOCATION_PROXIMITY, intent, 134217728);
            }
            if (this._LocationManager == null) {
                this._LocationManager = (LocationManager) getSystemService("location");
            }
            this._LocationManager.addProximityAlert(0.0d, 0.0d, 1.0f, -1, this._ProximityPendingIntent);
            Logging.Report("CellProd", "Added proximity alert", (Context) this);
            if (((Boolean) LlamaSettings.DebugCellsInRecent.GetValue(getApplicationContext())).booleanValue()) {
                ThreadComplainMustBeWorker();
                this._RecentCells.addFirst(new BeaconAndCalendar(Cell.ProximityRequest, Calendar.getInstance()));
            }
            if (((Boolean) LlamaSettings.CellPollingWithWakeLock.GetValue(this)).booleanValue()) {
                CellPollWakeLock.AcquireLock(this, "Force cell check");
            }
            if (((Boolean) LlamaSettings.CellPollingWithScreenWakeLock.GetValue(this)).booleanValue()) {
                CellPollWakeLock.AcquireScreenLock(this, "Force cell check (with screen)");
            }
            intent = new Intent(this, RtcReceiver.class);
            intent.putExtra("proximity", true);
            this._RtcPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), Constants.RTC_PROXIMITY_TIMEOUT, intent, 134217728);
            ((AlarmManager) getSystemService("alarm")).set(0, System.currentTimeMillis() + ((long) ((Integer) LlamaSettings.CellPollingActiveMillis.GetValue(this)).intValue()), this._RtcPendingIntent);
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void initFakeCellListener() {
        this.fakeCellHandler.removeCallbacks(this.fakeCellRunnable);
        this.fakeCellHandler.postDelayed(this.fakeCellRunnable, 10000);
        cellLocationChanged(new Cell(this.fakeCellCount, (short) 11, (short) 22));
    }

    /* Access modifiers changed, original: 0000 */
    public void EarthPointChanged(EarthPoint point) {
        if (point == null) {
            this._LastEarthPoint.clear();
        } else if (this._LastEarthPoint.size() == 0) {
            this._LastEarthPoint.add(point);
        } else {
            this._LastEarthPoint.set(0, point);
        }
        BeaconsChanged(this._LastEarthPoint, Beacon.EARTH_POINT);
    }

    /* Access modifiers changed, original: 0000 */
    public void BeaconsChanged(Iterable<Beacon> beacons, String beaconTypeId) {
        ThreadComplainMustBeWorker();
        evaluateAreasForBeaconTypeAndOtherAreas(beaconTypeId);
        for (Beacon b : beacons) {
            this._RecentCells.addFirst(new BeaconAndCalendar(b, Calendar.getInstance()));
        }
        SaveRecent();
        if (Instances.CellsActivity != null) {
            Instances.CellsActivity.Update();
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void cellLocationChanged(Cell cell) {
        if (cell != null) {
            boolean cellHasChanged;
            boolean addCellToRecent = false;
            boolean evaluateAreaForNewCell = false;
            Cell previousCell = this.LastCell;
            if (this.LastCell == null || !this.LastCell.equals(cell)) {
                cellHasChanged = true;
            } else {
                cellHasChanged = false;
            }
            if (cellHasChanged) {
                addCellToRecent = true;
                learnCellIfNeeded(cell);
                if (this._LocationLogger != null) {
                    this._LocationLogger.CellChange(cell);
                }
                if (this._IgnoredCells.contains(cell)) {
                    Logging.Report("Cell", "Ignored cell " + cell.ToColonSeparated(), (Context) this);
                } else {
                    if (this._LastCell.size() == 0) {
                        this._LastCell.add(cell);
                    } else {
                        this._LastCell.set(0, cell);
                    }
                    evaluateAreaForNewCell = true;
                }
                this.LastCell = cell;
            } else if (((Boolean) LlamaSettings.LogAllCellChanges.GetValue(this)).booleanValue()) {
                addCellToRecent = true;
            }
            if (addCellToRecent) {
                ThreadComplainMustBeWorker();
                this._RecentCells.addFirst(new BeaconAndCalendar(cell, Calendar.getInstance()));
                SaveRecent();
                if (Instances.CellsActivity != null) {
                    Instances.CellsActivity.Update();
                }
            }
            if (evaluateAreaForNewCell) {
                if (previousCell != null) {
                    testEvents(StateChange.CreateMccMnc(this, previousCell, cell));
                }
                evaluateAreasForBeaconTypeAndOtherAreas(cell.GetTypeId());
            }
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void learnCellIfNeeded(Cell cell) {
        if (this.LearningArea != null && this.LearningUntilDateTime != null && !cell.IsNoSignal()) {
            if (this.LearningUntilDateTime.compareTo(Calendar.getInstance().getTime()) <= 0) {
                Logging.Report("Learning time has expired", (Context) this);
                this.LearningArea = null;
                this.LearningUntilDateTime = null;
                initCellPoller();
            } else if (AddCellToArea(cell, this.LearningArea, false)) {
                evaluateAreasForBeaconTypeAndOtherAreas(cell.GetTypeId());
            }
        }
    }

    public boolean AddCellToArea(Cell cell, Area area) {
        return AddCellToArea(cell, area, true);
    }

    public boolean AddCellToArea(Beacon cell, Area area, boolean notifyUpdates) {
        if (!area.AddBeacon(cell)) {
            return false;
        }
        addCellToAreaMap(cell, area);
        this._Storage.SaveAreas(getApplicationContext(), this._Areas);
        evaluateAreasForBeaconTypeAndOtherAreas(cell.GetTypeId());
        if (notifyUpdates && Instances.CellsActivity != null) {
            Instances.CellsActivity.Update();
        }
        return true;
    }

    private HashSet<String> GetAreaListForBeacon(String beaconTypeId) {
        return (HashSet) this._AreasForBeaconTypeLookup.get(beaconTypeId);
    }

    private Collection<Beacon> GetLastBeaconsForBeaconType(String beaconTypeId) {
        return (Collection) this._LastBeaconsForTypeLookup.get(beaconTypeId);
    }

    /* Access modifiers changed, original: 0000 */
    public void addCellToAreaMap(Beacon cell, Area area) {
        if (cell.CanSimpleDetectArea()) {
            ArrayList<String> areas;
            if (this._CellToAreaMap.containsKey(cell)) {
                areas = (ArrayList) this._CellToAreaMap.get(cell);
            } else {
                areas = new ArrayList();
                this._CellToAreaMap.put(cell, areas);
            }
            ArrayUtils.AddUnique(areas, area.Name);
        }
    }

    public boolean RemoveCellsFromArea(Iterable<Beacon> beaconsToDelete, Area area) {
        boolean removedAny = false;
        for (Beacon beacon : beaconsToDelete) {
            removedAny |= RemoveCellFromArea(beacon, area, false);
        }
        this._Storage.SaveAreas(getApplicationContext(), this._Areas);
        AfterBeaconsRemoved(beaconsToDelete);
        if (Instances.AreasActivity != null) {
            Instances.AreasActivity.Update();
        }
        if (Instances.CellsActivity != null) {
            Instances.CellsActivity.Update();
        }
        return removedAny;
    }

    public boolean RemoveCellFromArea(Beacon cell, Area area) {
        boolean removed = RemoveCellFromArea(cell, area, true);
        if (removed) {
            AfterBeaconsRemoved(IterableHelpers.Create(cell));
            if (Instances.AreasActivity != null) {
                Instances.AreasActivity.Update();
            }
            if (Instances.CellsActivity != null) {
                Instances.CellsActivity.Update();
            }
        }
        return removed;
    }

    private boolean RemoveCellFromArea(Beacon cell, Area area, boolean saveAndUpdate) {
        if (!area.RemoveCell(cell)) {
            return false;
        }
        ArrayList<String> areaMapForCell = (ArrayList) this._CellToAreaMap.get(cell);
        if (areaMapForCell != null) {
            areaMapForCell.remove(area.Name);
        }
        if (saveAndUpdate) {
            this._Storage.SaveAreas(getApplicationContext(), this._Areas);
            if (Instances.AreasActivity != null) {
                Instances.AreasActivity.Update();
            }
            if (Instances.CellsActivity != null) {
                Instances.CellsActivity.Update();
            }
        }
        return true;
    }

    private void AfterBeaconsRemoved(Iterable<Beacon> beacons) {
        HashSet<String> beaconTypes = new HashSet();
        for (Beacon beacon : beacons) {
            String type = beacon.GetTypeId();
            if (type == Beacon.WIFI_MAC_ADDRESS) {
                type = Beacon.WIFI_NAME;
            }
            beaconTypes.add(type);
            evaluateAreasForBeaconTypeAndOtherAreas(type);
        }
    }

    public Iterable<BeaconAndCalendar> GetRecentCells() {
        ThreadComplainMustBeWorker();
        return IterableHelpers.ToArrayList(this._RecentCells);
    }

    public Iterable<EventHistory> GetEventHistory() {
        return this._EventHistory;
    }

    private void updateAreasBasedOnLastBeacons(String beaconTypeId) {
        Collection<Beacon> lastBeaconsForType = GetLastBeaconsForBeaconType(beaconTypeId);
        HashSet<String> areasListForBeacon = GetAreaListForBeacon(beaconTypeId);
        areasListForBeacon.clear();
        for (Beacon beacon : lastBeaconsForType) {
            List<String> areas = beacon.GetAreaNames(this);
            if (areas != null) {
                areasListForBeacon.addAll(areas);
            }
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void evaluateAreasForBeaconTypeAndOtherAreas(String beaconTypeId) {
        updateAreasBasedOnLastBeacons(beaconTypeId);
        HashSet<String> newAreas = new HashSet();
        if (this._AreasForCell != null) {
            newAreas.addAll(this._AreasForCell);
        }
        if (this._AreasForEarthPoint != null) {
            newAreas.addAll(this._AreasForEarthPoint);
        }
        if (this._AreasForWifi != null) {
            newAreas.addAll(this._AreasForWifi);
        }
        if (this._AreasForBluetooth != null) {
            newAreas.addAll(this._AreasForBluetooth);
        }
        boolean areasHaveChanged = false;
        HashSet<String> previousAreas = this.CurrentAreas;
        this.CurrentAreas = newAreas;
        ArrayList<StateChange> changes = new ArrayList();
        Iterator i$ = previousAreas.iterator();
        while (i$.hasNext()) {
            String oldAreaName = (String) i$.next();
            if (newAreas == null || !newAreas.contains(oldAreaName)) {
                changes.add(StateChange.Create(this, oldAreaName, false));
                areasHaveChanged = true;
            }
        }
        if (newAreas != null) {
            i$ = newAreas.iterator();
            while (i$.hasNext()) {
                String areaName = (String) i$.next();
                if (!previousAreas.contains(areaName)) {
                    changes.add(StateChange.Create(this, areaName, true));
                    areasHaveChanged = true;
                }
            }
        }
        testEvents(changes, null);
        if (areasHaveChanged) {
            String lastAreaNames;
            if (this.CurrentAreas.size() > 0) {
                StringBuffer sb = new StringBuffer();
                boolean needComma = false;
                i$ = this.CurrentAreas.iterator();
                while (i$.hasNext()) {
                    String s = (String) i$.next();
                    if (needComma) {
                        sb.append(", ");
                    }
                    sb.append(s);
                    needComma = true;
                }
                lastAreaNames = sb.toString();
            } else {
                lastAreaNames = null;
            }
            this._OngoingNotification.SetCurrentAreaName(lastAreaNames);
            MinimalisticTextIntegration.SetAreaNames(this, lastAreaNames);
            LlamaSettings.LastAreaNames.SetValueAndCommit(this, lastAreaNames, new CachedSetting[0]);
            if (Instances.AreasActivity != null) {
                Instances.AreasActivity.Update();
            }
        }
        this._Storage.SetLastBeacons(getApplicationContext(), this._LastBluetooth, this._LastCell, this._LastEarthPoint, this._LastWifi);
    }

    /* Access modifiers changed, original: 0000 */
    public void InitialiseEventTesting() {
        this._TestEventCounter = 0;
        this._EventsChanged = false;
        this._EventRtcPotentiallyChanged = false;
        this._EventHistoryChanged = false;
        this._VariablesHaveChanged = false;
    }

    /* Access modifiers changed, original: 0000 */
    public void FinishedEventTesting(Calendar optionalEventTime) {
        if (this._EventsChanged) {
            Logging.Report("Saving events", getApplicationContext());
            this._Storage.SaveEvents(getApplicationContext(), this._Events);
            OnEventsChanged(optionalEventTime);
            this._EventRtcPotentiallyChanged = false;
        }
        if (this._EventRtcPotentiallyChanged) {
            QueueRtcWake(optionalEventTime);
        }
        if (this._EventHistoryChanged) {
            this._Storage.SaveEventHistory(this, this._EventHistory);
        }
        if (this._VariablesHaveChanged) {
            Logging.Report("Variables", "Variables have changed, saving", (Context) this);
            this._Storage.SaveVariables(this, this._Variables);
        }
        IdeallyRunOnUiThread(new X() {
            /* Access modifiers changed, original: 0000 */
            public void R() {
                if (Instances.EventsActivity != null) {
                    Instances.EventsActivity.Update();
                }
            }
        });
        if (this._EventHistoryChanged) {
            IdeallyRunOnUiThread(new X() {
                /* Access modifiers changed, original: 0000 */
                public void R() {
                    if (Instances.EventHistoryActivity != null) {
                        Instances.EventHistoryActivity.Update();
                    }
                }
            });
        }
        if (this._EventActionCountChanged) {
            LlamaSettings.EventRuns.SetValueAndCommit(this, LlamaSettings.EventRuns.GetValue(this), new CachedSetting[0]);
            IdeallyRunOnUiThread(new X() {
                /* Access modifiers changed, original: 0000 */
                public void R() {
                    if (Instances.UiActivity != null) {
                        Instances.UiActivity.UpdateCounters();
                    }
                }
            });
        }
        this._Events.SanityCheck(this);
    }

    /* Access modifiers changed, original: 0000 */
    public void testEventsThreadSafe(StateChange stateChange) {
        testEventsThreadSafe(IterableHelpers.Create(stateChange), null);
    }

    /* Access modifiers changed, original: 0000 */
    public void testEventsThreadSafe(final Collection<StateChange> stateChange, final Calendar optionalEventTime) {
        if (!((Boolean) LlamaSettings.MultiThreadedMode.GetValue(this)).booleanValue()) {
            testEvents(stateChange, optionalEventTime);
        } else if (IsOnWorkerThread()) {
            testEvents(stateChange, optionalEventTime);
        } else {
            this._WorkerThreadHandler.post(new Runnable() {
                public void run() {
                    LlamaService.this.testEvents(stateChange, optionalEventTime);
                }
            });
        }
    }

    /* Access modifiers changed, original: 0000 */
    public boolean testEvents(StateChange stateChange) {
        return testEvents(IterableHelpers.Create(stateChange), null);
    }

    /* Access modifiers changed, original: 0000 */
    public boolean testEvents(Collection<StateChange> stateChanges, Calendar optionalEventTime) {
        boolean anyEventFired = false;
        if (!(stateChanges == null || stateChanges.size() == 0)) {
            ThreadComplainMustNotBeUi();
            InitialiseEventTesting();
            for (StateChange sc : stateChanges) {
                this._TestEventCounter = 0;
                testEventsInternal(sc);
            }
            if (this._EventsChanged || this._EventRtcPotentiallyChanged) {
                anyEventFired = true;
            }
            FinishedEventTesting(optionalEventTime);
        }
        return anyEventFired;
    }

    /* Access modifiers changed, original: 0000 */
    public void testEventsInternal(StateChange stateChange) {
        this._TestEventCounter++;
        if (this._TestEventCounter > ((Integer) LlamaSettings.EventRecursionLimit.GetValue(this)).intValue()) {
            HandleFriendlyError("Your events caused " + LlamaSettings.EventRecursionLimit.GetValue(this) + " consecutive event tests. Check your variable changes.", false);
            return;
        }
        this._LastStateChange = stateChange;
        boolean eventsHaveChanged = false;
        boolean eventDelayOrRepeatAtChanged = false;
        try {
            Logging.StartBuffering();
            Collection<Event> arrayList = new ArrayList(this._Events.GetEventsForTriggerId(stateChange.TriggerType, stateChange.EventName));
            Logging.Report("TestEvent", "StateChange triggertype=" + stateChange.TriggerType + " got " + arrayList.size() + " triggerable events.", (Context) this);
            for (Event e : arrayList) {
                if (e.Enabled) {
                    boolean eventHadTimers = e.HasTimers();
                    Tuple<EventTrigger, Boolean> eventTriggerInfo = e.TestEventConditions(stateChange, this, null);
                    EventTrigger eventTrigger = eventTriggerInfo.Item1;
                    boolean eventConditionsAreAllTrue = ((Boolean) eventTriggerInfo.Item2).booleanValue();
                    boolean eventWasDeleted = false;
                    if (eventTrigger == EventMeta.Delayed) {
                        if (e.CancelDelayedIfFailed || eventConditionsAreAllTrue) {
                            Logging.Report("TestEvent", e.Name + " delay trigger. Conditions valid = " + eventConditionsAreAllTrue, getBaseContext(), true, false, true);
                        } else {
                            Logging.Report("TestEvent", "Conditions are no longer true, but we don't want to cancel the delayed event. Pretending conditions are still true", getBaseContext(), true, false, true);
                            eventConditionsAreAllTrue = true;
                        }
                    }
                    if (!eventConditionsAreAllTrue) {
                        if (e.NextRepeatAtMillis != 0) {
                            Logging.Report(e.Name + " was previously repeating and is now false. Clearing next repeat", (Context) this);
                            e.NextRepeatAtMillis = 0;
                            AddEventHistory(new EventHistory(stateChange.CurrentDate, e.Name, null, 12));
                            eventsHaveChanged = true;
                        }
                        if (e.DelayedUntilMillis != 0 && e.CancelDelayedIfFailed) {
                            Logging.Report(e.Name + " was previously delay and cancellable, and is now false. Clearing delay", (Context) this);
                            e.DelayedUntilMillis = 0;
                            AddEventHistory(new EventHistory(stateChange.CurrentDate, e.Name, null, 13));
                            if (IsDeletableEvent(e)) {
                                Logging.Report(e.Name + " was one-shot and is no longer delayed, removing it.", (Context) this);
                                this._Events.DeleteByName(e.Name, eventHadTimers);
                                eventWasDeleted = true;
                            }
                            eventsHaveChanged = true;
                        }
                        if (e.ConfirmationStatus == 3) {
                            Logging.Report(e.Name + " was previously confirmed, and is now false. Clearing confirmation to prevent repeats", (Context) this);
                            e.ConfirmationStatus = 1;
                            eventsHaveChanged = true;
                        }
                    } else if (eventTrigger != null) {
                        boolean runEventActions;
                        Logging.Report("TestEvent", "Event " + e.Name + " success WITH trigger " + eventTrigger.getClass().getName(), getBaseContext(), true, false, true);
                        boolean triggerIsProhibited = false;
                        if (eventTrigger == EventMeta.Delayed) {
                            Logging.Report(e.Name + " was previously delayed and is firing. Clearing delay.", (Context) this);
                            e.DelayedUntilMillis = 0;
                            eventsHaveChanged = true;
                            runEventActions = true;
                        } else {
                            triggerIsProhibited = !e.IsConditionTriggerAllowed(eventTrigger);
                            if (triggerIsProhibited) {
                                Logging.Report(e.Name + " trigger " + eventTrigger + " was prohibited", (Context) this);
                                runEventActions = false;
                            } else if (e.HasDelay() && e.NextRepeatAtMillis == 0) {
                                if (e.DelayedUntilMillis == 0) {
                                    e.DelayedUntilMillis = (stateChange.CurrentMillis + ((long) ((e.DelayMinutes * 60) * 1000))) + ((long) (e.DelaySeconds * 1000));
                                    eventDelayOrRepeatAtChanged = true;
                                    Logging.Report(e.Name + " was needs to be delayed until " + e.DelayedUntilMillis, (Context) this);
                                    AddEventHistory(new EventHistory(stateChange.CurrentDate, e.Name, eventTrigger, 10));
                                } else {
                                    Logging.Report(e.Name + " is already delayed until " + e.DelayedUntilMillis, (Context) this);
                                }
                                runEventActions = false;
                            } else {
                                Logging.Report(e.Name + " no delaying needed", (Context) this);
                                runEventActions = true;
                            }
                        }
                        if (runEventActions) {
                            boolean confirmationCanRunActions;
                            if (e.ConfirmationStatus == 0) {
                                Logging.Report(e.Name + " confirmation not needed", (Context) this);
                                confirmationCanRunActions = true;
                            } else if (e.ConfirmationStatus != 3) {
                                Logging.Report(e.Name + " already awaiting confirmation", (Context) this);
                                confirmationCanRunActions = false;
                            } else if (eventTrigger == EventMeta.Repeated) {
                                Logging.Report(e.Name + " confirmation granted and repeating", (Context) this);
                                confirmationCanRunActions = true;
                            } else {
                                Logging.Report(e.Name + " confirmation granted, was retriggered. Requires confirmation again", (Context) this);
                                e.ConfirmationStatus = 1;
                                confirmationCanRunActions = false;
                            }
                            if (confirmationCanRunActions) {
                                Tuple<Boolean, Boolean> result = fireTriggeredEvent(e, stateChange.CurrentDate, Long.valueOf(stateChange.GetCurrentMillisRoundedToNextMinute()), eventTrigger, null, 0);
                                eventsHaveChanged |= ((Boolean) result.Item1).booleanValue();
                                eventDelayOrRepeatAtChanged |= ((Boolean) result.Item1).booleanValue();
                                if (((Boolean) result.Item2).booleanValue()) {
                                    this._Events.DeleteByName(e.Name, eventHadTimers);
                                    eventsHaveChanged = true;
                                    eventWasDeleted = true;
                                }
                            } else if (e.ConfirmationStatus == 1) {
                                Logging.Report(e.Name + " confirmation needed", (Context) this);
                                AddEventHistory(new EventHistory(stateChange.CurrentDate, e.Name, eventTrigger, 11));
                                showConfirmationForEvent(e);
                                eventsHaveChanged = true;
                            }
                        } else if (triggerIsProhibited) {
                            AddEventHistory(new EventHistory(stateChange.CurrentDate, e.Name, eventTrigger, 20));
                        }
                    }
                    if (!eventWasDeleted) {
                        this._Events.ReloadTriggersForEventIfItHadTimers(e, eventHadTimers);
                    }
                }
            }
            boolean z = this._EventsChanged;
            int i = (eventsHaveChanged || stateChange.GetEventsNeedSaving()) ? 1 : 0;
            this._EventsChanged = i | z;
            this._EventHistoryChanged |= false;
            z = this._EventRtcPotentiallyChanged;
            i = (eventDelayOrRepeatAtChanged || stateChange.TriggerType == 2 || stateChange.GetQueueRtcNeeded()) ? 1 : 0;
            this._EventRtcPotentiallyChanged = i | z;
            EnqueueQueuedEventsAndCheckVariableChanges();
        } finally {
            Logging.StopBufferingAndCommit("EventBuffer", this, false);
        }
    }

    private void showConfirmationForEvent(Event e) {
        e.ConfirmationStatus = 2;
        int notificationId = createNotificationForEvent(e, true);
        if (e.ConfirmationDialog) {
            Intent intent = new Intent(this, EventConfirmationActivity.class);
            intent.putExtra(Constants.EXTRA_NOTIFICATION_EVENT_NAME, e.Name);
            intent.putExtra(Constants.EXTRA_NOTIFICATION_ID_TO_CLEAR, notificationId);
            intent.addFlags(524288);
            intent.addFlags(67108864);
            intent.addFlags(8388608);
            intent.addFlags(268435456);
            intent.addFlags(134217728);
            intent.addFlags(1073741824);
            intent.addFlags(65536);
            startActivity(intent);
        }
    }

    private int createNotificationForEvent(Event e, boolean showTickerText) {
        int notificationId;
        PendingIntent contentIntent;
        int rand = this.r.nextInt(1000000);
        if (e.NotificationManagerNotificationId != 0) {
            notificationId = e.NotificationManagerNotificationId;
        } else {
            notificationId = Constants.OTHER_NOTIFICATION_STARTID + rand;
        }
        CharSequence tickerText = "Llama Confirmation";
        long when = System.currentTimeMillis();
        Context context = getBaseContext();
        CharSequence contentTitle = "Llama Confirmation";
        CharSequence contentText = e.Name;
        Intent notificationIntent;
        if (((Boolean) LlamaSettings.InstantConfirmation.GetValue(this)).booleanValue()) {
            notificationIntent = new Intent(this, LlamaService.class);
            notificationIntent.setAction(Constants.ACTION_CONFIRM_EVENT);
            notificationIntent.putExtra(Constants.EXTRA_NOTIFICATION_EVENT_NAME, e.Name);
            notificationIntent.putExtra(Constants.EXTRA_NOTIFICATION_ID_TO_CLEAR, notificationId);
            contentIntent = PendingIntent.getService(context, notificationId, notificationIntent, 0);
        } else {
            notificationIntent = new Intent(this, EventConfirmationActivity.class);
            notificationIntent.addFlags(524288);
            notificationIntent.addFlags(67108864);
            notificationIntent.addFlags(268435456);
            notificationIntent.putExtra(Constants.EXTRA_NOTIFICATION_EVENT_NAME, e.Name);
            notificationIntent.putExtra(Constants.EXTRA_NOTIFICATION_ID_TO_CLEAR, notificationId);
            contentIntent = PendingIntent.getActivity(context, notificationId, notificationIntent, 0);
        }
        Intent notificationClearIntent = new Intent(this, LlamaService.class);
        notificationClearIntent.setAction(Constants.ACTION_NOTIFICATION_CLEAR);
        notificationClearIntent.putExtra(Constants.EXTRA_NOTIFICATION_EVENT_NAME, e.Name);
        notificationClearIntent.putExtra(Constants.EXTRA_NOTIFICATION_ID_TO_CLEAR, notificationId);
        PendingIntent deleteIntent = PendingIntent.getService(context, notificationId, notificationClearIntent, 0);
        if (!showTickerText) {
            tickerText = null;
        }
        Notification notification = new Notification(R.drawable.ic_tab_events, tickerText, when);
        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
        notification.deleteIntent = deleteIntent;
        ((NotificationManager) getSystemService("notification")).notify(notificationId, notification);
        e.NotificationManagerNotificationId = notificationId;
        return notificationId;
    }

    public void SetEventConfirmationGranted(String eventName, int notificationIdToClear) {
        ((NotificationManager) getSystemService("notification")).cancel(notificationIdToClear);
        Event e = GetEventByName(eventName);
        if (e == null) {
            HandleFriendlyError(String.format(getString(R.string.hrTheEventNamed1NoLongerExists), new Object[]{eventName}), false);
            return;
        }
        e.ConfirmationStatus = 3;
        e.NotificationManagerNotificationId = 0;
        boolean eventHadTimers = e.HasTimers();
        if (((Boolean) fireTriggeredEvent(e, Calendar.getInstance(), null, EventMeta.Confirmed, null, 0).Item2).booleanValue()) {
            DeleteEventByNameNoSave(eventName);
        } else {
            this._Events.ReloadTriggersForEventIfItHadTimers(e, eventHadTimers);
        }
        this._Storage.SaveEvents(getApplicationContext(), this._Events);
        this._Storage.SaveEventHistory(getApplicationContext(), this._EventHistory);
        OnEventsChanged(null);
        if (Instances.EventsActivity != null) {
            Instances.EventsActivity.Update();
        }
    }

    public void SetEventConfirmationDenied(String eventName, int notificationIdToClear) {
        ((NotificationManager) getSystemService("notification")).cancel(notificationIdToClear);
        Event e = GetEventByName(eventName);
        if (e == null) {
            HandleFriendlyError(String.format(getString(R.string.hrTheEventNamed1NoLongerExists), new Object[]{eventName}), false);
            return;
        }
        e.ConfirmationStatus = 1;
        e.NotificationManagerNotificationId = 0;
        AddEventHistory(new EventHistory(Calendar.getInstance(), eventName, null, 14));
        this._Storage.SaveEvents(getApplicationContext(), this._Events);
        this._Storage.SaveEventHistory(getApplicationContext(), this._EventHistory);
        if (Instances.EventsActivity != null) {
            Instances.EventsActivity.Update();
        }
        if (Instances.EventHistoryActivity != null) {
            Instances.EventHistoryActivity.Update();
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void AddEventHistory(EventHistory history) {
        this._EventHistory.addFirst(history);
        while (this._EventHistory.size() > ((Integer) LlamaSettings.HistoryItems.GetValue(this)).intValue()) {
            this._EventHistory.removeLast();
        }
        this._EventHistoryChanged = true;
    }

    public Tuple<Boolean, Boolean> fireTriggeredEvent(Event e, Calendar currentDate, Long nextMinuteMillis, EventTrigger eventTrigger, Activity activity, int eventRunMode) {
        boolean eventsHaveChanged;
        if (nextMinuteMillis == null) {
            nextMinuteMillis = Long.valueOf(currentDate.getTimeInMillis());
        }
        e.RunActions(this, activity, nextMinuteMillis.longValue(), eventRunMode);
        AddEventHistory(new EventHistory(currentDate, e.Name, eventTrigger, e.Type));
        if (e.RepeatMinutesInterval != 0) {
            e.NextRepeatAtMillis = nextMinuteMillis.longValue() + ((long) ((e.RepeatMinutesInterval * 60) * 1000));
            eventsHaveChanged = true;
        } else {
            eventsHaveChanged = false;
        }
        return Tuple.Create(Boolean.valueOf(eventsHaveChanged), Boolean.valueOf(IsDeletableEvent(e)));
    }

    /* Access modifiers changed, original: 0000 */
    public boolean IsDeletableEvent(Event e) {
        switch (e.Type) {
            case 1:
            case 2:
                return true;
            default:
                return false;
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void EnqueueQueuedEventsAndCheckVariableChanges() {
        Logging.Report("Events to Enqueue " + this._EnqueuedEvents.size(), getApplicationContext());
        if (this._EnqueuedEvents.size() > 0) {
            this._Events.AddEvents(this._EnqueuedEvents);
            this._EnqueuedEvents.clear();
            this._EventsChanged = true;
            this._EventRtcPotentiallyChanged = true;
        }
        HandleVariableChanges();
    }

    private boolean HandleVariableChanges() {
        if (this._VariableChanges == null || this._VariableChanges.size() <= 0) {
            return false;
        }
        List<Tuple3<String, String, String>> changes = new ArrayList();
        for (Entry<String, String> entry : this._VariableChanges.entrySet()) {
            String key = (String) entry.getKey();
            String newValue = (String) entry.getValue();
            String oldValue = (String) this._Variables.get(key);
            if (oldValue == null) {
                oldValue = "";
            }
            changes.add(Tuple3.Create(key, oldValue, newValue));
            this._Variables.put(key, newValue);
        }
        this._VariableChanges = null;
        for (Tuple3<String, String, String> change : changes) {
            testEventsInternal(StateChange.CreateVariableChange(this, (String) change.Item1, (String) change.Item2, (String) change.Item3));
        }
        return true;
    }

    public void SetVariableValue(String variableName, String variableValue) {
        if (variableValue.equalsIgnoreCase((String) this._Variables.get(variableName))) {
            Logging.Report("Variables", "Variable '" + variableName + "' was already set to '" + variableValue + "'", (Context) this);
            return;
        }
        if (this._VariableChanges == null) {
            this._VariableChanges = new HashMap();
        }
        this._VariableChanges.put(variableName, variableValue);
        this._VariablesHaveChanged = true;
        MinimalisticTextIntegration.SetVariableValue(this, variableName, variableValue);
    }

    public String GetVariableValue(String variableName) {
        if (this._VariableChanges != null) {
            String possiblyChangedVariable = (String) this._VariableChanges.get(variableName);
            if (possiblyChangedVariable != null) {
                return possiblyChangedVariable;
            }
        }
        return (String) this._Variables.get(variableName);
    }

    public boolean EnqueueEventForAfterTestEvents(String originalEventName, EventAction<?> actionToEnqueue, Calendar currentEventTime, int minutesFromNow, int extraSecondsFromNow) {
        Calendar date = Calendar.getInstance();
        Event event = new Event("(Auto) " + originalEventName + " at " + new HourMinute(date.get(11), date.get(12)).toString());
        event.Type = 1;
        event._Actions.add(actionToEnqueue);
        return EnqueueEventForAfterTestEvents(event, currentEventTime, minutesFromNow, extraSecondsFromNow);
    }

    public boolean EnqueueEventForAfterTestEvents(Event newEvent, Calendar currentEventTime, int minutesFromNow, int extraSecondsFromNow) {
        if (minutesFromNow > 0 || extraSecondsFromNow > 0) {
            extraSecondsFromNow = EnforceMinimumDuration(minutesFromNow, extraSecondsFromNow);
            Calendar clone = (Calendar) currentEventTime.clone();
            clone.set(14, 0);
            newEvent.DelayedUntilMillis = (((long) ((minutesFromNow * 60) * 1000)) + clone.getTimeInMillis()) + ((long) (extraSecondsFromNow * 1000));
            newEvent.DelayMinutes = minutesFromNow;
            newEvent.DelaySeconds = extraSecondsFromNow;
        }
        return EnqueueEventForAfterTestEvents(newEvent);
    }

    private int EnforceMinimumDuration(int minutesFromNow, int secondsFromNow) {
        if (minutesFromNow <= 0 && secondsFromNow < 2) {
            return 2;
        }
        return secondsFromNow;
    }

    public boolean EnqueueEventForAfterTestEvents(Event eventToEnqueue) {
        Iterator i$ = this._EnqueuedEvents.iterator();
        while (i$.hasNext()) {
            if (((Event) i$.next()).Name.equals(eventToEnqueue.Name)) {
                Logging.Report("Already an enqueued event named " + eventToEnqueue.Name, (Context) this);
                return false;
            }
        }
        i$ = this._Events.iterator();
        while (i$.hasNext()) {
            if (((Event) i$.next()).Name.equals(eventToEnqueue.Name)) {
                Logging.Report("Already an existing event named " + eventToEnqueue.Name, (Context) this);
                return false;
            }
        }
        this._EnqueuedEvents.add(eventToEnqueue);
        return true;
    }

    /* Access modifiers changed, original: 0000 */
    public void RunSingleEvent(String eventName, boolean showConfirmation, Activity activity, EventTrigger trigger, int eventRunMode) {
        Event e = GetEventByName(eventName);
        if (e == null) {
            HandleFriendlyError(String.format(activity.getString(R.string.hrCouldNotFindEventNamed1), new Object[]{eventName}), true);
            return;
        }
        RunSingleEvent(e, showConfirmation, activity, trigger, eventRunMode);
    }

    /* Access modifiers changed, original: 0000 */
    public void RunSingleEvent(Event event, boolean showConfirmations, Activity activity, EventTrigger trigger, int eventRunMode) {
        boolean eventsHaveChanged;
        InitialiseEventTesting();
        boolean eventRequiresConfirmation = event.ConfirmationStatus != 0;
        if (showConfirmations && eventRequiresConfirmation) {
            Logging.Report(event.Name + " run by shortcut and confirmation needed", (Context) this);
            AddEventHistory(new EventHistory(Calendar.getInstance(), event.Name, trigger, 11));
            showConfirmationForEvent(event);
            eventsHaveChanged = true;
        } else {
            boolean eventHadTimers = event.HasTimers();
            Tuple<Boolean, Boolean> result = fireTriggeredEvent(event, Calendar.getInstance(), null, trigger, activity, eventRunMode);
            eventsHaveChanged = ((Boolean) result.Item1).booleanValue();
            boolean deleteEvent = ((Boolean) result.Item2).booleanValue();
            if (eventsHaveChanged && !deleteEvent) {
                this._Events.ReloadTriggersForEventIfItHadTimers(event, eventHadTimers);
            }
        }
        this._EventsChanged |= eventsHaveChanged;
        EnqueueQueuedEventsAndCheckVariableChanges();
        FinishedEventTesting(null);
    }

    /* Access modifiers changed, original: 0000 */
    public void onEnterArea(String newArea) {
        testEvents(StateChange.Create(this, newArea, true));
    }

    /* Access modifiers changed, original: 0000 */
    public void onLeaveArea(String oldArea) {
        testEvents(StateChange.Create(this, oldArea, false));
    }

    public HashSet<String> GetCurrentAreas() {
        ThreadComplainMustBeWorker();
        return this.CurrentAreas;
    }

    public List<Area> GetAreas() {
        ThreadComplainMustBeWorker();
        return this._Areas;
    }

    public void Quit(boolean neverRestartService) {
        this._Quitting = true;
        if (neverRestartService) {
            LlamaSettings.LlamaWasExitted.SetValueAndCommit(this, Boolean.valueOf(true), new CachedSetting[0]);
        }
        if (Instances.UiActivity != null) {
            Instances.UiActivity.finish();
        }
        Instances.Service.stopSelf();
    }

    public void ShowAbout(Activity contextWithView) {
        String dayActionCounts;
        String versionName = HelpersC.GetVersionName(this);
        if (versionName == null) {
            versionName = "Error getting version :(";
        }
        Date installDate = (Date) LlamaSettings.InstallDate.GetValue(this);
        if (installDate != null) {
            int days = DateHelpers.GetSimpleDifferenceInDays(Calendar.getInstance().getTime(), installDate);
            Integer runs = (Integer) LlamaSettings.EventRuns.GetValue(this);
            int runsInt = runs == null ? 0 : runs.intValue();
            dayActionCounts = String.format(getString(R.string.hrPerformed1ActionsSince2DaysAgo), new Object[]{Integer.valueOf(runsInt), Integer.valueOf(days)}) + "\n\n";
        } else {
            dayActionCounts = "";
        }
        StringBuilder translatorThanks = new StringBuilder();
        translatorThanks.append(contextWithView.getString(R.string.hrThanksToTranslators));
        TranslatorInfo info = TranslatorInfo.GetInfo(contextWithView);
        int i = 0;
        while (i < info.LanguageIds.length) {
            if (info.TranslatorNames[i] != null && info.TranslatorNames[i].length() > 0) {
                translatorThanks.append("\n");
                translatorThanks.append(info.Names[i]);
                translatorThanks.append(" - ");
                translatorThanks.append(info.TranslatorNames[i]);
            }
            i++;
        }
        translatorThanks.append("\n");
        translatorThanks.append(contextWithView.getString(R.string.hrAlsoThanksTo));
        translatorThanks.append("\n");
        translatorThanks.append(Constants.EXTRA_THANKS_PEOPLES);
        Builder alertbox = new AlertDialogEx.Builder(contextWithView);
        alertbox.setMessage(String.format(contextWithView.getString(R.string.hrLlamaForAndroidVersionContact), new Object[]{versionName}) + "" + "\n\n" + dayActionCounts + translatorThanks.toString());
        alertbox.setNegativeButton(R.string.hrOkThanks, new OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        final Activity activity = contextWithView;
        alertbox.setPositiveButton(R.string.hrDonate, new OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                LlamaService.this.ShowDonationActivity(activity);
            }
        });
        alertbox.show();
    }

    public Area CreateArea(String value) {
        Area area = new Area(value);
        this._Areas.add(area);
        this._Storage.SaveAreas(getApplicationContext(), this._Areas);
        if (Instances.AreasActivity != null) {
            Instances.AreasActivity.Update();
        }
        return area;
    }

    public void StartLearning(Area a, int seconds) {
        this.LearningArea = a;
        Calendar c = Calendar.getInstance();
        c.add(13, seconds);
        this.LearningUntilDateTime = c.getTime();
        if (this.LastCell != null) {
            learnCellIfNeeded(this.LastCell);
        }
        initCellPoller();
    }

    public void RenameAreaByName(String oldName, String newName) {
        for (int i = 0; i < this._Areas.size(); i++) {
            if (((Area) this._Areas.get(i)).Name.equals(oldName)) {
                ((Area) this._Areas.get(i)).Name = newName;
                this._Storage.SaveAreas(getApplicationContext(), this._Areas);
                if (UpdateAreaNameReferences(oldName, newName)) {
                    this._Storage.SaveEvents(getApplicationContext(), this._Events);
                }
                if (Instances.AreasActivity != null) {
                    Instances.AreasActivity.Update();
                    return;
                }
                return;
            }
        }
    }

    private boolean UpdateAreaNameReferences(String oldName, String newName) {
        boolean changed = false;
        Iterator i$ = this._Events.iterator();
        while (i$.hasNext()) {
            changed |= ((Event) i$.next()).RenameArea(oldName, newName);
        }
        for (ArrayList<String> areaList : this._CellToAreaMap.values()) {
            ArrayUtils.SwapItem(areaList, oldName, newName);
        }
        return changed;
    }

    private boolean UpdateProfileNameReferences(String oldName, String newName) {
        boolean changed = false;
        Iterator i$ = this._Events.iterator();
        while (i$.hasNext()) {
            changed |= ((Event) i$.next()).RenameProfile(oldName, newName);
        }
        return changed;
    }

    public boolean DeleteAreaByName(String areaName) {
        int i;
        boolean removed = false;
        for (i = 0; i < this._Areas.size(); i++) {
            if (((Area) this._Areas.get(i)).Name.equals(areaName)) {
                this._Areas.remove(i);
                this._Storage.SaveAreas(getApplicationContext(), this._Areas);
                removed = true;
                break;
            }
        }
        if (removed) {
            for (ArrayList<String> areaList : this._CellToAreaMap.values()) {
                for (i = areaList.size() - 1; i >= 0; i--) {
                    if (((String) areaList.get(i)).equals(areaName)) {
                        areaList.remove(i);
                    }
                }
            }
            if (this.LearningArea != null && this.LearningArea.Name.equals(areaName)) {
                StopLearning(false);
            }
            if (Instances.AreasActivity != null) {
                Instances.AreasActivity.Update();
            }
        }
        return removed;
    }

    public Area GetAreaByName(String areaName) {
        for (int i = 0; i < this._Areas.size(); i++) {
            Area a = (Area) this._Areas.get(i);
            if (a.Name.equals(areaName)) {
                return a;
            }
        }
        return null;
    }

    public Profile GetProfileByName(String profileName) {
        for (int i = 0; i < this._Profiles.size(); i++) {
            Profile p = (Profile) this._Profiles.get(i);
            if (p.Name.equals(profileName)) {
                return p;
            }
        }
        return null;
    }

    public Event GetEventByName(String eventName) {
        return this._Events.GetByName(eventName);
    }

    public void StopLearning(boolean updateUi) {
        this.LearningUntilDateTime = null;
        this.LearningArea = null;
        if (updateUi && Instances.AreasActivity != null) {
            Instances.AreasActivity.Update();
        }
        initCellPoller();
    }

    public Area GetLearningArea() {
        ThreadComplainMustBeWorker();
        if (this.LearningUntilDateTime == null || this.LearningUntilDateTime.compareTo(Calendar.getInstance().getTime()) <= 0) {
            return null;
        }
        return this.LearningArea;
    }

    public Date GetLearningUntilDate() {
        ThreadComplainMustBeWorker();
        return this.LearningUntilDateTime;
    }

    public Collection<Profile> GetProfiles() {
        return this._Profiles;
    }

    public void SetProfile(String newProfileName, boolean checkPreviousProfileForChanges, Integer lockProfilesForMinutes, boolean disableExistingProfileLock) {
        boolean lockingToSameProfile;
        if (checkPreviousProfileForChanges) {
            CheckIfVolumeChanged();
        }
        boolean profilesAreCurrentlyLocked = ((Boolean) LlamaSettings.ProfileLocked.GetValue(this)).booleanValue();
        Profile p = GetProfileByName(newProfileName);
        String lastProfileName = (String) LlamaSettings.LastProfileName.GetValue(this);
        String profileAfterLock = (String) LlamaSettings.ProfileAfterLockName.GetValue(this);
        if (lockProfilesForMinutes == null || !newProfileName.equals(lastProfileName)) {
            lockingToSameProfile = false;
        } else {
            lockingToSameProfile = true;
        }
        if (disableExistingProfileLock) {
            Instances.Service.DisableProfileLock(false, true);
            profilesAreCurrentlyLocked = false;
        }
        if (profilesAreCurrentlyLocked || lockingToSameProfile) {
            if (lockingToSameProfile) {
                Logging.Report("Previously locked profile is the same as the current one, just update the lock time", getApplicationContext());
                EnableProfileLock(lockProfilesForMinutes.intValue(), profileAfterLock, false);
                return;
            }
            Logging.Report("Set profile failed, Profiles are locked", getApplicationContext());
            if (p != null) {
                LlamaSettings.ProfileAfterLockName.SetValueAndCommit(this, p.Name, new CachedSetting[0]);
                if (!p.Name.equals(LlamaSettings.LastProfileName.GetValue(this))) {
                    HandleFriendlyInfo(getString(R.string.hrProfilesAreLockedGotoProfilesTabToUnlock), false);
                }
            }
        } else if (p == null) {
            HandleFriendlyError(String.format(getString(R.string.hrCouldNotChangeProfileBecauseNoProfileNamed1), new Object[]{newProfileName}), false);
        } else {
            p.Activate(this, this._OngoingNotification, this._NoisyContacts);
            LlamaSettings.LastProfileName.SetValueAndCommit(this, p.Name, LlamaSettings.LastNotificationIconIsWarning.SetValueForCommit(Boolean.valueOf(false)));
            this.LastProfileNameDateTime = Calendar.getInstance();
            if (lockProfilesForMinutes != null) {
                EnableProfileLock(lockProfilesForMinutes.intValue(), lastProfileName, false);
            }
            if (Instances.ProfilesActivity != null) {
                Instances.ProfilesActivity.Update();
            }
        }
    }

    public void HandleFriendlyError(String message, boolean forceAsToast) {
        ShowFriendlyInfo(true, message, forceAsToast);
    }

    public void HandleFriendlyInfo(String message, boolean forceAsToast) {
        ShowFriendlyInfo(false, message, forceAsToast);
    }

    public void HandleFriendlyError(Integer confirmationMessageId) {
        ShowNotification(null, true, confirmationMessageId);
    }

    public void ShowFriendlyInfo(boolean isError, String message, boolean forceAsToast) {
        int value = ((Integer) LlamaSettings.ErrorNotificationMode.GetValue(this)).intValue();
        if (value != 0) {
            if (forceAsToast || value == 1) {
                Toast.makeText(getApplicationContext(), "Llama-" + getString(isError ? R.string.hrError : R.string.hrInfo) + ": " + message, isError ? 1 : 0).show();
            } else {
                ShowNotification(message, isError, null);
            }
        }
    }

    private void ShowNotification(String message, boolean isError, Integer confirmationMessageId) {
        String title;
        String StringLeft;
        if (confirmationMessageId == null) {
            title = isError ? getString(R.string.hrLlamaErrorV2) : getString(R.string.hrLlamaInfoV2);
        } else if (!ConfirmationMessages.HasAcceptedMessage(this, confirmationMessageId.intValue())) {
            if (message == null) {
                message = ConfirmationMessages.GetMessageText(this, confirmationMessageId.intValue());
                if (message == null) {
                    Logging.Report("Failed to find a confirmation message with ID=" + confirmationMessageId, (Context) this);
                    return;
                }
            }
            title = getString(R.string.hrLlamaProblem);
        } else {
            return;
        }
        if (this._NotificationManager == null) {
            this._NotificationManager = (NotificationManager) getSystemService("notification");
        }
        long when = System.currentTimeMillis();
        int i = isError ? 17301624 : 17301624;
        StringBuilder append = new StringBuilder().append(title).append(" - ");
        if (message.length() > 300) {
            StringLeft = HelpersC.StringLeft(message, 50, true);
        } else {
            StringLeft = message;
        }
        Notification n = new Notification(i, append.append(StringLeft).toString(), when);
        n.flags = 16;
        Intent notificationIntent = new Intent(this, LlamaUi.class);
        notificationIntent.addFlags(524288);
        notificationIntent.addFlags(67108864);
        notificationIntent.addFlags(268435456);
        notificationIntent.putExtra(Constants.EXTRA_NOTIFICATION_MESSAGE, message);
        notificationIntent.putExtra(Constants.EXTRA_NOTIFICATION_TITLE, title);
        if (confirmationMessageId != null) {
            notificationIntent.putExtra(Constants.EXTRA_NOTIFICATION_CONFIRMATION_MESSAGE_ID, confirmationMessageId);
        }
        n.setLatestEventInfo(this, isError ? getString(R.string.hrLlamaErrorV2) : getString(R.string.hrLlamaInfoV2), message, PendingIntent.getActivity(this, Constants.OTHER_NOTIFICATION_STARTID + 0, notificationIntent, 1207959552));
        this._NotificationManager.notify(Constants.OTHER_NOTIFICATION_STARTID + 0, n);
    }

    /* Access modifiers changed, original: 0000 */
    public void QueueRtcWake(Calendar optionalEventTime) {
        Calendar nextWakeDateTime = null;
        String nextWakeLog = null;
        Calendar now = Calendar.getInstance();
        if (optionalEventTime != null && DateHelpers.GetDifferenceHours(optionalEventTime, now) < -48) {
            Logging.Report("Date of last event was more than 48 hours ago: " + DateHelpers.FormatDate(optionalEventTime) + " vs " + DateHelpers.FormatDate(now) + ". Determining next events from current date", (Context) this);
            optionalEventTime = null;
        }
        if (now.get(1) < 2010) {
            Logging.Report("OMFG! We've gone back in time. We're probably on a stupid ICS phone that can't handle its date properly after reboot. Not queueing an event", (Context) this);
            this._WonkyIcsClock = true;
            return;
        }
        Calendar roundedToNextMinuteDateTime;
        if (optionalEventTime == null) {
            roundedToNextMinuteDateTime = now;
        } else {
            roundedToNextMinuteDateTime = (Calendar) optionalEventTime.clone();
        }
        roundedToNextMinuteDateTime.set(14, 0);
        roundedToNextMinuteDateTime.add(13, 1);
        if (optionalEventTime == null) {
            Logging.Report("Finding events at or after " + DateHelpers.FormatDate(roundedToNextMinuteDateTime), (Context) this);
        } else {
            Logging.Report("Finding events at or after " + DateHelpers.FormatDateWithYear(roundedToNextMinuteDateTime) + " (previously a timed event at " + DateHelpers.FormatDateWithYear(optionalEventTime) + ")", (Context) this);
        }
        Iterator i$ = this._Events.iterator();
        while (i$.hasNext()) {
            Calendar repeatingOrDelayedWakeDateTime;
            String repeatOrDelayReason;
            String eventWakeReason;
            Event e = (Event) i$.next();
            if (e.NextRepeatAtMillis != 0) {
                repeatingOrDelayedWakeDateTime = DateHelpers.CreateCalendar(e.NextRepeatAtMillis);
                repeatOrDelayReason = "repeat at " + e.NextRepeatAtMillis;
            } else if (e.DelayedUntilMillis != 0) {
                repeatingOrDelayedWakeDateTime = DateHelpers.CreateCalendar(e.DelayedUntilMillis);
                repeatOrDelayReason = "delay at " + e.DelayedUntilMillis;
            } else {
                repeatingOrDelayedWakeDateTime = null;
                repeatOrDelayReason = null;
            }
            if (repeatingOrDelayedWakeDateTime != null && roundedToNextMinuteDateTime.compareTo(repeatingOrDelayedWakeDateTime) > 0) {
                repeatingOrDelayedWakeDateTime = null;
                repeatOrDelayReason = null;
            }
            Tuple<Calendar, String> eventConditionWakeTimeTuple = e.GetNextEventTime(roundedToNextMinuteDateTime);
            Calendar eventConditionWakeTime = eventConditionWakeTimeTuple.Item1;
            String conditionId = eventConditionWakeTimeTuple.Item2;
            Calendar eventWakeDateTime = DateHelpers.MinCalendarAndNotNull(repeatingOrDelayedWakeDateTime, eventConditionWakeTime);
            if (eventWakeDateTime == eventConditionWakeTime) {
                eventWakeReason = "Condition with ID " + conditionId;
            } else {
                eventWakeReason = repeatOrDelayReason;
            }
            nextWakeDateTime = DateHelpers.MinCalendarAndNotNull(nextWakeDateTime, eventWakeDateTime);
            if (eventWakeDateTime != null && nextWakeDateTime == eventWakeDateTime) {
                String str;
                StringBuilder append = new StringBuilder().append("Next RTC time caused by '").append(e.Name).append("': ").append(eventWakeReason).append(" at");
                if (nextWakeDateTime == null) {
                    str = "null";
                } else {
                    str = DateHelpers.FormatDateWithYear(nextWakeDateTime);
                }
                nextWakeLog = append.append(str).toString();
            }
        }
        Logging.Report("QueueRTC", nextWakeLog, (Context) this);
        if (nextWakeDateTime == null) {
            nextWakeDateTime = Calendar.getInstance();
            nextWakeDateTime.add(11, 24);
            nextWakeDateTime.set(12, 0);
            nextWakeDateTime.set(13, 0);
            nextWakeDateTime.set(14, 0);
        }
        if (this._CalendarReader != null) {
            Calendar nextCalendarEvent = this._CalendarReader.GetNextEventStartOrFinish(roundedToNextMinuteDateTime, roundedToNextMinuteDateTime, nextWakeDateTime);
            if (nextCalendarEvent != null && nextCalendarEvent.compareTo(nextWakeDateTime) < 0) {
                nextWakeDateTime = nextCalendarEvent;
                Logging.Report("QueueRTC", "Next wake changed to calendar event at " + DateHelpers.FormatDateWithYear(nextCalendarEvent), (Context) this);
            }
        }
        if (this._Events.GetEventsForTriggerId(35, null).size() > 0) {
            NextAlarmCondition.UpdateAlarmTimeIfNeeded(this, roundedToNextMinuteDateTime.getTimeInMillis());
            long alarmMillis = ((Long) LlamaSettings.LastAlarmTimeMillis.GetValue(this)).longValue();
            if (alarmMillis > 0) {
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(alarmMillis);
                if (c.compareTo(nextWakeDateTime) < 0) {
                    nextWakeDateTime = c;
                    Logging.Report("Setting next wake to an alarm at " + DateHelpers.FormatDate(c), (Context) this);
                }
            }
        }
        int hmTime = HourMinute.CalendarToInt(nextWakeDateTime);
        long ticks = nextWakeDateTime.getTimeInMillis();
        Logging.Report("Next wake: " + SimpleDateFormat.getDateTimeInstance().format(nextWakeDateTime.getTime()), getApplicationContext());
        Intent intent = new Intent(getApplicationContext(), RtcReceiver.class);
        if (hmTime != -1) {
            intent.putExtra(Constants.INTENT_RTC_CALLBACK, hmTime);
        }
        intent.putExtra("ticks", ticks);
        this._RtcPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), Constants.RTC_WAKE, intent, 134217728);
        ((AlarmManager) getSystemService("alarm")).set(0, nextWakeDateTime.getTimeInMillis(), this._RtcPendingIntent);
    }

    public HashMap<Beacon, ArrayList<String>> GetCellToAreaMap() {
        return this._CellToAreaMap;
    }

    public Collection<String> GetAreasNamesForBeacon(Beacon beacon) {
        ArrayList<String> areaNames = (ArrayList) this._CellToAreaMap.get(beacon);
        if (areaNames == null) {
            return IterableHelpers.Empty();
        }
        return areaNames;
    }

    public void OnRtcEvent(int hmTime, Calendar currentTime) {
        Logging.Report("OnRTCRvent " + hmTime + " " + currentTime.getTimeInMillis(), getApplicationContext());
        boolean isExactHmTime = currentTime.get(13) == 0 && currentTime.get(14) == 0;
        testEvents(StateChange.CreateStartEndPair(this, hmTime, currentTime, isExactHmTime), currentTime);
    }

    public void OnBluetoothDevice(String address, boolean isConnected) {
        testEvents(StateChange.CreateBluetoothChange(this, address, isConnected));
    }

    public void OnCarMode(boolean newCarMode) {
        testEvents(StateChange.CreateCarMode(this, newCarMode));
    }

    public void OnDeskMode(boolean newDeskMode) {
        testEvents(StateChange.CreateDeskMode(this, newDeskMode));
    }

    public Integer GetDockMode() {
        return UiModeManagerCompat.GetDockMode(this);
    }

    public void ExportSettings(Activity activity) {
        Logging.Report("ImportExport", "Exporting", (Context) this);
        if (this._Storage.SaveSharedPrefsToSd(getApplicationContext(), "")) {
            Helpers.ShowSimpleDialogMessage(activity, activity.getString(R.string.hrExportSuccess));
        } else {
            Helpers.ShowSimpleDialogMessage(activity, activity.getString(R.string.hrExportFailedSadFace));
        }
    }

    public void ImportSettings() {
        int importSuccesses = this._Storage.LoadSharedPrefsFromSd(getApplicationContext(), "");
        if (importSuccesses == 0) {
            HandleFriendlyError(getString(R.string.hrImportFailedSadFace), true);
            return;
        }
        ArrayList<String> items = new ArrayList();
        if ((importSuccesses & 1) != 0) {
            items.add(getString(R.string.hrStorageAreas));
        }
        if ((importSuccesses & 2) != 0) {
            items.add(getString(R.string.hrStorageEvents));
        }
        if ((importSuccesses & 4) != 0) {
            items.add(getString(R.string.hrStorageProfiles));
        }
        if ((importSuccesses & 8) != 0) {
            items.add(getString(R.string.hrStorageIgnoredCell));
        }
        String itemsString = Helpers.ConcatenateListOfStrings(items, ", ", " " + getString(R.string.hrAnd) + " ");
        HandleFriendlyInfo(String.format(getString(R.string.hrImportOf1Successful), new Object[]{itemsString}), true);
        Quit(false);
    }

    public void AddEvent(Event ev) {
        this._Events.Add(ev);
        this._Storage.SaveEvents(getApplicationContext(), this._Events);
        OnEventsChanged(null);
        if (Instances.EventsActivity != null) {
            Instances.EventsActivity.Update();
        }
    }

    public void AddProfile(Profile ev) {
        this._Profiles.add(ev);
        this._Storage.SaveProfiles(getApplicationContext(), this._Profiles);
        if (Instances.ProfilesActivity != null) {
            Instances.ProfilesActivity.Update();
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void OnEventsChanged(Calendar optionalEventTime) {
        ReinitEventTriggers();
        QueueRtcWake(optionalEventTime);
    }

    public void CreateReminderForAreaByName(String area, String value) {
        Event ev = new Event(String.format(getString(R.string.hrQuickReminderAt1), new Object[]{DateHelpers.FormatDate(Calendar.getInstance())}));
        ev.Type = 2;
        ev._Actions.add(new NotificationAction(value));
        ev._Conditions.add(new EnterAreaCondition(new String[]{area}));
        AddEvent(ev);
    }

    public void ToggleApn(boolean turnOn) {
        try {
            if (new Apn().SetApn(getApplicationContext(), getSharedPreferences("APN", 0), turnOn, true, Apn.SUFFIX_APN_DROID) == 0) {
                HandleFriendlyError(getString(R.string.hrNoApnsDefinedTip), false);
            }
        } catch (Exception ex) {
            Logging.Report(ex, (Context) this);
            if (VERSION.SDK_INT >= 14) {
                HandleFriendlyError(Integer.valueOf(4));
            }
        }
    }

    public boolean GetApnStatus() {
        return new Apn().IsApnEnabled(this);
    }

    public String GetLastProfileName() {
        return (String) LlamaSettings.LastProfileName.GetValue(this);
    }

    public Calendar GetLastProfileDateTime() {
        return this.LastProfileNameDateTime;
    }

    public void DeleteEventByName(String selectedEventName) {
        DeleteEventByNameNoSave(selectedEventName);
        this._Storage.SaveEvents(getApplicationContext(), this._Events);
        OnEventsChanged(null);
        if (Instances.EventsActivity != null) {
            Instances.EventsActivity.Update();
        }
    }

    private void DeleteEventByNameNoSave(String selectedEventName) {
        this._Events.DeleteByName(selectedEventName, true);
    }

    public Iterable<Event> GetEvents() {
        ThreadComplainMustBeWorker();
        return this._Events;
    }

    public void RenameEventByName(String oldName, String newName) {
        ThreadComplainMustBeWorker();
        if (this._Events.RenameEvent(oldName, newName)) {
            this._Storage.SaveEvents(getApplicationContext(), this._Events);
            if (Instances.EventsActivity != null) {
                Instances.EventsActivity.Update();
            }
        }
    }

    public void DeleteProfileByName(String selectedProfileName) {
        ThreadComplainMustBeWorker();
        DeleteProfileByNameNoSave(selectedProfileName);
        this._Storage.SaveProfiles(getApplicationContext(), this._Profiles);
        if (Instances.ProfilesActivity != null) {
            Instances.ProfilesActivity.Update();
        }
    }

    private void DeleteProfileByNameNoSave(String selectedProfileName) {
        ThreadComplainMustBeWorker();
        for (int i = 0; i < this._Profiles.size(); i++) {
            if (((Profile) this._Profiles.get(i)).Name.equals(selectedProfileName)) {
                this._Profiles.remove(i);
                return;
            }
        }
    }

    public void UpdateProfile(String oldName, Profile editedProfile) {
        ThreadComplainMustBeWorker();
        DeleteProfileByNameNoSave(oldName);
        this._Profiles.add(editedProfile);
        this._Storage.SaveProfiles(getApplicationContext(), this._Profiles);
        if (UpdateProfileNameReferences(oldName, editedProfile.Name)) {
            this._Storage.SaveEvents(getApplicationContext(), this._Events);
        }
        String lastProfileName = (String) LlamaSettings.LastProfileName.GetValue(this);
        if (lastProfileName != null && oldName.equals(lastProfileName)) {
            LlamaSettings.LastProfileName.SetValueAndCommit(this, editedProfile.Name, new CachedSetting[0]);
            if (!((Boolean) LlamaSettings.ProfileLocked.GetValue(this)).booleanValue()) {
                SetProfile(editedProfile.Name, false, null, false);
            }
        }
        if (Instances.ProfilesActivity != null) {
            Instances.ProfilesActivity.Update();
        }
    }

    public void UpdateEvent(String oldName, Event editedEvent) {
        ThreadComplainMustBeWorker();
        DeleteEventByNameNoSave(oldName);
        this._Events.Add(editedEvent);
        if (!editedEvent.HasDelay()) {
            editedEvent.DelayedUntilMillis = 0;
        }
        if (editedEvent.RepeatMinutesInterval == 0) {
            editedEvent.NextRepeatAtMillis = 0;
        }
        this._Storage.SaveEvents(getApplicationContext(), this._Events);
        Iterator i$ = this._EventHistory.iterator();
        while (i$.hasNext()) {
            EventHistory eh = (EventHistory) i$.next();
            if (eh.EventName.equals(oldName)) {
                eh.EventName = editedEvent.Name;
            }
        }
        OnEventsChanged(null);
        IdeallyRunOnUiThread(new X() {
            /* Access modifiers changed, original: 0000 */
            public void R() {
                if (Instances.EventsActivity != null) {
                    Instances.EventsActivity.Update();
                }
                if (Instances.EventHistoryActivity != null) {
                    Instances.EventHistoryActivity.Update();
                }
            }
        });
    }

    public String[] GetAreaNames() {
        ThreadComplainMustBeWorker();
        String[] result = new String[this._Areas.size()];
        for (int i = 0; i < this._Areas.size(); i++) {
            result[i] = ((Area) this._Areas.get(i)).Name;
        }
        return result;
    }

    public String[] GetProfileNames() {
        ThreadComplainMustBeWorker();
        String[] result = new String[this._Profiles.size()];
        for (int i = 0; i < this._Profiles.size(); i++) {
            result[i] = ((Profile) this._Profiles.get(i)).Name;
        }
        return result;
    }

    public void EnableKeyGuard(boolean enableKeyGuard, boolean requirePasswordOnce, boolean onEnableForceScreenOff) {
        KeyguardManager km = (KeyguardManager) getSystemService("keyguard");
        if (this._KeyguardLock == null) {
            Logging.Report("Keyguard lock token was null. Grrr.", getApplicationContext());
            this._KeyguardLock = km.newKeyguardLock(Constants.TAG);
        }
        if (enableKeyGuard) {
            this._KeyguardLock.reenableKeyguard();
            km.exitKeyguardSecurely(this);
            if (onEnableForceScreenOff) {
                TurnOffScreen();
                Logging.Report("Reenabled KeyGuard and turned off screen", getApplicationContext());
            } else {
                Logging.Report("Reenabled KeyGuard ", getApplicationContext());
            }
            this._KeyguardDisableWaitingForUser.SetValueAndCommit(this, Boolean.valueOf(false), new CachedSetting[0]);
        } else if (!requirePasswordOnce) {
            this._KeyguardLock.disableKeyguard();
            Logging.Report("Disabled KeyGuard immediately", getApplicationContext());
        } else if (km.inKeyguardRestrictedInputMode()) {
            this._KeyguardDisableWaitingForUser.SetValueAndCommit(this, Boolean.valueOf(true), new CachedSetting[0]);
            Logging.Report("Tried to disable KeyGuard, but we aren't unlocked. Waiting for an unlock", getApplicationContext());
        } else {
            this._KeyguardLock.disableKeyguard();
            Logging.Report("Disabled KeyGuard immediately because we were already unlocked", getApplicationContext());
        }
    }

    public void onKeyguardExitResult(boolean success) {
        Logging.Report("onKeyguardExitResult " + success, (Context) this);
    }

    public void SetEventEnabled(String eventName, boolean newEnabled) {
        Event event = GetEventByName(eventName);
        if (event == null) {
            HandleFriendlyError("There is no event named '" + eventName + "'", true);
            return;
        }
        event.Enabled = newEnabled;
        this._Storage.SaveEvents(getApplicationContext(), this._Events);
        OnEventsChanged(null);
        if (Instances.EventsActivity != null) {
            Instances.EventsActivity.Update();
        }
        if (Instances.EventHistoryActivity != null) {
            Instances.EventHistoryActivity.Update();
        }
    }

    public void ToggleMobileData(boolean turnOn) {
        MobileData.SetMobileDataEnabled(this, turnOn);
    }

    public void HandlePhoneShutdown() {
        testEvents(StateChange.CreatePhoneReboot(this, false));
        if (((Boolean) LlamaSettings.DebugCellsInRecent.GetValue(getApplicationContext())).booleanValue()) {
            ThreadComplainMustBeWorker();
            this._RecentCells.addFirst(new BeaconAndCalendar(Cell.PhoneShutdown, Calendar.getInstance()));
        }
        Instances.Service.Quit(false);
    }

    public void ToggleWifi(boolean turnOn, boolean turnOffEvenIfConnected) {
        if (turnOn) {
            TrySetWifi(true);
            this._NearbyWifiPoller.UpdateExpectedEnabledState(true);
        } else if (turnOffEvenIfConnected) {
            TrySetWifi(false);
            this._NearbyWifiPoller.UpdateExpectedEnabledState(false);
        } else {
            if (this._Wifi == null) {
                this._Wifi = (WifiManager) getSystemService("wifi");
            }
            WifiInfo info = this._Wifi.getConnectionInfo();
            String bsddid = info == null ? null : info.getBSSID();
            Logging.Report("WifiToggle", "ConnectionInfo " + (info == null ? "null-connection-info" : "BSSID=''"), (Context) this);
            if (info == null || bsddid == null || "00:00:00:00:00:00".equals(bsddid)) {
                TrySetWifi(false);
                this._NearbyWifiPoller.UpdateExpectedEnabledState(false);
                return;
            }
            HandleFriendlyInfo(getString(R.string.hrWifiNotTurnedOffAsItsConnected), false);
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void TrySetWifi(boolean turnOn) {
        if (this._Wifi == null) {
            this._Wifi = (WifiManager) getSystemService("wifi");
        }
        try {
            this._Wifi.setWifiEnabled(turnOn);
        } catch (SecurityException ex) {
            if (ex.getMessage() != null && ex.getMessage().indexOf("UPDATE_DEVICE_STATS") > 0) {
                HandleFriendlyError(Integer.valueOf(7));
            } else if ((ex.getMessage() == null || ex.getMessage().indexOf("INTERACT_ACROSS_USERS_FULL") <= 0) && (ex.getMessage() == null || ex.getMessage().indexOf("INTERACT_ACROSS_USERS") <= 0)) {
                throw new RuntimeException(ex);
            } else {
                HandleFriendlyError(Integer.valueOf(8));
            }
        }
    }

    public void ToggleBluetooth(boolean turnOn, boolean turnOffEvenIfConnected) {
        int result;
        if (turnOn || turnOffEvenIfConnected) {
            result = BluetoothHelper.ToggleBluetooth(this, turnOn);
        } else {
            try {
                if (this._AudioManager == null) {
                    this._AudioManager = (AudioManager) getSystemService("audio");
                }
                if (this._AudioManager.isBluetoothA2dpOn() || this._AudioManager.isBluetoothScoOn()) {
                    Logging.Report("Bluetooth A2DP or SCO is connected", (Context) this);
                    HandleFriendlyInfo(getString(R.string.hrBluetoothNotTurnedOffAsConnectedToAudioDevice), false);
                    result = 1;
                } else if (this._BluetoothDevices.IsAnyDeviceConnected()) {
                    Logging.Report("Bluetooth devices are connected", (Context) this);
                    HandleFriendlyInfo(getString(R.string.hrBluetoothNotTurnedOffAsItsConnected), false);
                    result = 1;
                } else {
                    Logging.Report("Bluetooth is not connected", (Context) this);
                    result = BluetoothHelper.ToggleBluetooth(this, false);
                }
            } catch (VerifyError e) {
                HandleFriendlyError(getString(R.string.hrBluetoothApiNotSupported), false);
                return;
            }
        }
        if (result == 2) {
            HandleFriendlyError(getString(R.string.hrCouldnNotTurnOnBluetoothMaybeAirplane), false);
        } else if (result == 0) {
            HandleFriendlyError(getString(R.string.hrBluetoothIsntSupported), false);
        }
    }

    public void RunApplication(SimplePackageInfo packageInfo) {
        try {
            Logging.Report("Attempting to run " + packageInfo.getFriendlyName() + " with " + packageInfo.getPackageName(), getApplicationContext());
            Intent i = getPackageManager().getLaunchIntentForPackage(packageInfo.getPackageName());
            i.setFlags(268435456);
            startActivity(i);
        } catch (Exception ex) {
            Logging.Report(ex, getApplicationContext());
            Log.e(Constants.TAG, "Failed to run app", ex);
            HandleFriendlyError(String.format(getString(R.string.hrCouldnotStart1), new Object[]{packageInfo.getFriendlyName()}), false);
        }
    }

    public void KillApplication(SimplePackageInfo applicationInfo, boolean useRoot) {
        if (useRoot) {
            String packageName = applicationInfo.PackageName;
            char[] arr$ = packageName.toCharArray();
            int len$ = arr$.length;
            int i$ = 0;
            while (i$ < len$) {
                char c = arr$[i$];
                if (c == '.' || Character.isJavaIdentifierPart(c)) {
                    i$++;
                } else {
                    Logging.Report("Package name " + packageName + " contains invalid chars", (Context) this);
                    HandleFriendlyError(String.format(getString(R.string.hrInvalidPackageNameToKill1), new Object[]{packageName}), false);
                    return;
                }
            }
            Logging.Report("Killing " + packageName + " with root privileges", (Context) this);
            RunWithRoot("pkill -9 " + packageName);
            return;
        }
        ActivityManager am = (ActivityManager) getSystemService("activity");
        Logging.Report("Killing " + applicationInfo.PackageName, (Context) this);
        am.restartPackage(applicationInfo.PackageName);
    }

    public void KillApplications(Iterable<SimplePackageInfo> applicationInfos, boolean useRoot) {
        for (SimplePackageInfo info : applicationInfos) {
            KillApplication(info, useRoot);
        }
    }

    public List<SimplePackageInfo> GetInstalledApps() {
        final PackageManager manager = getPackageManager();
        return IterableHelpers.Select(manager.getInstalledApplications(128), new Selector<ApplicationInfo, SimplePackageInfo>() {
            public SimplePackageInfo Do(ApplicationInfo value) {
                return new SimplePackageInfo(value.loadLabel(manager).toString(), value.packageName);
            }
        });
    }

    public Boolean GetChargingState() {
        if (this.IsCharging == null) {
            Intent intent = registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
            if (intent != null) {
                int plugged = intent.getIntExtra("plugged", -1);
                if (plugged == 1) {
                    this.IsCharging = Boolean.valueOf(true);
                    this.ChargingFrom = 2;
                } else if (plugged == 2) {
                    this.IsCharging = Boolean.valueOf(true);
                    this.ChargingFrom = 3;
                } else if (plugged == 4) {
                    this.IsCharging = Boolean.valueOf(true);
                    this.ChargingFrom = 4;
                } else {
                    this.IsCharging = Boolean.valueOf(false);
                    this.ChargingFrom = 0;
                }
            }
        }
        return this.IsCharging;
    }

    public int GetChargingFromState() {
        if (this.IsCharging == null) {
            GetChargingState();
        }
        return this.ChargingFrom;
    }

    public void ShowHelp(Activity activity) {
        ShowHtmlFile(activity, "help.htm", "help");
    }

    /* Access modifiers changed, original: 0000 */
    public void ShowHtmlFile(Activity activity, String fileName, String pageName) {
        String text;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(activity.getAssets().open(fileName)));
            StringBuilder buffer = new StringBuilder();
            while (true) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }
                buffer.append(line).append(10);
            }
            text = buffer.toString();
        } catch (Exception ex) {
            Logging.Report(ex, (Context) activity);
            text = "Error reading " + pageName + " page :(";
        }
        WebView wv = new WebView(activity);
        wv.loadData(URLEncoder.encode(text).replace("+", "%20"), "text/html", "utf-8");
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(1);
        dialog.addContentView(wv, new LayoutParams(-1, -2));
        dialog.show();
    }

    private void ShowDonationActivity(Activity activity) {
        ShowHtmlFile(activity, "donate.htm", "donation");
    }

    public void ShowDonation(Activity contextWithView) {
        ShowDonationActivity(contextWithView);
    }

    public static void ShowLlamaMapInMarket(Activity activity) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse("market://details?id=com.kebab.LlamaMap"));
        intent.setFlags(268435456);
        intent.setFlags(65536);
        intent.setFlags(8388608);
        intent.setFlags(1073741824);
        intent.setFlags(134217728);
        activity.startActivity(intent);
    }

    public void ToggleAirplane(boolean turnOn) {
        if (GetIsAirplaneModeEnabled() != turnOn || AirplaneCompat.IsSecureSystemSetting()) {
            AirplaneCompat.SetAirplaneMode(this, turnOn);
            Logging.Report("AirplaneMode", "Updated airplane mode to " + turnOn, (Context) this);
            return;
        }
        Logging.Report("AirplaneMode", "Airplane mode was already " + turnOn, (Context) this);
    }

    public void OnMusicPlaybackStateNearlyChanged() {
        this._Handler.postDelayed(new Runnable() {
            public void run() {
                LlamaService.this.testEvents(StateChange.CreateMusicPlaybackChanged(LlamaService.this));
            }
        }, 1000);
    }

    public boolean GetIsMusicPlaying() {
        return this._AudioManager.isMusicActive();
    }

    public void IncrementEventRuns(int amount) {
        LlamaSettings.EventRuns.SetValueNoCommit(Integer.valueOf(((Integer) LlamaSettings.EventRuns.GetValue(this)).intValue() + amount));
        this._EventActionCountChanged = true;
    }

    public void ChangeBrightness(boolean auto, int brightnessPercent, Activity activity, boolean allowExtraActivity) {
        ContentResolver resolver = getContentResolver();
        WindowManager.LayoutParams attrs;
        if (auto) {
            System.putInt(resolver, Constants.SCREEN_BRIGHTNESS_MODE_KEY, 1);
            if (activity != null) {
                attrs = activity.getWindow().getAttributes();
                if (attrs.screenBrightness < 0.5f) {
                    attrs.screenBrightness = 0.5f;
                    activity.getWindow().setAttributes(attrs);
                    return;
                }
                return;
            } else if (allowExtraActivity) {
                BrightnessChangerActivity.StartActivity(this, auto, brightnessPercent);
                return;
            } else {
                return;
            }
        }
        int brightnessOutOf255 = CalculateBrightness(brightnessPercent);
        System.putInt(resolver, Constants.SCREEN_BRIGHTNESS_MODE_KEY, 0);
        System.putInt(resolver, "screen_brightness", brightnessOutOf255);
        if (activity != null) {
            Window current = activity.getWindow();
            while (true) {
                Window other = current.getContainer();
                if (other != null) {
                    current = other;
                } else {
                    attrs = current.getAttributes();
                    attrs.screenBrightness = ((float) brightnessPercent) / 100.0f;
                    activity.getWindow().setAttributes(attrs);
                    return;
                }
            }
        } else if (allowExtraActivity) {
            BrightnessChangerActivity.StartActivity(this, auto, brightnessPercent);
        }
    }

    public static int CalculateBrightness(int brightnessPercent) {
        return (brightnessPercent * 255) / 100;
    }

    public void EnableSync(boolean enable, boolean runSyncImmediately) {
        Logging.Report("Setting Master Sync - " + enable, getApplicationContext());
        ContentResolver.setMasterSyncAutomatically(enable);
        if (enable && runSyncImmediately) {
            Logging.Report("Running syncs", getApplicationContext());
            AccountManager accmgr = AccountManager.get(getApplicationContext());
            for (SyncAdapterType type : ContentResolver.getSyncAdapterTypes()) {
                Logging.Report("Running syncs for" + type.accountType, getApplicationContext());
                for (Account account : accmgr.getAccountsByType(type.accountType)) {
                    if (ContentResolver.getSyncAutomatically(account, type.authority)) {
                        ContentResolver.requestSync(account, type.authority, new Bundle());
                    }
                }
            }
        }
    }

    public void ChangeMusicVolume(int volume) {
        if (this._AudioManager == null) {
            this._AudioManager = (AudioManager) getSystemService("audio");
        }
        this._AudioManager.setStreamVolume(3, volume, 0);
    }

    public void AcquireRoot() {
        try {
            Runtime.getRuntime().exec("su");
        } catch (IOException e) {
            Logging.Report(e, (Context) this);
        }
    }

    public void Reboot() {
        if (DateHelpers.GetTimeSpanInMinutes(SystemClock.uptimeMillis()) < 5) {
            HandleFriendlyError(String.format(getString(R.string.hrRebootFailWarning), new Object[]{Integer.valueOf(minutes)}), false);
            return;
        }
        String command;
        HandleFriendlyInfo(getString(R.string.hrRebooting), true);
        switch (((Integer) LlamaSettings.RootRebootCommand.GetValue(this)).intValue()) {
            case 1:
                command = "reboot -p";
                break;
            default:
                command = "reboot";
                break;
        }
        Logging.Report("Rebooting with " + command, (Context) this);
        RunWithRoot(command);
    }

    public void RunWithRoot(String command) {
        RunWithRoot(command, this);
    }

    public static void RunWithRoot(String command, Context context) {
        RunWithRoot(command, context, false);
    }

    public static void RunWithRoot(String command, Context context, boolean waitForIt) {
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command);
            os.writeBytes("\n");
            os.writeBytes("exit");
            os.writeBytes("\n");
            os.flush();
            if (waitForIt) {
                Worker worker = new Worker(process, null);
                worker.start();
                try {
                    Logging.Report("RunWithRoot", "Waiting for SU to finish", context);
                    worker.join(6000);
                    if (worker.finished) {
                        Logging.Report("RunWithRoot", "SU finished", context);
                        return;
                    }
                    Logging.Report("RunWithRoot", "SU failed to get root access", context);
                    Instances.GetServiceOrRestart(context).HandleFriendlyError(context.getString(R.string.hrLlamaCouldNotGetRootAccess), false);
                } catch (InterruptedException ex) {
                    Logging.Report(ex, context);
                }
            }
        } catch (IOException e) {
            Logging.Report(e, context);
        }
    }

    public List<String> GetConnectedBluetoothDevices() {
        return this._BluetoothDevices.GetConnectedDevice();
    }

    public void EnableProfileLock(int minutes, String profileAfterLockExpires, boolean clearCurrentProfile) {
        String profileUnlockAt;
        Logging.Report("EnableProfileLock for " + minutes, (Context) this);
        RemoveProfileLockEvent(false);
        if (minutes != Integer.MAX_VALUE) {
            Calendar date = Calendar.getInstance();
            Event event = new Event("(Auto) profiles were locked at " + new HourMinute(date).toString());
            event.Type = 1;
            date.add(12, minutes);
            int start = HourMinute.CalendarToInt(date);
            profileUnlockAt = new HourMinute(date).toString();
            date.add(12, 1);
            TimeBetweenCondition afterTime = new TimeBetweenCondition(start, HourMinute.CalendarToInt(date));
            LockProfileChangesAction unlockProfilesAction = new LockProfileChangesAction(false);
            event._Conditions.add(afterTime);
            event._Actions.add(unlockProfilesAction);
            if (((String) LlamaSettings.VibrateWhenProfilesUnlock.GetValue(this)).length() > 0) {
                event._Actions.add(new VibrateAction((String) LlamaSettings.VibrateWhenProfilesUnlock.GetValue(this)));
            }
            AddEvent(event);
            Logging.Report("Created profile unlock event", (Context) this);
        } else {
            profileUnlockAt = Constants.PROFILE_NEVER_UNLOCK;
        }
        CachedBooleanSetting cachedBooleanSetting = LlamaSettings.ProfileLocked;
        Boolean valueOf = Boolean.valueOf(true);
        CachedSetting[] cachedSettingArr = new CachedSetting[3];
        cachedSettingArr[0] = LlamaSettings.ProfileLockedUntilTimeString.SetValueForCommit(profileUnlockAt);
        cachedSettingArr[1] = LlamaSettings.ProfileAfterLockName.SetValueForCommit(profileAfterLockExpires);
        cachedSettingArr[2] = clearCurrentProfile ? LlamaSettings.LastProfileName.SetValueForCommit(null) : null;
        cachedBooleanSetting.SetValueAndCommit(this, valueOf, cachedSettingArr);
        SetNotificationForProfileLock(profileUnlockAt);
        Logging.Report("Locked profiles until " + profileUnlockAt, (Context) this);
        if (Instances.ProfilesActivity != null) {
            Instances.ProfilesActivity.Update();
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void RemoveProfileLockEvent(boolean saveEvents) {
        boolean foundUnlockEvent = false;
        if (this._Events.DeleteByNamePrefix("(Auto) profiles were locked at ")) {
            Logging.Report("Removed profile unlock event", (Context) this);
            foundUnlockEvent = true;
        }
        if (saveEvents && foundUnlockEvent) {
            this._Storage.SaveEvents(getApplicationContext(), this._Events);
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void SetNotificationForProfileLock(String unlockTimeString) {
        if (this._OngoingNotification != null) {
            if (unlockTimeString == null || Constants.PROFILE_NEVER_UNLOCK.equals(unlockTimeString)) {
                this._OngoingNotification.SetCurrentProfileName(getString(R.string.hrProfileLocked));
            } else {
                this._OngoingNotification.SetCurrentProfileName(String.format(getString(R.string.hrLockedUntil1), new Object[]{unlockTimeString}));
            }
            this._OngoingNotification.SetCurrentIcon(null, null, true);
        }
    }

    public void DisableProfileLock(boolean setProfileToLast, boolean killDisableProfileLockEvent) {
        Logging.Report("DisableProfileLock", (Context) this);
        if (((Boolean) LlamaSettings.ProfileLocked.GetValue(this)).booleanValue()) {
            if (killDisableProfileLockEvent) {
                RemoveProfileLockEvent(true);
            }
            LlamaSettings.ProfileLocked.SetValueAndCommit(this, Boolean.valueOf(false), LlamaSettings.ProfileLockedUntilTimeString.SetValueForCommit(null));
            if (setProfileToLast) {
                String profileName = (String) LlamaSettings.ProfileAfterLockName.GetValue(this);
                if (profileName != null) {
                    SetProfile(profileName, false, null, false);
                }
                LlamaSettings.ProfileAfterLockName.SetValueAndCommit(this, null, new CachedSetting[0]);
                return;
            }
            this._OngoingNotification.SetCurrentProfileName(getString(R.string.hrProfileChangesUnlocked));
            this._OngoingNotification.SetCurrentIcon(null, null, false);
            return;
        }
        Logging.Report("Profiles were not locked", (Context) this);
    }

    public void UiCreatedOrUnpaused() {
        CheckIfVolumeChanged();
    }

    public void SetNotificationIcon(Integer notificationIcon, Integer notificationDots) {
        if (!(((Boolean) LlamaSettings.ProfileLocked.GetValue(this)).booleanValue() || this._OngoingNotification == null)) {
            this._OngoingNotification.SetCurrentIcon(notificationIcon, notificationDots, false);
        }
        if (notificationIcon == null || notificationIcon.intValue() == -1) {
            if (notificationDots != null && notificationDots.intValue() != -1) {
                LlamaSettings.LastNotificationIconDots.SetValueAndCommit(this, notificationDots, new CachedSetting[0]);
            }
        } else if (notificationDots == null || notificationDots.intValue() == -1) {
            LlamaSettings.LastNotificationIcon.SetValueAndCommit(this, notificationIcon, new CachedSetting[0]);
        } else {
            LlamaSettings.LastNotificationIcon.SetValueAndCommit(this, notificationIcon, LlamaSettings.LastNotificationIconDots.SetValueForCommit(notificationDots));
        }
    }

    public void ToggleCellPolling(boolean turnOn) {
        LlamaSettings.CellPollingMode.SetValueAndCommit(this, Integer.valueOf(turnOn ? 1 : 2), new CachedSetting[0]);
        initCellPoller();
    }

    public void HandleWifiScanResults(List<ScanResult> results) {
        if (((Boolean) LlamaSettings.NearbyWifiEnabled.GetValue(this)).booleanValue()) {
            if (this._Wifi == null) {
                this._Wifi = (WifiManager) getSystemService("wifi");
            }
            this._LastWifi.clear();
            if (results != null) {
                for (ScanResult wifi : results) {
                    this._LastWifi.add(new WifiNamedNetwork(wifi.SSID));
                    this._LastWifi.add(new WifiMacAddress(wifi.BSSID, wifi.SSID));
                }
                BeaconsChanged(this._LastWifi, Beacon.WIFI_NAME);
            }
        }
    }

    public void HandleBluetoothDiscoveryResults(Iterable<BluetoothDevice> devices) {
        if (((Boolean) LlamaSettings.NearbyBtEnabled.GetValue(this)).booleanValue()) {
            this._LastBluetooth.clear();
            for (BluetoothDevice bt : devices) {
                this._LastBluetooth.add(new BluetoothBeacon(bt.getName(), bt.getAddress()));
            }
            for (Tuple<String, String> bt2 : this._BluetoothDevices.GetConnectedBluetoothDevices()) {
                this._LastBluetooth.add(new BluetoothBeacon((String) bt2.Item1, (String) bt2.Item2));
            }
            BeaconsChanged(this._LastBluetooth, Beacon.BLUETOOTH);
        }
    }

    public Location GetLastLocation(Integer minimumRadius) {
        if (this._LocationManager == null) {
            this._LocationManager = (LocationManager) getSystemService("location");
        }
        Criteria c = new Criteria();
        c.setAccuracy(1);
        String provider = this._LocationManager.getBestProvider(c, true);
        if (provider == null) {
            return null;
        }
        Location location = this._LocationManager.getLastKnownLocation(provider);
        if (location == null) {
            return null;
        }
        if (minimumRadius == null || location.getAccuracy() >= ((float) minimumRadius.intValue())) {
            return location;
        }
        location.setAccuracy((float) minimumRadius.intValue());
        return location;
    }

    public void UpdateAreaMapPoints(String areaName, ArrayList<Location> locations) {
        Area area = GetAreaByName(areaName);
        if (area == null) {
            HandleFriendlyError(getString(R.string.hrUnableToUpdateArea1, new Object[]{areaName}), true);
            return;
        }
        for (int i = area._Cells.size() - 1; i >= 0; i--) {
            if (((Beacon) area._Cells.get(i)).GetTypeId().equals(Beacon.EARTH_POINT)) {
                area._Cells.remove(i);
            }
        }
        Iterator i$ = locations.iterator();
        while (i$.hasNext()) {
            area._Cells.add(new EarthPoint((Location) i$.next()));
        }
        this._Storage.SaveAreas(this, this._Areas);
        evaluateAreasForBeaconTypeAndOtherAreas(Beacon.EARTH_POINT);
        if (Instances.AreasActivity != null) {
            Instances.AreasActivity.Update();
        }
    }

    public void ShowMapIntentForArea(Activity activity, ArrayList<Location> locationOverride, Area area) {
        Activity activity2 = activity;
        ArrayList<Location> arrayList = locationOverride;
        ShowMapIntent(activity2, String.format(activity.getString(R.string.hrPositionsFor1), new Object[]{area.Name}), arrayList, GetOtherLocationsForIntent(area), false, activity.getString(R.string.hrSave), activity.getString(R.string.hrCancel));
    }

    public void ShowMapIntentForArea(Activity activity, Area area) {
        Activity activity2 = activity;
        ShowMapIntent(activity2, String.format(activity.getString(R.string.hrPositionsFor1), new Object[]{area.Name}), area.GetMapPointsAsLocations(), GetOtherLocationsForIntent(area), false, activity.getString(R.string.hrSave), activity.getString(R.string.hrCancel));
    }

    public void ShowMapIntent(Activity activity, String title, EarthPoint beacon) {
        ArrayList<Location> locations = new ArrayList();
        locations.add(beacon.ToLocation());
        ShowMapIntent(activity, title, locations, GetOtherLocationsForIntent(null), true, activity.getString(R.string.hrClose), "");
    }

    public void ShowMapIntent(Activity activity, String title, Iterable<EarthPoint> beacons) {
        ArrayList<Location> locations = new ArrayList();
        for (EarthPoint e : beacons) {
            locations.add(e.ToLocation());
        }
        ShowMapIntent(activity, title, locations, GetOtherLocationsForIntent(null), true, activity.getString(R.string.hrClose), "");
    }

    /* Access modifiers changed, original: 0000 */
    public Tuple<ArrayList<Location>, ArrayList<String>> GetOtherLocationsForIntent(Area excludeArea) {
        ArrayList<Location> locations = new ArrayList();
        ArrayList<String> names = new ArrayList();
        Iterator it = this._Areas.iterator();
        while (it.hasNext()) {
            Area a = (Area) it.next();
            if (!a.equals(excludeArea)) {
                Iterator i$ = a._Cells.iterator();
                while (i$.hasNext()) {
                    Beacon b = (Beacon) i$.next();
                    if (b.IsMapBased()) {
                        locations.add(((EarthPoint) b).ToLocation());
                        names.add(a.Name);
                    }
                }
            }
        }
        return new Tuple(locations, names);
    }

    private void ShowMapIntent(Activity activity, String title, ArrayList<Location> locations, Tuple<ArrayList<Location>, ArrayList<String>> otherAreas, boolean readOnly, String button1Text, String button2Text) {
        ShowMapIntent(activity, title, locations, otherAreas == null ? null : (ArrayList) otherAreas.Item1, otherAreas == null ? null : (ArrayList) otherAreas.Item2, readOnly, button1Text, button2Text);
    }

    private void ShowMapIntent(final Activity activity, String title, ArrayList<Location> locations, ArrayList<Location> otherLocations, ArrayList<String> otherLocationNames, boolean readOnly, String button1Text, String button2Text) {
        Intent i = new Intent("android.intent.action.MAIN");
        i.setComponent(new ComponentName(Constants.LLAMA_MAP_PACKAGE_NAME, Constants.LLAMA_MAP_ACTIVITY_NAME));
        i.putParcelableArrayListExtra(LlamaMapConstants.EXTRA_LOCATIONS, locations);
        i.putParcelableArrayListExtra(LlamaMapConstants.EXTRA_OTHER_LOCATIONS, otherLocations);
        i.putStringArrayListExtra(LlamaMapConstants.EXTRA_OTHER_LOCATION_NAMES, otherLocationNames);
        i.putExtra(LlamaMapConstants.EXTRA_READ_ONLY, readOnly);
        if (title != null) {
            i.putExtra(LlamaMapConstants.EXTRA_TITLE, title);
        }
        if (button1Text != null) {
            i.putExtra(LlamaMapConstants.EXTRA_BUTTON1_TEXT, button1Text);
        }
        if (button2Text != null) {
            i.putExtra(LlamaMapConstants.EXTRA_BUTTON2_TEXT, button2Text);
        }
        try {
            activity.startActivityForResult(i, Constants.LLAMAP_INTENT);
        } catch (ActivityNotFoundException e) {
            new AlertDialogEx.Builder(activity).setTitle(R.string.hrLlamaMapNotInstalled).setMessage(R.string.hrLlamaMapNeededMessage).setPositiveButton(R.string.hrGoToMarket, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    LlamaService.ShowLlamaMapInMarket(activity);
                }
            }).setNegativeButton(R.string.hrCancel, null).show();
        }
    }

    public void Vibrate(String vibratePattern) {
        String[] vibrateParts = vibratePattern.split("[\\s\\x20:;\\.\\-,]", -1);
        if (this._Vibrator == null) {
            this._Vibrator = (Vibrator) getSystemService("vibrator");
        }
        long[] longs = Helpers.StringsToLongs(vibrateParts);
        long total = 0;
        for (long j : longs) {
            total += j;
        }
        Logging.Report("Vibrate", "Vibrating " + vibratePattern, (Context) this);
        _VibrateWakeLock.AcquireLock(this, "Start vibration");
        this._Vibrator.cancel();
        this._Handler.postDelayed(new Runnable() {
            public void run() {
                LlamaService._VibrateWakeLock.ReleaseLock(LlamaService.this);
            }
        }, total);
        this._Vibrator.vibrate(longs, -1);
    }

    public boolean GetIsHeadSetConnected() {
        if (this._AudioManager == null) {
            this._AudioManager = (AudioManager) getSystemService("audio");
        }
        return this._AudioManager.isWiredHeadsetOn();
    }

    public boolean GetIsHeadSetWithMicConnected() {
        return this._HeadsetHasMicrophone;
    }

    public void ChangeScreenTimeout(Integer timeInSeconds) {
        System.putInt(getContentResolver(), "screen_off_timeout", (timeInSeconds == null ? 259200 : timeInSeconds.intValue()) * 1000);
    }

    public void ChangeWifiPolling(int pollingIntervalMins) {
        LlamaPollingWakeLock.AcquireLock(this, "ChangeWifiPolling");
        LlamaSettings.NearbyWifiInterval.SetValueAndCommit(this, Integer.valueOf(pollingIntervalMins), new CachedSetting[0]);
        this._Handler.postDelayed(new Runnable() {
            public void run() {
                LlamaService.this.initWifiPoller(true, true);
                LlamaPollingWakeLock.ReleaseLock(LlamaService.this);
            }
        }, 1);
    }

    public void ChangeAndroidLocationPolling(int pollingIntervalMins) {
        LlamaPollingWakeLock.AcquireLock(this, "ChangeLocationPolling");
        LlamaSettings.AndroidLocationInterval.SetValueAndCommit(this, Integer.valueOf(pollingIntervalMins), new CachedSetting[0]);
        this._Handler.postDelayed(new Runnable() {
            public void run() {
                LlamaService.this.initLocationListener();
                LlamaPollingWakeLock.ReleaseLock(LlamaService.this);
            }
        }, 1);
    }

    public void ChangeBluetoothPolling(int pollingIntervalMins) {
        LlamaPollingWakeLock.AcquireLock(this, "ChangeBluetoothPolling");
        LlamaSettings.NearbyBtInterval.SetValueAndCommit(this, Integer.valueOf(pollingIntervalMins), new CachedSetting[0]);
        this._Handler.postDelayed(new Runnable() {
            public void run() {
                LlamaService.this.initBluetoothPoller(true);
                LlamaPollingWakeLock.ReleaseLock(LlamaService.this);
            }
        }, 1);
    }

    public void SendEmail() {
        Intent i = new Intent("android.intent.action.SEND");
        i.setType("message/rfc822");
        i.putExtra("android.intent.extra.EMAIL", new String[]{"kebabapps@gmail.com"});
        i.putExtra("android.intent.extra.SUBJECT", "A Llama in distress (" + HelpersC.GetVersionName(this) + ")");
        i.putExtra("android.intent.extra.TEXT", "");
        Intent chooserIntent = Intent.createChooser(i, getString(R.string.hrSelectEmailApplication));
        chooserIntent.addFlags(268435456);
        startActivity(chooserIntent);
    }

    public void ViewWebsite() {
        Intent i = new Intent("android.intent.action.VIEW");
        i.addFlags(268435456);
        i.setData(Uri.parse("http://kebabapps.blogspot.com"));
        startActivity(i);
    }

    public void TurnOnScreen(int dimBrightFull) {
        int flag;
        PowerManager pm = (PowerManager) getSystemService("power");
        if (dimBrightFull == 0) {
            flag = 6;
        } else if (dimBrightFull == 1) {
            flag = 10;
        } else {
            flag = 26;
        }
        pm.newWakeLock((268435456 | flag) | 536870912, "TurnOnScreenWakelock").acquire(2000);
    }

    public boolean GetIsScreenOn() {
        return ((PowerManager) getSystemService("power")).isScreenOn();
    }

    public StateChange GetLastStateChange() {
        if (this._LastStateChange == null) {
            this._LastStateChange = StateChange.CreateBase(this);
        }
        return this._LastStateChange;
    }

    public void ToggleUsb(boolean turnOn) {
        this._UsbStorage.ToggleUsb(turnOn);
    }

    public void ToggleUsbOnOff() {
        this._UsbStorage.ToggleUsbOnOff();
    }

    public void SendMediaAction(int action) {
        long now = SystemClock.uptimeMillis();
        Intent intent = new Intent("android.intent.action.MEDIA_BUTTON");
        intent.putExtra("android.intent.extra.KEY_EVENT", new KeyEvent(now, now, 0, action, 0));
        sendOrderedBroadcast(intent, null);
        intent = new Intent("android.intent.action.MEDIA_BUTTON");
        intent.putExtra("android.intent.extra.KEY_EVENT", new KeyEvent(now, now, 1, action, 0));
        sendOrderedBroadcast(intent, null);
    }

    public void SetNoisyContacts(Profile p) {
        this._NoisyContacts.SetNoisyContacts(p);
    }

    public void Toggle4G(boolean turnOn) {
        WimaxHelper.ChangeWimax(this, turnOn);
    }

    public void startShortcut(Intent intent, String shortcutName) {
        startShortcut(intent, shortcutName, 0);
    }

    public void startShortcut(Intent intent, String friendlyName, int startType) {
        int failResourceId;
        intent.addFlags(268435456);
        intent.setSourceBounds(new Rect(0, 0, 1, 1));
        switch (startType) {
            case 1:
                failResourceId = R.string.hrFailedToStartShortcut1;
                startService(intent);
                return;
            case 2:
                sendBroadcast(intent);
                return;
            default:
                failResourceId = R.string.hrFailedToStartShortcut1;
                try {
                    startActivity(intent);
                    return;
                } catch (Exception ex) {
                    Logging.Report(ex, (Context) this);
                    HandleFriendlyError(String.format(getString(failResourceId), new Object[]{friendlyName}), false);
                }
        }
        Logging.Report(ex, (Context) this);
        HandleFriendlyError(String.format(getString(failResourceId), new Object[]{friendlyName}), false);
    }

    public boolean GetIsAirplaneModeEnabled() {
        return AirplaneCompat.GetAirplaneMode(this);
    }

    public void ToggleHaptic(boolean turnOn) {
        System.putInt(getContentResolver(), "haptic_feedback_enabled", turnOn ? 1 : 0);
    }

    public void ToggleWifiSleepPolicy(int policyMode) {
        int systemValue;
        switch (policyMode) {
            case 1:
                systemValue = 1;
                break;
            case 2:
                systemValue = 2;
                break;
            default:
                systemValue = 0;
                break;
        }
        System.putInt(getContentResolver(), "wifi_sleep_policy", systemValue);
    }

    public HashSet<Cell> GetIgnoredCells() {
        ThreadComplainMustBeWorker();
        return new HashSet(this._IgnoredCells);
    }

    public List<String> GetListOfCalendars() {
        if (this._CalendarReader != null) {
            return this._CalendarReader._AllCalendarNames;
        }
        return new ArrayList();
    }

    public List<CalendarItem> GetCurrentCalendarItems() {
        if (this._CalendarReader != null) {
            return this._CalendarReader.GetCurrentItems();
        }
        return new ArrayList();
    }

    public void ToggleScreenRotation(Boolean enableRotation, int cyanogenModCustomRotationBitmask) {
        int accelerometerSetting;
        if (enableRotation == null) {
            accelerometerSetting = 1;
            System.putInt(getContentResolver(), "accelerometer_rotation_mode", cyanogenModCustomRotationBitmask);
        } else if (enableRotation.booleanValue()) {
            accelerometerSetting = 1;
        } else {
            accelerometerSetting = 0;
        }
        System.putInt(getContentResolver(), "accelerometer_rotation", accelerometerSetting);
    }

    public void TriggerNamedEvent(String name) {
        testEvents(StateChange.CreateEventTrigger(this, name));
    }

    public void TurnOffScreen() {
        if (DeviceAdminCompat.IsSupported()) {
            try {
                DeviceAdminCompat.LockNow(this);
                return;
            } catch (SecurityException e) {
                HandleFriendlyError(getString(R.string.hrScreenOffError) + " " + getString(R.string.hrDeviceAdminError), false);
                return;
            }
        }
        HandleFriendlyError(getString(R.string.hrScreenOffError) + " " + getString(R.string.hrDeviceAdminNotSupported), false);
    }

    public void ChangePassword(String password) {
        if (DeviceAdminCompat.IsSupported()) {
            try {
                String passwordError = DeviceAdminCompat.ChangePassword(this, password);
                if (passwordError != null) {
                    HandleFriendlyError(getString(R.string.hrChangePasswordError) + " " + passwordError, false);
                    return;
                }
                return;
            } catch (SecurityException e) {
                HandleFriendlyError(getString(R.string.hrChangePasswordError) + " " + getString(R.string.hrDeviceAdminError), false);
                return;
            }
        }
        HandleFriendlyError(getString(R.string.hrChangePasswordError) + " " + getString(R.string.hrDeviceAdminNotSupported), false);
    }

    public HashSet<String> GetEventTypes() {
        HashSet<String> result = new HashSet();
        Iterator it = this._Events.iterator();
        while (it.hasNext()) {
            Event e = (Event) it.next();
            Iterator i$ = e._Actions.iterator();
            while (i$.hasNext()) {
                result.add(((EventFragment) i$.next()).getId());
            }
            i$ = e._Conditions.iterator();
            while (i$.hasNext()) {
                result.add(((EventFragment) i$.next()).getId());
            }
        }
        return result;
    }

    public void ClearVariables() {
        this._Variables.clear();
        this._Storage.SaveVariables(this, this._Variables);
        if (Instances.EventsActivity != null) {
            Instances.EventsActivity.Update();
        }
    }

    public void MakeNoise(String toneUri, String toneName, int streamId, String eventName) {
        if (this._QueuedSoundPlayer == null) {
            this._QueuedSoundPlayer = new QueuedSoundPlayer(this);
        }
        this._QueuedSoundPlayer.EnqueueAndPlay(new QueuedSound(toneUri, toneName, streamId, eventName));
    }

    public void StopNoise() {
        if (this._QueuedSoundPlayer == null) {
            this._QueuedSoundPlayer = new QueuedSoundPlayer(this);
        }
        this._QueuedSoundPlayer.StopAll();
    }

    public AudioManager getAudioManager() {
        if (this._AudioManager == null) {
            this._AudioManager = (AudioManager) getSystemService("audio");
        }
        return this._AudioManager;
    }

    public void BulkDeleteBeaconType(String beaconType) {
        boolean hadRemoves = false;
        Iterator i$ = this._Areas.iterator();
        while (i$.hasNext()) {
            Area a = (Area) i$.next();
            for (int i = a._Cells.size() - 1; i >= 0; i--) {
                Beacon b = (Beacon) a._Cells.get(i);
                if (beaconType.equals(b.GetTypeId())) {
                    hadRemoves |= RemoveCellFromArea(b, a, false);
                }
            }
        }
        if (hadRemoves) {
            this._Storage.SaveAreas(getApplicationContext(), this._Areas);
            if (Instances.AreasActivity != null) {
                Instances.AreasActivity.Update();
            }
            if (Instances.CellsActivity != null) {
                Instances.CellsActivity.Update();
            }
        }
    }

    public void ChangePhoneNetworkMode(int action) {
        Intent intent = new Intent(TwoGThreeG.ACTION_MODIFY_NETWORK_MODE);
        intent.putExtra(TwoGThreeG.EXTRA_NETWORK_MODE, action);
        sendBroadcast(intent);
        Intent intent2 = new Intent(TwoGThreeG.ACTION_MODIFY_NETWORK_MODE_API_LEVEL_17_CM10_1);
        intent2.putExtra(TwoGThreeG.EXTRA_NETWORK_MODE, action);
        sendBroadcast(intent2);
    }

    public void ShutdownPhone() {
        if (DateHelpers.GetTimeSpanInMinutes(SystemClock.uptimeMillis()) < 5) {
            HandleFriendlyError(String.format(getString(R.string.hrShutdownFailWarning), new Object[]{Integer.valueOf(minutes)}), false);
            return;
        }
        String command;
        HandleFriendlyInfo(getString(R.string.hrShuttingDown), true);
        switch (((Integer) LlamaSettings.RootShutdownCommand.GetValue(this)).intValue()) {
            case 1:
                command = "poweroff -f";
                break;
            case 2:
                command = "poweroff -d 8 -f";
                break;
            case 3:
                break;
            default:
                command = "poweroff";
                break;
        }
        command = "reboot -p";
        Logging.Report("Shutting down with" + command, (Context) this);
        RunWithRoot(command);
    }

    public void Speak(String text, int streamId, String ownerName) {
        if (this._QueuedSoundPlayer == null) {
            this._QueuedSoundPlayer = new QueuedSoundPlayer(this);
        }
        this._QueuedSoundPlayer.EnqueueAndPlay(new QueuedSpeech(ExpandVariables(text), streamId, ownerName));
    }

    public String ExpandVariables(String text) {
        for (Entry<String, String> kvp : this._Variables.entrySet()) {
            String key = (String) kvp.getKey();
            String value = (String) kvp.getValue();
            if (this._VariableChanges != null) {
                String actualValue = (String) this._VariableChanges.get(key);
                if (actualValue != null) {
                    value = actualValue;
                }
            }
            text = text.replace("##" + key + "##", value);
        }
        return text;
    }

    public void ForceCalendarLoad() {
        if (this._CalendarReader == null) {
            this._CalendarReader = new CalendarReader(this);
        }
    }

    public String GetActivePackageName() {
        return this._ForegroundAppWatcher == null ? "" : this._ForegroundAppWatcher.GetRecentApp();
    }

    public void SetAppWatcherInterval(int millis) {
        if (this._ForegroundAppWatcher != null) {
            this._ForegroundAppWatcher.SetInterval(millis);
        }
    }

    public HashMap<String, HashSet<String>> GetAllLlamaVariableKeyValues() {
        HashMap<String, HashSet<String>> result = new HashMap();
        Iterator it = this._Events.iterator();
        while (it.hasNext()) {
            HashSet<String> valuesForKey;
            Event e = (Event) it.next();
            Iterator i$ = e._Conditions.iterator();
            while (i$.hasNext()) {
                EventCondition<?> ec = (EventCondition) i$.next();
                if (ec.getId().equals(EventFragment.LLAMA_VARIABLE_CHANGED)) {
                    LlamaVariableCondition vc = (LlamaVariableCondition) ec;
                    valuesForKey = (HashSet) result.get(vc.GetVariableName());
                    if (valuesForKey == null) {
                        valuesForKey = new HashSet();
                        result.put(vc.GetVariableName(), valuesForKey);
                    }
                    valuesForKey.add(vc.GetVariableValue());
                }
            }
            i$ = e._Actions.iterator();
            while (i$.hasNext()) {
                EventAction<?> ea = (EventAction) i$.next();
                if (ea.getId().equals(EventFragment.SET_LLAMA_VARIABLE)) {
                    SetLlamaVariableAction va = (SetLlamaVariableAction) ea;
                    valuesForKey = (HashSet) result.get(va.GetVariableName());
                    if (valuesForKey == null) {
                        valuesForKey = new HashSet();
                        result.put(va.GetVariableName(), valuesForKey);
                    }
                    valuesForKey.add(va.GetVariableValue());
                }
            }
        }
        return result;
    }

    public Tuple<Integer, Integer> GetCurrentNotificationIcon() {
        return new Tuple(LlamaSettings.LastNotificationIcon.GetValue(this), LlamaSettings.LastNotificationIconDots.GetValue(this));
    }

    public int GetScreenRotation() {
        return ScreenRotationCompat.GetScreenRotation(((WindowManager) getSystemService("window")).getDefaultDisplay());
    }

    public ArrayList<String> GetAllGroupNames() {
        HashSet<String> result = new HashSet(this._Events.size());
        Iterator i$ = this._Events.iterator();
        while (i$.hasNext()) {
            Event e = (Event) i$.next();
            if (e.GroupName.length() > 0) {
                result.add(e.GroupName);
            }
        }
        return new ArrayList(result);
    }

    public static HashMap<String, String> GetCurrentLlamaTones(Context context) {
        if (Instances.Service != null) {
            return new HashMap(Instances.Service._LlamaTones);
        }
        HashMap<String, String> map = new HashMap();
        new LlamaStorage().LoadLlamaTones(context, map);
        return map;
    }

    public void SetCurrentLlamaTones(Profile profile) {
        Iterator i$ = profile.LlamaTones.iterator();
        while (i$.hasNext()) {
            Tuple<String, String> llamaTone = (Tuple) i$.next();
            this._LlamaTones.put(llamaTone.Item1, llamaTone.Item2);
        }
        Logging.Report(LlamaToneContentProvider.TAG, "Updated " + profile.LlamaTones.size() + " llamaTones", (Context) this);
        this._Storage.SaveLlamaTones(getApplicationContext(), this._LlamaTones);
    }

    public static HashMap<String, String> GetAllLlamaToneNamesAndCurrentValues(Context context) {
        ArrayList<Profile> profiles = GetProfilesEvenIfNotRunning(context);
        HashMap<String, String> currentTones = GetCurrentLlamaTones(context);
        HashMap<String, String> result = new HashMap();
        Iterator it = profiles.iterator();
        while (it.hasNext()) {
            Iterator i$ = ((Profile) it.next()).LlamaTones.iterator();
            while (i$.hasNext()) {
                Tuple<String, String> llamaTone = (Tuple) i$.next();
                result.put(llamaTone.Item1, currentTones.get(llamaTone.Item1));
            }
        }
        return result;
    }

    public static HashSet<String> GetAllLlamaToneNames(Context context) {
        ArrayList<Profile> profiles = GetProfilesEvenIfNotRunning(context);
        HashSet<String> result = new HashSet();
        Iterator it = profiles.iterator();
        while (it.hasNext()) {
            Iterator i$ = ((Profile) it.next()).LlamaTones.iterator();
            while (i$.hasNext()) {
                result.add(((Tuple) i$.next()).Item1);
            }
        }
        return result;
    }

    private static ArrayList<Profile> GetProfilesEvenIfNotRunning(Context context) {
        if (Instances.Service != null) {
            return new ArrayList(Instances.Service._Profiles);
        }
        return new LlamaStorage().LoadProfiles(context);
    }

    public void CopyLlamaTonesToProfiles(ArrayList<Tuple<String, String>> llamaTones, HashSet<String> selectedProfiles) {
        Iterator i$ = this._Profiles.iterator();
        while (i$.hasNext()) {
            Profile p = (Profile) i$.next();
            if (selectedProfiles.contains(p.Name)) {
                p.LlamaTones.clear();
                p.LlamaTones.addAll(llamaTones);
            }
        }
        this._Storage.SaveProfiles(this, this._Profiles);
    }

    public void SetGroupEnabled(String groupName, boolean enabled) {
        Iterator i$ = this._Events.iterator();
        while (i$.hasNext()) {
            Event e = (Event) i$.next();
            if (HelpersC.StringEquals(groupName, e.GroupName)) {
                e.Enabled = enabled;
            }
        }
        this._Storage.SaveEvents(getApplicationContext(), this._Events);
        OnEventsChanged(null);
        if (Instances.EventsActivity != null) {
            Instances.EventsActivity.Update();
        }
    }

    public void RenameGroup(String oldGroupName, String newGroupName) {
        Iterator i$ = this._Events.iterator();
        while (i$.hasNext()) {
            Event e = (Event) i$.next();
            if (HelpersC.StringEquals(oldGroupName, e.GroupName)) {
                e.GroupName = newGroupName;
            }
        }
        this._Storage.SaveEvents(getApplicationContext(), this._Events);
        if (Instances.EventsActivity != null) {
            Instances.EventsActivity.Update();
        }
    }

    public void ClearAllRecentCells() {
        ThreadComplainMustBeWorker();
        this._RecentCells.clear();
        SaveRecent();
        if (Instances.CellsActivity != null) {
            Instances.CellsActivity.Update();
        }
    }

    public void HandleNfcIntent(Intent intent) {
        byte[] nfcId = intent.getByteArrayExtra("android.nfc.extra.ID");
        if (nfcId == null || nfcId.length == 0) {
            Logging.Report("Nfc", "NFC Id was null or zerolength", (Context) this);
        }
        String nfcHexId = HelpersC.toHexString(nfcId);
        if (this._NfcWatcher != null) {
            NfcFriendlyName nfcFriendlyName;
            synchronized (this._NfcNames) {
                nfcFriendlyName = (NfcFriendlyName) this._NfcNames.get(nfcHexId);
            }
            this._NfcWatcher.notifyNfcPresent(nfcHexId, nfcFriendlyName == null ? null : nfcFriendlyName.Name);
            return;
        }
        testEvents(StateChange.CreateNfc(this, nfcHexId));
    }

    public void RegisterNfcWatcher(NfcWatcher watcher) {
        this._NfcWatcher = watcher;
        Logging.Report("NFC", "NFC watcher registered", (Context) this);
    }

    public void UnregisterNfcWatcher(NfcWatcher watcher) {
        if (this._NfcWatcher == watcher) {
            this._NfcWatcher = null;
            Logging.Report("NFC", "NFC watcher unregistered", (Context) this);
            return;
        }
        Logging.Report("NFC", "Attempted to unregister the wrong NFC watcher", (Context) this);
    }

    public void UnregisterNfcWatcherForce() {
        this._NfcWatcher = null;
    }

    public String GetNfcName(String _NfcHexId, boolean useUnknown) {
        String string;
        synchronized (this._NfcNames) {
            NfcFriendlyName name = (NfcFriendlyName) this._NfcNames.get(_NfcHexId);
            if (name == null) {
                string = useUnknown ? getString(R.string.hrUnknown) : null;
            } else {
                string = name.Name;
            }
        }
        return string;
    }

    public void AddNfcTag(String nfcHexId, String result) {
        synchronized (this._NfcNames) {
            NfcFriendlyName original = (NfcFriendlyName) this._NfcNames.put(nfcHexId, new NfcFriendlyName(nfcHexId, result));
            this._Storage.SaveNfcNames(this, this._NfcNames.values());
            if (original != null) {
                Helpers.ShowTip((Context) this, (int) R.string.hrTheFriendlyNameForNfcTag1WasModified);
            }
        }
    }

    public ArrayList<NfcFriendlyName> GetAllNfcTags(boolean sortByName) {
        ArrayList<NfcFriendlyName> nfcs = new ArrayList(this._NfcNames.values());
        if (sortByName) {
            Collections.sort(nfcs, NfcFriendlyName.NameComparer);
        }
        return nfcs;
    }

    public void DeleteNfcTag(String hexString, boolean save) {
        synchronized (this._NfcNames) {
            this._NfcNames.remove(hexString);
            if (save) {
                this._Storage.SaveNfcNames(this, this._NfcNames.values());
            }
        }
    }

    public void SaveNfcTagChanges() {
        synchronized (this._NfcNames) {
            this._Storage.SaveNfcNames(this, this._NfcNames.values());
        }
    }

    public void IdeallyRunOnUiThread(Runnable work) {
        if (!((Boolean) LlamaSettings.MultiThreadedMode.GetValue(this)).booleanValue()) {
            work.run();
        } else if (IsOnUiThread()) {
            work.run();
        } else {
            this._UiThreadHandler.post(work);
        }
    }

    public void IdeallyRunOnWorkerThread(Runnable work) {
        if (!((Boolean) LlamaSettings.MultiThreadedMode.GetValue(this)).booleanValue()) {
            work.run();
        } else if (IsOnWorkerThread()) {
            work.run();
        } else {
            this._WorkerThreadHandler.post(work);
        }
    }

    public <T> void RunOnWorkerThreadThenUiThread(Activity activityForProgress, LWorkBase work) {
        Dialog d = new ProgressDialog(activityForProgress);
        d.setCancelable(false);
        d.setCanceledOnTouchOutside(false);
        d.setIndeterminate(true);
        d.setOwnerActivity(activityForProgress);
        d.setMessage("Llama is thinking...");
        RunOnWorkerThreadThenUiThread(d, work);
    }

    public <T> void RunOnWorkerThreadThenUiThread(LWorkBase work) {
        RunOnWorkerThreadThenUiThread((Dialog) null, work);
    }

    private <T> void RunOnWorkerThreadThenUiThread(final Dialog d, LWorkBase work) {
        if (((Boolean) LlamaSettings.MultiThreadedMode.GetValue(this)).booleanValue()) {
            Dialog fullScreenDialog;
            Runnable delayedDialogShow;
            if (d != null) {
                fullScreenDialog = new Dialog(d.getContext(), 16973840);
                fullScreenDialog.setCancelable(false);
                fullScreenDialog.setCanceledOnTouchOutside(false);
                fullScreenDialog.show();
                delayedDialogShow = new Runnable() {
                    public void run() {
                        d.show();
                    }
                };
                this._UiThreadHandler.postDelayed(delayedDialogShow, 1000);
            } else {
                delayedDialogShow = null;
                fullScreenDialog = null;
            }
            final LWorkBase lWorkBase = work;
            final Dialog dialog = d;
            this._WorkerThreadHandler.post(new Runnable() {
                public void run() {
                    lWorkBase.RunInWorkerThread();
                    if (delayedDialogShow != null) {
                        LlamaService.this._UiThreadHandler.removeCallbacks(delayedDialogShow);
                    }
                    LlamaService.this._UiThreadHandler.post(new Runnable() {
                        public void run() {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                            if (fullScreenDialog != null) {
                                fullScreenDialog.dismiss();
                            }
                            lWorkBase.RunInUiThread();
                        }
                    });
                }
            });
            return;
        }
        work.RunInWorkerThread();
        work.RunInUiThread();
    }

    public Integer GetLastSignalStrength() {
        return this._LastSignalStrength;
    }

    public Cell GetCurrentCell() {
        if (this._LastCell.size() == 0) {
            return Cell.NoSignal;
        }
        return (Cell) this._LastCell.get(0);
    }
}
