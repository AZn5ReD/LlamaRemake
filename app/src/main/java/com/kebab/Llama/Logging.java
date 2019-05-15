package com.kebab.Llama;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Logging implements UncaughtExceptionHandler {
    private static Logging LoggingInstance;
    public static String TOAST_TAG = null;
    public static boolean WriteToLogCat = false;
    private static boolean _Buffer;
    private static StringBuffer _BufferData = new StringBuffer();
    static String[] _Filter = new String[0];
    private static File localFolder = new File(Environment.getExternalStorageDirectory(), Constants.LLAMA_EXTERNAL_STORAGE_ROOT);
    public static String llamaDeathlocalPath = new File(localFolder, "LlamaDeath.txt").getAbsolutePath();
    private static String localPath = new File(localFolder, "LlamaLog.txt").getAbsolutePath();
    private UncaughtExceptionHandler defaultUEH = Thread.getDefaultUncaughtExceptionHandler();

    private Logging() {
    }

    public static void Init(Context context) {
        if (LoggingInstance == null) {
            LoggingInstance = new Logging();
            Thread.setDefaultUncaughtExceptionHandler(LoggingInstance);
            try {
                File deathFile = new File(llamaDeathlocalPath);
                if (deathFile.exists() && new LlamaStorage().SaveSharedPrefsToSd(context, "LlamaDeath" + System.currentTimeMillis() + "_")) {
                    deathFile.delete();
                }
            } catch (Exception ex) {
                Report(ex);
            }
            InitFilter((String) LlamaSettings.DebugTagFilter.GetValue(context));
        }
    }

    public static void ReportSensitive(String tag, String s, Context context) {
        if (((Boolean) LlamaSettings.LogSensitiveData.GetValue(context)).booleanValue()) {
            Report(tag, s, context, false, false, false);
            return;
        }
        Report(tag, "Sensitive", context, false, false, false);
    }

    public static void ReportSensitive(String tag, String s, Context context, boolean dontLogToToast, boolean dontLogToFile, boolean dontLogToLogCat) {
        if (((Boolean) LlamaSettings.LogSensitiveData.GetValue(context)).booleanValue()) {
            Report(tag, s, context, dontLogToToast, dontLogToFile, dontLogToLogCat);
            return;
        }
        Report(tag, "Sensitive", context, dontLogToToast, dontLogToFile, dontLogToLogCat);
    }

    public static void ReportSensitive(String tag, String s, Exception ex, Context context) {
    }

    public static void Report(String s, Context context) {
        Report(null, s, context, false, false, false);
    }

    public static void Report(String tag, String s, Context context) {
        Report(tag, s, context, false, false, false);
    }

    public static void Report(String tag, String s, Exception ex, Context context) {
        Writer result = new StringWriter();
        try {
            result.append(s).append(" +++++ ");
            PrintWriter printWriter = new PrintWriter(result);
            ex.printStackTrace(printWriter);
            printWriter.close();
        } catch (Exception e) {
        }
        Report(tag, result.toString(), context);
    }

    public static void Report(String tag, String s, Context context, boolean dontLogToToast, boolean dontLogToFile, boolean dontLogToLogCat) {
        String tagAndMessage;
        if (tag == null) {
            tagAndMessage = s;
        } else {
            tagAndMessage = tag + ": " + s;
        }
        if (!dontLogToToast) {
            try {
                if (((Boolean) LlamaSettings.DebugToasts.GetValue(context)).booleanValue() && (((TOAST_TAG != null && TOAST_TAG.equals(tag)) || TOAST_TAG == null) && IsValidForFilter(tag))) {
                    Toast.makeText(context, tagAndMessage, 0).show();
                }
            } catch (Exception e) {
            }
        }
        if (!dontLogToFile) {
            if (_Buffer) {
                _BufferData.append(tagAndMessage).append("\n");
            } else if (((Boolean) LlamaSettings.WriteToLlamaLog.GetValue(context)).booleanValue() && IsValidForFilter(tag)) {
                String timestamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());
                StringBuilder sb = new StringBuilder();
                StringBuilder append = sb.append(timestamp).append("\t");
                if (tag == null) {
                    tag = "-";
                }
                append.append(tag).append("\t").append(tagAndMessage).append("\r\n");
                if (localPath != null) {
                    writeToFile(sb.toString());
                }
            }
        }
        if (WriteToLogCat && !dontLogToLogCat) {
            Log.i(Constants.TAG, tagAndMessage);
        }
    }

    static boolean IsValidForFilter(String tag) {
        if (_Filter.length == 0) {
            return true;
        }
        if (tag == null) {
            return false;
        }
        String lowerTag = tag.toLowerCase();
        for (String tagFilter : _Filter) {
            if (lowerTag.contains(tagFilter)) {
                return true;
            }
        }
        return false;
    }

    public static void Report(Throwable e, Context context, boolean force) {
        if (force || ((Boolean) LlamaSettings.WriteToLlamaLog.GetValue(context)).booleanValue()) {
            Report(e);
        }
    }

    public static void Report(Throwable e, Context context) {
        Report(e, context, false);
    }

    private static void Report(Throwable e) {
        String timestamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());
        Writer result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        printWriter.println("--------" + timestamp + "------------------------------------");
        e.printStackTrace(printWriter);
        printWriter.close();
        if (localPath != null) {
            writeToFile(result.toString());
        }
    }

    public void uncaughtException(Thread t, Throwable e) {
        Report(e);
        try {
            localFolder.mkdirs();
        } catch (SecurityException ex) {
            Report(ex, null);
            Log.e(Constants.TAG, "Log failed to create folder", ex);
        }
        try {
            BufferedWriter bos = new BufferedWriter(new FileWriter(llamaDeathlocalPath, true));
            bos.write("Your Llama has died. This file will make it try to export your data on the next run.");
            bos.close();
        } catch (Exception e2) {
            Log.e(Constants.TAG, "Log failed", e);
            e.printStackTrace();
        }
        this.defaultUEH.uncaughtException(t, e);
    }

    private static void writeToFile(String text) {
        try {
            localFolder.mkdirs();
        } catch (SecurityException ex) {
            Log.e(Constants.TAG, "Log failed to create folder", ex);
        }
        try {
            BufferedWriter bos = new BufferedWriter(new FileWriter(localPath, true));
            bos.write(text);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            Log.e(Constants.TAG, "Log failed", e);
            e.printStackTrace();
        }
    }

    public static void StopBufferingAndCommit(String tag, Context context, boolean dontLogToToast) {
        _Buffer = false;
        if (_BufferData.length() > 0) {
            Report(tag, _BufferData.toString(), context, dontLogToToast, false, true);
        }
        _BufferData = new StringBuffer();
    }

    public static void StartBuffering() {
        _Buffer = true;
    }

    public static void InitFilter(String getValue) {
        if (getValue == null) {
            _Filter = new String[0];
            return;
        }
        _Filter = getValue.split(",", -1);
        if (_Filter.length == 1 && _Filter[0].length() == 0) {
            _Filter = new String[0];
            return;
        }
        for (int i = 0; i < _Filter.length; i++) {
            _Filter[i] = _Filter[i].toLowerCase();
        }
    }
}
