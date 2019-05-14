package com.kebab.Llama;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.kebab.DateHelpers;
import com.kebab.Llama.Instances.HelloableListActivity;
import java.util.ArrayList;
import java.util.List;

public class CalendarDebugActivity extends HelloableListActivity {
    BaseAdapter _Adapter;
    List<CalendarItem> _Data = new ArrayList();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this._Adapter = new BaseAdapter() {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v;
                String attending;
                String privacy;
                if (convertView == null) {
                    v = View.inflate(CalendarDebugActivity.this, R.layout.calendar_debug_list_item, null);
                } else {
                    v = convertView;
                }
                CalendarItem item = (CalendarItem) getItem(position);
                switch (item.AttendingStatus) {
                    case 0:
                        attending = "accepted";
                        break;
                    case 1:
                        attending = "tentative";
                        break;
                    case 2:
                        attending = "declined";
                        break;
                    case 4:
                        attending = "attend unknown";
                        break;
                    default:
                        attending = "?a?";
                        break;
                }
                switch (item.Privacy) {
                    case 0:
                        privacy = "default privacy";
                        break;
                    case 1:
                        privacy = "public";
                        break;
                    case 2:
                        privacy = "private";
                        break;
                    default:
                        privacy = "?p?";
                        break;
                }
                ((TextView) v.findViewById(R.id.text1)).setText(item.Name);
                ((TextView) v.findViewById(R.id.text2)).setText(DateHelpers.FormatDateWithYear(item.Start) + " -\n" + DateHelpers.FormatDateWithYear(item.End));
                ((TextView) v.findViewById(R.id.text3)).setText(item.IsAllDay ? "all day" : "timed");
                ((TextView) v.findViewById(R.id.text4)).setText(item.ShowAsAvailable ? "available" : "busy");
                ((TextView) v.findViewById(R.id.text5)).setText(item.CalendarName);
                ((TextView) v.findViewById(R.id.text6)).setText(privacy);
                ((TextView) v.findViewById(R.id.text7)).setText(attending);
                return v;
            }

            public int getCount() {
                return CalendarDebugActivity.this._Data.size();
            }

            public Object getItem(int position) {
                return CalendarDebugActivity.this._Data.get(position);
            }

            public long getItemId(int position) {
                return (long) position;
            }
        };
        getListView().setAdapter(this._Adapter);
        setTitle(getString(R.string.hrCalendarDebugTitle1, new Object[]{Integer.valueOf(5)}));
    }

    public void onDestroy() {
        this._Data = null;
        this._Adapter = null;
        getListView().setAdapter(null);
        super.onDestroy();
    }

    /* Access modifiers changed, original: protected */
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }

    public void onResume() {
        super.onResume();
        if (Instances.Service != null) {
            Instances.Service.ForceCalendarLoad();
            Update();
        }
    }

    public void Update() {
        if (Instances.HasServiceOrRestart(getApplicationContext())) {
            this._Data = Instances.Service.GetCurrentCalendarItems();
            this._Adapter.notifyDataSetChanged();
        }
    }
}
