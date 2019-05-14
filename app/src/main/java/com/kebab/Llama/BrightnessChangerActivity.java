package com.kebab.Llama;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class BrightnessChangerActivity extends Activity {
    static final String EXTRA_AUTO = "auto";
    static final String EXTRA_BRIGHTNESS_PERCENT = "brightnessPercent";

    public static void StartActivity(Context context, boolean auto, int brightnessPercent) {
        Instances.FinishAllActivities();
        if (Instances.UiActivity != null) {
            Instances.UiActivity.finish();
        }
        Intent intent = new Intent(context, BrightnessChangerActivity.class);
        intent.addFlags(268435456);
        intent.addFlags(8388608);
        intent.addFlags(1073741824);
        intent.putExtra(EXTRA_AUTO, auto);
        intent.putExtra(EXTRA_BRIGHTNESS_PERCENT, brightnessPercent);
        context.startActivity(intent);
    }

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Intent intent = getIntent();
        if ((intent.getFlags() & 1048576) == 1048576) {
            if (Instances.UiActivity != null) {
                Instances.UiActivity.finish();
            }
            finish();
            return;
        }
        requestWindowFeature(1);
        TextView tv = new TextView(this);
        tv.setTextColor(-1);
        tv.setText("Llama is changing the brightness!");
        tv.setPadding(30, 30, 30, 30);
        tv.setGravity(17);
        addContentView(tv, new LayoutParams(-1, -1));
        final boolean auto = intent.getBooleanExtra(EXTRA_AUTO, false);
        final int brightnessPercent = intent.getIntExtra(EXTRA_BRIGHTNESS_PERCENT, 50);
        Instances.FinishAllActivities();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Instances.Service.ChangeBrightness(auto, brightnessPercent, BrightnessChangerActivity.this, false);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                if (Instances.UiActivity != null) {
                                    Instances.UiActivity.finish();
                                }
                                BrightnessChangerActivity.this.finish();
                            }
                        }, 500);
                    }
                }, 500);
            }
        }, 500);
    }
}
