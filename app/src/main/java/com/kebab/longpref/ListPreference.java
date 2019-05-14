package com.kebab.longpref;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class ListPreference extends android.preference.ListPreference {
    public ListPreference(Context context) {
        super(context);
    }

    public ListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* Access modifiers changed, original: protected */
    public void onBindView(View view) {
        super.onBindView(view);
        CheckBoxPreference.FixPrefView(view);
    }
}
