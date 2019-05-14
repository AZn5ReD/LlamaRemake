package com.kebab.Llama;

import android.content.Context;
import android.content.Intent;
import com.kebab.Llama.EventConditions.EventCondition;
import java.util.ArrayList;

public class EventEditActivityDialog extends EventEditActivity {

    public static class ConditionEdit {
        public static Intent CreateIntentForConditionEdit(Context context, boolean isAnd, ArrayList<EventCondition<?>> conditions, boolean triggerEveryTime) {
            boolean z = true;
            Event e = new Event("TemporaryEvent");
            e._Conditions = conditions;
            e._Actions = new ArrayList();
            Intent i = new Intent(context, EventEditActivityDialog.class);
            i.putExtra(Constants.EXTRA_CONDITION_EDIT_MODE, true);
            String str = Constants.EXTRA_CONDITION_OR_MODE;
            if (isAnd) {
                z = false;
            }
            i.putExtra(str, z);
            i.putExtra("Event", e);
            i.putExtra(Constants.EXTRA_EVENT_OR_SHOULD_TRIGGER_EVERY_TIME, triggerEveryTime);
            return i;
        }

        public static ArrayList<EventCondition<?>> GetResultForConditionEdit(Intent data) {
            return ((Event) data.getParcelableExtra("Event"))._Conditions;
        }

        public static boolean GetTriggerEveryTimeResultForConditionEdit(Intent data) {
            return data.getBooleanExtra(Constants.EXTRA_EVENT_OR_SHOULD_TRIGGER_EVERY_TIME, false);
        }
    }
}
