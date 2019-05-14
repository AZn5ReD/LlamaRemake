package com.kebab.Llama;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Build.VERSION;
import android.os.Handler;
import com.kebab.DateHelpers;
import com.kebab.IterableHelpers;
import com.kebab.Tuple;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class CalendarReader {
    static final List<CalendarItem> EMPTY = Arrays.asList(new CalendarItem[0]);
    static final String TAG = "CalendarReader";
    private static boolean USE_OFFICIAL_API;
    static String[] _CalendarNames = new String[]{"calendar", "calendarEx", "com.android.calendar"};
    private static Pattern _CleanCalendarAccount = Pattern.compile("[^A-Za-z0-9@\\.]");
    final int REQUEUE_WAKE_TIMEOUT_MS = 30000;
    ArrayList<String> _AllCalendarNames = new ArrayList();
    public ContentObserver _CalendarChangeObserver = new ContentObserver(null) {
        public void onChange(boolean selfChange) {
            if (CalendarReader.this._RequeueWakePosted) {
                Logging.Report(CalendarReader.TAG, "Calendars changed. Already waiting for RTC wake requeue", CalendarReader.this._Service);
                return;
            }
            Logging.Report(CalendarReader.TAG, "Calendars changed. Requeuing RTC wake to see changes in 30000ms", CalendarReader.this._Service);
            CalendarReader.this._Handler.postDelayed(CalendarReader.this._RequeueWakeRunnable, 30000);
            CalendarReader.this._RequeueWakePosted = true;
        }
    };
    ArrayList<CalendarItem> _CurrentEvents = new ArrayList();
    Handler _Handler = new Handler();
    boolean _RequeueWakePosted = false;
    Runnable _RequeueWakeRunnable = new Runnable() {
        public void run() {
            CalendarReader.this._RequeueWakePosted = false;
            CalendarReader.this._Service.QueueRtcWake(null);
        }
    };
    LlamaService _Service;
    ArrayList<String> _ValidCalendarNames = new ArrayList();

    static {
        boolean z = true;
        if (VERSION.SDK_INT < 14) {
            z = false;
        }
        USE_OFFICIAL_API = z;
    }

    public CalendarReader(LlamaService service) {
        this._Service = service;
        ContentResolver contentResolver = service.getContentResolver();
        for (String calendar : _CalendarNames) {
            try {
                Uri uri = Uri.parse("content://" + calendar + "/calendars");
                if (contentResolver.query(uri, new String[]{"_id"}, null, null, null) != null) {
                    this._ValidCalendarNames.add(calendar);
                    contentResolver.registerContentObserver(uri, true, this._CalendarChangeObserver);
                }
            } catch (Exception ex) {
                Logging.Report(ex, (Context) service);
            }
        }
        Logging.ReportSensitive(TAG, "Found calendars: " + IterableHelpers.ConcatenateString(this._ValidCalendarNames, ","), service);
    }

    public void fillStateChange(Calendar rtcWakeDateTime, StateChange stateChange) {
        if (rtcWakeDateTime == null) {
            stateChange.StartingEvents = EMPTY;
            stateChange.EndingEvents = EMPTY;
            stateChange.CurrentEvents = EMPTY;
            return;
        }
        ArrayList<CalendarItem> calendarItems = new ArrayList();
        fillListWithCalendarEvents(rtcWakeDateTime, rtcWakeDateTime, calendarItems);
        ArrayList<CalendarItem> startingEvents = new ArrayList();
        ArrayList<CalendarItem> endingEvents = new ArrayList();
        UpdateCurrentEvents(rtcWakeDateTime, calendarItems);
        Iterator i$ = calendarItems.iterator();
        while (i$.hasNext()) {
            CalendarItem item = (CalendarItem) i$.next();
            if (item.Start.compareTo(rtcWakeDateTime) == 0) {
                item.ToLowercase();
                startingEvents.add(item);
            }
            if (item.End.compareTo(rtcWakeDateTime) == 0) {
                item.ToLowercase();
                endingEvents.add(item);
            }
        }
        stateChange.StartingEvents = startingEvents;
        stateChange.EndingEvents = endingEvents;
        stateChange.CurrentEvents = this._CurrentEvents;
        Logging.ReportSensitive(TAG, "Starting: '" + stateChange.StartingEvents + "' Ending: '" + stateChange.EndingEvents + "' Current: '" + stateChange.CurrentEvents + "'", this._Service);
    }

    public Calendar GetNextEventStartOrFinish(Calendar currentDateTime, Calendar roundedToNextDateTime, Calendar nextWakeDateTime) {
        long startTicks = System.currentTimeMillis();
        ArrayList<CalendarItem> calendarItems = new ArrayList();
        Calendar earliest = null;
        String earliestName = null;
        boolean earliestStarting = false;
        fillListWithCalendarEvents(currentDateTime, nextWakeDateTime, calendarItems);
        Logging.Report(TAG, "Calendar read took " + (System.currentTimeMillis() - startTicks) + " ms", this._Service);
        Iterator i$ = calendarItems.iterator();
        while (i$.hasNext()) {
            CalendarItem item = (CalendarItem) i$.next();
            if (item.End.compareTo(roundedToNextDateTime) >= 0) {
                Calendar startOrEnd;
                boolean starting;
                if (item.Start.compareTo(currentDateTime) >= 0) {
                    startOrEnd = item.Start;
                    starting = true;
                } else {
                    startOrEnd = item.End;
                    starting = false;
                }
                if (earliest == null || earliest.compareTo(startOrEnd) > 0) {
                    earliest = startOrEnd;
                    earliestName = item.Name;
                    earliestStarting = starting;
                }
            }
        }
        UpdateCurrentEvents(currentDateTime, calendarItems);
        if (earliest == null) {
            return null;
        }
        Logging.ReportSensitive(TAG, "Next calendar event is " + earliestName + " " + (earliestStarting ? "starting" : "ending") + " at " + DateHelpers.FormatDebugDate(earliest), this._Service);
        return earliest;
    }

    private void fillListWithCalendarEvents(Calendar currentDateTime, Calendar nextWakeDateTime, ArrayList<CalendarItem> calendarItems) {
        Logging.Report(TAG, "Looking for events between " + DateHelpers.FormatDebugDate(currentDateTime) + " and " + DateHelpers.FormatDebugDate(nextWakeDateTime), this._Service);
        ContentResolver contentResolver = this._Service.getContentResolver();
        StringBuilder sb = new StringBuilder();
        List<Tuple<String, String>> calendars = new ArrayList();
        Iterator i$ = this._ValidCalendarNames.iterator();
        while (i$.hasNext()) {
            String calendar = (String) i$.next();
            Cursor cursor = contentResolver.query(Uri.parse("content://" + calendar + "/calendars"), USE_OFFICIAL_API ? new String[]{"_id", "calendar_displayName", "visible", "ownerAccount"} : new String[]{"_id", "displayName", "selected", "_sync_account"}, null, null, null);
            if (cursor == null) {
                Logging.ReportSensitive(TAG, "Failed to get cursor for calendar " + calendar, this._Service);
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(calendar);
            } else {
                int offsetForCalendarGroup = calendars.size();
                while (cursor.moveToNext()) {
                    String _id = cursor.getString(0);
                    String displayName = cursor.getString(1);
                    Boolean selected = Boolean.valueOf(!cursor.getString(2).equals("0"));
                    String account = cursor.getString(3);
                    if (account == null) {
                        account = this._Service.getString(R.string.hrUnknown);
                    }
                    calendars.add(Tuple.Create(_id, displayName + " (" + _CleanCalendarAccount.matcher(account).replaceAll("") + ")"));
                }
                cursor.close();
                try {
                    Logging.StartBuffering();
                    for (int i = offsetForCalendarGroup; i < calendars.size(); i++) {
                        String id = ((Tuple) calendars.get(i)).Item1;
                        String calendarName = ((Tuple) calendars.get(i)).Item2;
                        Builder builder = Uri.parse("content://" + calendar + "/instances/when").buildUpon();
                        ContentUris.appendId(builder, currentDateTime.getTimeInMillis());
                        ContentUris.appendId(builder, nextWakeDateTime.getTimeInMillis());
                        Cursor eventCursor = null;
                        eventCursor = contentResolver.query(builder.build(), USE_OFFICIAL_API ? new String[]{"title", "begin", "end", "allDay", "originalAllDay", "eventStatus", "selfAttendeeStatus", "accessLevel", "accessLevel", "accessLevel", "rrule", "availability", "duration"} : new String[]{"title", "begin", "end", "allDay", "originalAllDay", "eventStatus", "selfAttendeeStatus", "selected", "visibility", "access_level", "rrule", "transparency"}, "Calendar_id=" + id, null, "startDay ASC, startMinute ASC");
                        if (eventCursor != null) {
                            while (eventCursor.moveToNext()) {
                                String title = eventCursor.getString(0);
                                Calendar begin = Calendar.getInstance();
                                Calendar end = Calendar.getInstance();
                                long startMillis = eventCursor.getLong(1);
                                long endMillis = eventCursor.getLong(2);
                                int allDay = eventCursor.getInt(3);
                                int originalAllDay = eventCursor.getInt(4);
                                int eventStatus = eventCursor.getInt(5);
                                int selfAttendeeStatus = eventCursor.getInt(6);
                                int selected2 = eventCursor.getInt(7);
                                int visibility = eventCursor.getInt(8);
                                int access_level = eventCursor.getInt(9);
                                int rrule = eventCursor.getInt(10);
                                int transparency = eventCursor.getInt(11);
                                String duration = USE_OFFICIAL_API ? eventCursor.getString(12) : null;
                                begin.setTimeInMillis(startMillis);
                                end.setTimeInMillis(endMillis);
                                if (title == null || title.length() == 0) {
                                    Logging.ReportSensitive(TAG, "Ignoring unnamed event - " + title + " start=" + begin + " end=" + end, this._Service, true, true, true);
                                } else {
                                    int attendingStatus;
                                    boolean showAsAvailable;
                                    int privacy;
                                    switch (selfAttendeeStatus) {
                                        case 1:
                                            attendingStatus = 0;
                                            break;
                                        case 2:
                                            attendingStatus = 2;
                                            break;
                                        case 4:
                                            attendingStatus = 1;
                                            break;
                                        default:
                                            attendingStatus = 4;
                                            break;
                                    }
                                    switch (transparency) {
                                        case 0:
                                            showAsAvailable = false;
                                            break;
                                        case 1:
                                            showAsAvailable = true;
                                            break;
                                        case Constants.MENU_QUIT /*100*/:
                                            showAsAvailable = false;
                                            attendingStatus = 1;
                                            break;
                                        case Constants.MENU_SETTINGS /*101*/:
                                            showAsAvailable = false;
                                            attendingStatus = 0;
                                            break;
                                        default:
                                            showAsAvailable = true;
                                            break;
                                    }
                                    switch (visibility) {
                                        case 2:
                                            privacy = 2;
                                            break;
                                        case 3:
                                            privacy = 1;
                                            break;
                                        default:
                                            privacy = 0;
                                            break;
                                    }
                                    calendarItems.add(new CalendarItem(title, calendarName, begin, end, showAsAvailable, attendingStatus, privacy, allDay == 1));
                                    Logging.ReportSensitive(TAG, title + " start=" + DateHelpers.FormatDate(begin) + " end=" + DateHelpers.FormatDate(end) + " allDay=" + allDay + " originalAllDay=" + originalAllDay + " eventStatus=" + eventStatus + " selfAttendeeStatus=" + selfAttendeeStatus + " selected=" + selected2 + " visibility=" + visibility + " access_level=" + access_level + " rrule=" + rrule + " transparency=" + transparency + " duration=" + duration, this._Service, true, false, false);
                                }
                            }
                            eventCursor.close();
                        }
                    }
                    Logging.StopBufferingAndCommit(TAG, this._Service, false);
                } catch (Exception ex) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append(calendar);
                    Logging.ReportSensitive(TAG, "Failed to read calendar " + calendar, ex, this._Service);
                } catch (Throwable th) {
                    Logging.StopBufferingAndCommit(TAG, this._Service, false);
                }
            }
        }
        this._AllCalendarNames.clear();
        this._AllCalendarNames.ensureCapacity(calendars.size());
        for (Tuple<String, String> calendar2 : calendars) {
            this._AllCalendarNames.add(calendar2.Item2);
        }
        if (sb.length() > 0) {
            this._Service.HandleFriendlyError(String.format(this._Service.getString(R.string.hrFailedToReadCalendar1), new Object[]{sb.toString()}), false);
        }
    }

    private long RFCWhateverTheFuckToMillis(String str) {
        int sign = 1;
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        int len = str.length();
        int index = 0;
        if (len < 1) {
            return 0;
        }
        char c = str.charAt(0);
        if (c == '-') {
            sign = -1;
            index = 0 + 1;
        } else if (c == '+') {
            index = 0 + 1;
        }
        if (len < index) {
            return 0;
        }
        if (str.charAt(index) != 'P') {
            Logging.Report("Duration.parse(str='" + str + "') expected 'P' at index=" + index, this._Service);
            return 0;
        }
        int n = 0;
        for (index++; index < len; index++) {
            c = str.charAt(index);
            if (c >= '0' && c <= '9') {
                n = (n * 10) + (c - 48);
            } else if (c == 'W') {
                weeks = n;
                n = 0;
            } else if (c == 'H') {
                hours = n;
                n = 0;
            } else if (c == 'M') {
                minutes = n;
                n = 0;
            } else if (c == 'S') {
                seconds = n;
                n = 0;
            } else if (c == 'D') {
                days = n;
                n = 0;
            } else if (c != 'T') {
                Logging.Report("Duration.parse(str='" + str + "') unexpected char '" + c + "' at index=" + index, this._Service);
                return 0;
            }
        }
        long result = (((long) seconds) + ((((long) minutes) + ((((long) hours) + ((((long) days) + ((long) (weeks * 7))) * 24)) * 60)) * 60)) * 1000;
        return sign != 1 ? -result : result;
    }

    private void UpdateCurrentEvents(Calendar currentDateTime, ArrayList<CalendarItem> calendarItems) {
        this._CurrentEvents = new ArrayList();
        Iterator i$ = calendarItems.iterator();
        while (i$.hasNext()) {
            CalendarItem item = (CalendarItem) i$.next();
            if (item.Start.compareTo(currentDateTime) <= 0 && item.End.compareTo(currentDateTime) > 0) {
                item.ToLowercase();
                this._CurrentEvents.add(item);
            }
        }
        Logging.ReportSensitive(TAG, "Current events are '" + DumpCalendarItemList(this._CurrentEvents) + "'", this._Service);
    }

    public ArrayList<CalendarItem> GetCurrentItems() {
        ArrayList<CalendarItem> list = new ArrayList();
        Calendar start = Calendar.getInstance();
        Calendar finish = (Calendar) start.clone();
        finish.add(12, 5);
        fillListWithCalendarEvents(start, finish, list);
        return list;
    }

    public static String DumpCalendarItemList(ArrayList<CalendarItem> items) {
        StringBuilder sb = new StringBuilder();
        Iterator i$ = items.iterator();
        while (i$.hasNext()) {
            ((CalendarItem) i$.next()).toString(sb);
        }
        return sb.toString();
    }
}
