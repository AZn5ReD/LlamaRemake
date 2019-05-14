package com.kebab;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.Images.Media;
import android.view.Display;
import android.view.WindowManager;
import com.kebab.Llama.Constants;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.Logging;
import com.kebab.ResultRegisterableActivity.ResultCallback;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class WallpaperHelper {
    static final String WALLPAPER_DIRECTORY = "WallpaperCrops";

    public static void ShowWallpaperPicker(ResultRegisterableActivity c, ResultCallback callback) {
        WallpaperManager wm = (WallpaperManager) c.GetActivity().getSystemService("wallpaper");
        WindowManager winm = (WindowManager) c.GetActivity().getSystemService("window");
        int width = wm.getDesiredMinimumWidth();
        int height = wm.getDesiredMinimumHeight();
        Display display = winm.getDefaultDisplay();
        if (width <= 0) {
            width = display.getWidth();
        }
        if (height <= 0) {
            height = display.getHeight();
        }
        Uri outputUri = Uri.fromFile(new File(GetLlamaWallpaperStorage(true, c.GetActivity()), System.currentTimeMillis() + ".png"));
        Logging.Report("Wallpaper", "Requesting wallpaper picker for " + width + EventFragment.LEAVE_AREA_CONDITION + height + " to save as " + outputUri.toString(), c.GetActivity());
        Intent i = new Intent("android.intent.action.PICK", Media.EXTERNAL_CONTENT_URI);
        i.putExtra("crop", "true");
        i.putExtra("aspectX", width);
        i.putExtra("aspectY", height);
        i.putExtra("outputX", width);
        i.putExtra("outputY", height);
        i.putExtra("return-data", false);
        i.putExtra("output", outputUri);
        i.putExtra("outputFormat", CompressFormat.PNG.toString());
        i.putExtra("noFaceDetection", true);
        c.RegisterActivityResult(i, callback, outputUri);
    }

    public static File GetLlamaWallpaperStorage() {
        return GetLlamaWallpaperStorage(false, null);
    }

    public static File GetLlamaWallpaperStorage(boolean createFolder, Context context) {
        File llamaRoot = new File(Environment.getExternalStorageDirectory(), Constants.LLAMA_EXTERNAL_STORAGE_ROOT);
        File llamaWallpapersRoot = new File(llamaRoot, WALLPAPER_DIRECTORY);
        if (createFolder) {
            llamaWallpapersRoot.mkdirs();
            try {
                new File(llamaRoot, ".nomedia").createNewFile();
            } catch (IOException e) {
                Logging.Report(e, context);
            }
        }
        return llamaWallpapersRoot;
    }

    public static FileInputStream OpenWallpaperCrop(String cropId) {
        try {
            return new FileInputStream(new File(GetLlamaWallpaperStorage(), cropId));
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public static boolean SetWallpaper(LlamaService service, String croppedImageId) {
        FileInputStream fileStream = OpenWallpaperCrop(croppedImageId);
        if (fileStream == null) {
            return false;
        }
        boolean success = false;
        try {
            ((WallpaperManager) service.getSystemService("wallpaper")).setStream(fileStream);
            success = true;
        } catch (IOException e) {
            Logging.Report(e, (Context) service);
        } catch (Throwable th) {
            try {
                fileStream.close();
            } catch (IOException e2) {
                Logging.Report(e2, (Context) service);
            }
        }
        try {
            fileStream.close();
            return success;
        } catch (IOException e22) {
            Logging.Report(e22, (Context) service);
            return success;
        }
    }
}
