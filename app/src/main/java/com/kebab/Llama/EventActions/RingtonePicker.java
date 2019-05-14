package com.kebab.Llama.EventActions;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import com.kebab.AlertDialogEx.Builder;
import com.kebab.Helpers;
import com.kebab.Llama.Instances;
import com.kebab.Llama.LlamaSettings;
import com.kebab.Llama.Logging;
import com.kebab.Llama.R;
import com.kebab.ResultRegisterableActivity;
import com.kebab.ResultRegisterableActivity.ResultCallback;
import java.util.ArrayList;
import java.util.List;

public class RingtonePicker {

    public interface RingtonePickerResultCallback {
        void run(Uri uri, String str, boolean z);
    }

    public static void Show(ResultRegisterableActivity activity, String existingToneUriString, RingtonePickerResultCallback ringtonePickerResultCallback) {
        Show(R.string.hrRingtonePickerType, activity, existingToneUriString, null, false, ringtonePickerResultCallback);
    }

    public static void Show(int titleResId, ResultRegisterableActivity activity, String existingToneUriString, String noUriMessage, boolean useBuiltInRingtonePickerPicker, RingtonePickerResultCallback ringtonePickerResultCallback) {
        final Activity context = activity.GetActivity();
        String[] typesArray = context.getResources().getStringArray(R.array.ringtoneTypes);
        if (noUriMessage != null) {
            String[] a = new String[(typesArray.length + 1)];
            a[0] = noUriMessage;
            for (int i = 0; i < typesArray.length; i++) {
                a[i + 1] = typesArray[i];
            }
            typesArray = a;
        }
        final String str = noUriMessage;
        final RingtonePickerResultCallback ringtonePickerResultCallback2 = ringtonePickerResultCallback;
        final boolean z = useBuiltInRingtonePickerPicker;
        final ResultRegisterableActivity resultRegisterableActivity = activity;
        final String str2 = existingToneUriString;
        new Builder(context).setTitle(titleResId).setItems(typesArray, new OnClickListener() {
            public void onClick(DialogInterface dialog, int index) {
                dialog.dismiss();
                if (str == null || index != 0) {
                    int ringtoneType;
                    switch (index - (str == null ? 0 : 1)) {
                        case 0:
                            ringtoneType = 1;
                            break;
                        case 1:
                            ringtoneType = 2;
                            break;
                        case 2:
                            ringtoneType = 4;
                            break;
                        case 3:
                            RingtonePicker.showOtherRingtonePicker(context, context.getResources().getStringArray(R.array.ringtoneTypes)[3]);
                            return;
                        default:
                            ringtoneType = 1;
                            break;
                    }
                    final Intent ringtonePickerIntent = new Intent();
                    ringtonePickerIntent.setAction("android.intent.action.RINGTONE_PICKER");
                    if (z) {
                        PackageManager packageManager = context.getPackageManager();
                        String myPackageName = context.getPackageName();
                        List<ResolveInfo> infos = packageManager.queryIntentActivities(ringtonePickerIntent, 0);
                        final ArrayList<ActivityInfo> useful = new ArrayList();
                        ArrayList<String> namesList = new ArrayList();
                        for (ResolveInfo info : infos) {
                            if (!info.activityInfo.packageName.equals(myPackageName)) {
                                useful.add(info.activityInfo);
                                namesList.add(Helpers.CharSequenceToStringOrEmpty(info.loadLabel(packageManager)));
                            }
                        }
                        if (useful.size() == 0) {
                            Helpers.ShowSimpleDialogMessage(context, context.getString(R.string.hrLlamaCouldntFindAnyRingtonePickers));
                            return;
                        } else if (useful.size() == 1) {
                            ActivityInfo first = (ActivityInfo) useful.get(0);
                            if (first.targetActivity == null || first.targetActivity.length() <= 0) {
                                ringtonePickerIntent.setPackage(first.packageName);
                            } else {
                                ringtonePickerIntent.setClassName(first.packageName, first.targetActivity);
                            }
                            RingtonePicker.ShowRingtonePickerWithIntent(resultRegisterableActivity, str2, ringtonePickerResultCallback2, ringtoneType, ringtonePickerIntent);
                            return;
                        } else {
                            new Builder(resultRegisterableActivity.GetActivity()).setTitle(R.string.hrChooseARingtonePicker).setItems((CharSequence[]) namesList.toArray(new String[namesList.size()]), new OnClickListener() {
                                public void onClick(DialogInterface arg0, int index) {
                                    ActivityInfo selected = (ActivityInfo) useful.get(index);
                                    if (selected.targetActivity == null || selected.targetActivity.length() <= 0) {
                                        ringtonePickerIntent.setPackage(selected.packageName);
                                    } else {
                                        ringtonePickerIntent.setClassName(selected.packageName, selected.targetActivity);
                                    }
                                    RingtonePicker.ShowRingtonePickerWithIntent(resultRegisterableActivity, str2, ringtonePickerResultCallback2, ringtoneType, ringtonePickerIntent);
                                }
                            }).show();
                            return;
                        }
                    }
                    RingtonePicker.ShowRingtonePickerWithIntent(resultRegisterableActivity, str2, ringtonePickerResultCallback2, ringtoneType, ringtonePickerIntent);
                    return;
                }
                ringtonePickerResultCallback2.run(null, null, false);
            }
        }).show();
    }

    private static void ShowRingtonePickerWithIntent(ResultRegisterableActivity activity, String existingToneUriString, final RingtonePickerResultCallback ringtonePickerResultCallback, int ringtoneType, Intent ringtonePickerIntent) {
        Uri existingRingtone;
        final Activity context = activity.GetActivity();
        if (existingToneUriString == null) {
            existingRingtone = null;
        } else {
            try {
                existingRingtone = Uri.parse(existingToneUriString);
            } catch (Exception e) {
                existingRingtone = null;
            }
        }
        if (existingRingtone != null) {
            ringtonePickerIntent.putExtra("android.intent.extra.ringtone.EXISTING_URI", existingRingtone);
        }
        ringtonePickerIntent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
        ringtonePickerIntent.putExtra("android.intent.extra.ringtone.SHOW_SILENT", true);
        ringtonePickerIntent.putExtra("android.intent.extra.ringtone.TYPE", ringtoneType);
        try {
            activity.RegisterActivityResult(ringtonePickerIntent, new ResultCallback() {
                public void HandleResult(int resultCode, Intent data, Object extraStateInfo) {
                    if (resultCode == -1) {
                        String ringtoneTitle;
                        boolean isSilent;
                        Uri value = (Uri) data.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
                        Logging.Report("RingTonePicker", "Picked ringtone was " + value, context);
                        if (value != null) {
                            value = RingtonePicker.ConvertUriIfRequired(value, context);
                            Ringtone tone = RingtoneManager.getRingtone(context, value);
                            if (tone == null) {
                                ringtoneTitle = context.getString(R.string.hrUnknown);
                            } else {
                                ringtoneTitle = tone.getTitle(context);
                                if (ringtoneTitle == null) {
                                    ringtoneTitle = context.getString(R.string.hrUnknown);
                                }
                            }
                            isSilent = false;
                        } else {
                            ringtoneTitle = context.getString(R.string.hrSilent);
                            isSilent = true;
                        }
                        ringtonePickerResultCallback.run(value, ringtoneTitle, isSilent);
                    }
                }
            }, null);
        } catch (Exception e2) {
            new Builder(context).setMessage(R.string.hrRingtonePickerFailedToOpen).setPositiveButton(R.string.hrOkeyDoke, null).show();
        }
    }

    public static Uri ConvertUriIfRequired(Uri value, Context context) {
        if (value == null) {
            return null;
        }
        if (!((Boolean) LlamaSettings.ResolveContentUris.GetValue(context)).booleanValue()) {
            return value;
        }
        String ringtoneUriResult = value.toString();
        if (ringtoneUriResult == null || !ringtoneUriResult.startsWith("content://")) {
            return value;
        }
        Cursor cursor;
        String filePath;
        Uri filePathUri;
        try {
            cursor = context.getContentResolver().query(value, new String[]{"_data"}, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow("_data");
            cursor.moveToFirst();
            filePath = cursor.getString(column_index);
            filePathUri = Uri.parse(filePath);
        } catch (Exception ex) {
            filePathUri = null;
            cursor = null;
            filePath = null;
            Logging.Report(ex, context);
            Helpers.ShowTip(context, context.getString(R.string.hrCouldNotResolveContentUri));
        }
        if (filePathUri != null) {
            value = filePathUri;
            ringtoneUriResult = filePath;
        }
        if (cursor == null) {
            return value;
        }
        cursor.close();
        return value;
    }

    static void showOtherRingtonePicker(final Activity context, String dialogTitle) {
        new Builder(context).setTitle(dialogTitle).setMessage(R.string.hrErrrMusicDescription).setPositiveButton(R.string.hrYes, new OnClickListener() {
            public void onClick(DialogInterface dialog, int index) {
                dialog.dismiss();
                try {
                    Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("market://search?q=Ringtone%20Picker"));
                    intent.setFlags(268435456);
                    context.startActivity(intent);
                } catch (Exception e) {
                    Instances.Service.HandleFriendlyError(context.getString(R.string.hrProblemStartingAndroidMarket), false);
                }
            }
        }).setNegativeButton(R.string.hrNo, null).show();
    }
}
