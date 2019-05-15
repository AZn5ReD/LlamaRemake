package com.kebab.Llama;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import com.kebab.AlertDialogEx.Builder;
import com.kebab.CachedSetting;
import com.kebab.CachedStringSetting;
import com.kebab.DateHelpers;
import com.kebab.Helpers;
import com.kebab.IterableHelpers;
import com.kebab.Llama.LlamaListTabBase.LlamaListTabBaseImpl;
import com.kebab.Selector;
import com.kebab.TextEntryDialog;
import com.kebab.TextEntryDialog.ButtonHandler;
import com.kebab.Tuple;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class CellsActivity extends LlamaListTabBase {
    public static String[] _RandomTips;
    BaseAdapter _Adapter;
    int[] _AreaColours;
    HashMap<Beacon, List<Tuple<String, String>>> _CachedCellToToAreaMap = new HashMap();
    ArrayList<Beacon> _CachedCells = new ArrayList();
    private Collection<?> _ContextMenuAreasAfter = IterableHelpers.Empty();
    private Collection<?> _ContextMenuAreasBefore = IterableHelpers.Empty();
    Beacon _ContextMenuCell;
    ArrayList<Tuple<String, CharSequence>> _Data;
    final CachedStringSetting _ShowMapIntentAreaName = new CachedStringSetting("AreasActivity", "_ShowMapIntentAreaName", "");

    public CellsActivity() {
        SetImpl(new LlamaListTabBaseImpl(R.layout.tab_recent, LlamaSettings.HelpRecent, R.string.hrHelpRecent) {
            public void Update() {
                CellsActivity.this.Update();
            }

            /* Access modifiers changed, original: protected */
            public String[] InitAndGetTabRandomTips() {
                return CellsActivity.this.InitAndGetTabRandomTips();
            }

            /* Access modifiers changed, original: protected */
            public CharSequence[] getContextSensitiveMenuItems() {
                return CellsActivity.this.getContextSensitiveMenuItems();
            }

            /* Access modifiers changed, original: protected */
            public boolean handleContextSensitiveItem(CharSequence menu) {
                return CellsActivity.this.handleContextSensitiveItem(menu);
            }
        });
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Instances.CellsActivity = this;
        this._Data = new ArrayList();
        this._AreaColours = new int[]{LlamaSettings.GetColourPositive(this), Constants.COLOUR_YELLOW, Constants.COLOUR_MAGENTA, Constants.COLOUR_CYAN, LlamaSettings.GetColourAltPositive(this)};
        this._Adapter = new BaseAdapter() {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v;
                if (convertView == null) {
                    v = View.inflate(CellsActivity.this, 17367053, null);
                } else {
                    v = convertView;
                }
                Tuple<String, CharSequence> map = (Tuple) getItem(position);
                ((TextView) v.findViewById(16908308)).setText((CharSequence) map.Item1);
                ((TextView) v.findViewById(16908309)).setText((CharSequence) map.Item2, BufferType.SPANNABLE);
                return v;
            }

            public int getCount() {
                return CellsActivity.this._Data.size();
            }

            public Object getItem(int position) {
                return CellsActivity.this._Data.get(position);
            }

            public long getItemId(int position) {
                return (long) position;
            }
        };
        setListAdapter(this._Adapter);
        registerForContextMenu(getListView());
    }

    public void onResume() {
        super.onResume();
        if (Instances.Service != null) {
            Update();
        }
    }

    public void onDestroy() {
        Instances.CellsActivity = null;
        super.onDestroy();
    }

    public void Update() {
        if (Instances.HasServiceOrRestart(this)) {
            Instances.Service.RunOnWorkerThreadThenUiThread((Activity) this, new LWork2<Iterable<BeaconAndCalendar>, HashSet<Cell>>() {
                /* Access modifiers changed, original: protected */
                public Iterable<BeaconAndCalendar> InWorkerThread1() {
                    return Instances.Service.GetRecentCells();
                }

                /* Access modifiers changed, original: protected */
                public HashSet<Cell> InWorkerThread2() {
                    return Instances.Service.GetIgnoredCells();
                }

                /* Access modifiers changed, original: protected */
                public void InUiThread(Iterable<BeaconAndCalendar> cells, HashSet<Cell> ignoredCells) {
                    CellsActivity.this._Data.clear();
                    CellsActivity.this._CachedCells.clear();
                    HashMap<String, Integer> areaNameToColourMap = new HashMap();
                    for (BeaconAndCalendar BeaconAndCalendar : cells) {
                        Beacon c = BeaconAndCalendar.Beacon;
                        String line1 = DateHelpers.formatTime(BeaconAndCalendar.Calendar) + " - " + Helpers.CapitaliseFirstLetter(c.getFriendlyTypeName()) + ": " + c.toFormattedString();
                        boolean isCell = c instanceof Cell;
                        String itemPrefix = (isCell && ignoredCells.contains((Cell) c)) ? "(" + CellsActivity.this.getString(R.string.hrIgnored) + ") " : "";
                        List<Tuple<String, String>> areas = c.GetAreaNamesWithInfo(Instances.Service);
                        if (areas != null) {
                            IterableHelpers.OrderBy(areas, new Comparator<Tuple<String, String>>() {
                                public int compare(Tuple<String, String> x, Tuple<String, String> y) {
                                    return ((String) x.Item1).compareToIgnoreCase((String) y.Item1);
                                }
                            });
                        }
                        CellsActivity.this._CachedCellToToAreaMap.put(c, areas);
                        SpannableStringBuilder line2 = new SpannableStringBuilder();
                        if (areas != null && areas.size() != 0) {
                            boolean needComma = false;
                            line2.append(itemPrefix);
                            for (Tuple<String, String> s : areas) {
                                if (needComma) {
                                    line2.append(", ");
                                }
                                needComma = true;
                                int start = line2.length();
                                line2.append((CharSequence) s.Item1);
                                if (s.Item2 != null) {
                                    line2.append(" (").append((CharSequence) s.Item2).append(")");
                                }
                                if (((Integer) LlamaSettings.ColourEventList.GetValue(CellsActivity.this)).intValue() != 0) {
                                    Integer colour = (Integer) areaNameToColourMap.get(s.Item1);
                                    if (colour == null && areaNameToColourMap.size() < CellsActivity.this._AreaColours.length) {
                                        colour = Integer.valueOf(CellsActivity.this._AreaColours[areaNameToColourMap.size()]);
                                        areaNameToColourMap.put(s.Item1, colour);
                                    }
                                    if (colour != null) {
                                        line2.setSpan(new ForegroundColorSpan(colour.intValue()), start, line2.length(), 33);
                                    }
                                }
                                if (line2.length() > 100) {
                                    line2.append("...");
                                    break;
                                }
                            }
                        }
                        String description;
                        if (isCell) {
                            description = Cell.GetDebugCellDescription((Cell) c, CellsActivity.this);
                            if (description == null) {
                                description = CellsActivity.this.getString(R.string.hrNotAddedToAnyAreas);
                            }
                        } else {
                            description = CellsActivity.this.getString(R.string.hrNotAddedToAnyAreas);
                        }
                        line2.append(itemPrefix);
                        if (((Integer) LlamaSettings.ColourEventList.GetValue(CellsActivity.this)).intValue() != 0) {
                            line2.setSpan(new ForegroundColorSpan(LlamaSettings.GetColourNegative(CellsActivity.this)), line2.length(), line2.length(), 34);
                        }
                        line2.append(description);
                        CellsActivity.this._Data.add(new Tuple(line1, line2));
                        CellsActivity.this._CachedCells.add(c);
                    }
                    CellsActivity.this._Adapter.notifyDataSetChanged();
                }
            });
        }
    }

    /* Access modifiers changed, original: protected */
    public CharSequence[] getContextSensitiveMenuItems() {
        if (!((Boolean) LlamaSettings.AndroidLocationEnabled.GetValue(this)).booleanValue()) {
            return new CharSequence[0];
        }
        return new CharSequence[]{getString(R.string.hrViewAllMapPoints)};
    }

    /* Access modifiers changed, original: protected */
    public boolean handleContextSensitiveItem(CharSequence item) {
        if (!getString(R.string.hrViewAllMapPoints).equals(item)) {
            return false;
        }
        Iterable beacons = new ArrayList();
        Iterator i$ = this._CachedCells.iterator();
        while (i$.hasNext()) {
            Beacon b = (Beacon) i$.next();
            if (b.IsMapBased()) {
                ((ArrayList) beacons).add((EarthPoint) b);
            }
        }
        Instances.Service.ShowMapIntent((Activity) this, getString(R.string.hrAllRecentMapPositions), beacons);
        return true;
    }

    /* Access modifiers changed, original: protected */
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        super.openContextMenu(v);
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        this._ContextMenuCell = (Beacon) this._CachedCells.get(info.position);
        if (Instances.HasServiceOrRestart(this)) {
            this._ContextMenuAreasBefore = (info.position > 0) ? Instances.Service.GetAreasNamesForBeacon((Beacon) this._CachedCells.get(info.position - 1)) : IterableHelpers.Empty(null);
            this._ContextMenuAreasAfter = info.position < this._CachedCells.size() + -1 ? Instances.Service.GetAreasNamesForBeacon((Beacon) this._CachedCells.get(info.position + 1)) : IterableHelpers.Empty(null);
        }
        HashSet<Cell> ignoredCells = Instances.Service.GetIgnoredCells();
        menu.add(0, Constants.MENU_ADD_CELL_TO_AREA, 0, R.string.hrAddToArea);
        if (this._ContextMenuCell.IsMapBased()) {
            menu.add(0, Constants.MENU_VIEW_ON_MAP, 0, R.string.hrViewCoordinatesOnMap);
        }
        List<Tuple<String, String>> areas = (List) this._CachedCellToToAreaMap.get(this._ContextMenuCell);
        if (!this._ContextMenuCell.IsMapBased()) {
            if (areas != null && areas.size() > 0) {
                menu.add(0, Constants.MENU_REMOVE_CELL_FROM_AREA, 0, R.string.hrRemoveFromArea);
            }
            if (!(this._ContextMenuCell instanceof Cell)) {
                return;
            }
            if (ignoredCells.contains((Cell) this._ContextMenuCell)) {
                menu.add(0, 126, 0, R.string.hrUnignoreCell);
            } else {
                menu.add(0, Constants.MENU_IGNORE_CELL, 0, R.string.hrIgnoreCell);
            }
        } else if (areas != null && areas.size() > 0) {
            menu.add(0, 3, 0, R.string.hrEditAreasPositionOnMap);
        }
    }

    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 3:
                EditMapPointsForArea();
                break;
            case Constants.MENU_ADD_CELL_TO_AREA /*107*/:
                AddContextMenuCellToArea();
                break;
            case Constants.MENU_REMOVE_CELL_FROM_AREA /*111*/:
                RemoveContextMenuCellFromArea();
                break;
            case Constants.MENU_VIEW_ON_MAP /*124*/:
                this._ShowMapIntentAreaName.SetValueAndCommit(this, "", new CachedSetting[0]);
                Instances.Service.ShowMapIntent((Activity) this, getString(R.string.hrPositionOfARecentLocationEstimate), (EarthPoint) this._ContextMenuCell);
                break;
            case Constants.MENU_IGNORE_CELL /*125*/:
                IgnoreContextMenuCell();
                break;
            case 126:
                UnignoreContextMenuCell();
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void UnignoreContextMenuCell() {
        if (this._ContextMenuCell instanceof Cell) {
            Instances.Service.RemoveIgnoredCell((Cell) this._ContextMenuCell);
        }
    }

    private void IgnoreContextMenuCell() {
        if (this._ContextMenuCell instanceof Cell) {
            Instances.Service.AddIgnoredCell((Cell) this._ContextMenuCell);
        }
    }

    private void RemoveContextMenuCellFromArea() {
        final String[] nameArray = (String[]) IterableHelpers.ToArray(IterableHelpers.Select((List) this._CachedCellToToAreaMap.get(this._ContextMenuCell), new Selector<Tuple<String, String>, String>() {
            public String Do(Tuple<String, String> value) {
                return (String) value.Item1;
            }
        }), String.class);
        Arrays.sort(nameArray);
        Builder alertbox = new Builder(this);
        alertbox.setTitle(String.format(getString(R.string.hrWhichAreaDoYouWantToRemove1From), new Object[]{this._ContextMenuCell.toFormattedString()}));
        alertbox.setSingleChoiceItems(nameArray, 0, new OnClickListener() {
            public void onClick(DialogInterface dialog, int index) {
                dialog.dismiss();
                if (index >= 0) {
                    Area a = Instances.Service.GetAreaByName(nameArray[index]);
                    if (a == null) {
                        Instances.Service.HandleFriendlyError(String.format(CellsActivity.this.getString(R.string.hrCouldntFindTheAreaNamed1s), new Object[]{nameArray[index]}), true);
                        return;
                    }
                    Instances.Service.RemoveCellFromArea(CellsActivity.this._ContextMenuCell, a);
                }
            }
        });
        alertbox.show();
    }

    private void EditMapPointsForArea() {
        final String[] nameArray = (String[]) IterableHelpers.ToArray(IterableHelpers.Select((List) this._CachedCellToToAreaMap.get(this._ContextMenuCell), new Selector<Tuple<String, String>, String>() {
            public String Do(Tuple<String, String> value) {
                return (String) value.Item1;
            }
        }), String.class);
        Arrays.sort(nameArray);
        Builder alertbox = new Builder(this);
        alertbox.setTitle(R.string.hrWhichAreaDoYouWantToEdit);
        alertbox.setSingleChoiceItems(nameArray, 0, new OnClickListener() {
            public void onClick(DialogInterface dialog, int index) {
                dialog.dismiss();
                if (index >= 0) {
                    Area a = Instances.Service.GetAreaByName(nameArray[index]);
                    if (a == null) {
                        Instances.Service.HandleFriendlyError(String.format(CellsActivity.this.getString(R.string.hrCouldntFindTheAreaNamed1s), new Object[]{nameArray[index]}), true);
                        return;
                    }
                    CellsActivity.this._ShowMapIntentAreaName.SetValueAndCommit(CellsActivity.this, a.Name, new CachedSetting[0]);
                    Instances.Service.ShowMapIntentForArea(CellsActivity.this, a);
                }
            }
        });
        alertbox.show();
    }

    private void AddContextMenuCellToArea() {
        int i;
        ArrayList<Area> areas = new ArrayList();
        ArrayList<String> names = new ArrayList();
        ArrayList<Area> topAreas = new ArrayList();
        ArrayList<String> topNames = new ArrayList();
        HashSet<String> nearbyNames = new HashSet();
        nearbyNames.addAll((Collection<? extends String>) this._ContextMenuAreasBefore);
        nearbyNames.addAll((Collection<? extends String>) this._ContextMenuAreasAfter);
        for (Area a : IterableHelpers.OrderBy(Instances.Service.GetAreas(), Area.NameComparator)) {
            if (nearbyNames.contains(a.Name)) {
                topAreas.add(a);
                topNames.add(a.Name);
            } else {
                areas.add(a);
                names.add(a.Name);
            }
        }
        String[] nameArray = new String[((names.size() + topNames.size()) + 1)];
        nameArray[0] = getString(R.string.hrCreateNewArea);
        for (i = 0; i < topNames.size(); i++) {
            nameArray[i + 1] = (String) topNames.get(i);
        }
        int topCount = topNames.size() + 1;
        for (i = 0; i < names.size(); i++) {
            nameArray[i + topCount] = (String) names.get(i);
        }
        final ArrayList<Area> areasInCorrectOrder = topAreas;
        areasInCorrectOrder.addAll(areas);
        Builder alertbox = new Builder(this);
        alertbox.setTitle(String.format(getString(R.string.hrWhichAreaDoYouWantToAdd1To), new Object[]{this._ContextMenuCell.toFormattedString()}));
        alertbox.setSingleChoiceItems(nameArray, 0, new OnClickListener() {
            public void onClick(DialogInterface dialog, int index) {
                dialog.dismiss();
                if (index == 0) {
                    TextEntryDialog.Show(CellsActivity.this, CellsActivity.this.getString(R.string.hrEnterANameForTheNewArea), new ButtonHandler() {
                        public void Do(String result) {
                            if (result.length() > 0) {
                                CellsActivity.this.AddContextMenuCellToArea(CellsActivity.this._ContextMenuCell, Instances.Service.CreateArea(result));
                            }
                        }
                    });
                } else if (index > 0) {
                    CellsActivity.this.AddContextMenuCellToArea(CellsActivity.this._ContextMenuCell, (Area) areasInCorrectOrder.get(index - 1));
                }
            }
        });
        alertbox.show();
    }

    private void AddContextMenuCellToArea(Beacon beacon, Area area) {
        if (beacon.IsMapBased()) {
            this._ShowMapIntentAreaName.SetValueAndCommit(this, area.Name, new CachedSetting[0]);
            ArrayList<Location> locations = area.GetMapPointsAsLocations();
            locations.add(((EarthPoint) beacon).ToLocation());
            Instances.Service.ShowMapIntentForArea(this, locations, area);
            return;
        }
        Instances.Service.AddCellToArea(beacon, area, true);
    }

    /* Access modifiers changed, original: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.LLAMAP_INTENT && resultCode == -1 && ((String) this._ShowMapIntentAreaName.GetValue(this)).length() > 0) {
            Instances.Service.UpdateAreaMapPoints((String) this._ShowMapIntentAreaName.GetValue(this), data.<Location>getParcelableArrayListExtra(LlamaMapConstants.EXTRA_LOCATIONS));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /* Access modifiers changed, original: protected */
    public String[] InitAndGetTabRandomTips() {
        if (_RandomTips == null) {
            _RandomTips = new String[]{getString(R.string.hrRecentTip1)};
        }
        return _RandomTips;
    }
}
