package com.kebab.Llama;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class SmartWakeLock {
    int _Flags;
    long _LastAcquireTime;
    WakeLock _WakeLock;
    String _WakeLockTag;

    public SmartWakeLock(String tag) {
        this(tag, 1);
    }

    public SmartWakeLock(String tag, int flags) {
        this._WakeLockTag = tag;
        this._Flags = flags;
    }

    public void AcquireLock(Context context, String reason) {
        if (this._WakeLock == null) {
            this._WakeLock = ((PowerManager) context.getSystemService("power")).newWakeLock(this._Flags, this._WakeLockTag);
        }
        if (this._WakeLock.isHeld()) {
            Logging.Report("SWL-" + this._WakeLockTag, this._WakeLockTag + " already acquired when reacquiring for " + reason, context);
        } else {
            Logging.Report("SWL-" + this._WakeLockTag, this._WakeLockTag + " acquiring for " + reason, context);
            this._WakeLock.acquire();
        }
        this._LastAcquireTime = System.currentTimeMillis();
    }

    public void ReleaseLock(Context context) {
        if (this._WakeLock == null) {
            this._WakeLock = ((PowerManager) context.getSystemService("power")).newWakeLock(this._Flags, this._WakeLockTag);
        }
        if (this._WakeLock == null) {
            return;
        }
        if (this._WakeLock.isHeld()) {
            Logging.Report("SWL-" + this._WakeLockTag, this._WakeLockTag + " wakelock still held, releasing after " + (System.currentTimeMillis() - this._LastAcquireTime) + " ms", context);
            this._WakeLock.release();
            return;
        }
        Logging.Report("SW-" + this._WakeLockTag, this._WakeLockTag + " wakelock not held", context);
    }

    public boolean IsAcquired(Context context) {
        if (this._WakeLock == null) {
            this._WakeLock = ((PowerManager) context.getSystemService("power")).newWakeLock(this._Flags, this._WakeLockTag);
        }
        return this._WakeLock.isHeld();
    }
}
