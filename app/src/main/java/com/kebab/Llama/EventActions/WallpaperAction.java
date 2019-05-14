package com.kebab.Llama.EventActions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceActivity;
import com.kebab.AppendableCharSequence;
import com.kebab.ClickablePreferenceEx;
import com.kebab.ClickablePreferenceEx.GotResultHandler;
import com.kebab.Llama.Event;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.Logging;
import com.kebab.Llama.R;
import com.kebab.PreferenceEx;
import com.kebab.ResultRegisterableActivity;
import com.kebab.ResultRegisterableActivity.ResultCallback;
import com.kebab.WallpaperHelper;
import java.io.IOException;

public class WallpaperAction extends EventAction<WallpaperAction> {
    String _CroppedImageId;
    String _OriginalFilename;

    public WallpaperAction(String originalFilename, String croppedImageId) {
        this._OriginalFilename = originalFilename;
        this._CroppedImageId = croppedImageId;
    }

    public void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        WallpaperHelper.SetWallpaper(service, this._CroppedImageId);
    }

    public boolean RenameProfile(String oldName, String newName) {
        return false;
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 2;
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.WALLPAPER_ACTION;
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(this._OriginalFilename);
        sb.append("|");
        sb.append(this._CroppedImageId);
    }

    public static WallpaperAction CreateFrom(String[] parts, int currentPart) {
        return new WallpaperAction(parts[currentPart + 1], parts[currentPart + 2]);
    }

    public PreferenceEx<WallpaperAction> CreatePreference(PreferenceActivity context) {
        final PreferenceActivity preferenceActivity = context;
        return new ClickablePreferenceEx<WallpaperAction>((ResultRegisterableActivity) context, context.getString(R.string.hrActionWallpaper), this) {
            /* Access modifiers changed, original: protected */
            public String GetHumanReadableValue(Context context, WallpaperAction value) {
                return value._OriginalFilename;
            }

            /* Access modifiers changed, original: protected */
            public void OnPreferenceClicked(ResultRegisterableActivity host, WallpaperAction existingValue, final GotResultHandler<WallpaperAction> gotResultHandler) {
                WallpaperHelper.ShowWallpaperPicker(host, new ResultCallback() {
                    public void HandleResult(int resultCode, Intent data, Object extraStateInfo) {
                        if (data == null) {
                            Logging.Report("Wallpaper", "Wallpaper result was null", preferenceActivity);
                        } else if (resultCode != -1) {
                            Logging.Report("Wallpaper", "Wallpaper resultcode was " + resultCode, preferenceActivity);
                        } else {
                            Uri uri = (Uri) extraStateInfo;
                            if (uri == null) {
                                Logging.Report("Wallpaper", "Wallpaper uri was null", preferenceActivity);
                                return;
                            }
                            Logging.Report("Wallpaper", "Wallpaper uri was " + uri.toString(), preferenceActivity);
                            String filename = uri.getLastPathSegment();
                            gotResultHandler.HandleResult(new WallpaperAction("Wallpaper " + filename, filename));
                        }
                    }
                });
            }
        };
    }

    public void AppendActionDescription(Context context, AppendableCharSequence sb) throws IOException {
        sb.append(String.format(context.getString(R.string.hrChangeWallpaperTo1), new Object[]{this._OriginalFilename}));
    }

    public String GetIsValidError(Context context) {
        if (this._CroppedImageId == null || this._CroppedImageId.length() == 0) {
            return context.getString(R.string.hrPleaseChooseAWallpaper);
        }
        return null;
    }

    public boolean IsHarmful() {
        return false;
    }
}
