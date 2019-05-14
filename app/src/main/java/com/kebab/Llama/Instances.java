package com.kebab.Llama;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.kebab.Llama.LlamaListTabBase.LlamaListTabBaseImpl;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class Instances {
    static ArrayList<WeakReference<Activity>> AllActivities = new ArrayList();
    public static AreasActivity AreasActivity;
    public static CellsActivity CellsActivity;
    public static LlamaListTabBaseImpl CurrentTab;
    public static EventHistoryActivity EventHistoryActivity;
    public static EventsActivity EventsActivity;
    public static ProfilesActivity ProfilesActivity;
    public static LlamaService Service;
    public static LlamaUi UiActivity;

    public static class HelloableListActivity extends ListActivity {
        /* Access modifiers changed, original: protected */
        public void onCreate(Bundle savedInstanceState) {
            Instances.ActivitySaysHello(this);
            super.onCreate(savedInstanceState);
        }

        /* Access modifiers changed, original: protected */
        public void onDestroy() {
            super.onDestroy();
            Instances.ActivitySaysBye(this);
        }
    }

    public static class HelloableActivity extends Activity {
        /* Access modifiers changed, original: protected */
        public void onCreate(Bundle savedInstanceState) {
            Instances.ActivitySaysHello(this);
            super.onCreate(savedInstanceState);
        }

        /* Access modifiers changed, original: protected */
        public void onDestroy() {
            super.onDestroy();
            Instances.ActivitySaysBye(this);
        }
    }

    public static class HelloablePreferenceActivity extends PreferenceActivity {
        /* Access modifiers changed, original: protected */
        public void onCreate(Bundle savedInstanceState) {
            Instances.ActivitySaysHello(this);
            super.onCreate(savedInstanceState);
        }

        /* Access modifiers changed, original: protected */
        public void onDestroy() {
            super.onDestroy();
            Instances.ActivitySaysBye(this);
        }
    }

    public static boolean HasServiceOrRestart(Context context) {
        if (Service != null) {
            return true;
        }
        StartService(context);
        return false;
    }

    public static LlamaService GetServiceOrRestart(Context context) {
        if (Service != null) {
            return Service;
        }
        StartService(context);
        return null;
    }

    public static void StartService(Context context) {
        Intent svc = new Intent(context, LlamaService.class);
        svc.putExtra(Constants.INTENT_FROM_UI, true);
        context.startService(svc);
    }

    public static void ActivitySaysHello(Activity a) {
    }

    public static void ActivitySaysBye(Activity a) {
    }

    public static void FinishAllActivities() {
    }
}
