package com.kebab.Llama;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.SpannableStringBuilder;
import com.kebab.AppendableCharSequence;
import com.kebab.DateHelpers;
import com.kebab.Helpers;
import com.kebab.Llama.EventActions.EventAction;
import com.kebab.Llama.EventConditions.EventCondition;
import com.kebab.Ref;
import com.kebab.Tuple;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Iterator;

public class Event implements Parcelable {
    public static final int CONFIRMATION_AWAITING = 2;
    public static final int CONFIRMATION_GRANTED = 3;
    public static final int CONFIRMATION_NOT_REQUIRED = 0;
    public static final int CONFIRMATION_REQUIRED = 1;
    public static final Creator<Event> CREATOR = new Creator<Event>() {
        public Event createFromParcel(Parcel in) {
            return Event.CreateFromPsv(in.readString());
        }

        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
    public static final int FALSE = 0;
    public static final int GROUP_RESULT_UNDETERMINED = 3;
    public static final Comparator<Event> NameComparator = new Comparator<Event>() {
        public int compare(Event x, Event y) {
            return x.Name.compareToIgnoreCase(y.Name);
        }
    };
    public static final int TRIGGER = 2;
    public static final int TRUE = 1;
    public static final int TYPE_AUTO = 1;
    public static final int TYPE_ONE_OFF = 2;
    public static final int TYPE_PERMANENT = 0;
    public boolean CancelDelayedIfFailed;
    public boolean ConfirmationDialog = true;
    public int ConfirmationStatus;
    public int DelayMinutes;
    public int DelaySeconds;
    public long DelayedUntilMillis;
    public boolean Enabled = true;
    public String GroupName = "";
    public String Name;
    public long NextRepeatAtMillis;
    public int NotificationManagerNotificationId;
    public String ProhibitedTriggers = "";
    public int RepeatMinutesInterval;
    public int Type;
    public ArrayList<EventAction<?>> _Actions = new ArrayList();
    public ArrayList<EventCondition<?>> _Conditions = new ArrayList();

    public Event(String name) {
        this.Name = name;
    }

    public static Event CreateFromPsv(String psv) throws RuntimeException {
        String[] parts = psv.split("\\|", -1);
        String eventName = new String(LlamaStorage.SimpleUnescape(parts[0]));
        try {
            Event result = new Event(eventName);
            String[] metaDataParts = parts[1].split("-", -1);
            result.Type = Integer.parseInt(metaDataParts[0]);
            if (metaDataParts.length > 1) {
                result.Enabled = metaDataParts[1].equals("1");
            }
            if (metaDataParts.length > 2) {
                result.RepeatMinutesInterval = Integer.parseInt(metaDataParts[2]);
                result.DelayMinutes = Integer.parseInt(metaDataParts[3]);
                result.CancelDelayedIfFailed = metaDataParts[4].equals("1");
                result.NextRepeatAtMillis = Long.parseLong(metaDataParts[5]);
                result.DelayedUntilMillis = Long.parseLong(metaDataParts[6]);
                result.ConfirmationStatus = Integer.parseInt(metaDataParts[7]);
            }
            if (metaDataParts.length > 8) {
                result.ConfirmationDialog = metaDataParts[8].equals("1");
            }
            if (metaDataParts.length > 9) {
                result.NotificationManagerNotificationId = Integer.parseInt(metaDataParts[9]);
            }
            if (metaDataParts.length > 10) {
                result.GroupName = LlamaStorage.SimpleUnescape(metaDataParts[10]);
            }
            if (metaDataParts.length > 11) {
                result.DelaySeconds = Integer.parseInt(metaDataParts[11]);
            }
            if (metaDataParts.length > 12) {
                result.ProhibitedTriggers = metaDataParts[12];
            }
            ReadFragmentsIntoLists(2, true, parts, result._Conditions, result._Actions);
            return result;
        } catch (Exception ex) {
            throw new RuntimeException("Could not read event: " + eventName, ex);
        }
    }

    public static void ReadFragmentsIntoLists(int startPart, boolean searchForFragmentStart, String[] parts, ArrayList<EventCondition<?>> conditions, ArrayList<EventAction<?>> actions) {
        int currentPart = startPart;
        boolean foundFragmentStart = !searchForFragmentStart;
        while (currentPart < parts.length) {
            if (!foundFragmentStart) {
                if (parts[currentPart].equals(":")) {
                    foundFragmentStart = true;
                }
                currentPart++;
            } else if (parts[currentPart].length() == 0) {
                currentPart++;
            } else {
                Tuple<EventFragment<?>, Integer> createdFragmentInfo = EventFragment.CreateFromFactory(parts, currentPart);
                currentPart += ((Integer) createdFragmentInfo.Item2).intValue() + 1;
                if (((EventFragment) createdFragmentInfo.Item1).IsCondition()) {
                    conditions.add((EventCondition) createdFragmentInfo.Item1);
                } else {
                    actions.add((EventAction) createdFragmentInfo.Item1);
                }
            }
        }
        if (conditions != null) {
            conditions.trimToSize();
        }
        if (actions != null) {
            actions.trimToSize();
        }
    }

    public String ToPsv() {
        StringBuilder sb = new StringBuilder();
        ToPsv(sb);
        return sb.toString();
    }

    public void ToPsv(StringBuilder sb) {
        String str;
        sb.append(LlamaStorage.SimpleEscape(this.Name)).append("|");
        StringBuilder append = sb.append(this.Type).append("-");
        if (this.Enabled) {
            str = "1";
        } else {
            str = "0";
        }
        append = append.append(str).append("-").append(this.RepeatMinutesInterval).append("-").append(this.DelayMinutes).append("-");
        if (this.CancelDelayedIfFailed) {
            str = "1";
        } else {
            str = "0";
        }
        append = append.append(str).append("-").append(this.NextRepeatAtMillis).append("-").append(this.DelayedUntilMillis).append("-").append(this.ConfirmationStatus).append("-");
        if (this.ConfirmationDialog) {
            str = "1";
        } else {
            str = "0";
        }
        append.append(str).append("-").append(this.NotificationManagerNotificationId).append("-").append(LlamaStorage.SimpleEscape(this.GroupName)).append("-").append(this.DelaySeconds).append("-").append(this.ProhibitedTriggers);
        sb.append("|");
        sb.append(":|");
        Iterator i$ = this._Conditions.iterator();
        while (i$.hasNext()) {
            ((EventCondition) i$.next()).ToPsv(sb);
            sb.append("|");
        }
        i$ = this._Actions.iterator();
        while (i$.hasNext()) {
            ((EventAction) i$.next()).ToPsv(sb);
            sb.append("|");
        }
    }

    public boolean equals(Event e) {
        if (e != null && e.Name.compareTo(this.Name) == 0) {
            return true;
        }
        return false;
    }

    public boolean equals(Object o) {
        if (o != null && (o instanceof Event)) {
            return equals((Event) o);
        }
        return false;
    }

    public int hashCode() {
        return this.Name.hashCode();
    }

    public Tuple<EventTrigger, Boolean> TestEventConditions(StateChange stateChange, LlamaService service, Activity activityJunk) {
        EventTrigger triggerCondition = null;
        int conditionCount = this._Conditions.size();
        boolean keepTestingConditions = true;
        if (this.DelayedUntilMillis != 0 && stateChange.CurrentMillis >= this.DelayedUntilMillis) {
            triggerCondition = EventMeta.Delayed;
            if (conditionCount == 0) {
                Logging.Report("TestEvent", "Event " + this.Name + " triggered by delay. It has no conditions, so will definitely fire", service.getBaseContext(), true, false, true);
                keepTestingConditions = false;
            } else {
                Logging.Report("TestEvent", "Event " + this.Name + " triggered by delay. It has conditions, we must check them", service.getBaseContext(), true, false, true);
            }
        } else if (conditionCount == 0) {
            Logging.Report("TestEvent", "Event " + this.Name + " has no conditions. Current=" + stateChange.CurrentMillis + " vs " + this.DelayedUntilMillis + ". It will not run, but also will not reset it's delay/repeat", service.getBaseContext(), true, false, true);
            return Tuple.Create(null, Boolean.valueOf(true));
        }
        Ref<EventTrigger> additionalTriggerInfo = new Ref();
        Iterator i$ = this._Conditions.iterator();
        while (i$.hasNext()) {
            EventCondition<?> c = (EventCondition) i$.next();
            if (keepTestingConditions) {
                additionalTriggerInfo.Value = null;
                switch (c.TestCondition(stateChange, service, additionalTriggerInfo)) {
                    case 1:
                        conditionCount--;
                        break;
                    case 2:
                        conditionCount--;
                        if (triggerCondition == null) {
                            if (additionalTriggerInfo.Value == null) {
                                triggerCondition = c;
                                break;
                            }
                            triggerCondition = additionalTriggerInfo.Value;
                            break;
                        }
                        break;
                    default:
                        keepTestingConditions = false;
                        Logging.Report("TestEvent", "Event '" + this.Name + "' failed at condition " + c.getClass().getName(), service, true, false, true);
                        break;
                }
            }
            c.PeekStateChange(stateChange, service);
        }
        boolean conditionsHaveBeenMet = conditionCount == 0;
        if (conditionsHaveBeenMet && triggerCondition != null) {
            return Tuple.Create(triggerCondition, Boolean.valueOf(true));
        }
        if (this.NextRepeatAtMillis != 0 && stateChange.CurrentMillis >= this.NextRepeatAtMillis) {
            Logging.Report("TestEvent", "Event " + this.Name + " triggered by repeat", service.getBaseContext(), true, false, true);
            triggerCondition = EventMeta.Repeated;
        } else if (stateChange.TriggerType == 22 && this.Name.equals(stateChange.EventName)) {
            triggerCondition = EventMeta.NamedEvent;
        } else if (triggerCondition == EventMeta.Delayed) {
            Logging.Report("TestEvent", "Event " + this.Name + " delay trigger, conditions met=" + conditionsHaveBeenMet, service.getBaseContext(), true, false, true);
        } else {
            Logging.Report("TestEvent", "Event " + this.Name + " success but NO trigger condition", service.getBaseContext(), true, false, true);
        }
        return Tuple.Create(triggerCondition, Boolean.valueOf(conditionsHaveBeenMet));
    }

    public void RunActions(LlamaService service, Activity activity, long currentTimeRoundedToNextMinuteMillis, int eventRunMode) {
        int count = 0;
        Iterator i$ = this._Actions.iterator();
        while (i$.hasNext()) {
            EventAction<?> a = (EventAction) i$.next();
            count++;
            a.PerformAction(service, activity, this, currentTimeRoundedToNextMinuteMillis, eventRunMode);
            Logging.Report("Ran actions for action with ID " + a.getId(), (Context) service);
        }
        service.IncrementEventRuns(count);
    }

    public Tuple<Calendar, String> GetNextEventTime(Calendar currentDateTime) {
        Calendar date = null;
        String conditionId = null;
        Iterator i$ = this._Conditions.iterator();
        while (i$.hasNext()) {
            EventCondition<?> ec = (EventCondition) i$.next();
            Calendar eventDate = ec.GetNextEventTime(currentDateTime);
            date = DateHelpers.MinCalendarAndNotNull(date, eventDate);
            if (date == eventDate) {
                conditionId = ec.getId();
            }
        }
        return new Tuple(date, conditionId);
    }

    public boolean HasDelay() {
        return this.DelayMinutes > 0 || this.DelaySeconds > 0;
    }

    public void AppendEventConditionDescription(Context context, SpannableStringBuilder sb) {
        boolean needComma = false;
        int sbStart = sb.length();
        Iterator i$ = this._Conditions.iterator();
        while (i$.hasNext()) {
            EventCondition<?> ec = (EventCondition) i$.next();
            if (needComma) {
                sb.append(" ");
            }
            try {
                ec.AppendConditionDescription(context, sb, null, 0, 0);
                needComma = true;
            } catch (IOException bothered) {
                throw new RuntimeException(bothered);
            }
        }
        if (sb.length() > sbStart) {
            Helpers.CapitaliseFirstLetter(sb, sbStart);
        }
    }

    public void AppendEventActionDescription(Context context, AppendableCharSequence appendableCharSequence, boolean includeAdvancedDetails) throws IOException {
        boolean needComma = false;
        int initialLength = appendableCharSequence.length();
        if (includeAdvancedDetails) {
            if (this.ConfirmationStatus != 0) {
                appendableCharSequence.append(context.getString(R.string.hrEventRequireConfirmation));
            }
            if (HasDelay()) {
                CharSequence text = String.format(context.getString(R.string.hrDelayFor1), new Object[]{Helpers.GetHoursMinutesSeconds(context, (this.DelayMinutes * 60) + this.DelaySeconds)});
                if (appendableCharSequence.length() != initialLength) {
                    appendableCharSequence.append((CharSequence) ", ");
                    appendableCharSequence.append(text);
                } else {
                    int sbLength = appendableCharSequence.length();
                    appendableCharSequence.append(text);
                }
            }
            if (this.ConfirmationStatus != 0 || HasDelay()) {
                appendableCharSequence.append((CharSequence) " ");
                appendableCharSequence.append(context.getString(R.string.hrAndThen));
                appendableCharSequence.append((CharSequence) " ");
            }
        }
        for (int i = 0; i < this._Actions.size(); i++) {
            EventAction<?> ea = (EventAction) this._Actions.get(i);
            if (needComma) {
                if (i == this._Actions.size() - 1) {
                    appendableCharSequence.append((CharSequence) " ");
                    appendableCharSequence.append(context.getString(R.string.hrAnd));
                    appendableCharSequence.append((CharSequence) " ");
                } else {
                    appendableCharSequence.append((CharSequence) ", ");
                }
            }
            ea.AppendActionDescription(context, appendableCharSequence);
            needComma = true;
        }
        if (includeAdvancedDetails && this.RepeatMinutesInterval > 0) {
            appendableCharSequence.append((CharSequence) " ");
            appendableCharSequence.append(String.format(context.getString(R.string.hrEvery1minutes), new Object[]{Helpers.GetHoursMinutesSeconds(context, this.RepeatMinutesInterval * 60)}));
        }
    }

    public boolean RenameArea(String oldName, String newName) {
        boolean changed = false;
        Iterator i$ = this._Conditions.iterator();
        while (i$.hasNext()) {
            changed |= ((EventCondition) i$.next()).RenameArea(oldName, newName);
        }
        return changed;
    }

    public boolean RenameProfile(String oldName, String newName) {
        boolean changed = false;
        Iterator i$ = this._Actions.iterator();
        while (i$.hasNext()) {
            changed |= ((EventAction) i$.next()).RenameProfile(oldName, newName);
        }
        return changed;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(ToPsv());
    }

    public static Event CreateDefault(Context context, int eventCount) {
        return new Event(String.format(context.getString(R.string.hrNewEvent1), new Object[]{Integer.valueOf(eventCount + 1)}));
    }

    public boolean HasTimers() {
        return (this.NextRepeatAtMillis == 0 && this.DelayedUntilMillis == 0) ? false : true;
    }

    public boolean IsConditionTriggerAllowed(EventTrigger trigger) {
        return this.ProhibitedTriggers.indexOf(new StringBuilder().append(",").append(trigger.getEventTriggerReasonId()).append(",").toString()) == -1;
    }
}
