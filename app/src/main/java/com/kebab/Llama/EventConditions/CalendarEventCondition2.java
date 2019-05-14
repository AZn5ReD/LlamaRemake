package com.kebab.Llama.EventConditions;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.preference.PreferenceActivity;
import android.text.Html;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import com.kebab.AlertDialogEx.Builder;
import com.kebab.AsyncProgressDialog;
import com.kebab.ClickablePreferenceEx;
import com.kebab.ClickablePreferenceEx.GotResultHandler;
import com.kebab.Helpers;
import com.kebab.IterableHelpers;
import com.kebab.Llama.CalendarDebugActivity;
import com.kebab.Llama.CalendarItem;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.EventMeta;
import com.kebab.Llama.EventMeta.ConditionStaticInitter2;
import com.kebab.Llama.EventTrigger;
import com.kebab.Llama.Instances;
import com.kebab.Llama.LlamaStorage;
import com.kebab.Llama.Logging;
import com.kebab.Llama.R;
import com.kebab.Llama.StateChange;
import com.kebab.PreferenceEx;
import com.kebab.Ref;
import com.kebab.ResultRegisterableActivity;
import com.kebab.Selector;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CalendarEventCondition2 extends EventCondition<CalendarEventCondition2> {
    public static String MY_ID;
    public static int[] MY_TRIGGERS;
    public static int MY_TRIGGER_1;
    public static int MY_TRIGGER_2;
    ArrayList<String> _CalendarNames;
    boolean _CurrentEventsContainsSubstring;
    ArrayList<String> _EventNameSubstrings;
    Boolean _IsAllDay;
    Boolean _ShowAsAvailable;

    static {
        EventMeta.InitCondition(EventFragment.CALENDAR_EVENT2, new ConditionStaticInitter2() {
            public void UpdateStatics(String id, int[] triggers, int triggerOn, int triggerOff) {
                CalendarEventCondition2.MY_ID = id;
                CalendarEventCondition2.MY_TRIGGERS = triggers;
                CalendarEventCondition2.MY_TRIGGER_1 = triggerOn;
                CalendarEventCondition2.MY_TRIGGER_2 = triggerOff;
            }
        });
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return MY_ID;
    }

    public int[] getEventTriggers() {
        return MY_TRIGGERS;
    }

    public CalendarEventCondition2(ArrayList<String> eventNameSubstrings, boolean currentEventsContainsSubstring, ArrayList<String> calendarNames, Boolean isAllDay, Boolean isShowAsAvailable) {
        this._EventNameSubstrings = eventNameSubstrings;
        this._CurrentEventsContainsSubstring = currentEventsContainsSubstring;
        this._CalendarNames = calendarNames;
        this._IsAllDay = isAllDay;
        this._ShowAsAvailable = isShowAsAvailable;
        Helpers.ArrayListToLowerCase(this._EventNameSubstrings);
        Helpers.ArrayListToLowerCase(this._CalendarNames);
    }

    public CalendarEventCondition2(CalendarEventCondition condition) {
        this._CurrentEventsContainsSubstring = condition._CurrentEventsContainsSubstring;
        this._EventNameSubstrings = condition._EventNameSubstrings;
        this._CalendarNames = condition._CalendarNames;
        this._IsAllDay = null;
        this._ShowAsAvailable = null;
    }

    /* Access modifiers changed, original: 0000 */
    public boolean EventListMatchesCondition(List<CalendarItem> stateEventList, Context context) {
        for (CalendarItem item : stateEventList) {
            Iterator i$;
            boolean calendarNameMatches = false;
            boolean eventNameMatches = false;
            if (this._EventNameSubstrings.size() > 0) {
                i$ = this._EventNameSubstrings.iterator();
                while (i$.hasNext()) {
                    if (item.Name.indexOf((String) i$.next()) >= 0) {
                        eventNameMatches = true;
                        break;
                    }
                }
                if (!eventNameMatches) {
                    continue;
                }
            }
            if (this._CalendarNames.size() > 0) {
                i$ = this._CalendarNames.iterator();
                while (i$.hasNext()) {
                    if (item.CalendarName.equals((String) i$.next())) {
                        calendarNameMatches = true;
                        break;
                    }
                }
                if (!calendarNameMatches) {
                    continue;
                }
            }
            if (this._IsAllDay != null) {
                if ((!item.IsAllDay) == this._IsAllDay.booleanValue()) {
                    continue;
                }
            }
            if (this._ShowAsAvailable != null) {
                if ((!item.ShowAsAvailable) != this._ShowAsAvailable.booleanValue()) {
                }
            }
            Logging.Report("Calendar", "Event " + item.Name + "matched all conditions", context);
            return true;
        }
        return false;
    }

    public int TestCondition(StateChange state, Context context, Ref<EventTrigger> ref) {
        if (this._CurrentEventsContainsSubstring) {
            if ((state.TriggerType == MY_TRIGGER_1 || state.TriggerType == MY_TRIGGER_2) && EventListMatchesCondition(state.StartingEvents, context)) {
                return 2;
            }
            if (EventListMatchesCondition(state.CurrentEvents, context)) {
                return 1;
            }
            return 0;
        } else if ((state.TriggerType == MY_TRIGGER_1 || state.TriggerType == MY_TRIGGER_2) && EventListMatchesCondition(state.EndingEvents, context)) {
            return 2;
        } else {
            if (EventListMatchesCondition(state.CurrentEvents, context)) {
                return 0;
            }
            return 1;
        }
    }

    public boolean RenameArea(String oldName, String newName) {
        return false;
    }

    public void AppendConditionSimple(Context context, Appendable sb) throws IOException {
        sb.append(getFriendlyDescription(context, R.string.hrCalendarEvents1Contains2Description, R.string.hrCalendarEvents1DoesNotContains2Description, R.string.hrCalendarEvents1ContainsAnyEventDescription, R.string.hrCalendarEvents1DoesNotContainAnyEventDescription));
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 7;
    }

    public static CalendarEventCondition2 CreateFrom(String[] parts, int currentPart) {
        ArrayList<String> calendarNames;
        ArrayList<String> substrings = Helpers.SplitToArrayList(LlamaStorage.SimpleUnescape(parts[currentPart + 2]), "\\|", -1);
        if (parts[currentPart + 3].equals("0")) {
            calendarNames = new ArrayList();
        } else {
            calendarNames = Helpers.SplitToArrayList(LlamaStorage.SimpleUnescape(parts[currentPart + 3]), "\\|", -1);
            for (int i = 0; i < calendarNames.size(); i++) {
                calendarNames.set(i, LlamaStorage.SimpleUnescape((String) calendarNames.get(i)));
            }
        }
        return new CalendarEventCondition2(substrings, parts[currentPart + 1].equals("1"), calendarNames, Helpers.NullableBooleanFromString(parts[currentPart + 4]), Helpers.NullableBooleanFromString(parts[currentPart + 5]));
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        String str;
        Iterable escapedCalendarNames = new ArrayList(this._CalendarNames.size());
        Iterator i$ = this._CalendarNames.iterator();
        while (i$.hasNext()) {
            escapedCalendarNames.add(LlamaStorage.SimpleEscape((String) i$.next()));
        }
        if (this._CurrentEventsContainsSubstring) {
            str = "1";
        } else {
            str = "0";
        }
        sb.append(str).append("|").append(LlamaStorage.SimpleEscape(IterableHelpers.ConcatenateString(this._EventNameSubstrings, "|"))).append("|").append(LlamaStorage.SimpleEscape(IterableHelpers.ConcatenateString(escapedCalendarNames, "|"))).append("|").append(Helpers.NullableBooleanToString(this._IsAllDay)).append("|").append(Helpers.NullableBooleanToString(this._ShowAsAvailable)).append("|").append(Helpers.NullableBooleanToString(null)).append("|").append(Helpers.NullableBooleanToString(null));
    }

    /* Access modifiers changed, original: 0000 */
    public String getFriendlyDescription(Context context, int resourceIdContains, int resourceIdDoesNotContain, int resourceIdContainsAny, int resourceIdDoesNotContainAny) {
        String result;
        StringBuilder sb2 = new StringBuilder();
        if (this._CalendarNames.size() == 0) {
            sb2.append(context.getString(R.string.hrAnyCalendar));
        } else {
            Helpers.ConcatenateListOfStrings(sb2, IterableHelpers.Select(this._CalendarNames, new Selector<String, String>() {
                public String Do(String value) {
                    return value.substring(0, value.lastIndexOf(" ("));
                }
            }), ", ", " " + context.getString(R.string.hrOr) + " ");
        }
        if (this._EventNameSubstrings.size() == 0) {
            if (!this._CurrentEventsContainsSubstring) {
                resourceIdContainsAny = resourceIdDoesNotContainAny;
            }
            result = String.format(context.getString(resourceIdContainsAny), new Object[]{sb2.toString()});
        } else {
            Helpers.ConcatenateListOfStrings(new StringBuilder(), this._EventNameSubstrings, ", ", " " + context.getString(R.string.hrOr) + " ");
            if (!this._CurrentEventsContainsSubstring) {
                resourceIdContains = resourceIdDoesNotContain;
            }
            result = String.format(context.getString(resourceIdContains), new Object[]{sb2.toString(), sb1.toString()});
        }
        if (this._IsAllDay == null && this._ShowAsAvailable == null) {
            return result;
        }
        ArrayList<String> parts = new ArrayList();
        StringBuilder sb = new StringBuilder(result);
        sb.append(" (");
        sb.append(context.getString(R.string.hrCalendarEventMustBe));
        sb.append(" ");
        if (this._IsAllDay != null) {
            parts.add(context.getString(this._IsAllDay.booleanValue() ? R.string.hrAllDay : R.string.hrNotAllDay));
        }
        if (this._ShowAsAvailable != null) {
            parts.add(context.getString(this._ShowAsAvailable.booleanValue() ? R.string.hrShownAsAvailable : R.string.hrNotShownAsAvailable));
        }
        Helpers.ConcatenateListOfStrings(sb, parts, ", ", " " + context.getString(R.string.hrAnd) + " ");
        sb.append(")");
        return sb.toString();
    }

    public PreferenceEx<CalendarEventCondition2> CreatePreference(PreferenceActivity context) {
        final PreferenceActivity preferenceActivity = context;
        return new ClickablePreferenceEx<CalendarEventCondition2>((ResultRegisterableActivity) context, context.getString(R.string.hrCalendarEvent), this) {
            /* Access modifiers changed, original: protected */
            public String GetHumanReadableValue(Context context, CalendarEventCondition2 value) {
                return value.getFriendlyDescription(context, R.string.hrCalendarEvents1Contains2, R.string.hrCalendarEvents1DoesNotContains2, R.string.hrCalendarEvents1ContainsAnyEvent, R.string.hrCalendarEvents1DoesNotContainAnyEvent);
            }

            /* Access modifiers changed, original: protected */
            public void OnPreferenceClicked(ResultRegisterableActivity host, CalendarEventCondition2 existingValue, GotResultHandler<CalendarEventCondition2> gotResultHandler) {
                final ResultRegisterableActivity resultRegisterableActivity = host;
                final CalendarEventCondition2 calendarEventCondition2 = existingValue;
                final GotResultHandler<CalendarEventCondition2> gotResultHandler2 = gotResultHandler;
                new AsyncProgressDialog<Object, Object, List<String>>(preferenceActivity, preferenceActivity.getString(R.string.hrPleaseWait), preferenceActivity.getString(R.string.hrPleaseWait), true, true) {
                    /* Access modifiers changed, original: protected */
                    public List<String> DoWorkInBackground(Object[] params) {
                        return Instances.Service.GetListOfCalendars();
                    }

                    /* Access modifiers changed, original: protected */
                    public void MarkWorkAsCancelled() {
                    }

                    /* Access modifiers changed, original: protected */
                    public void OnAsyncCompletedSuccessfully(List<String> allCalendarNames) {
                        View v = ((LayoutInflater) preferenceActivity.getSystemService("layout_inflater")).inflate(R.layout.calendareventdialog, null);
                        final EditText text = (EditText) v.findViewById(R.id.text);
                        Button separator = (Button) v.findViewById(R.id.separator);
                        final Spinner spinner = (Spinner) v.findViewById(R.id.calendarSpinner);
                        final Spinner calendarAllDaySpinner = (Spinner) v.findViewById(R.id.calendarAllDaySpinner);
                        final Spinner calendarAvailabilitySpinner = (Spinner) v.findViewById(R.id.calendarAvailabilitySpinner);
                        ListView list = (ListView) v.findViewById(R.id.list);
                        final SparseBooleanArray checks = new SparseBooleanArray();
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(resultRegisterableActivity.GetActivity(), R.layout.two_line_checked_list_item, allCalendarNames) {
                            public View getView(final int position, View convertView, ViewGroup parent) {
                                if (convertView == null) {
                                    convertView = View.inflate(resultRegisterableActivity.GetActivity(), R.layout.two_line_checked_list_item, null);
                                }
                                TextView text1 = (TextView) convertView.findViewById(R.id.text1);
                                TextView text2 = (TextView) convertView.findViewById(R.id.text2);
                                CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
                                String value = (String) getItem(position);
                                int bracketPos = value.lastIndexOf("(");
                                text1.setText(value.substring(0, bracketPos));
                                text2.setText(value.substring(bracketPos));
                                checkBox.setChecked(checks.get(position));
                                checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        checks.put(position, isChecked);
                                    }
                                });
                                return convertView;
                            }
                        };
                        list.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        LayoutParams layoutParams = list.getLayoutParams();
                        layoutParams.height *= list.getAdapter().getCount();
                        list.setLayoutParams(layoutParams);
                        text.setText(IterableHelpers.ConcatenateString(calendarEventCondition2._EventNameSubstrings, "|"));
                        spinner.setSelection(calendarEventCondition2._CurrentEventsContainsSubstring ? 0 : 1);
                        if (calendarEventCondition2._IsAllDay != null) {
                            calendarAllDaySpinner.setSelection(calendarEventCondition2._IsAllDay.booleanValue() ? 1 : 2);
                        } else {
                            calendarAllDaySpinner.setSelection(0);
                        }
                        if (calendarEventCondition2._ShowAsAvailable != null) {
                            calendarAvailabilitySpinner.setSelection(calendarEventCondition2._ShowAsAvailable.booleanValue() ? 1 : 2);
                        } else {
                            calendarAvailabilitySpinner.setSelection(0);
                        }
                        for (int i = 0; i < allCalendarNames.size(); i++) {
                            if (IterableHelpers.FindIndexIgnoreCase(calendarEventCondition2._CalendarNames, (String) allCalendarNames.get(i)) != null) {
                                checks.put(i, true);
                            }
                        }
                        TextView htmlText = (TextView) v.findViewById(R.id.htmlText);
                        htmlText.setText(Html.fromHtml(preferenceActivity.getString(R.string.hrCalendarEventDialogExtraEventSettings)));
                        htmlText.setOnClickListener(new OnClickListener() {
                            public void onClick(View paramView) {
                                preferenceActivity.startActivity(new Intent(preferenceActivity, CalendarDebugActivity.class));
                            }
                        });
                        separator.setOnClickListener(new OnClickListener() {
                            public void onClick(View v) {
                                text.getText().insert(text.getSelectionEnd(), "|");
                            }
                        });
                        final List<String> list2 = allCalendarNames;
                        final SparseBooleanArray sparseBooleanArray = checks;
                        new Builder(resultRegisterableActivity.GetActivity()).setView(v).setPositiveButton(R.string.hrOk, new DialogInterface.OnClickListener() {
                            /* JADX WARNING: Removed duplicated region for block: B:31:0x00d1  */
                            /* JADX WARNING: Removed duplicated region for block: B:26:0x0080  */
                            /* Code decompiled incorrectly, please refer to instructions dump. */
                            public void onClick(DialogInterface dialog, int which) {
                                Boolean isAllDay;
                                Boolean isShowAsAvailable;
                                boolean isCurrentEvent = spinner.getSelectedItemPosition() == 0;
                                ArrayList<String> events = Helpers.SplitToArrayList(text.getText().toString(), "\\|", -1);
                                ArrayList<String> calendars = new ArrayList();
                                for (int i = 0; i < list2.size(); i++) {
                                    if (sparseBooleanArray.get(i)) {
                                        calendars.add(list2.get(i));
                                    }
                                }
                                switch (calendarAllDaySpinner.getSelectedItemPosition()) {
                                    case 1:
                                        isAllDay = Boolean.valueOf(true);
                                        break;
                                    case 2:
                                        isAllDay = Boolean.valueOf(false);
                                        break;
                                    default:
                                        isAllDay = null;
                                        break;
                                }
                                switch (calendarAvailabilitySpinner.getSelectedItemPosition()) {
                                    case 1:
                                        isShowAsAvailable = Boolean.valueOf(true);
                                        break;
                                    case 2:
                                        isShowAsAvailable = Boolean.valueOf(false);
                                        break;
                                    default:
                                        isShowAsAvailable = null;
                                        break;
                                }
                                final CalendarEventCondition2 result = new CalendarEventCondition2(events, isCurrentEvent, calendars, isAllDay, isShowAsAvailable);
                                dialog.dismiss();
                                boolean foundSpaces = false;
                                Iterator i$ = result._EventNameSubstrings.iterator();
                                while (i$.hasNext()) {
                                    String s = (String) i$.next();
                                    if (!s.startsWith(" ")) {
                                        if (s.endsWith(" ")) {
                                        }
                                    }
                                    foundSpaces = true;
                                    if (foundSpaces) {
                                        gotResultHandler2.HandleResult(result);
                                        return;
                                    } else {
                                        new Builder(resultRegisterableActivity.GetActivity()).setMessage(R.string.hrCalendarEventNameSpaceWarning).setPositiveButton(R.string.hrRemoveSpaces, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                for (int i = result._EventNameSubstrings.size() - 1; i >= 0; i--) {
                                                    String trimmed = ((String) result._EventNameSubstrings.get(i)).trim();
                                                    if (trimmed.length() > 0) {
                                                        result._EventNameSubstrings.set(i, trimmed);
                                                    } else {
                                                        result._EventNameSubstrings.remove(i);
                                                    }
                                                }
                                                gotResultHandler2.HandleResult(result);
                                            }
                                        }).setNegativeButton(R.string.hrLeaveSpaces, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                gotResultHandler2.HandleResult(result);
                                            }
                                        }).setOnCancelListener(new OnCancelListener() {
                                            public void onCancel(DialogInterface dialog) {
                                                gotResultHandler2.HandleResult(result);
                                            }
                                        }).show();
                                        return;
                                    }
                                }
                                if (foundSpaces) {
                                }
                            }
                        }).setNegativeButton(R.string.hrCancel, null).show();
                    }
                }.execute(new Object[]{null, null});
            }
        };
    }

    public String GetIsValidError(Context c) {
        return null;
    }
}
