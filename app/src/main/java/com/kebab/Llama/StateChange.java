package com.kebab.Llama;

import android.content.Context;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import com.kebab.ApiCompat.WifiCompat;
import com.kebab.DateHelpers;
import com.kebab.IterableHelpers;
import com.kebab.Lazy;
import com.kebab.Lazy.Getter;
import com.kebab.Llama.EventConditions.HourMinute;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class StateChange {
    public int BatteryLevel;
    public int ChargingFrom;
    public Boolean ChargingState;
    public Cell CurrentCellForMcc;
    public Calendar CurrentDate;
    public List<CalendarItem> CurrentEvents;
    public int CurrentHmTime;
    public long CurrentMillis;
    public int CurrentPhoneState;
    private HashMap<String, String> CurrentVariables;
    public Lazy<String> CurrentWifiAddress;
    public Lazy<String> CurrentWifiName;
    public int DayOfTheWeek;
    public String DisconnectedWifiAddress;
    public String DisconnectedWifiName;
    public List<CalendarItem> EndingEvents;
    public String EventName;
    public boolean HeadSetConnected;
    public boolean HeadSetHasMic;
    public boolean IsAirplaneModeEnabled;
    public boolean IsCarMode;
    public boolean IsDeskMode;
    public boolean IsExactHmTime;
    public boolean IsMusicActive;
    public boolean IsRoaming;
    public int LastPhoneState;
    public boolean MobileDataConnected;
    public boolean MobileDataEnabled;
    public String NotificationPackageName;
    public String NotificationTickerText;
    public HashSet<String> OtherAreas;
    public List<String> OtherBluetoothDevices;
    public String PackageName;
    public String PackageNameExiting;
    public Cell PreviousCellForMcc;
    public boolean ScreenIsOn;
    public int ScreenRotation;
    public Integer SignalStrength;
    public List<CalendarItem> StartingEvents;
    public String TriggerAreaName;
    public String TriggerBluetoothAddress;
    public String TriggerNfcId;
    public int TriggerType = -1;
    public String VariableName;
    public String VariableValueNew;
    public String VariableValueOld;
    Long _CurrentMillisRoundedToNextMinute;
    boolean _EventsNeedSaving;
    boolean _QueueRtcNeeded;
    Boolean _WifiHotSpotEnabled;
    WifiInfo __WifiInfo;

    private StateChange() {
    }

    public static StateChange Create(LlamaService service, String areaName, boolean isEntered) {
        StateChange base = CreateBase(service);
        base.TriggerAreaName = areaName;
        base.TriggerType = isEntered ? 0 : 1;
        return base;
    }

    public static Collection<StateChange> CreateStartEndPair(LlamaService service, int hmTime, Calendar currentTime, boolean isMinuteAlignedTrigger) {
        StateChange base = CreateBase(service);
        base.CurrentHmTime = hmTime;
        base.IsExactHmTime = isMinuteAlignedTrigger;
        base.CurrentDate = currentTime;
        base.CurrentMillis = currentTime.getTimeInMillis();
        base.TriggerType = 2;
        if (service._CalendarReader != null) {
            service._CalendarReader.fillStateChange(currentTime, base);
        }
        if (base.StartingEvents.size() == 0) {
            if (base.EndingEvents.size() == 0) {
                return IterableHelpers.Create(base);
            }
            return IterableHelpers.Create(base);
        } else if (base.EndingEvents.size() == 0) {
            return IterableHelpers.Create(base);
        } else {
            StateChange base2 = CreateBase(service);
            base2.CurrentHmTime = base.CurrentHmTime;
            base2.IsExactHmTime = false;
            base2.CurrentDate = base.CurrentDate;
            base2.CurrentMillis = base.CurrentMillis;
            base2.TriggerType = 29;
            base2.StartingEvents = base.StartingEvents;
            base.StartingEvents = CalendarReader.EMPTY;
            Collection<StateChange> pairList = new ArrayList();
            pairList.add(base);
            pairList.add(base2);
            return pairList;
        }
    }

    public static Collection<StateChange> CreateAppStartEnd(LlamaService service, String previousApp, String currentApp) {
        StateChange base = CreateBase(service);
        base.TriggerType = 31;
        base.PackageNameExiting = previousApp;
        base.PackageName = currentApp;
        StateChange base2 = CreateBase(service);
        base2.TriggerType = 30;
        base2.PackageName = currentApp;
        ArrayList<StateChange> pairList = new ArrayList(2);
        pairList.add(base);
        pairList.add(base2);
        return pairList;
    }

    public static StateChange CreateBattery(LlamaService service, boolean isNowCharging, int chargingFrom) {
        StateChange base = CreateBase(service);
        if (isNowCharging) {
            base.TriggerType = 3;
        } else {
            base.TriggerType = 4;
        }
        base.ChargingFrom = chargingFrom;
        return base;
    }

    static StateChange CreateBase(LlamaService service) {
        return CreateBase(service, null);
    }

    static StateChange CreateBase(final LlamaService service, WifiInfo wifiInfo) {
        boolean z = true;
        LlamaService.ThreadComplainMustNotBeUi();
        StateChange stateChange = new StateChange();
        stateChange.CurrentDate = Calendar.getInstance();
        stateChange.CurrentHmTime = HourMinute.CalendarToInt(stateChange.CurrentDate);
        stateChange.IsExactHmTime = false;
        stateChange.CurrentMillis = stateChange.CurrentDate.getTimeInMillis();
        stateChange.OtherAreas = service.CurrentAreas;
        stateChange.DayOfTheWeek = stateChange.CurrentDate.get(7);
        stateChange.ChargingState = service.GetChargingState();
        stateChange.ChargingFrom = service.GetChargingFromState();
        stateChange.IsMusicActive = service.GetIsMusicPlaying();
        Integer dockMode = service.GetDockMode();
        boolean z2 = dockMode == null ? false : dockMode.intValue() == 3;
        stateChange.IsCarMode = z2;
        z2 = dockMode == null ? false : dockMode.intValue() == 2;
        stateChange.IsDeskMode = z2;
        stateChange.OtherBluetoothDevices = service.GetConnectedBluetoothDevices();
        stateChange.HeadSetConnected = service.GetIsHeadSetConnected();
        stateChange.HeadSetHasMic = service.GetIsHeadSetWithMicConnected();
        stateChange.BatteryLevel = ((Integer) LlamaSettings.LastBatteryPercent.GetValue(service)).intValue();
        stateChange.StartingEvents = CalendarReader.EMPTY;
        stateChange.EndingEvents = CalendarReader.EMPTY;
        stateChange.CurrentEvents = service._CalendarReader != null ? service._CalendarReader._CurrentEvents : CalendarReader.EMPTY;
        stateChange.ScreenIsOn = service.GetIsScreenOn();
        stateChange.IsAirplaneModeEnabled = service.GetIsAirplaneModeEnabled();
        stateChange.CurrentVariables = service._Variables;
        stateChange.PackageName = service.GetActivePackageName();
        stateChange.ScreenRotation = ScreenRotationToRotationFlag(service.GetScreenRotation());
        if (((Integer) LlamaSettings.MobileData.GetValue(service)).intValue() == 1) {
            z2 = true;
        } else {
            z2 = false;
        }
        stateChange.MobileDataEnabled = z2;
        if (((Integer) LlamaSettings.MobileDataConnected.GetValue(service)).intValue() != 1) {
            z = false;
        }
        stateChange.MobileDataConnected = z;
        int GetIsInCall = service.GetIsInCall();
        stateChange.LastPhoneState = GetIsInCall;
        stateChange.CurrentPhoneState = GetIsInCall;
        stateChange.IsRoaming = service.getRoamingStatus();
        stateChange.SignalStrength = service._LastSignalStrength;
        stateChange.CurrentCellForMcc = service.GetCurrentCell();
        stateChange.__WifiInfo = wifiInfo;
        stateChange.CurrentWifiName = new Lazy(new Getter<String>(stateChange) {
            final /* synthetic */ StateChange val$stateChange;

            public String Get() {
                if (this.val$stateChange.__WifiInfo == null) {
                    this.val$stateChange.__WifiInfo = service.GetWifiInfo();
                }
                if (SupplicantState.COMPLETED.equals(this.val$stateChange.__WifiInfo.getSupplicantState())) {
                    return WifiCompat.GetWifiName(this.val$stateChange.__WifiInfo);
                }
                Logging.Report("WifiCondition", "Supplicant state was " + this.val$stateChange.__WifiInfo.getSupplicantState(), service);
                return null;
            }
        });
        stateChange.CurrentWifiAddress = new Lazy(new Getter<String>(stateChange) {
            final /* synthetic */ StateChange val$stateChange;

            public String Get() {
                if (this.val$stateChange.__WifiInfo == null) {
                    this.val$stateChange.__WifiInfo = service.GetWifiInfo();
                }
                if (SupplicantState.COMPLETED.equals(this.val$stateChange.__WifiInfo.getSupplicantState())) {
                    return this.val$stateChange.__WifiInfo.getBSSID();
                }
                Logging.Report("WifiCondition", "Supplicant state was " + this.val$stateChange.__WifiInfo.getSupplicantState(), service);
                return null;
            }
        });
        return stateChange;
    }

    public static StateChange CreateMusicPlaybackChanged(LlamaService service) {
        StateChange base = CreateBase(service);
        base.TriggerType = base.IsMusicActive ? 5 : 6;
        return base;
    }

    public static StateChange CreateCarMode(LlamaService service, boolean newCarMode) {
        StateChange base = CreateBase(service);
        base.TriggerType = newCarMode ? 27 : 28;
        return base;
    }

    public static StateChange CreateDeskMode(LlamaService service, boolean newDeskMode) {
        StateChange base = CreateBase(service);
        base.TriggerType = newDeskMode ? 25 : 26;
        return base;
    }

    public static StateChange CreateBluetoothChange(LlamaService service, String address, boolean isConnected) {
        StateChange base = CreateBase(service);
        base.TriggerBluetoothAddress = address;
        base.TriggerType = isConnected ? 8 : 9;
        return base;
    }

    public static StateChange CreateHeadsetPlugged(LlamaService llamaService, boolean plugged, boolean pluggedWithMicrophone) {
        StateChange base = CreateBase(llamaService);
        base.TriggerType = plugged ? 10 : 11;
        return base;
    }

    public static StateChange CreateBatteryLevel(LlamaService llamaService, int batteryLevel) {
        StateChange base = CreateBase(llamaService);
        base.TriggerType = 12;
        return base;
    }

    public static StateChange CreateScreenOnOff(LlamaService llamaService, boolean isScreenOn) {
        StateChange base = CreateBase(llamaService);
        base.TriggerType = isScreenOn ? 13 : 14;
        return base;
    }

    public void SetEventsNeedSaving() {
        this._EventsNeedSaving = true;
    }

    public boolean GetEventsNeedSaving() {
        return this._EventsNeedSaving;
    }

    public void SetQueueRtcNeeded() {
        this._QueueRtcNeeded = true;
    }

    public boolean GetQueueRtcNeeded() {
        return this._QueueRtcNeeded;
    }

    public static StateChange CreateWifiDisconnect(LlamaService llamaService, String oldWifiNetworkName, String oldWifiNetworkBssid) {
        StateChange base = CreateBase(llamaService);
        base.TriggerType = 16;
        base.DisconnectedWifiName = oldWifiNetworkName;
        base.DisconnectedWifiAddress = oldWifiNetworkBssid;
        return base;
    }

    public static StateChange CreateWifiConnect(LlamaService llamaService, WifiInfo wifiInfo) {
        StateChange base = CreateBase(llamaService, wifiInfo);
        base.TriggerType = 15;
        return base;
    }

    public static StateChange CreateAirplaneMode(LlamaService llamaService, boolean active) {
        StateChange base = CreateBase(llamaService);
        base.TriggerType = active ? 17 : 18;
        return base;
    }

    public static StateChange CreateAppNotification(LlamaService llamaService, String notificationPackageName, String notificationTickerText) {
        StateChange base = CreateBase(llamaService);
        base.TriggerType = 19;
        base.NotificationPackageName = notificationPackageName;
        base.NotificationTickerText = notificationTickerText;
        return base;
    }

    public static StateChange CreatePhoneReboot(LlamaService llamaService, boolean isStartUp) {
        StateChange base = CreateBase(llamaService);
        base.TriggerType = isStartUp ? 21 : 20;
        return base;
    }

    public static StateChange CreateAudioBecomingNoisy(LlamaService llamaService) {
        StateChange base = CreateBase(llamaService);
        base.TriggerType = 24;
        return base;
    }

    public long GetCurrentMillisRoundedToNextMinute() {
        if (this._CurrentMillisRoundedToNextMinute == null) {
            this._CurrentMillisRoundedToNextMinute = DateHelpers.RoundToNextMinute(this.CurrentDate);
        }
        return this._CurrentMillisRoundedToNextMinute.longValue();
    }

    public static StateChange CreateEventTrigger(LlamaService llamaService, String name) {
        StateChange base = CreateBase(llamaService);
        base.TriggerType = 22;
        base.EventName = name;
        return base;
    }

    public static StateChange CreateVariableChange(LlamaService llamaService, String variableName, String oldValue, String newValue) {
        StateChange base = CreateBase(llamaService);
        base.TriggerType = 23;
        base.VariableName = variableName;
        base.VariableValueOld = oldValue;
        base.VariableValueNew = newValue;
        return base;
    }

    public String GetVariableForName(String variableName) {
        String value = (String) this.CurrentVariables.get(variableName);
        if (value == null) {
            return "";
        }
        return value;
    }

    public static StateChange CreatePhoneState(LlamaService llamaService, int oldPhoneState) {
        StateChange base = CreateBase(llamaService);
        base.LastPhoneState = oldPhoneState;
        switch (base.CurrentPhoneState) {
            case 4:
                base.TriggerType = 37;
                break;
            case 8:
                base.TriggerType = 33;
                break;
            default:
                base.TriggerType = 32;
                break;
        }
        return base;
    }

    public static StateChange CreatePhoneStateIsNowInCall(LlamaService llamaService) {
        StateChange base = CreateBase(llamaService);
        base.TriggerType = 33;
        return base;
    }

    public static StateChange CreateScreenRotation(LlamaService llamaService, int newRotation) {
        StateChange base = CreateBase(llamaService);
        base.TriggerType = 34;
        base.ScreenRotation = ScreenRotationToRotationFlag(newRotation);
        return base;
    }

    static int ScreenRotationToRotationFlag(int rotationValue) {
        switch (rotationValue) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 4;
            default:
                return 8;
        }
    }

    public static StateChange CreateUserPresent(LlamaService llamaService) {
        StateChange base = CreateBase(llamaService);
        base.TriggerType = 36;
        return base;
    }

    public boolean WifiHotSpotEnabled(Context context) {
        if (this._WifiHotSpotEnabled == null) {
            this._WifiHotSpotEnabled = Boolean.valueOf(WifiAccessPoint.IsEnabled(context));
        }
        return this._WifiHotSpotEnabled.booleanValue();
    }

    public static StateChange CreateNfc(LlamaService llamaService, String nfcHexId) {
        StateChange base = CreateBase(llamaService);
        base.TriggerNfcId = nfcHexId;
        base.TriggerType = 38;
        return base;
    }

    public static StateChange CreateRoaming(LlamaService llamaService, boolean newRoamingState) {
        StateChange base = CreateBase(llamaService);
        base.IsRoaming = newRoamingState;
        base.TriggerType = newRoamingState ? 39 : 40;
        return base;
    }

    public static StateChange CreateMobileData(LlamaService llamaService, boolean isEnabled) {
        StateChange base = CreateBase(llamaService);
        base.TriggerType = isEnabled ? 41 : 42;
        return base;
    }

    public static StateChange CreateMobileDataConnected(LlamaService llamaService, boolean isConnected) {
        StateChange base = CreateBase(llamaService);
        base.TriggerType = isConnected ? 43 : 44;
        base.MobileDataConnected = isConnected;
        return base;
    }

    public static StateChange CreateSignalStrength(LlamaService llamaService) {
        StateChange base = CreateBase(llamaService);
        base.TriggerType = 45;
        return base;
    }

    public static StateChange CreateMccMnc(LlamaService llamaService, Cell previousCell, Cell currentCell) {
        StateChange base = CreateBase(llamaService);
        base.CurrentCellForMcc = currentCell;
        base.PreviousCellForMcc = previousCell;
        base.TriggerType = 46;
        return base;
    }
}
