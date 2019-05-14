package com.kebab.Llama;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.kebab.DateHelpers;
import com.kebab.Llama.Instances.HelloableListActivity;
import java.util.ArrayList;
import java.util.HashMap;

public class EventHistoryActivity extends HelloableListActivity {
    SimpleAdapter _Adapter;
    HashMap<Cell, ArrayList<String>> _CachedCellToToAreaMap;
    ArrayList<Cell> _CachedCells = new ArrayList();
    Cell _ContextMenuCell;
    ArrayList<HashMap<String, String>> _Data;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Instances.EventHistoryActivity = this;
        setTitle(R.string.hrLlamaDashEventHistory);
        this._Data = new ArrayList();
        this._Adapter = new SimpleAdapter(this, this._Data, 17367053, new String[]{"line1", "line2"}, new int[]{16908308, 16908309});
        setListAdapter(this._Adapter);
        Update();
    }

    public void onResume() {
        Thread.currentThread().setPriority(5);
        super.onResume();
        Update();
    }

    public void onPause() {
        super.onPause();
        Thread.currentThread().setPriority(1);
    }

    public void onDestroy() {
        Instances.EventHistoryActivity = null;
        super.onDestroy();
    }

    /* Access modifiers changed, original: 0000 */
    public void Update() {
        if (!Instances.HasServiceOrRestart(this)) {
            return;
        }
        if (LlamaService.IsOnWorkerThread()) {
            runOnUiThread(new Runnable() {
                public void run() {
                    EventHistoryActivity.this.Update();
                }
            });
            return;
        }
        HashMap<String, String> metaMap = new HashMap();
        for (SimpleEventTrigger meta : EventMeta.AllTriggers.values()) {
            metaMap.put(meta.getEventTriggerReasonId(), meta.GetName(this));
        }
        for (EventMeta meta2 : EventMeta.All.values()) {
            metaMap.put(meta2.Id, meta2.Name);
        }
        Iterable<EventHistory> datas = Instances.Service.GetEventHistory();
        this._Data.clear();
        for (EventHistory eventHistory : datas) {
            String secondLine;
            HashMap<String, String> map = new HashMap();
            String triggerName = (String) metaMap.get(eventHistory.TriggerType);
            if (triggerName == null || triggerName.equals(EventFragment.SIMPLE_TRIGGER_NOT_APPLICABLE)) {
                triggerName = getString(R.string.hrUnknown);
            }
            map.put("line1", DateHelpers.FormatDate(eventHistory.TriggerTime) + " - " + eventHistory.EventName);
            switch (eventHistory.EventHistoryType) {
                case 10:
                    secondLine = String.format(getString(R.string.hrDelayTriggeredBy1Condition), new Object[]{triggerName});
                    break;
                case 11:
                    secondLine = String.format(getString(R.string.hrConfirmationTriggeredBy1Condition), new Object[]{triggerName});
                    break;
                case 12:
                    secondLine = getString(R.string.hrEventRepeatingCancelled);
                    break;
                case 13:
                    secondLine = getString(R.string.hrEventDelayCancelled);
                    break;
                case 14:
                    secondLine = getString(R.string.hrConfirmationDeniedTriggerName);
                    break;
                case 20:
                    secondLine = String.format(getString(R.string.hrTrigger1Prohibitted), new Object[]{triggerName});
                    break;
                default:
                    secondLine = String.format(getString(R.string.hrTriggeredBy1Condition), new Object[]{triggerName});
                    break;
            }
            map.put("line2", secondLine);
            this._Data.add(map);
        }
        this._Adapter.notifyDataSetChanged();
    }

    /* Access modifiers changed, original: protected */
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String selectedEventName = (String) ((HashMap) this._Data.get(position)).get("line1");
        EditEvent(selectedEventName.substring(selectedEventName.indexOf("-") + 2));
    }

    private void EditEvent(String selectedEventName) {
        Event event = Instances.Service.GetEventByName(selectedEventName);
        if (event != null) {
            Intent settingsActivity = new Intent(getBaseContext(), EventEditActivity.class);
            settingsActivity.putExtra("Event", event);
            startActivityForResult(settingsActivity, Constants.REQUEST_CODE_EDIT_EVENT_ACTION);
        }
    }

    /* Access modifiers changed, original: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.REQUEST_CODE_EDIT_EVENT_ACTION /*205*/:
                EventsActivity.HandleEventEditResult(resultCode, data, this, Instances.Service);
                return;
            default:
                return;
        }
    }
}
