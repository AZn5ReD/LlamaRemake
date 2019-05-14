package com.kebab;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TextEntryDialog {

    public interface ButtonHandler {
        void Do(String str);
    }

    public static void Show(Activity activity, String message, ButtonHandler okHandler) {
        Show(activity, message, null, okHandler, null);
    }

    public static void Show(Activity activity, String message, ButtonHandler okHandler, int inputType) {
        Show(activity, message, null, okHandler, null, inputType);
    }

    public static void Show(Activity activity, String message, String originalValue, ButtonHandler okHandler) {
        Show(activity, message, originalValue, okHandler, null);
    }

    public static void Show(Activity activity, String message, ButtonHandler okHandler, ButtonHandler cancelHandler) {
        Show(activity, message, null, okHandler, cancelHandler);
    }

    public static void Show(Activity activity, String message, String originalValue, ButtonHandler okHandler, ButtonHandler cancelHandler) {
        Show(activity, message, originalValue, okHandler, cancelHandler, 16385);
    }

    @SuppressLint("WrongConstant")
    public static void Show(Activity activity, String message, String originalValue, final ButtonHandler okHandler, ButtonHandler cancelHandler, int inputType) {
        View mainView;
        Builder alert = new AlertDialogEx.Builder(activity);
        final View input = new EditText(activity);
        ((EditText) input).setSelectAllOnFocus(true);
        ((EditText) input).setInputType(inputType);
        if (message == null || message.length() <= 50) {
            alert.setTitle(message);
            mainView = input;
        } else {
            View layout = new LinearLayout(activity);
            ((LinearLayout) layout).setOrientation(1);
            layout.setLayoutParams(new LayoutParams(-1, -2));
            TextView messageView = new TextView(activity);
            messageView.setLayoutParams(new LayoutParams(-1, -2));
            messageView.setText(message);
            messageView.setTextSize(18.0f);
            ((LinearLayout) layout).addView(messageView);
            input.setLayoutParams(new LayoutParams(-1, -2));
            ((LinearLayout) layout).addView(input);
            mainView = layout;
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(new Rect());
        }
        mainView.setLayoutParams(new LayoutParams(-1, -2));
        alert.setView(mainView);
        alert.setPositiveButton("Ok", new OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (okHandler != null) {
                    okHandler.Do(((EditText) input).getText().toString().trim());
                }
            }
        });
        final ButtonHandler buttonHandler = cancelHandler;
        alert.setNegativeButton("Cancel", new OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (buttonHandler != null) {
                    buttonHandler.Do(((EditText) input).getText().toString().trim());
                }
            }
        });
        Dialog dialog = alert.create();
        if (originalValue != null) {
            ((EditText) input).setText(originalValue);
        }
        dialog.setOwnerActivity(activity);
        dialog.getWindow().setSoftInputMode(32);
        dialog.getWindow().setSoftInputMode(37);
        dialog.show();
    }
}
