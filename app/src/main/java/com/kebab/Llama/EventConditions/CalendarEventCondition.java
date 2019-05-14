package com.kebab.Llama.EventConditions;

import com.kebab.Helpers;
import com.kebab.Llama.EventActions.EventFragmentCompat;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.LlamaStorage;
import java.util.ArrayList;

public class CalendarEventCondition extends EventFragmentCompat<CalendarEventCondition> {
    public ArrayList<String> _CalendarNames;
    public boolean _CurrentEventsContainsSubstring;
    public ArrayList<String> _EventNameSubstrings;

    private CalendarEventCondition(ArrayList<String> eventNameSubstrings, boolean currentEventsContainsSubstring, ArrayList<String> calendarNames, Boolean isAllDay, Boolean isShowAsAvailable) {
        this._EventNameSubstrings = eventNameSubstrings;
        this._CurrentEventsContainsSubstring = currentEventsContainsSubstring;
        this._CalendarNames = calendarNames;
        Helpers.ArrayListToLowerCase(this._EventNameSubstrings);
        Helpers.ArrayListToLowerCase(this._CalendarNames);
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.CALENDAR_EVENT;
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 3;
    }

    public static CalendarEventCondition CreateFrom(String[] parts, int currentPart) {
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
        return new CalendarEventCondition(substrings, parts[currentPart + 1].equals("1"), calendarNames, null, null);
    }
}
