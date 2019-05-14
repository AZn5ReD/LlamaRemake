package com.kebab.longpref;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class CheckBoxPreference extends android.preference.CheckBoxPreference {
    public CheckBoxPreference(Context context) {
        super(context);
    }

    public CheckBoxPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckBoxPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /* Access modifiers changed, original: protected */
    public void onBindView(View view) {
        super.onBindView(view);
        FixPrefView(view);
    }

    public static void FixPrefView(View view) {
        ((TextView) view.findViewById(16908304)).setMaxLines(100);
    }
}
