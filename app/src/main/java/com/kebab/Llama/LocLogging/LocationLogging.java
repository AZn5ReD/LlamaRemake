package com.kebab.Llama.LocLogging;

import android.os.Environment;
import android.os.SystemClock;
import com.kebab.DateHelpers;
import com.kebab.Llama.Cell;
import com.kebab.Llama.Constants;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.LlamaSettings;
import com.kebab.Llama.Logging;
import com.kebab.Llama.R;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class LocationLogging {
    private static final int BUFFER_COMMIT_LIMIT = 4096;
    private static final int MILLIS_BUFFER_LIMIT = 600000;
    private static final int MILLIS_BUFFER_LIMIT_SD_CARD_ACCESS_FAFF_TIME = 540000;
    static final long MILLIS_PER_DAY = 86400000;
    private static final String TAG = "LlamaTrail";
    ByteArrayOutputStream _Buffer = new ByteArrayOutputStream();
    DataOutputStream _BufferOutput = new DataOutputStream(this._Buffer);
    long _DaysSinceEpochInBuffer;
    long _LastElapsedMillis;
    LlamaService _Service;

    public LocationLogging(LlamaService llamaService) {
        this._Service = llamaService;
        this._LastElapsedMillis = SystemClock.elapsedRealtime() - 540000;
    }

    public void LogLlamaStart() {
        WriteToLog(new LlamaOnLog());
    }

    public void LogLlamaEnd() {
        WriteToLog(new LlamaOffLog());
    }

    public void CellChange(Cell cell) {
        WriteToLog(new CellChangeLog(cell));
    }

    private void WriteToLog(LocationLogBase item) {
        CheckBufferIsCorrectDay();
        try {
            item.LogToBuffer(this._BufferOutput);
        } catch (IOException e) {
            Logging.Report(e, this._Service);
        }
        MaybeCommitToMemory();
    }

    public void onLowMemory() {
        CommitToMemory();
    }

    public void onFinishing() {
        CommitToMemory();
    }

    /* Access modifiers changed, original: 0000 */
    public void CheckBufferIsCorrectDay() {
        long daysSinceEpoch = GetDaysSinceEpoch();
        if (!(this._DaysSinceEpochInBuffer == 0 || this._DaysSinceEpochInBuffer == daysSinceEpoch)) {
            CommitToMemory();
        }
        this._DaysSinceEpochInBuffer = daysSinceEpoch;
    }

    static long GetDaysSinceEpoch() {
        return System.currentTimeMillis() / MILLIS_PER_DAY;
    }

    static int GetCurrentMillisInDay() {
        return (int) (System.currentTimeMillis() % MILLIS_PER_DAY);
    }

    /* Access modifiers changed, original: 0000 */
    public void MaybeCommitToMemory() {
        long currentElapsed = SystemClock.elapsedRealtime();
        if (this._Buffer.size() > BUFFER_COMMIT_LIMIT) {
            Logging.Report(TAG, "Buffer size limit reached", this._Service);
            CommitToMemory();
        } else if (this._LastElapsedMillis == 0) {
            Logging.Report(TAG, "First write from buffer", this._Service);
            CommitToMemory();
        } else if (currentElapsed - this._LastElapsedMillis > 600000) {
            Logging.Report(TAG, "Buffer time limit reached", this._Service);
            CommitToMemory();
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void CommitToMemory() {
        String fileName = DateHelpers.FormatIsoUtcDateNoTime(this._DaysSinceEpochInBuffer * MILLIS_PER_DAY) + ".llamaloc";
        FileOutputStream fos = null;
        try {
            if (((Boolean) LlamaSettings.LocationLoggingToSdCard.GetValue(this._Service)).booleanValue()) {
                File llamaLocFolder = new File(new File(Environment.getExternalStorageDirectory(), Constants.LLAMA_EXTERNAL_STORAGE_ROOT), Constants.LLAMA_LOCATION_DIR_NAME);
                llamaLocFolder.mkdirs();
                fos = new FileOutputStream(new File(llamaLocFolder, fileName), true);
            } else {
                fos = this._Service.openFileOutput(fileName, 32769);
            }
            this._Buffer.writeTo(fos);
            fos.close();
            this._LastElapsedMillis = SystemClock.elapsedRealtime();
            fos = null;
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Logging.Report(e, this._Service);
                }
            }
        } catch (Exception e2) {
            Logging.Report(e2, this._Service);
            this._Service.HandleFriendlyError(this._Service.getString(R.string.hrFailedToWriteToLocationLog), false);
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e22) {
                    Logging.Report(e22, this._Service);
                }
            }
        } catch (Throwable th) {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e222) {
                    Logging.Report(e222, this._Service);
                }
            }
        }
        this._Buffer.reset();
    }
}
