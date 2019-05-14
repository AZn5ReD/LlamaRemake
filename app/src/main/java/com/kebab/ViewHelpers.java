package com.kebab;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class ViewHelpers {
    public static void HideIme(Context context, View paramView) {
        ((InputMethodManager) context.getSystemService("input_method")).hideSoftInputFromWindow(paramView.getWindowToken(), 0);
    }
}
