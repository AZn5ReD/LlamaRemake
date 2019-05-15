package com.kebab.Llama;

import android.app.Activity;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.kebab.ApiCompat.IntentCompat;
import com.kebab.AppendableCharSequence;
import com.kebab.IterableHelpers;
import com.kebab.Llama.Instances.HelloableListActivity;
import com.kebab.TextEntryDialog;
import com.kebab.TextEntryDialog.ButtonHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class LauncherShortcutActivity extends HelloableListActivity {
    private static String CUSTOM_EVENT;
    private static String EVENT_PREFIX;
    private static String PROFILE_PREFIX;
    SimpleAdapter _Adapter;
    ArrayList<HashMap<String, String>> _Data = new ArrayList();

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        PROFILE_PREFIX = getString(R.string.hrProfile) + " - ";
        EVENT_PREFIX = getString(R.string.hrEvent) + " - ";
        CUSTOM_EVENT = getString(R.string.hrCustomEventActions);
        this._Adapter = new SimpleAdapter(this, this._Data, 17367053, new String[]{"line1", "line2"}, new int[]{16908308, 16908309}) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                TextView tv2 = (TextView) v.findViewById(16908309);
                ((TextView) v.findViewById(16908308)).setTextColor(Integer.parseInt((String) ((HashMap) getItem(position)).get("colour")));
                return v;
            }
        };
        setTitle(R.string.hrCreateLlamaShortcut);
        setListAdapter(this._Adapter);
        Update();
    }

    /* Access modifiers changed, original: protected */
    public void onListItemClick(ListView l, View v, int position, long id) {
        final String shortcutType;
        final String shortcutTargetName;
        super.onListItemClick(l, v, position, id);
        String selectedItemName = (String) ((HashMap) this._Data.get(position)).get("line1");
        if (selectedItemName.startsWith(PROFILE_PREFIX)) {
            shortcutType = "Profile";
            shortcutTargetName = selectedItemName.substring(PROFILE_PREFIX.length());
        } else if (selectedItemName.startsWith(EVENT_PREFIX)) {
            shortcutType = "Event";
            shortcutTargetName = selectedItemName.substring(EVENT_PREFIX.length());
        } else if (selectedItemName.startsWith(CUSTOM_EVENT)) {
            createCustomEvent();
            return;
        } else {
            return;
        }
        TextEntryDialog.Show((Activity) this, getString(R.string.hrEnterALabelForTheShortcut), shortcutTargetName, new ButtonHandler() {
            public void Do(String result) {
                if (result.length() == 0) {
                    result = shortcutTargetName;
                }
                LauncherShortcutActivity.this.CreateIntentAndFinish(shortcutType, shortcutTargetName, result);
            }
        });
    }

    /* Access modifiers changed, original: 0000 */
    public void CreateIntentAndFinish(String shortcutType, String shortcutData, String shortcutName) {
        Intent shortcutIntent = new Intent(Constants.ACTION_RUN_SHORTCUT);
        shortcutIntent.setClassName(this, LauncherShortcutRunnerActivity.class.getName());
        shortcutIntent.putExtra(Constants.EXTRA_LLAMA_SHORTCUT_TYPE, shortcutType);
        shortcutIntent.putExtra(Constants.EXTRA_LLAMA_SHORTCUT_DATA, shortcutData);
        shortcutIntent.addFlags(8388608);
        shortcutIntent.addFlags(1073741824);
        if (IntentCompat.SupportsClearTask()) {
            shortcutIntent.addFlags(268435456);
            shortcutIntent.addFlags(IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
        }
        Intent intent = new Intent();
        intent.putExtra("android.intent.extra.shortcut.INTENT", shortcutIntent);
        intent.putExtra("android.intent.extra.shortcut.NAME", shortcutName);
        intent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(this, R.drawable.icon));
        setResult(-1, intent);
        finish();
    }

    private void createCustomEvent() {
        Intent settingsActivity = new Intent(getBaseContext(), EventEditActivity.class);
        settingsActivity.putExtra("Event", new Event(getString(R.string.hrCustomEvent)));
        settingsActivity.putExtra(Constants.EXTRA_ANONYMOUS_EVENT, true);
        startActivityForResult(settingsActivity, Constants.REQUEST_CODE_ADD_EVENT_ACTION);
    }

    /* Access modifiers changed, original: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.REQUEST_CODE_ADD_EVENT_ACTION /*207*/:
                if (resultCode == -1) {
                    Event editedEvent = (Event) data.getParcelableExtra("Event");
                    if (editedEvent._Actions.size() > 0) {
                        CreateIntentAndFinish(Constants.SHORTCUT_TYPE_ANONYMOUS_EVENT, editedEvent.ToPsv(), editedEvent.Name);
                        return;
                    }
                    return;
                }
                return;
            default:
                return;
        }
    }

    public void Update() {
        if (Instances.HasServiceOrRestart(getApplicationContext())) {
            HashMap<String, String> map;
            Iterable<Profile> profiles = Instances.Service.GetProfiles();
            Iterable<Event> events = Instances.Service.GetEvents();
            Iterable<Profile> profilesCopy = IterableHelpers.OrderBy(profiles, Profile.NameComparator);
            Iterable<Event> eventsCopy = IterableHelpers.OrderBy(events, Event.NameComparator);
            String c1 = String.valueOf(Color.rgb(220, 30, 30));
            String c2 = String.valueOf(Color.rgb(60, 200, 235));
            String c3 = String.valueOf(Color.rgb(60, 213, 60));
            this._Data.clear();
            for (Profile p : profilesCopy) {
                map = new HashMap();
                map.put("line1", PROFILE_PREFIX + p.Name);
                map.put("line2", String.format(getString(R.string.hrSwitchToProfile1), new Object[]{p.Name}));
                map.put("colour", c1);
                this._Data.add(map);
            }
            HashMap<String, String> map2 = new HashMap();
            map2.put("line1", CUSTOM_EVENT);
            map2.put("line2", getString(R.string.hrSpecifyACustomSetOfActionsForTheShortcut));
            map2.put("colour", c2);
            this._Data.add(map2);
            StringBuilder sb = new StringBuilder();
            AppendableCharSequence sbWrapped = AppendableCharSequence.Wrap(sb);
            for (Event e : eventsCopy) {
                sb.setLength(0);
                try {
                    e.AppendEventActionDescription(this, sbWrapped, false);
                    if (sb.length() > 0) {
                        sb.setCharAt(0, ("" + sb.charAt(0)).toUpperCase().charAt(0));
                    }
                    map = new HashMap();
                    map.put("line1", EVENT_PREFIX + e.Name);
                    map.put("line2", sb.toString());
                    map.put("colour", c3);
                    this._Data.add(map);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            this._Adapter.notifyDataSetChanged();
        }
    }
}
