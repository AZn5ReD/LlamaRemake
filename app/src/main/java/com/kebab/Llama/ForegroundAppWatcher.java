package com.kebab.Llama;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.os.Handler;
import java.util.List;

public class ForegroundAppWatcher {
    final String TAG = "AppWatcher";
    ActivityManager _ActivityManager;
    Handler _Handler = new Handler();
    Runnable _HandlerRunnable = new Runnable() {
        public void run() {
            final String currentPackageName = ForegroundAppWatcher.this.GetCurrentApp();
            if (!ForegroundAppWatcher.this._LastPackageName.equals(currentPackageName)) {
                final String lastPackage = ForegroundAppWatcher.this._LastPackageName;
                ForegroundAppWatcher.this._LastPackageName = currentPackageName;
                Logging.Report("AppWatcher", "Out with " + lastPackage + ", long live " + currentPackageName, ForegroundAppWatcher.this._Service);
                ForegroundAppWatcher.this._Service.IdeallyRunOnWorkerThread(new X() {
                    /* Access modifiers changed, original: 0000 */
                    public void R() {
                        ForegroundAppWatcher.this._Service.testEvents(StateChange.CreateAppStartEnd(ForegroundAppWatcher.this._Service, lastPackage, currentPackageName), null);
                    }
                });
            }
            ForegroundAppWatcher.this._Handler.postDelayed(ForegroundAppWatcher.this._HandlerRunnable, (long) ForegroundAppWatcher.this._IntervalMillis);
        }
    };
    int _IntervalMillis;
    String _LastPackageName;
    LlamaService _Service;

    public ForegroundAppWatcher(int delayMillis, LlamaService service) {
        this._IntervalMillis = delayMillis;
        this._ActivityManager = (ActivityManager) service.getSystemService("activity");
        this._Service = service;
        this._LastPackageName = GetCurrentApp();
    }

    public String GetRecentApp() {
        return this._LastPackageName;
    }

    private String GetCurrentApp() {
        List<RunningTaskInfo> infos = this._ActivityManager.getRunningTasks(1);
        if (infos == null || infos.size() == 0) {
            return "";
        }
        RunningTaskInfo task = (RunningTaskInfo) infos.get(0);
        if (".BrightnessChangerActivity".equals(task.topActivity.getShortClassName())) {
            return this._LastPackageName;
        }
        return task.topActivity.getPackageName();
    }

    public void SetInterval(int millis) {
        this._IntervalMillis = millis;
        StopWatching();
        StartWatching();
    }

    public void StartWatching() {
        Logging.Report("AppWatcher", "Watching tasks every " + this._IntervalMillis, this._Service);
        this._Handler.postDelayed(this._HandlerRunnable, (long) this._IntervalMillis);
    }

    public void StopWatching() {
        Logging.Report("AppWatcher", "Stopped watching tasks", this._Service);
        this._Handler.removeCallbacks(this._HandlerRunnable);
    }
}
