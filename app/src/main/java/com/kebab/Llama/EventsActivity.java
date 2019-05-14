package com.kebab.Llama;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableStringBuilder;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import com.kebab.AlertDialogEx.Builder;
import com.kebab.AppendableCharSequence;
import com.kebab.DateHelpers;
import com.kebab.Helpers;
import com.kebab.IterableHelpers;
import com.kebab.Llama.EventActions.EventAction;
import com.kebab.Llama.EventConditions.EventCondition;
import com.kebab.Llama.LlamaListTabBase.LlamaListTabBaseImpl;
import com.kebab.Llama.LlamaListTabBase.LlamaListTabInterface;
import com.kebab.Ref;
import com.kebab.TextEntryDialog;
import com.kebab.TextEntryDialog.ButtonHandler;
import com.kebab.Tuple3Mutable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class EventsActivity extends ExpandableListActivity implements LlamaListTabInterface {
    private static String DISABLE_EVENT_PREFIX;
    public static String[] _RandomTips;
    BaseExpandableListAdapter _Adapter;
    private volatile long _CurrentThreadId;
    ArrayList<ExpandyGroup> _Data = new ArrayList();
    Object _EventColourLock = new Object();
    ImageButton _FilterButton;
    HashSet<String> _FilterFragmentIds = new HashSet();
    String _FilterText;
    String _FilterTextLower;
    boolean _GroupedView = true;
    LlamaListTabBaseImpl _Impl;

    static class ExpandyGroup extends ArrayList<Tuple3Mutable<String, CharSequence, Event>> {
        public String GroupName;

        public static Comparator<? super ExpandyGroup> NameComparator(final String noGroupString) {
            return new Comparator<ExpandyGroup>() {
                public int compare(ExpandyGroup x, ExpandyGroup y) {
                    int comparison = x.GroupName.compareToIgnoreCase(y.GroupName);
                    if (comparison == 0) {
                        return 0;
                    }
                    if (x.GroupName.equals(noGroupString)) {
                        return 1;
                    }
                    if (y.GroupName.equals(noGroupString)) {
                        return -1;
                    }
                    return comparison;
                }
            };
        }

        public ExpandyGroup(String name) {
            this.GroupName = name;
        }
    }

    public void SetImpl(LlamaListTabBaseImpl impl) {
        this._Impl = impl;
        this._Impl._Activity = this;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this._Impl.onCreate(savedInstanceState);
        Instances.EventsActivity = this;
        DISABLE_EVENT_PREFIX = "(" + getString(R.string.hrDisabled) + ") ";
        this._Adapter = new BaseExpandableListAdapter() {
            public Object getChild(int group, int item) {
                return ((ExpandyGroup) EventsActivity.this._Data.get(group)).get(item);
            }

            public long getChildId(int paramInt1, int paramInt2) {
                return 0;
            }

            public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
                View v;
                if (convertView == null) {
                    v = View.inflate(EventsActivity.this, R.layout.two_line_listitem_with_border, null);
                } else {
                    v = convertView;
                }
                Tuple3Mutable<String, CharSequence, Event> map = (Tuple3Mutable) getChild(groupPosition, childPosition);
                if (map.Item2 == null) {
                    CharSequence plainEventDescription = EventsActivity.this.GetEventDescription((Event) map.Item3, 0, null, 0, 0);
                    synchronized (EventsActivity.this._EventColourLock) {
                        if (map.Item2 == null) {
                            map.Item2 = plainEventDescription;
                        }
                    }
                }
                ((TextView) v.findViewById(R.id.text1)).setText((CharSequence) map.Item1);
                ((TextView) v.findViewById(R.id.text2)).setText((CharSequence) map.Item2, BufferType.SPANNABLE);
                return v;
            }

            public int getChildrenCount(int group) {
                return ((ExpandyGroup) EventsActivity.this._Data.get(group)).size();
            }

            public Object getGroup(int group) {
                return EventsActivity.this._Data.get(group);
            }

            public int getGroupCount() {
                return EventsActivity.this._Data.size();
            }

            public long getGroupId(int paramInt) {
                return 0;
            }

            public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
                View v;
                if (!EventsActivity.this._GroupedView || getGroupCount() <= 1) {
                    if (convertView == null || convertView.getTag() != null) {
                        v = View.inflate(EventsActivity.this, R.layout.expandy_group_empty, null);
                    } else {
                        v = convertView;
                    }
                    v.setTag(null);
                    if (!isExpanded) {
                        EventsActivity.this.getExpandableListView().expandGroup(groupPosition);
                    }
                } else {
                    if (convertView == null || convertView.getTag() == null) {
                        v = View.inflate(EventsActivity.this, R.layout.expandy_group_item, null);
                    } else {
                        v = convertView;
                    }
                    v.setTag(EventsActivity.this._EventColourLock);
                    ((TextView) v.findViewById(R.id.text)).setText(((ExpandyGroup) getGroup(groupPosition)).GroupName);
                }
                return v;
            }

            public boolean hasStableIds() {
                return false;
            }

            public boolean isChildSelectable(int paramInt1, int paramInt2) {
                return true;
            }
        };
        setListAdapter(this._Adapter);
        registerForContextMenu(getExpandableListView());
        ((ImageButton) findViewById(R.id.addButton)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                EventsActivity.this.AddNewEvent();
            }
        });
        ((ImageButton) findViewById(R.id.historyButton)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                EventsActivity.this.ViewEventHistory();
            }
        });
        this._FilterButton = (ImageButton) findViewById(R.id.filterButton);
        this._FilterButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                EventsActivity.this.ShowFilters();
            }
        });
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        this._Impl.onCreateContextMenu(menu, v, menuInfo);
        ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) menuInfo;
        int child = ExpandableListView.getPackedPositionChild(info.packedPosition);
        int group = ExpandableListView.getPackedPositionGroup(info.packedPosition);
        if (child == -1) {
            menu.add(0, 23, 0, R.string.hrEnableAll);
            menu.add(0, 24, 0, R.string.hrDisableAll);
            menu.add(0, 25, 0, R.string.hrRenameGroup);
            return;
        }
        menu.add(0, 12, 0, R.string.hrEditEvent);
        menu.add(0, 18, 0, R.string.hrCopyEvent);
        menu.add(0, Constants.MENU_TEST_ACTIONS, 0, R.string.hrRunEventActions);
        if (((CharSequence) ((Tuple3Mutable) ((ExpandyGroup) this._Data.get(group)).get(child)).Item2).toString().startsWith(DISABLE_EVENT_PREFIX)) {
            menu.add(0, Constants.MENU_ENABLE_ITEM, 0, R.string.hrEnableEvent);
        } else {
            menu.add(0, Constants.MENU_DISABLE_ITEM, 0, R.string.hrDisableEvent);
        }
        menu.add(0, 13, 0, R.string.hrDeleteEvent);
        menu.add(0, 26, 0, R.string.hrShareEvent);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return this._Impl.onOptionsItemSelected(item);
    }

    public void onPause() {
        super.onPause();
        this._Impl.onPause();
        this._CurrentThreadId = 0;
        Instances.EventsActivity = null;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        this._Impl.onPrepareOptionsMenu(menu);
        super.onPrepareOptionsMenu(menu);
        Update();
        this._Adapter.notifyDataSetChanged();
        return true;
    }

    public void onResume() {
        super.onResume();
        this._Impl.onResume();
        Instances.EventsActivity = this;
        if (Instances.Service != null) {
            Update();
        }
        if (this._FilterFragmentIds.size() > 0) {
            Helpers.ShowTip((Context) this, getString(R.string.hrYouHaveFiltersActive));
        }
    }

    public EventsActivity() {
        SetImpl(new LlamaListTabBaseImpl(R.layout.tab_events, LlamaSettings.HelpEvents, R.string.hrHelpEvents) {
            public void Update() {
                EventsActivity.this.Update();
            }

            /* Access modifiers changed, original: protected */
            public String[] InitAndGetTabRandomTips() {
                return EventsActivity.this.InitAndGetTabRandomTips();
            }

            /* Access modifiers changed, original: protected */
            public CharSequence[] getContextSensitiveMenuItems() {
                return EventsActivity.this.getContextSensitiveMenuItems();
            }

            /* Access modifiers changed, original: protected */
            public boolean handleContextSensitiveItem(CharSequence menu) {
                return EventsActivity.this.handleContextSensitiveItem(menu);
            }
        });
    }

    /* Access modifiers changed, original: protected */
    public void ShowFilters() {
        LlamaService service = Instances.GetServiceOrRestart(this);
        if (service != null) {
            EventMeta meta;
            ArrayList<EventMeta> metas = new ArrayList(EventMeta.All.size());
            HashSet<String> eventIds = service.GetEventTypes();
            for (EventMeta meta2 : EventMeta.All.values()) {
                if (!meta2.IsCompatibility && eventIds.contains(meta2.Id)) {
                    metas.add(meta2);
                }
            }
            Collections.sort(metas, EventMeta.TypeThenNameComparer);
            CharSequence[] allFragmentNames = new CharSequence[metas.size()];
            final String[] allFragmentId = new String[metas.size()];
            boolean[] selectedItems = new boolean[metas.size()];
            int index = 0;
            Iterator i$ = metas.iterator();
            while (i$.hasNext()) {
                EventMeta meta2 = (EventMeta) i$.next();
                allFragmentNames[index] = (meta2.IsCondition ? getString(R.string.hrConditionAbbr) : getString(R.string.hrActionAbbr)) + ": " + meta2.Name;
                allFragmentId[index] = meta2.Id;
                if (this._FilterFragmentIds.contains(meta2.Id)) {
                    selectedItems[index] = true;
                }
                index++;
            }
            final Ref<Dialog> dialogReference = new Ref();
            View titleView = View.inflate(this, R.layout.custom_title_two_buttons, null);
            ((TextView) titleView.findViewById(R.id.text)).setText(R.string.hrFilterViewSearch);
            ((ImageButton) titleView.findViewById(R.id.searchButton)).setOnClickListener(new OnClickListener() {
                public void onClick(View paramView) {
                    EventsActivity.this.StartSearch(new Runnable() {
                        public void run() {
                            ((Dialog) dialogReference.Value).dismiss();
                        }
                    });
                }
            });
            ((ImageButton) titleView.findViewById(R.id.groupButton)).setOnClickListener(new OnClickListener() {
                public void onClick(View paramView) {
                    EventsActivity.this.ToggleGrouping();
                    ((Dialog) dialogReference.Value).dismiss();
                }
            });
            Dialog d = new Builder(this).setTitle(R.string.hrFilterViewSearch).setMultiChoiceItems(allFragmentNames, selectedItems, new OnMultiChoiceClickListener() {
                public void onClick(DialogInterface dialog, int position, boolean isChecked) {
                    if (isChecked) {
                        EventsActivity.this._FilterFragmentIds.add(allFragmentId[position]);
                    } else {
                        EventsActivity.this._FilterFragmentIds.remove(allFragmentId[position]);
                    }
                    EventsActivity.this.Update();
                }
            }).setPositiveButton(R.string.hrOkeyDoke, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setNegativeButton(R.string.hrReset, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    EventsActivity.this._FilterFragmentIds.clear();
                    EventsActivity.this._FilterText = null;
                    EventsActivity.this._FilterTextLower = null;
                    EventsActivity.this.Update();
                    dialog.dismiss();
                }
            }).setCustomTitle(titleView).create();
            dialogReference.Value = d;
            d.show();
        }
    }

    /* Access modifiers changed, original: protected */
    public void UpdateFilterButtonImage() {
        if (this._FilterFragmentIds.size() > 0 || (this._FilterText != null && this._FilterText.length() > 0)) {
            this._FilterButton.setImageDrawable(getResources().getDrawable(R.drawable.tb_filter_on));
        } else {
            this._FilterButton.setImageDrawable(getResources().getDrawable(R.drawable.tb_filter_off));
        }
    }

    /* Access modifiers changed, original: protected */
    public void StartSearch(final Runnable runnable) {
        TextEntryDialog.Show((Activity) this, getString(R.string.hrWhatEventDescriptionsShouldIShow), this._FilterText, new ButtonHandler() {
            public void Do(String result) {
                EventsActivity.this._FilterText = result;
                EventsActivity.this._FilterTextLower = result.toLowerCase();
                EventsActivity.this.Update();
                if (runnable != null) {
                    runnable.run();
                }
            }
        });
    }

    /* Access modifiers changed, original: protected */
    public void ToggleGrouping() {
        this._GroupedView = !this._GroupedView;
        if (this._GroupedView) {
            Helpers.ShowTip((Context) this, (int) R.string.hrEventsNowGroupedEditEventNamesToGroup);
        } else {
            Helpers.ShowTip((Context) this, (int) R.string.hrEventsNotGrouped);
        }
        Update();
    }

    public void onDestroy() {
        Instances.EventsActivity = null;
        super.onDestroy();
    }

    public boolean onContextItemSelected(MenuItem item) {
        ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item.getMenuInfo();
        int child = ExpandableListView.getPackedPositionChild(info.packedPosition);
        int group = ExpandableListView.getPackedPositionGroup(info.packedPosition);
        if (child == -1) {
            String selectedGroupName = ((ExpandyGroup) this._Data.get(group)).GroupName;
            if (selectedGroupName.equals(getString(R.string.hrNoGroupString))) {
                selectedGroupName = "";
            }
            switch (item.getItemId()) {
                case 23:
                    Instances.Service.SetGroupEnabled(selectedGroupName, true);
                    break;
                case 24:
                    Instances.Service.SetGroupEnabled(selectedGroupName, false);
                    break;
                case 25:
                    final String capturedOldName = selectedGroupName;
                    TextEntryDialog.Show((Activity) this, getString(R.string.hrPleaseEnterANewGroupName), selectedGroupName, new ButtonHandler() {
                        public void Do(String result) {
                            Instances.Service.RenameGroup(capturedOldName, result);
                        }
                    });
                    break;
            }
            return false;
        }
        final String selectedEventName = ((Tuple3Mutable) ((ExpandyGroup) this._Data.get(group)).get(child)).Item1.toString();
        switch (item.getItemId()) {
            case 12:
                EditEvent(selectedEventName);
                break;
            case 13:
                new Builder(this).setTitle(R.string.hrDeleteEvent).setCancelable(true).setMessage(String.format(getString(R.string.hrAreYouSureYouWantToDelete1), new Object[]{selectedEventName})).setPositiveButton(R.string.hrYes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Instances.Service.DeleteEventByName(selectedEventName);
                    }
                }).setNegativeButton(R.string.hrNo, null).show();
                break;
            case 18:
                CopyEvent(selectedEventName);
                break;
            case 26:
                LlamaService service = Instances.GetServiceOrRestart(this);
                if (service != null) {
                    Event e = service.GetEventByName(selectedEventName);
                    List<Event> list = new ArrayList();
                    list.add(e);
                    SocialLlama.ShareEvent(this, selectedEventName, list);
                    break;
                }
                break;
            case Constants.MENU_TEST_ACTIONS /*117*/:
                Instances.Service.RunSingleEvent(selectedEventName, false, (Activity) this, EventMeta.EventListTest, 3);
                break;
            case Constants.MENU_ENABLE_ITEM /*122*/:
                Instances.Service.SetEventEnabled(selectedEventName, true);
                break;
            case Constants.MENU_DISABLE_ITEM /*123*/:
                Instances.Service.SetEventEnabled(selectedEventName, false);
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void CopyEvent(String selectedEventName) {
        Event clonedEvent = Event.CreateFromPsv(Instances.Service.GetEventByName(selectedEventName).ToPsv());
        clonedEvent.Name += " - " + getString(R.string.hrCopy);
        while (Instances.Service.GetEventByName(clonedEvent.Name) != null) {
            clonedEvent.Name += " 2";
        }
        Instances.Service.AddEvent(clonedEvent);
        EditEvent(clonedEvent.Name);
    }

    public boolean onChildClick(ExpandableListView l, View v, int group, int position, long id) {
        boolean result = super.onChildClick(l, v, group, position, id);
        EditEvent((String) ((Tuple3Mutable) ((ExpandyGroup) this._Data.get(group)).get(position)).Item1);
        return true;
    }

    /* Access modifiers changed, original: 0000 */
    public void ViewEventHistory() {
        startActivity(new Intent(this, EventHistoryActivity.class));
    }

    public boolean onSearchRequested() {
        StartSearch(null);
        return false;
    }

    public void Update() {
        if (Instances.HasServiceOrRestart(getApplicationContext())) {
            LlamaService.ThreadComplainMustBeUi();
            Instances.Service.RunOnWorkerThreadThenUiThread((Activity) this, new LWork1<Iterable<Event>>() {
                /* Access modifiers changed, original: protected */
                public Iterable<Event> InWorkerThread() {
                    return IterableHelpers.ToArrayList(Instances.Service.GetEvents());
                }

                /* Access modifiers changed, original: protected */
                public void InUiThread(Iterable<Event> events) {
                    Iterable<Event> eventsCopy = IterableHelpers.OrderBy(events, Event.NameComparator);
                    String noGroupString = EventsActivity.this.getString(R.string.hrNoGroupString);
                    HashMap<String, ExpandyGroup> groupies = new HashMap();
                    EventsActivity.this._Data.clear();
                    for (Event e : eventsCopy) {
                        if (e.Type != 1 || ((Boolean) LlamaSettings.ShowAutoEvents.GetValue(EventsActivity.this)).booleanValue()) {
                            String groupName;
                            if (EventsActivity.this._FilterFragmentIds.size() > 0) {
                                boolean foundFilter = false;
                                Iterator i$ = e._Conditions.iterator();
                                while (i$.hasNext()) {
                                    if (EventsActivity.this._FilterFragmentIds.contains(((EventCondition) i$.next()).getId())) {
                                        foundFilter = true;
                                        break;
                                    }
                                }
                                i$ = e._Actions.iterator();
                                while (i$.hasNext()) {
                                    if (EventsActivity.this._FilterFragmentIds.contains(((EventAction) i$.next()).getId())) {
                                        foundFilter = true;
                                        break;
                                    }
                                }
                                if (!foundFilter) {
                                }
                            }
                            SpannableStringBuilder plainEventDescription = null;
                            if (!(EventsActivity.this._FilterTextLower == null || EventsActivity.this._FilterTextLower.length() <= 0 || e.Name.toLowerCase().contains(EventsActivity.this._FilterTextLower))) {
                                plainEventDescription = EventsActivity.this.GetEventDescription(e, 0, null, 0, 0);
                                if (!plainEventDescription.toString().toLowerCase().contains(EventsActivity.this._FilterTextLower)) {
                                }
                            }
                            if (!EventsActivity.this._GroupedView || e.GroupName.length() <= 0) {
                                groupName = noGroupString;
                            } else {
                                groupName = e.GroupName;
                            }
                            ExpandyGroup exp = (ExpandyGroup) groupies.get(groupName);
                            if (exp == null) {
                                exp = new ExpandyGroup(groupName);
                                EventsActivity.this._Data.add(exp);
                                groupies.put(groupName, exp);
                            }
                            Tuple3Mutable<String, CharSequence, Event> map = new Tuple3Mutable();
                            map.Item1 = e.Name;
                            map.Item2 = plainEventDescription;
                            map.Item3 = e;
                            exp.add(map);
                        }
                    }
                    Collections.sort(EventsActivity.this._Data, ExpandyGroup.NameComparator(noGroupString));
                    EventsActivity.this.StartUpdateEventDescriptionsInOtherThread();
                    EventsActivity.this._Adapter.notifyDataSetChanged();
                    EventsActivity.this.UpdateFilterButtonImage();
                }
            });
        }
    }

    private void StartUpdateEventDescriptionsInOtherThread() {
        final StateChange lastStateChange = Instances.Service.GetLastStateChange();
        Thread t = new Thread(new Runnable() {
            public void run() {
                EventsActivity.this.UpdateEventDescriptionsInOtherThread(lastStateChange);
            }
        });
        this._CurrentThreadId = t.getId();
        t.start();
    }

    private void UpdateEventDescriptionsInOtherThread(StateChange lastStateChange) {
        long meThread = Thread.currentThread().getId();
        ArrayList<ExpandyGroup> data = new ArrayList(this._Data);
        Handler uiThreadHandler = new Handler(Looper.getMainLooper());
        Runnable updateListRunnable = new Runnable() {
            public void run() {
                EventsActivity.this._Adapter.notifyDataSetChanged();
            }
        };
        int falseColour = LlamaSettings.GetColourNegative(this);
        int trueColour = LlamaSettings.GetColourPositive(this);
        for (int i = 0; i < data.size(); i++) {
            ExpandyGroup exp = (ExpandyGroup) data.get(i);
            for (int j = 0; j < exp.size(); j++) {
                if (this._CurrentThreadId != meThread) {
                    Logging.Report("EventsActivity background thread is unwanted :(", (Context) this);
                    break;
                }
                SpannableStringBuilder richTextBuilder = GetEventDescription((Event) ((Tuple3Mutable) exp.get(j)).Item3, ((Integer) LlamaSettings.ColourEventList.GetValue(this)).intValue(), lastStateChange, trueColour, falseColour);
                synchronized (this._EventColourLock) {
                    ((Tuple3Mutable) exp.get(j)).Item2 = richTextBuilder;
                }
                uiThreadHandler.removeCallbacks(updateListRunnable);
                uiThreadHandler.post(updateListRunnable);
            }
        }
    }

    private SpannableStringBuilder GetEventDescription(Event e, int eventColourMode, StateChange lastStateChange, int trueColour, int falseColour) {
        SpannableStringBuilder richTextBuilder = new SpannableStringBuilder();
        richTextBuilder.append(e.Enabled ? "" : DISABLE_EVENT_PREFIX);
        if (e.DelayedUntilMillis > 0) {
            richTextBuilder.append(String.format(getString(R.string.hrDelayedUntil1), new Object[]{DateHelpers.formatTimeNoSecondsIfZero(new Date(e.DelayedUntilMillis))})).append(" ");
        } else if (e.ConfirmationStatus == 2) {
            richTextBuilder.append(getString(R.string.hrAwaitingConfirmation)).append(" ");
        } else if (e.NextRepeatAtMillis > 0) {
            richTextBuilder.append(String.format(getString(R.string.hrRepeatingAt1), new Object[]{DateHelpers.formatTimeNoSecondsIfZero(new Date(e.NextRepeatAtMillis))})).append(" ");
        }
        int startPos = richTextBuilder.length();
        boolean needComma = false;
        Iterator i$ = e._Conditions.iterator();
        while (i$.hasNext()) {
            StateChange stateChange;
            EventCondition<?> ec = (EventCondition) i$.next();
            if (needComma) {
                richTextBuilder.append(" ");
            }
            if (eventColourMode != 0) {
                stateChange = lastStateChange;
                int start = richTextBuilder.length();
            } else {
                stateChange = null;
            }
            try {
                ec.AppendConditionDescription(this, richTextBuilder, stateChange, trueColour, falseColour);
            } catch (IOException e2) {
            }
            if (!needComma && richTextBuilder.length() > 0) {
                richTextBuilder.replace(startPos, startPos + 1, ("" + richTextBuilder.charAt(startPos)).toUpperCase());
            }
            needComma = true;
        }
        richTextBuilder.append(" - ");
        try {
            e.AppendEventActionDescription(getBaseContext(), AppendableCharSequence.Wrap(richTextBuilder), true);
            return richTextBuilder;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void EditEvent(String selectedEventName) {
        Intent settingsActivity = new Intent(getBaseContext(), EventEditActivity.class);
        settingsActivity.putExtra("Event", Instances.Service.GetEventByName(selectedEventName));
        settingsActivity.putExtra(Constants.EXTRA_IS_EDIT, true);
        startActivityForResult(settingsActivity, Constants.REQUEST_CODE_EDIT_EVENT_ACTION);
    }

    /* Access modifiers changed, original: 0000 */
    public void AddNewEvent() {
        int eventsCount = 0;
        Iterator i$ = this._Data.iterator();
        while (i$.hasNext()) {
            eventsCount += ((ExpandyGroup) i$.next()).size();
        }
        Intent settingsActivity = new Intent(getBaseContext(), EventEditActivity.class);
        settingsActivity.putExtra("Event", Event.CreateDefault(this, eventsCount));
        settingsActivity.putExtra(Constants.EXTRA_IS_EDIT, false);
        startActivityForResult(settingsActivity, Constants.REQUEST_CODE_ADD_EVENT_ACTION);
    }

    /* Access modifiers changed, original: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.REQUEST_CODE_EDIT_EVENT_ACTION /*205*/:
                HandleEventEditResult(resultCode, data, this, Instances.Service);
                return;
            case Constants.REQUEST_CODE_ADD_EVENT_ACTION /*207*/:
                if (resultCode == -1) {
                    Event editedEvent = (Event) data.getParcelableExtra("Event");
                    while (Instances.Service.GetEventByName(editedEvent.Name) != null) {
                        editedEvent.Name += " 2";
                    }
                    if (editedEvent._Actions.size() != 0 || editedEvent._Conditions.size() != 0) {
                        Instances.Service.AddEvent(editedEvent);
                        return;
                    }
                    return;
                }
                return;
            default:
                return;
        }
    }

    public static void HandleEventEditResult(int resultCode, final Intent data, final Activity activity, Context context) {
        if (resultCode == -1) {
            final Event editedEvent = (Event) data.getParcelableExtra("Event");
            if (editedEvent.RepeatMinutesInterval > 0 && editedEvent._Conditions.size() == 0) {
                editedEvent.NextRepeatAtMillis = 0;
            }
            Instances.Service.RunOnWorkerThreadThenUiThread(activity, new LWork0() {
                /* Access modifiers changed, original: protected */
                public void InWorkerThread() {
                    String oldName = data.getStringExtra(Constants.OLD_NAME);
                    if (!editedEvent.Name.equals(oldName)) {
                        while (Instances.Service.GetEventByName(editedEvent.Name) != null) {
                            StringBuilder stringBuilder = new StringBuilder();
                            Event event = editedEvent;
                            event.Name = stringBuilder.append(event.Name).append(" 2").toString();
                        }
                    }
                    Instances.Service.UpdateEvent(oldName, editedEvent);
                }

                /* Access modifiers changed, original: protected */
                public void InUiThread() {
                    if (editedEvent.RepeatMinutesInterval > 0 && editedEvent._Conditions.size() > 0) {
                        new Builder(activity).setMessage(R.string.hrRepeatedEventEditWarning).setPositiveButton(R.string.hrYes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Instances.Service.IdeallyRunOnWorkerThread(new X() {
                                    /* Access modifiers changed, original: 0000 */
                                    public void R() {
                                        Instances.Service.TriggerNamedEvent(editedEvent.Name);
                                    }
                                });
                            }
                        }).setNegativeButton(R.string.hrNo, null).show();
                    }
                }
            });
        }
    }

    /* Access modifiers changed, original: protected */
    public CharSequence[] getContextSensitiveMenuItems() {
        return new CharSequence[]{getString(R.string.hrVariables)};
    }

    /* Access modifiers changed, original: protected */
    public boolean handleContextSensitiveItem(CharSequence menu) {
        if (!getString(R.string.hrVariables).equals(menu)) {
            return false;
        }
        if (Instances.Service._Variables.size() > 0) {
            String[] items = new String[Instances.Service._Variables.size()];
            int count = 0;
            for (Entry<String, String> entry : Instances.Service._Variables.entrySet()) {
                items[count] = ((String) entry.getKey()) + " = " + ((String) entry.getValue());
                count++;
            }
            new Builder(this).setTitle(R.string.hrVariables).setItems(items, null).setPositiveButton(R.string.hrOkeyDoke, null).setNegativeButton(R.string.hrReset, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    new Builder(EventsActivity.this).setTitle(R.string.hrVariables).setMessage(R.string.hrAreYouSureYouWantToClearVariables).setPositiveButton(R.string.hrYes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Instances.Service.ClearVariables();
                        }
                    }).setNegativeButton(R.string.hrNo, null).show();
                }
            }).show();
        } else {
            new Builder(this).setTitle(R.string.hrVariables).setMessage(R.string.hrNoVariablesSet).setPositiveButton(R.string.hrOkeyDoke, null).show();
        }
        return true;
    }

    /* Access modifiers changed, original: protected */
    public String[] InitAndGetTabRandomTips() {
        if (_RandomTips == null) {
            _RandomTips = new String[]{getString(R.string.hrEventsTip1), getString(R.string.hrEventsTip2)};
        }
        return _RandomTips;
    }

    @SuppressLint("ResourceType")
    public void onContentChanged() {
        ExpandableListView v = (ExpandableListView) findViewById(R.id.expandylist);
        v.setId(16908298);
        super.onContentChanged();
        v.setId(R.id.expandylist);
    }
}
