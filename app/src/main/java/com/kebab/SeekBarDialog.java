package com.kebab;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import com.kebab.SeekBarDialogView.ValueFormatter;

public class SeekBarDialog {

    public interface ButtonHandler {
        void Do(int i);
    }

    public static void Show(Activity activity, int value, int min, int max, String topMostValue, String dialogMessage, ValueFormatter formatter, ButtonHandler okHandler, ButtonHandler cancelHandler) {
        Show(activity, value, min, max, topMostValue, dialogMessage, dialogMessage, formatter, okHandler, cancelHandler);
    }

    public static void Show(Activity activity, int value, int min, int max, String topMostValue, String title, String dialogMessage, ValueFormatter formatter, ButtonHandler okHandler, ButtonHandler cancelHandler) {
        ShowViewHelper(activity, new SeekBarDialogView(value, min, max, topMostValue, dialogMessage, formatter), title, dialogMessage, okHandler, cancelHandler);
    }

    public static void Show(Activity activity, int value, int min, int max, String topMostValue, String dialogMessage, String textValueSuffix, ButtonHandler okHandler, ButtonHandler cancelHandler) {
        ShowViewHelper(activity, new SeekBarDialogView(value, min, max, topMostValue, dialogMessage, textValueSuffix), dialogMessage, dialogMessage, okHandler, cancelHandler);
    }

    public static void ShowViewHelper(Activity activity, final SeekBarDialogView view, String title, String dialogMessage, final ButtonHandler okHandler, final ButtonHandler cancelHandler) {
        Builder alert = new AlertDialogEx.Builder(activity);
        alert.setTitle(title);
        alert.setView(view.createSeekBarDialogView(activity));
        alert.setPositiveButton("Ok", new OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (okHandler != null) {
                    okHandler.Do(view.GetResult());
                }
            }
        });
        alert.setNegativeButton("Cancel", new OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (cancelHandler != null) {
                    cancelHandler.Do(view.GetResult());
                }
            }
        });
        alert.create().show();
    }
}
