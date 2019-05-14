package com.kebab.Llama;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Handler;
import com.kebab.CachedLongSetting;
import com.kebab.CachedSetting;
import com.kebab.DateHelpers;
import java.util.Calendar;
import java.util.List;

public class NearbyWifiPollerOld {
    private static final long AUTO_SCAN_WAIT_TIME_MILLIS = 8000;
    private static final long SCAN_RESULTS_WAS_FEWER_RETRY_TIMEOUT = 1000;
    boolean _ExpectedEnabledState;
    CachedLongSetting _LastScanCompletedAtTicks;
    int _LastScanCount;
    WifiLock _Lock;
    private String _PendingIntentAction;
    private int _PendingIntentRequestCode;
    boolean _PollerRequestedScan;
    boolean _Retrying;
    PendingIntent _RtcPendingIntent;
    List<ScanResult> _ScanData;
    int _ScanIntervalMinutes;
    Object _ScanningTimeoutToken;
    protected long _ScheduleSoonerTicks;
    LlamaService _Service;
    protected final String _Tag;
    boolean _WaitingForAdapterToTurnOn;
    boolean _WaitingForScanResults;
    Handler _WifiScanFailTimeoutHandler;
    Runnable _WifiScanFailTimeoutRunnable;

    private NearbyWifiPollerOld(LlamaService service, String tag, CachedLongSetting lastScheduleSetting, int pendingIntentRequestCode, String pendingIntentActionString) {
        this._ScanningTimeoutToken = new Object();
        this._WifiScanFailTimeoutHandler = new Handler();
        this._WifiScanFailTimeoutRunnable = new Runnable() {
            public void run() {
                Logging.Report(NearbyWifiPollerOld.this._Tag, "Wifi scan didn't happen. Lets try again", NearbyWifiPollerOld.this._Service);
                if (!NearbyWifiPollerOld.this._Service._Wifi.startScan()) {
                    WifiPollWakeLock.ReleaseLock(NearbyWifiPollerOld.this._Service);
                    NearbyWifiPollerOld.this.OnScanningCompleted(NearbyWifiPollerOld.this._ScanningTimeoutToken);
                }
            }
        };
        this._Service = service;
        this._Tag = tag;
        this._PendingIntentRequestCode = pendingIntentRequestCode;
        this._PendingIntentAction = pendingIntentActionString;
        this._LastScanCompletedAtTicks = lastScheduleSetting;
    }

    public void Init(int scanIntervalMinutes) {
        Logging.Report(this._Tag + ": Initing for " + scanIntervalMinutes, this._Service);
        this._ScanIntervalMinutes = scanIntervalMinutes;
        ScheduleNextScan();
    }

    public void StartPoll() {
        if (AdapterIsEnabled()) {
            Logging.Report(this._Tag + ": Starting poll", this._Service);
            if (this._ScheduleSoonerTicks == 0) {
                this._ExpectedEnabledState = true;
            }
            this._ScheduleSoonerTicks = 0;
            StartScanning(true);
            return;
        }
        Logging.Report(this._Tag + ": Starting poll, but adapter not enabled", this._Service);
        this._ExpectedEnabledState = false;
        this._WaitingForAdapterToTurnOn = true;
        if (SetAdapterEnabled(true)) {
            PrepareToWaitForAdapterToBeEnabled();
            return;
        }
        Logging.Report(this._Tag + ": Failed to enable adapter", this._Service);
        ScheduleNextScan();
    }

    public void OnAdapterEnabled() {
        if (this._WaitingForAdapterToTurnOn) {
            this._WaitingForAdapterToTurnOn = false;
            StartScanning(false);
            return;
        }
        Logging.Report(this._Tag + ": Adapter enabled, but we weren't waiting for it", this._Service);
    }

    private void StartScanning(boolean adapterWasPreviouslyEnabled) {
        this._PollerRequestedScan = true;
        Logging.Report(this._Tag + ": Starting scan", this._Service);
        if (StartAdapterScanning(adapterWasPreviouslyEnabled)) {
            this._WaitingForScanResults = true;
            return;
        }
        Logging.Report(this._Tag + ": Failed to start scan", this._Service);
        RestoreExpectedState();
        ScheduleNextScan();
    }

    /* Access modifiers changed, original: protected */
    public void OnScanningCompleted(Object data) {
        boolean _AwkwardMode;
        Logging.Report(this._Tag + ": Scan completed", this._Service);
        PrepareScanDataForService(data);
        this._LastScanCompletedAtTicks.SetValueAndCommit(this._Service, Long.valueOf(System.currentTimeMillis()), new CachedSetting[0]);
        if (this._ScheduleSoonerTicks > 0) {
            _AwkwardMode = true;
        } else {
            _AwkwardMode = false;
        }
        ScheduleNextScan();
        if (!_AwkwardMode) {
            if (this._PollerRequestedScan) {
                RestoreExpectedState();
            }
            this._PollerRequestedScan = false;
        }
        SendScanDataToService();
    }

    private void ScheduleNextScan() {
        if (this._ScanIntervalMinutes != 0 && this._ScanIntervalMinutes != Integer.MAX_VALUE) {
            long j;
            Context context = this._Service.getApplicationContext();
            long currentTime = System.currentTimeMillis();
            long gapTicks = (long) ((this._ScanIntervalMinutes * 60) * 1000);
            if (this._ScheduleSoonerTicks > 0) {
                j = this._ScheduleSoonerTicks;
            } else {
                j = gapTicks;
            }
            long nextScheduleTicks = currentTime + j;
            if (currentTime - ((Long) this._LastScanCompletedAtTicks.GetValue(context)).longValue() > 2 * gapTicks) {
                Logging.Report(this._Tag + ": it has been more than " + gapTicks + " since last poll", context);
                StartPoll();
                return;
            }
            ((AlarmManager) context.getSystemService("alarm")).set(0, nextScheduleTicks, GetOrCreatePendingIntent());
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(nextScheduleTicks);
            Logging.Report(this._Tag, this._Tag + ": Scheduled next wake for " + DateHelpers.FormatDate(cal), this._Service);
            OnScheduledNextScan();
        }
    }

    private PendingIntent GetOrCreatePendingIntent() {
        if (this._RtcPendingIntent == null) {
            Intent intent = new Intent(this._Service, RtcReceiver.class);
            intent.setAction(this._PendingIntentAction);
            this._RtcPendingIntent = PendingIntent.getBroadcast(this._Service, this._PendingIntentRequestCode, intent, 268435456);
        }
        return this._RtcPendingIntent;
    }

    public void Cancel() {
        this._ScanIntervalMinutes = 0;
        if (this._WaitingForScanResults) {
            Logging.Report(this._Tag + ": Cancelled during a scan", this._Service);
            CancelScan();
            RestoreExpectedState();
        }
        Logging.Report(this._Tag + ": Removing RTC intent", this._Service);
        ((AlarmManager) this._Service.getSystemService("alarm")).cancel(GetOrCreatePendingIntent());
    }

    public void UpdateExpectedEnabledState(boolean newExpectedState) {
        this._ExpectedEnabledState = newExpectedState;
    }

    private void RestoreExpectedState() {
        if (this._ScanIntervalMinutes == 0 || this._ScanIntervalMinutes == Integer.MAX_VALUE) {
            Logging.Report(this._Tag + ": Not setting adapter back to " + this._ExpectedEnabledState + " because interval is " + this._ScanIntervalMinutes, this._Service);
            return;
        }
        Logging.Report(this._Tag + ": Setting adapter back to " + this._ExpectedEnabledState, this._Service);
        SetAdapterEnabled(this._ExpectedEnabledState);
    }

    public NearbyWifiPollerOld(LlamaService service) {
        this(service, "WifiPoll", LlamaSettings.LastNearbyWifiPollTicks, Constants.RTC_WIFI_POLL, Constants.ACTION_RTC_WIFI_POLL);
    }

    private WifiLock GetWifiLock() {
        if (this._Lock == null) {
            this._Lock = this._Service._Wifi.createWifiLock(this._Tag);
        }
        return this._Lock;
    }

    /* Access modifiers changed, original: protected */
    public void PrepareScanDataForService(Object data) {
        this._ScanData = null;
        boolean wifiEnabled = this._Service._Wifi.isWifiEnabled();
        if (data == this._ScanningTimeoutToken || !wifiEnabled) {
            if (!wifiEnabled) {
                Logging.Report(this._Tag, "Wifi not enabled, but we've received a scancompleted. Ignoring, it must be delayed.", this._Service);
            }
            this._Retrying = false;
        } else {
            this._ScanData = this._Service._Wifi.getScanResults();
            if (this._ScanData != null && this._ScanData.size() >= this._LastScanCount) {
                this._Retrying = false;
            } else if (this._Retrying) {
                this._Service.AddDebugCell(Cell.WifiStillEmpty);
                Logging.Report(this._Tag, "Last wifi network count was less than last time's. Already retried, stopping.", this._Service);
            } else {
                Logging.Report(this._Tag, "Last wifi network count (" + (this._ScanData == null ? "null" : Integer.valueOf(this._ScanData.size())) + ") was less than last time's (" + this._LastScanCount + "). Retrying", this._Service);
                this._ScanData = null;
                this._ScheduleSoonerTicks = 10000;
                this._Retrying = true;
                this._Service.AddDebugCell(Cell.WifiEmpty);
            }
            if (this._ScanData == null) {
                this._LastScanCount = 0;
            } else {
                this._ScheduleSoonerTicks = 0;
                this._LastScanCount = this._ScanData.size();
            }
        }
        this._WifiScanFailTimeoutHandler.removeCallbacks(this._WifiScanFailTimeoutRunnable);
        if (GetWifiLock().isHeld()) {
            GetWifiLock().release();
        }
        WifiPollWakeLock.ReleaseLock(this._Service);
    }

    /* Access modifiers changed, original: protected */
    public void SendScanDataToService() {
        if (this._ScanData != null) {
            this._Service.HandleWifiScanResults(this._ScanData);
        }
    }

    /* Access modifiers changed, original: protected */
    public void PrepareToWaitForAdapterToBeEnabled() {
        WifiPollWakeLock.AcquireLock(this._Service, "Waiting for wifi to activate");
    }

    /* Access modifiers changed, original: protected */
    public boolean AdapterIsEnabled() {
        return this._Service._Wifi.isWifiEnabled();
    }

    public void CancelScan() {
        if (GetWifiLock().isHeld()) {
            GetWifiLock().release();
        }
        WifiPollWakeLock.ReleaseLock(this._Service);
    }

    /* Access modifiers changed, original: protected */
    public boolean SetAdapterEnabled(boolean enabled) {
        if (GetWifiLock().isHeld()) {
            GetWifiLock().release();
        }
        return this._Service._Wifi.setWifiEnabled(enabled);
    }

    /* Access modifiers changed, original: protected */
    public boolean StartAdapterScanning(boolean adapterWasPreviouslyEnabled) {
        if (!GetWifiLock().isHeld()) {
            GetWifiLock().acquire();
        }
        WifiPollWakeLock.AcquireLock(this._Service, "scanning");
        if (adapterWasPreviouslyEnabled) {
            this._Service._Wifi.startScan();
        }
        this._WifiScanFailTimeoutHandler.postDelayed(this._WifiScanFailTimeoutRunnable, AUTO_SCAN_WAIT_TIME_MILLIS);
        return true;
    }

    /* Access modifiers changed, original: protected */
    public void OnScheduledNextScan() {
        WifiPollWakeLock.ReleaseLock(this._Service);
    }
}
