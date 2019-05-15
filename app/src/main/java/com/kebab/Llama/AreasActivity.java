package com.kebab.Llama;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.kebab.AlertDialogEx.Builder;
import com.kebab.CachedSetting;
import com.kebab.CachedStringSetting;
import com.kebab.DateHelpers;
import com.kebab.Helpers;
import com.kebab.IterableHelpers;
import com.kebab.Llama.LlamaListTabBase.LlamaListTabBaseImpl;
import com.kebab.TextEntryDialog;
import com.kebab.TextEntryDialog.ButtonHandler;
import com.kebab.Tuple;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class AreasActivity extends LlamaListTabBase {
    private static final Integer LAST_LOCATION_RADIUS_OVERRIDE = Integer.valueOf(400);
    public static String[] _RandomTips;
    SimpleAdapter _Adapter;
    ArrayList<HashMap<String, String>> _Data = new ArrayList();
    Handler _LearningTimer;
    final CachedStringSetting _ShowMapIntentAreaName = new CachedStringSetting("AreasActivity", "_ShowMapIntentAreaName", "");
    Runnable reloadRunnable = new Runnable() {
        public void run() {
            AreasActivity.this.Update();
        }
    };

    public AreasActivity() {
        SetImpl(new LlamaListTabBaseImpl(R.layout.tab_areas, LlamaSettings.HelpAreas, R.string.hrHelpAreas) {
            public void Update() {
                AreasActivity.this.Update();
            }

            /* Access modifiers changed, original: protected */
            public String[] InitAndGetTabRandomTips() {
                return AreasActivity.this.InitAndGetTabRandomTips();
            }

            /* Access modifiers changed, original: protected */
            public CharSequence[] getContextSensitiveMenuItems() {
                return new CharSequence[0];
            }

            /* Access modifiers changed, original: protected */
            public boolean handleContextSensitiveItem(CharSequence menu) {
                return false;
            }
        });
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Instances.AreasActivity = this;
        this._Adapter = new SimpleAdapter(this, this._Data, 17367053, new String[]{"line1", "line2"}, new int[]{16908308, 16908309}) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (((Integer) LlamaSettings.ColourEventList.GetValue(AreasActivity.this)).intValue() != 0) {
                    TextView tv = (TextView) v.findViewById(16908308);
                    Map<String, String> itemData = (Map) AreasActivity.this._Data.get(position);
                    tv.setTextColor(((TextView) v.findViewById(16908309)).getTextColors());
                    if (itemData.get("current") != null) {
                        tv.setTextColor(LlamaSettings.GetColourPositive(AreasActivity.this));
                    }
                }
                return v;
            }
        };
        setListAdapter(this._Adapter);
        registerForContextMenu(getListView());
        ((ImageButton) findViewById(R.id.addButton)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                AreasActivity.this.AddNewArea();
            }
        });
    }

    public void onPause() {
        super.onPause();
        EnsureLearningTimer(false);
    }

    public void onResume() {
        super.onResume();
        if (Instances.Service != null) {
            Update();
        }
    }

    public void onDestroy() {
        Instances.AreasActivity = null;
        EnsureLearningTimer(false);
        super.onDestroy();
    }

    /* Access modifiers changed, original: protected */
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String selectedArea = (String) ((HashMap) this._Data.get(position)).get("line1");
        final Area area = Instances.Service.GetAreaByName(selectedArea);
        if (area == null) {
            Logging.Report("Area named " + selectedArea + " has vanished", (Context) this);
            return;
        }
        CharSequence[] beaconNames = new CharSequence[area._Cells.size()];
        final ArrayList<Beacon> beaconCopy = new ArrayList(area._Cells);
        final boolean[] selectedArray = new boolean[area._Cells.size()];
        boolean hasMapPoints = false;
        for (int i = 0; i < area._Cells.size(); i++) {
            Beacon beacon = (Beacon) area._Cells.get(i);
            beaconNames[i] = Helpers.CapitaliseFirstLetter(beacon.getFriendlyTypeName()) + "\n" + beacon.toFormattedString();
            if (beacon.IsMapBased()) {
                hasMapPoints = true;
            }
        }
        if (hasMapPoints && area._Cells.size() == 1) {
            ShowMapIntentForArea(area);
        } else if (area._Cells.size() == 0) {
            new Builder(this).setMessage(R.string.hrThisAreaHasNotBeenDefinedLongTapItOrLookInTheRecentTab).setPositiveButton(R.string.hrOkeyDoke, null).show();
        } else {
            AlertDialog.Builder builder = new Builder(this).setTitle(R.string.hrEditArea).setMultiChoiceItems(beaconNames, selectedArray, new OnMultiChoiceClickListener() {
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    selectedArray[which] = isChecked;
                }
            }).setNeutralButton(R.string.hrRemove, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    ArrayList<Beacon> beaconsToDelete = new ArrayList();
                    for (int i = 0; i < selectedArray.length; i++) {
                        if (selectedArray[i]) {
                            beaconsToDelete.add(beaconCopy.get(i));
                        }
                    }
                    Instances.Service.RemoveCellsFromArea(beaconsToDelete, area);
                    dialog.dismiss();
                }
            }).setNegativeButton(R.string.hrCancel, null);
            if (hasMapPoints) {
                builder.setPositiveButton(R.string.hrViewMap, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        AreasActivity.this.ShowMapIntentForArea(area);
                        dialog.dismiss();
                    }
                });
            }
            builder.show();
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void ShowMapIntentForArea(Area area) {
        this._ShowMapIntentAreaName.SetValueAndCommit(this, area.Name, new CachedSetting[0]);
        Instances.Service.ShowMapIntentForArea(this, area);
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        String selectedAreaName = (String) ((HashMap) this._Data.get(((AdapterContextMenuInfo) menuInfo).position)).get("line1");
        menu.add(0, 2, 0, R.string.hrCreateReminder);
        menu.add(0, 3, 0, R.string.hrRenameArea);
        if (((Boolean) LlamaSettings.AndroidLocationEnabled.GetValue(this)).booleanValue()) {
            menu.add(0, 20, 0, R.string.hrSetPositionOnMap);
        }
        Area learningArea = Instances.Service.GetLearningArea();
        if (learningArea == null || !learningArea.Name.equals(selectedAreaName)) {
            menu.add(0, 6, 0, R.string.hrStartLearningArea);
        } else {
            menu.add(0, 7, 0, R.string.hrStopLearningArea);
        }
        menu.add(0, 5, 0, R.string.hrDeleteArea);
    }

    public boolean onContextItemSelected(MenuItem item) {
        final String selectedAreaName = (String) ((HashMap) this._Data.get(((AdapterContextMenuInfo) item.getMenuInfo()).position)).get("line1");
        switch (item.getItemId()) {
            case 2:
                CreateReminder(selectedAreaName);
                break;
            case 3:
                RenameArea(selectedAreaName);
                break;
            case 5:
                new Builder(this).setTitle(R.string.hrDeleteArea).setCancelable(true).setMessage(String.format(getString(R.string.hrAreYouSureYouWantToDelete1), new Object[]{selectedAreaName})).setPositiveButton(R.string.hrYes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Instances.Service.DeleteAreaByName(selectedAreaName);
                    }
                }).setNegativeButton(R.string.hrNo, null).show();
                break;
            case 6:
                Area a = Instances.Service.GetAreaByName(selectedAreaName);
                if (a != null) {
                    ShowStartLearningDialog(a);
                    break;
                }
                Logging.Report("Area named " + selectedAreaName + " has vanished", (Context) this);
                break;
            case 7:
                Instances.Service.StopLearning(true);
                break;
            case 20:
                SetLocationFromMap(selectedAreaName);
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void SetLocationFromMap(String areaName) {
        Area a = Instances.Service.GetAreaByName(areaName);
        ArrayList<Location> locations = a.GetMapPointsAsLocations();
        if (locations.size() == 0) {
            Location loc = Instances.Service.GetLastLocation(LAST_LOCATION_RADIUS_OVERRIDE);
            if (loc == null) {
                loc = new Location(Constants.LLAMA_EXTERNAL_STORAGE_ROOT);
                loc.setLatitude(0.0d);
                loc.setLongitude(0.0d);
                loc.setAccuracy(400.0f);
            }
            locations.add(loc);
        }
        this._ShowMapIntentAreaName.SetValueAndCommit(this, areaName, new CachedSetting[0]);
        Instances.Service.ShowMapIntentForArea(this, locations, a);
    }

    private void CreateReminder(final String selectedAreaName) {
        TextEntryDialog.Show(this, String.format(getString(R.string.hrEnterAReminderForArea1), new Object[]{selectedAreaName}), new ButtonHandler() {
            public void Do(String result) {
                if (result.length() > 0) {
                    Instances.Service.CreateReminderForAreaByName(selectedAreaName, result);
                }
            }
        });
    }

    /* Access modifiers changed, original: 0000 */
    public void RenameArea(final String oldName) {
        TextEntryDialog.Show((Activity) this, getString(R.string.hrEnterANewNameForTheArea), oldName, new ButtonHandler() {
            public void Do(String result) {
                if (result.length() > 0) {
                    if (!result.equals(oldName)) {
                        while (Instances.Service.GetAreaByName(result) != null) {
                            result = result + " 2";
                        }
                    }
                    Instances.Service.RenameAreaByName(oldName, result);
                }
            }
        });
    }

    /* Access modifiers changed, original: 0000 */
    public void AddNewArea() {
        TextEntryDialog.Show(this, getString(R.string.hrNewLocationPrompt), new ButtonHandler() {
            public void Do(String result) {
                if (result.length() > 0) {
                    while (Instances.Service.GetAreaByName(result) != null) {
                        result = result + " 2";
                    }
                    AreasActivity.this.ShowStartLearningDialog(Instances.Service.CreateArea(result));
                }
            }
        });
    }

    public void Update() {
        if (Instances.HasServiceOrRestart(getApplicationContext())) {
            Instances.Service.RunOnWorkerThreadThenUiThread((Activity) this, new LWork4<Area, Date, HashSet<String>, List<Area>>() {
                /* Access modifiers changed, original: protected */
                public Area InWorkerThread1() {
                    return Instances.Service.GetLearningArea();
                }

                /* Access modifiers changed, original: protected */
                public Date InWorkerThread2() {
                    return Instances.Service.GetLearningUntilDate();
                }

                /* Access modifiers changed, original: protected */
                public HashSet<String> InWorkerThread3() {
                    return new HashSet(Instances.Service.GetCurrentAreas());
                }

                /* Access modifiers changed, original: protected */
                public List<Area> InWorkerThread4() {
                    return new ArrayList(Instances.Service.GetAreas());
                }

                /* Access modifiers changed, original: protected */
                public void InUiThread(Area learningArea, Date learningUntil, HashSet<String> currentAreas, List<Area> areas) {
                    Iterable<Area> areaCopy = IterableHelpers.OrderBy(areas, Area.NameComparator);
                    AreasActivity.this._Data.clear();
                    String remainingTime = null;
                    if (learningArea == null || learningUntil == null) {
                        AreasActivity.this.EnsureLearningTimer(false);
                    } else {
                        if (((learningUntil.getTime() - Calendar.getInstance().getTime().getTime()) / 1000) / 60 == 0) {
                            remainingTime = String.format(AreasActivity.this.getString(R.string.hr1SecondsToGo), new Object[]{Long.valueOf((learningUntil.getTime() - Calendar.getInstance().getTime().getTime()) / 1000)});
                        } else {
                            remainingTime = String.format(AreasActivity.this.getString(R.string.hr1MinutesToGo), new Object[]{Long.valueOf((learningUntil.getTime() - Calendar.getInstance().getTime().getTime()) / 1000) / 60});
                        }
                        AreasActivity.this.EnsureLearningTimer(true);
                    }
                    StringBuffer sb = new StringBuffer();
                    for (Area a : areaCopy) {
                        sb.setLength(0);
                        HashMap<String, String> map = new HashMap();
                        map.put("line1", a.Name);
                        if (a.equals(learningArea)) {
                            sb.append(AreasActivity.this.getString(R.string.hrLearningDash));
                            sb.append(remainingTime);
                            sb.append(" - ");
                        }
                        if (currentAreas.contains(a.Name)) {
                            sb.append(AreasActivity.this.getString(R.string.hrCurrentDash));
                            map.put("current", "1");
                        }
                        List<Tuple<String, Integer>> counts = a.GetCountOfBeaconTypes();
                        boolean needComma = false;
                        for (int i = 0; i < counts.size(); i++) {
                            if (needComma) {
                                if (i == counts.size() - 1) {
                                    sb.append(" ");
                                    sb.append(AreasActivity.this.getString(R.string.hrAnd));
                                    sb.append(" ");
                                } else {
                                    sb.append(", ");
                                }
                            }
                            int count = ((Integer) ((Tuple) counts.get(i)).Item2).intValue();
                            sb.append(count + " " + Beacon.GetSingleOrPluralName((String) ((Tuple) counts.get(i)).Item1, count));
                            needComma = true;
                        }
                        map.put("line2", sb.toString());
                        AreasActivity.this._Data.add(map);
                    }
                    AreasActivity.this._Adapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void EnsureLearningTimer(boolean enabled) {
        if (enabled) {
            if (this._LearningTimer == null) {
                this._LearningTimer = new Handler();
            } else {
                this._LearningTimer.removeCallbacks(this.reloadRunnable);
            }
            this._LearningTimer.postDelayed(this.reloadRunnable, 1000);
        } else if (this._LearningTimer != null) {
            this._LearningTimer.removeCallbacks(this.reloadRunnable);
            this._LearningTimer = null;
        }
    }

    private void ShowStartLearningDialog(final Area a) {
        Builder alertbox = new Builder(this);
        alertbox.setTitle(String.format(getString(R.string.hrHowLongWillYouBeAt1For), new Object[]{a.Name}));
        String second = getString(R.string.hrSecond);
        String seconds = getString(R.string.hrSeconds);
        String minute = getString(R.string.hrMinute);
        String minutes = getString(R.string.hrMinutes);
        String hour = getString(R.string.hrHour);
        String hours = getString(R.string.hrHours);
        final int[] secondsArray = new int[]{0, 1, 30, 60, Constants.MENU_LOCK_PROFILES, 300, 600, 900, 1200, 1800, 2700, DateHelpers.SECONDS_PER_HOUR, 7200, 14400, 28800};
        alertbox.setSingleChoiceItems(new String[]{getString(R.string.hrImNotThere), "1 " + second, "30 " + seconds, "1 " + minute, "2 " + minutes, "5 " + minutes, "10 " + minutes, "15 " + minutes, "20 " + minutes, "30 " + minutes, "45 " + minutes, "1 " + hour, "2 " + hours, "4 " + hours, "8 " + hours}, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
                if (arg1 != 0) {
                    AreasActivity.this.StartLearning(a, secondsArray[arg1]);
                }
            }
        });
        alertbox.show();
    }

    /* Access modifiers changed, original: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.LLAMAP_INTENT && resultCode == -1) {
            Instances.Service.UpdateAreaMapPoints((String) this._ShowMapIntentAreaName.GetValue(this), data.<Location>getParcelableArrayListExtra(LlamaMapConstants.EXTRA_LOCATIONS));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /* Access modifiers changed, original: 0000 */
    public void StartLearning(Area a, int seconds) {
        Instances.Service.StartLearning(a, seconds);
        Update();
    }

    /* Access modifiers changed, original: protected */
    public String[] InitAndGetTabRandomTips() {
        if (_RandomTips == null) {
            _RandomTips = new String[]{getString(R.string.hrAreasTip1), getString(R.string.hrAreasTip2)};
        }
        return _RandomTips;
    }
}
