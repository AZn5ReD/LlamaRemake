package com.kebab.Llama;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Handler;
import com.kebab.CachedLongSetting;
import com.kebab.CachedSetting;
import com.kebab.DateHelpers;
import java.util.Calendar;
import java.util.List;

public class NearbyWifiPoller {
    static int MAX_SCAN_TRIES = 3;
    private static final int MAX_ZERO_RESULT_SCANS = 1;
    static final int WIFI_SCAN_TIMEOUT = 8000;
    private static final int ZERO_RESULT_SCAN_INTERVAL = 10;
    protected final String TAG = "WifiPoll";
    Boolean _ExpectedEnabledState;
    CachedLongSetting _LastScanCompletedAtTicks;
    WifiLock _Lock;
    private String _PendingIntentAction;
    private int _PendingIntentRequestCode;
    long _PollCycleStartedAtTicks;
    boolean _PollerRequestedScan;
    PendingIntent _RtcPendingIntent;
    List<ScanResult> _ScanData;
    int _ScanIntervalMinutes;
    LlamaService _Service;
    boolean _WaitingForAdapterToTurnOn;
    boolean _WaitingForScanResults;
    Handler _WifiScanHandler = new Handler();
    Runnable _WifiScanTookTooLongRunnable = new Runnable() {
        public void run() {
            NearbyWifiPoller.this.getClass();
            Logging.Report("WifiPoll", "Scan didn't complete within 8000", NearbyWifiPoller.this._Service);
            NearbyWifiPoller.this.OnScanFailed();
        }
    };
    int _ZeroResultScans;

    public NearbyWifiPoller(LlamaService service) {
        this._Service = service;
        this._LastScanCompletedAtTicks = LlamaSettings.LastNearbyWifiPollTicks;
        this._PendingIntentRequestCode = Constants.RTC_WIFI_POLL;
        this._PendingIntentAction = Constants.ACTION_RTC_WIFI_POLL;
    }

    public void Cancel() {
        this._ScanIntervalMinutes = 0;
        ReleaseWifiLock();
    }

    public void Init(int scanIntervalMinutes, boolean forcePoll, boolean forceStoreCurrentState) {
        if (scanIntervalMinutes == Integer.MAX_VALUE) {
            this._ExpectedEnabledState = null;
        }
        this._ScanIntervalMinutes = scanIntervalMinutes;
        ScheduleNextScan(forcePoll, true, forceStoreCurrentState);
    }

    public void StartPoll(boolean storeCurrentStateAsExpectedState) {
        this._WaitingForAdapterToTurnOn = false;
        this._WaitingForScanResults = false;
        this._PollerRequestedScan = false;
        WifiPollWakeLock.AcquireLock(this._Service, "Starting cycle");
        if (((Boolean) LlamaSettings.NearbyWifiDisableForHotSpot.GetValue(this._Service)).booleanValue() && WifiAccessPoint.IsEnabled(this._Service)) {
            Logging.Report("WifiPoll", "Wifi Hotspot enabled, clearing expected state (was " + this._ExpectedEnabledState + ") aborting wifipoll.", this._Service);
            this._ExpectedEnabledState = null;
            OnScanFailed();
            return;
        }
        this._PollCycleStartedAtTicks = System.currentTimeMillis();
        if (AdapterIsEnabled()) {
            Logging.Report("WifiPoll", "Starting cycle. Adapter already enabled", this._Service);
            if (storeCurrentStateAsExpectedState) {
                UpdateExpectedEnabledState(true);
            }
            OnAdapterReadyForScan(true, 0);
            return;
        }
        Logging.Report("WifiPoll", "Starting cycle. Adapter not enabled, enabling", this._Service);
        if (storeCurrentStateAsExpectedState) {
            UpdateExpectedEnabledState(false);
        }
        this._WaitingForAdapterToTurnOn = true;
        SetAdapterEnabled(true, new Runnable() {
            public void run() {
                Logging.Report("WifiPoll", "Starting cycle. Adapter failed to turn on", NearbyWifiPoller.this._Service);
                NearbyWifiPoller.this.OnScanFailed();
            }
        });
    }

    public void OnScanningCompleted() {
        if (this._Service._Wifi.isWifiEnabled()) {
            this._WifiScanHandler.removeCallbacks(this._WifiScanTookTooLongRunnable);
            List<ScanResult> result = this._Service._Wifi.getScanResults();
            this._Service.AddDebugCell(Cell.WifiScanResults);
            this._LastScanCompletedAtTicks.SetValueAndCommit(this._Service, Long.valueOf(System.currentTimeMillis()), new CachedSetting[0]);
            if (this._WaitingForScanResults) {
                Logging.Report("WifiPoll", "Scanning completed with " + (result == null ? "null result" : result.size() + " networks") + ". We are expecting a scan.", this._Service);
                this._WaitingForScanResults = false;
                if (result == null) {
                    Logging.Report("WifiPoll", "Scan result was null", this._Service);
                    OnScanFailed();
                    return;
                } else if (AdapterIsEnabled()) {
                    OnScanSuccess(result, true);
                    return;
                } else {
                    Logging.Report("WifiPoll", "Scan result receive, but adapter not enabled", this._Service);
                    OnScanFailed();
                    return;
                }
            } else if (result == null) {
                Logging.Report("WifiPoll", "Unexpected scan completed with null result, doing nothing", this._Service);
                return;
            } else {
                Logging.Report("WifiPoll", "Unexpected scan completed with " + result.size() + " networks, accepting", this._Service);
                OnScanSuccess(result, false);
                return;
            }
        }
        Logging.Report("WifiPoll", "OnScanningCompleted while wifi was disabled. Ignoring.", this._Service);
    }

    public void UpdateExpectedEnabledState(boolean newExpectedState) {
        this._ExpectedEnabledState = Boolean.valueOf(newExpectedState);
    }

    private WifiLock GetWifiLock() {
        if (this._Lock == null) {
            this._Lock = this._Service._Wifi.createWifiLock(2, "WifiPoll");
            Logging.Report("WifiPoll", "CREATED WIFI LOCK", this._Service);
        }
        return this._Lock;
    }

    private void AcquireWifiLock() {
        WifiLock l = GetWifiLock();
        if (!l.isHeld()) {
            l.acquire();
            Logging.Report("WifiPoll", "ACQUIRED WIFI LOCK", this._Service);
        }
    }

    private void ReleaseWifiLock() {
        WifiLock l = GetWifiLock();
        if (l.isHeld()) {
            l.release();
            Logging.Report("WifiPoll", "RELEASED WIFI LOCK", this._Service);
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

    public void OnAdapterHasBeenEnabled() {
        if (this._WaitingForAdapterToTurnOn) {
            this._WaitingForAdapterToTurnOn = false;
            OnAdapterReadyForScan(false, 0);
        }
    }

    /* Access modifiers changed, original: protected */
    public boolean AdapterIsEnabled() {
        return this._Service._Wifi.isWifiEnabled();
    }

    /* Access modifiers changed, original: protected */
    public void SetAdapterEnabled(final boolean enabled, final Runnable stateChangeFailedCallback) {
        this._Service._Handler.postDelayed(new Runnable() {
            public void run() {
                if (enabled) {
                    NearbyWifiPoller.this._Service.AddDebugCell(Cell.WifiOn);
                    NearbyWifiPoller.this.AcquireWifiLock();
                } else {
                    NearbyWifiPoller.this._Service.AddDebugCell(Cell.WifiOff);
                    NearbyWifiPoller.this.ReleaseWifiLock();
                }
                if (!NearbyWifiPoller.this._Service._Wifi.setWifiEnabled(enabled) && stateChangeFailedCallback != null) {
                    stateChangeFailedCallback.run();
                }
            }
        }, 50);
    }

    private void OnAdapterReadyForScan(final boolean wasPreviouslyEnabled, int failCount) {
        this._WaitingForAdapterToTurnOn = false;
        if (this._Service._Wifi.startScan()) {
            this._Service.AddDebugCell(Cell.WifiScanStart);
            this._WaitingForScanResults = true;
            this._WifiScanHandler.postDelayed(this._WifiScanTookTooLongRunnable, 8000);
            return;
        }
        String supplState;
        this._Service.AddDebugCell(Cell.WifiScanFail);
        int wifiState = this._Service._Wifi.getWifiState();
        SupplicantState s = this._Service._Wifi.getConnectionInfo().getSupplicantState();
        if (s == null) {
            supplState = "null";
        } else {
            supplState = WifiInfo.getDetailedStateOf(s).toString();
        }
        this._WaitingForScanResults = false;
        Logging.Report("WifiPoll", "Start scan failed at attempt " + failCount + ". wifi state was " + wifiState + ", suppl=" + supplState, this._Service);
        final int newFailCount = failCount + 1;
        if (newFailCount < MAX_SCAN_TRIES) {
            this._Service._Handler.postDelayed(new Runnable() {
                public void run() {
                    NearbyWifiPoller.this.OnAdapterReadyForScan(wasPreviouslyEnabled, newFailCount);
                }
            }, 5000);
            return;
        }
        Logging.Report("WifiPoll", "Start scan failed " + MAX_SCAN_TRIES + " times. Giving up", this._Service);
        OnScanFailed();
    }

    private void OnScanFailed() {
        this._ScanData = null;
        this._WaitingForAdapterToTurnOn = false;
        OnScanCompletion(true, false);
    }

    private void OnScanSuccess(List<ScanResult> data, boolean allowRestorePreviousState) {
        if (data.size() == 0) {
            Logging.Report("WifiPoll", "Scan size is zero, ignoring and checking again shortly", this._Service);
            this._ZeroResultScans++;
            if (this._ZeroResultScans <= 1) {
                Logging.Report("WifiPoll", "Scan size is zero, ignoring and checking again shortly", this._Service);
                data = null;
            } else {
                Logging.Report("WifiPoll", "Scan size is zero after " + this._ZeroResultScans + " tries, accepting lack of networks", this._Service);
                this._ZeroResultScans = 0;
            }
        } else {
            this._ZeroResultScans = 0;
        }
        this._ScanData = data;
        OnScanCompletion(allowRestorePreviousState, false);
    }

    private void OnScanCompletion(boolean allowRestorePreviousState, boolean allowImmediateNextScan) {
        ReleaseWifiLock();
        ScheduleNextScan(false, allowImmediateNextScan, false);
        ReportDataToService();
        if (allowRestorePreviousState) {
            Logging.Report("WifiPoll", "Scan cycle completed. Restoring state to " + this._ExpectedEnabledState, this._Service);
            RestorePreviousState();
        } else {
            Logging.Report("WifiPoll", "Scan cycle completed. Not restoring state.", this._Service);
        }
        WifiPollWakeLock.ReleaseLock(this._Service);
    }

    private void ReportDataToService() {
        if (this._ScanData != null) {
            this._Service.HandleWifiScanResults(this._ScanData);
        }
    }

    private void ScheduleNextScan(boolean forceImmediatePoll, boolean allowImmediateScan, boolean forceStoreCurrentStateIfImmediate) {
        Context context = this._Service.getApplicationContext();
        if (forceImmediatePoll) {
            Logging.Report("WifiPoll", "Force-poll", context);
            StartPoll(forceStoreCurrentStateIfImmediate);
        }
        if (this._ScanIntervalMinutes != 0 && this._ScanIntervalMinutes != Integer.MAX_VALUE) {
            long j;
            long currentTime = System.currentTimeMillis();
            long gapTicks = (long) ((this._ScanIntervalMinutes * 60) * 1000);
            if (this._ZeroResultScans > 0) {
                j = 10;
            } else {
                j = gapTicks;
            }
            long nextScheduleTicks = currentTime + j;
            if (currentTime - ((Long) this._LastScanCompletedAtTicks.GetValue(context)).longValue() > 2 * gapTicks && !forceImmediatePoll) {
                Logging.Report("WifiPoll", "It has been more than " + gapTicks + "ms since last poll", context);
                if (allowImmediateScan) {
                    StartPoll(true);
                    return;
                }
                Logging.Report("WifiPoll", "Not allowed to start polling immediately", context);
            }
            ((AlarmManager) context.getSystemService("alarm")).set(0, nextScheduleTicks, GetOrCreatePendingIntent());
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(nextScheduleTicks);
            Logging.Report("WifiPoll", "Scheduled next cycle for " + DateHelpers.FormatDate(cal), this._Service);
        }
    }

    private void RestorePreviousState() {
        if (this._ExpectedEnabledState == null) {
            Logging.Report("WifiPoll", "Previous state not changed as it was null", this._Service);
        } else {
            SetAdapterEnabled(this._ExpectedEnabledState.booleanValue(), null);
        }
    }
}
