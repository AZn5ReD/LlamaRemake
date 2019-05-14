package com.kebab.Llama;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import com.kebab.CachedLongSetting;
import com.kebab.CachedSetting;
import com.kebab.DateHelpers;
import java.util.Calendar;

public class NearbyBluetoothPoller {
    boolean _ExpectedEnabledState;
    CachedLongSetting _LastScanCompletedAtTicks;
    private String _PendingIntentAction;
    private int _PendingIntentRequestCode;
    boolean _PollerRequestedScan;
    PendingIntent _RtcPendingIntent;
    Iterable<BluetoothDevice> _ScanData;
    int _ScanIntervalMinutes;
    LlamaService _Service;
    protected final String _Tag;
    boolean _WaitingForAdapterToTurnOn;
    boolean _WaitingForScanResults;

    private NearbyBluetoothPoller(LlamaService service, String tag, CachedLongSetting lastScheduleSetting, int pendingIntentRequestCode, String pendingIntentActionString) {
        this._Service = service;
        this._Tag = tag;
        this._PendingIntentRequestCode = pendingIntentRequestCode;
        this._PendingIntentAction = pendingIntentActionString;
        this._LastScanCompletedAtTicks = lastScheduleSetting;
    }

    public void Init(int scanIntervalMinutes, boolean forcePoll) {
        Logging.Report(this._Tag + ": Initing for " + scanIntervalMinutes, this._Service);
        this._ScanIntervalMinutes = scanIntervalMinutes;
        ScheduleNextScan(forcePoll, true);
    }

    public void StartPoll() {
        if (AdapterIsEnabled()) {
            Logging.Report(this._Tag + ": Starting poll", this._Service);
            this._ExpectedEnabledState = true;
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
        ScheduleNextScan(false, false);
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
        ScheduleNextScan(false, false);
    }

    /* Access modifiers changed, original: protected */
    public void OnScanningCompleted(Iterable<BluetoothDevice> data) {
        Logging.Report(this._Tag + ": Scan completed", this._Service);
        PrepareScanDataForService(data);
        this._LastScanCompletedAtTicks.SetValueAndCommit(this._Service, Long.valueOf(System.currentTimeMillis()), new CachedSetting[0]);
        ScheduleNextScan(false, true);
        SendScanDataToService();
        if (this._PollerRequestedScan) {
            RestoreExpectedState();
        }
        this._PollerRequestedScan = false;
    }

    private void ScheduleNextScan(boolean forceImmediatePoll, boolean allowImmediatePoll) {
        Context context = this._Service.getApplicationContext();
        if (forceImmediatePoll) {
            Logging.Report(this._Tag, "Force-poll", context);
            StartPoll();
        }
        if (this._ScanIntervalMinutes != 0 && this._ScanIntervalMinutes != Integer.MAX_VALUE) {
            long currentTime = System.currentTimeMillis();
            long gapTicks = (long) ((this._ScanIntervalMinutes * 60) * 1000);
            long nextScheduleTicks = currentTime + gapTicks;
            if (currentTime - ((Long) this._LastScanCompletedAtTicks.GetValue(context)).longValue() > 2 * gapTicks && !forceImmediatePoll) {
                Logging.Report(this._Tag + ": it has been more than " + gapTicks + " since last poll", context);
                if (allowImmediatePoll) {
                    StartPoll();
                    return;
                }
                Logging.Report(this._Tag + ": ...but we aren't allowed to call StartPoll", context);
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
        Logging.Report(this._Tag + ": Setting adapter back to " + this._ExpectedEnabledState, this._Service);
        SetAdapterEnabled(this._ExpectedEnabledState);
    }

    public NearbyBluetoothPoller(LlamaService service) {
        this(service, "BtPoll", LlamaSettings.LastNearbyBluetoothPollTicks, Constants.RTC_BT_POLL, Constants.ACTION_RTC_BT_POLL);
    }

    /* Access modifiers changed, original: protected */
    public void PrepareScanDataForService(Iterable<BluetoothDevice> devices) {
        this._ScanData = devices;
    }

    /* Access modifiers changed, original: protected */
    public void SendScanDataToService() {
        if (this._ScanData != null) {
            this._Service.HandleBluetoothDiscoveryResults(this._ScanData);
        }
    }

    /* Access modifiers changed, original: protected */
    public boolean AdapterIsEnabled() {
        BluetoothAdapter a = BluetoothAdapter.getDefaultAdapter();
        if (a == null) {
            return false;
        }
        return a.isEnabled();
    }

    public void CancelScan() {
        BluetoothAdapter a = BluetoothAdapter.getDefaultAdapter();
        if (a != null) {
            a.cancelDiscovery();
        }
    }

    /* Access modifiers changed, original: protected */
    public boolean SetAdapterEnabled(boolean enabled) {
        BluetoothAdapter a = BluetoothAdapter.getDefaultAdapter();
        if (a == null) {
            return false;
        }
        if (enabled) {
            return a.enable();
        }
        return a.disable();
    }

    /* Access modifiers changed, original: protected */
    public boolean StartAdapterScanning(boolean adapterWasPreviouslyEnabled) {
        return BluetoothAdapter.getDefaultAdapter().startDiscovery();
    }

    /* Access modifiers changed, original: protected */
    public void PrepareToWaitForAdapterToBeEnabled() {
    }

    /* Access modifiers changed, original: protected */
    public void OnScheduledNextScan() {
    }
}
