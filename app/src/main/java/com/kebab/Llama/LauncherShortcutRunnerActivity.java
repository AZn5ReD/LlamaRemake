package com.kebab.Llama;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.kebab.Helpers;
import com.kebab.Llama.Instances.HelloableActivity;

public class LauncherShortcutRunnerActivity extends HelloableActivity {
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Intent intent = getIntent();
        String action = intent.getAction();
        if (Constants.ACTION_RUN_SHORTCUT.equals(action)) {
            LlamaService service = Instances.Service;
            if (service != null) {
                service.HandleLlamaShortcut(intent, this);
            } else if (((Boolean) LlamaSettings.LlamaWasExitted.GetValue(this)).booleanValue()) {
                Helpers.ShowTip((Context) this, getString(R.string.hrTheLlamaServiceMustBeRunningForShortcutsToWork));
            } else {
                intent.setClass(this, LlamaService.class);
                startService(intent);
            }
            PostDelayedQuit();
        } else if (!"android.nfc.action.TAG_DISCOVERED".equals(action) && !"android.nfc.action.NDEF_DISCOVERED".equals(action) && !"android.nfc.action.TECH_DISCOVERED".equals(action)) {
        } else {
            if (((Boolean) LlamaSettings.LlamaWasExitted.GetValue(this)).booleanValue()) {
                finish();
                return;
            }
            Logging.Report("Got NFC intent - " + action, (Context) this);
            if (Instances.Service == null) {
                intent.setClass(this, LlamaService.class);
                startService(intent);
            } else {
                Instances.Service.HandleNfcIntent(intent);
            }
            finish();
        }
    }

    private void PostDelayedQuit() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Instances.FinishAllActivities();
                if (Instances.UiActivity != null) {
                    Instances.UiActivity.finish();
                }
                LauncherShortcutRunnerActivity.this.finish();
            }
        }, 100);
    }
}
