package com.kebab.Llama.EventConditions;

import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import com.kebab.DualTimePickerPreference;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.EventMeta;
import com.kebab.Llama.EventMeta.ConditionStaticInitter1;
import com.kebab.Llama.EventTrigger;
import com.kebab.Llama.R;
import com.kebab.Llama.StateChange;
import com.kebab.OnGetValueEx;
import com.kebab.PreferenceEx;
import com.kebab.Ref;
import java.io.IOException;
import java.util.Calendar;

public class TimeBetweenCondition extends EventCondition<TimeBetweenCondition> {
    public static String MY_ID;
    public static int MY_TRIGGER;
    public static int[] MY_TRIGGERS;
    int _HmTime1;
    int _HmTime2;

    static {
        EventMeta.InitCondition(EventFragment.TIME_BETWEEN_CONDITION, new ConditionStaticInitter1() {
            public void UpdateStatics(String id, int[] triggers, int trigger) {
                TimeBetweenCondition.MY_ID = id;
                TimeBetweenCondition.MY_TRIGGERS = triggers;
                TimeBetweenCondition.MY_TRIGGER = trigger;
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

    public TimeBetweenCondition(int hmTime1, int hmTime2) {
        this._HmTime1 = hmTime1;
        this._HmTime2 = hmTime2;
    }

    public int TestCondition(StateChange state, Context context, Ref<EventTrigger> ref) {
        int i = 0;
        if (state.TriggerType == MY_TRIGGER && state.CurrentHmTime == this._HmTime1 && state.IsExactHmTime) {
            return 2;
        }
        int currentHmTime = state.CurrentHmTime;
        if (this._HmTime1 > this._HmTime2) {
            if (currentHmTime >= this._HmTime1 || currentHmTime < this._HmTime2) {
                i = 1;
            }
            return i;
        } else if (currentHmTime < this._HmTime1 || currentHmTime >= this._HmTime2) {
            return 0;
        } else {
            return 1;
        }
    }

    public Calendar GetNextEventTime(Calendar currentDateTime) {
        HourMinute hourMinute;
        int currentHourMinutesInt = HourMinute.CalendarToInt(currentDateTime);
        Calendar adjustedDateTime = (Calendar) currentDateTime.clone();
        if (this._HmTime1 > currentHourMinutesInt) {
            if (this._HmTime2 >= this._HmTime1 || this._HmTime2 <= currentHourMinutesInt) {
                hourMinute = HourMinute.IntToHoursMinutesTo(this._HmTime1);
            } else {
                hourMinute = HourMinute.IntToHoursMinutesTo(this._HmTime2);
            }
        } else if (this._HmTime2 > currentHourMinutesInt) {
            hourMinute = HourMinute.IntToHoursMinutesTo(this._HmTime2);
        } else {
            adjustedDateTime.add(5, 1);
            hourMinute = HourMinute.IntToHoursMinutesTo(Math.min(this._HmTime1, this._HmTime2));
        }
        adjustedDateTime.set(11, hourMinute.Hours);
        adjustedDateTime.set(12, hourMinute.Minutes);
        adjustedDateTime.set(13, 0);
        adjustedDateTime.set(14, 0);
        return adjustedDateTime;
    }

    public boolean RenameArea(String oldName, String newName) {
        return false;
    }

    public void AppendConditionSimple(Context context, Appendable sb) throws IOException {
        HourMinute time1 = HourMinute.IntToHoursMinutesTo(this._HmTime1);
        HourMinute time2 = HourMinute.IntToHoursMinutesTo(this._HmTime2);
        sb.append(String.format(context.getString(R.string.hrBetween1And2), new Object[]{time1.toString(), time2.toString()}));
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 2;
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(this._HmTime1).append("|");
        sb.append(this._HmTime2);
    }

    public static TimeBetweenCondition CreateFrom(String[] parts, int currentPart) {
        return new TimeBetweenCondition(Integer.parseInt(parts[currentPart + 1]), Integer.parseInt(parts[currentPart + 2]));
    }

    public PreferenceEx<TimeBetweenCondition> CreatePreference(PreferenceActivity context) {
        DualTimePickerPreference<TimeBetweenCondition> pref = new DualTimePickerPreference(context);
        pref.setTitle(R.string.hrTimeBetween);
        pref.setSummary("");
        pref.setDefaultValue(HourMinute.IntToHoursMinutesTo(this._HmTime1), HourMinute.IntToHoursMinutesTo(this._HmTime2));
        pref.SetOnGetValueExCallback(new OnGetValueEx<TimeBetweenCondition>() {
            public TimeBetweenCondition GetValue(Preference preference) {
                DualTimePickerPreference<?> pref = (DualTimePickerPreference) preference;
                int hm1 = HourMinute.HoursMinutesToInt(pref.getHour1(), pref.getMinute1());
                int hm2 = HourMinute.HoursMinutesToInt(pref.getHour2(), pref.getMinute2());
                if (hm1 == hm2) {
                    hm2++;
                    if (hm2 > HourMinute.HoursMinutesToInt(23, 59)) {
                        hm2 = HourMinute.HoursMinutesToInt(0, 0);
                    }
                }
                return new TimeBetweenCondition(hm1, hm2);
            }
        });
        return pref;
    }

    public String GetIsValidError(Context context) {
        return null;
    }
}
