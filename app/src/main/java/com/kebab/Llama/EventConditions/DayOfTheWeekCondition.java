package com.kebab.Llama.EventConditions;

import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import com.kebab.ArrayHelpers;
import com.kebab.DateHelpers;
import com.kebab.Helpers;
import com.kebab.ListPreferenceMultiselect;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DayOfTheWeekCondition extends EventCondition<DayOfTheWeekCondition> {
    public static String MY_ID;
    public static int MY_TRIGGER;
    public static int[] MY_TRIGGERS;
    int _DayOfTheWeek;

    static {
        EventMeta.InitCondition(EventFragment.DAY_OF_THE_WEEK_CONDITION, new ConditionStaticInitter1() {
            public void UpdateStatics(String id, int[] triggers, int trigger) {
                DayOfTheWeekCondition.MY_ID = id;
                DayOfTheWeekCondition.MY_TRIGGERS = triggers;
                DayOfTheWeekCondition.MY_TRIGGER = trigger;
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

    public DayOfTheWeekCondition(int dayOfTheWeek) {
        this._DayOfTheWeek = dayOfTheWeek;
    }

    public Calendar GetNextEventTime(Calendar currentDateTime) {
        Calendar adjustedDateTime = (Calendar) currentDateTime.clone();
        if (HourMinute.CalendarToInt(currentDateTime) >= HourMinute.MIDNIGHT) {
            adjustedDateTime.add(5, 1);
        }
        adjustedDateTime.set(11, 0);
        adjustedDateTime.set(12, 0);
        adjustedDateTime.set(13, 0);
        adjustedDateTime.set(14, 0);
        return adjustedDateTime;
    }

    public int TestCondition(StateChange state, Context context, Ref<EventTrigger> ref) {
        if (!HasBitIndexMarked(this._DayOfTheWeek, state.DayOfTheWeek)) {
            return 0;
        }
        if (state.TriggerType == MY_TRIGGER && state.CurrentHmTime == HourMinute.MIDNIGHT && state.IsExactHmTime) {
            return 2;
        }
        return 1;
    }

    public boolean RenameArea(String oldName, String newName) {
        return false;
    }

    public void AppendConditionSimple(Context context, Appendable sb) throws IOException {
        List<String> days = GetDayNamesByBitmask(this._DayOfTheWeek);
        if (days.size() != 0) {
            String dayNames = Helpers.ConcatenateListOfStrings(days, ", ", " " + context.getString(R.string.hrOr) + " ");
            sb.append(String.format(context.getString(R.string.hrWhenIts1), new Object[]{dayNames}));
        }
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 1;
    }

    public static DayOfTheWeekCondition CreateFrom(String[] parts, int currentPart) {
        return new DayOfTheWeekCondition(Integer.parseInt(parts[currentPart + 1]));
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(this._DayOfTheWeek);
    }

    public static List<String> GetDayNamesByBitmask(int value) {
        ArrayList<String> selectedDayNames = new ArrayList();
        if (HasBitIndexMarked(value, 2)) {
            selectedDayNames.add(DateHelpers.GetDayName(2));
        }
        if (HasBitIndexMarked(value, 3)) {
            selectedDayNames.add(DateHelpers.GetDayName(3));
        }
        if (HasBitIndexMarked(value, 4)) {
            selectedDayNames.add(DateHelpers.GetDayName(4));
        }
        if (HasBitIndexMarked(value, 5)) {
            selectedDayNames.add(DateHelpers.GetDayName(5));
        }
        if (HasBitIndexMarked(value, 6)) {
            selectedDayNames.add(DateHelpers.GetDayName(6));
        }
        if (HasBitIndexMarked(value, 7)) {
            selectedDayNames.add(DateHelpers.GetDayName(7));
        }
        if (HasBitIndexMarked(value, 1)) {
            selectedDayNames.add(DateHelpers.GetDayName(1));
        }
        return selectedDayNames;
    }

    public static boolean HasBitIndexMarked(int value, int bitIndex) {
        int bitmaskForBitIndex = (int) Math.pow(2.0d, (double) bitIndex);
        return (value & bitmaskForBitIndex) == bitmaskForBitIndex;
    }

    public PreferenceEx<DayOfTheWeekCondition> CreatePreference(PreferenceActivity context) {
        return CreateListPreferenceMultiselect(context, context.getString(R.string.hrDayOfTheWeek), (String[]) ArrayHelpers.SpliceArrays(DateHelpers.GetDayNames(), new String[0], String.class), GetDayNamesByBitmask(this._DayOfTheWeek), new OnGetValueEx<DayOfTheWeekCondition>() {
            public DayOfTheWeekCondition GetValue(Preference preference) {
                int finalValue = 0;
                for (String value : ((ListPreferenceMultiselect) preference).getValues()) {
                    Integer dayOfWeek = DateHelpers.GetDayOfWeekForDayName(value);
                    if (dayOfWeek != null) {
                        finalValue |= (int) Math.pow(2.0d, (double) dayOfWeek.intValue());
                    }
                }
                return new DayOfTheWeekCondition(finalValue);
            }
        });
    }

    public String GetIsValidError(Context context) {
        return this._DayOfTheWeek == 0 ? context.getString(R.string.hrChooseAtLeastOneDay) : null;
    }
}
