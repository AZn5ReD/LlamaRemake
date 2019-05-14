package com.kebab.Llama.Content;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;
import com.kebab.Helpers;
import com.kebab.Llama.Constants;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.Logging;
import com.kebab.Llama.R;
import com.kebab.LlamaToneRateLimits;
import com.kebab.Tuple3;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class LlamaToneContentProvider extends ContentProvider {
    public static final Uri CONTENT_URI = Uri.parse("content://com.kebab.llama.tone");
    static final String PATH_SEGMENT_NAMED = "named";
    public static final String TAG = "LlamaTones";

    public int delete(Uri paramUri, String paramString, String[] paramArrayOfString) {
        Log.i(TAG, "LlamaTonesdelete");
        return 0;
    }

    public String getType(Uri paramUri) {
        Log.i(TAG, "LlamaTonesgetType");
        return null;
    }

    public AssetFileDescriptor openAssetFile(Uri uri, String mode) {
        AssetFileDescriptor fd;
        Log.i(TAG, "LlamaTonesopenAssetFile");
        Tuple3<String, Integer, Integer> namedLookupInfo = GetSpeakerItem(uri);
        String namedLookup = namedLookupInfo.Item1;
        Integer rateLimitId = namedLookupInfo.Item2;
        Integer rateLimitSeconds = namedLookupInfo.Item3;
        LlamaToneRateLimits rateLimits = null;
        if (rateLimitId != null) {
            rateLimits = LlamaToneRateLimits.Instance(getContext());
            if (!rateLimits.CanLlamaTonePlay(rateLimitId.intValue())) {
                rateLimits.RegisterLlamaTonePlayed(getContext(), rateLimitId.intValue(), rateLimitSeconds.intValue());
                Logging.Report("LlamaToneRate", "LlamaTone with ratelimitId=" + rateLimitId + " not allowed to play yet.", getContext());
                try {
                    fd = getContext().getResources().getAssets().openFd("blank.mp3");
                    Log.i(TAG, "LlamaTonesLoaded dummy silent asset.");
                    return fd;
                } catch (IOException e) {
                    Logging.Report(TAG, "Failed to load dummy silent asset", getContext());
                    return null;
                }
            }
        }
        String toneForName = (String) LlamaService.GetCurrentLlamaTones(getContext()).get(namedLookup);
        Logging.Report(TAG, "Using tone '" + namedLookup + "' as '" + toneForName + "'", getContext());
        if (Constants.SilentRingtone.equals(toneForName)) {
            try {
                fd = getContext().getResources().getAssets().openFd("blank.mp3");
                Log.i(TAG, "LlamaTonesLoaded dummy silent asset.");
                return fd;
            } catch (IOException e2) {
                Logging.Report(TAG, "Failed to load dummy silent asset", getContext());
                return null;
            }
        }
        try {
            fd = getContext().getContentResolver().openAssetFileDescriptor(Uri.parse(toneForName), mode);
            Log.i(TAG, "LlamaTonesLoaded external asset.");
            if (rateLimits == null || rateLimitId == null) {
                return fd;
            }
            rateLimits.RegisterLlamaTonePlayed(getContext(), rateLimitId.intValue(), rateLimitSeconds.intValue());
            return fd;
        } catch (FileNotFoundException e3) {
            try {
                fd = getContext().getResources().getAssets().openFd("blank.mp3");
                Log.i(TAG, "LlamaTonesLoaded dummy asset.");
                return fd;
            } catch (IOException e4) {
                Logging.Report(TAG, "Failed to load dummy silent asset", getContext());
                return null;
            }
        }
    }

    public Uri insert(Uri paramUri, ContentValues paramContentValues) {
        Log.i(TAG, "LlamaTonesinsert");
        return null;
    }

    public boolean onCreate() {
        Log.i(TAG, "LlamaTonesoncreate");
        return true;
    }

    public Cursor query(Uri paramUri, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2) {
        Log.i(TAG, "LlamaTonesquery" + paramUri + "aaa" + paramArrayOfString1 + "bbb" + paramString1);
        String title = GetSpeakerItem(paramUri).Item1;
        if (title == null) {
            title = "Unknown";
        }
        MatrixCursor result = new MatrixCursor(new String[]{"title"}, 1);
        result.addRow(new Object[]{title});
        return result;
    }

    public int update(Uri paramUri, ContentValues paramContentValues, String paramString, String[] paramArrayOfString) {
        Log.i(TAG, "LlamaTonesupdate");
        return 0;
    }

    public static Uri CreateUri(Context context, String toneName, int rateLimitId, int rateLimitSeconds) {
        toneName = toneName.replace('/', ' ').replace('&', ' ');
        String friendlyName = toneName;
        if (rateLimitSeconds > 0) {
            friendlyName = friendlyName + " (" + context.getString(R.string.hrRateLimit1SecondsSilence, new Object[]{Integer.valueOf(rateLimitSeconds)}) + ")";
        }
        return Uri.withAppendedPath(CONTENT_URI, "named/" + Uri.encode(toneName) + "&" + rateLimitId + "&" + rateLimitSeconds + "&" + "/" + Uri.encode(friendlyName));
    }

    public static Tuple3<String, Integer, Integer> GetSpeakerItem(Uri uri) {
        if (uri.getAuthority().equals(CONTENT_URI.getAuthority())) {
            String path = uri.getPath();
            List<String> pathSegments = uri.getPathSegments();
            if (path.length() < 1) {
                throw new RuntimeException("Content URI must have a path: " + uri.toString());
            }
            String pathSegmentType = (String) pathSegments.get(0);
            if (!pathSegmentType.equals(PATH_SEGMENT_NAMED)) {
                throw new RuntimeException("Content URI had unknown path segment type: " + pathSegmentType);
            } else if (path.length() < 2) {
                throw new RuntimeException("Named content URI must tone name: " + uri.toString());
            } else {
                String[] parts = ((String) pathSegments.get(1)).split("&", -1);
                int rateLimitSeconds = 0;
                int rateLimitId = 0;
                if (parts.length > 0) {
                    Integer idValue = Helpers.ParseIntOrNull(parts[1]);
                    Integer secondsValue = Helpers.ParseIntOrNull(parts[2]);
                    if (idValue != null) {
                        rateLimitId = idValue.intValue();
                    }
                    if (secondsValue != null) {
                        rateLimitSeconds = secondsValue.intValue();
                    }
                }
                return Tuple3.Create(parts[0], Integer.valueOf(rateLimitId), Integer.valueOf(rateLimitSeconds));
            }
        }
        throw new RuntimeException("Content URI was invalid: " + uri.toString());
    }
}
