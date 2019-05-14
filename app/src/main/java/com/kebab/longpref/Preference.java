package com.kebab.longpref;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class Preference extends android.preference.Preference {
    public Preference(Context context) {
        super(context);
    }

    public Preference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Preference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /* Access modifiers changed, original: protected */
    public void onBindView(View view) {
        super.onBindView(view);
        CheckBoxPreference.FixPrefView(view);
    }
}
