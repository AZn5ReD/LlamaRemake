package com.kebab.Llama.EventConditions;

import android.content.Context;
import com.kebab.Llama.Event;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.EventMeta;
import com.kebab.Llama.EventMeta.ConditionStaticInitterCustomTriggers;
import com.kebab.Llama.EventTrigger;
import com.kebab.Llama.LlamaStorage;
import com.kebab.Llama.R;
import com.kebab.Llama.StateChange;
import com.kebab.Ref;
import java.util.ArrayList;
import java.util.Iterator;

public class GroupAndCondition extends GroupOfEventConditions<GroupAndCondition> {
    public static String MY_ID;

    static {
        EventMeta.InitCondition(EventFragment.GROUP_AND_CONDITION, new ConditionStaticInitterCustomTriggers() {
            public void UpdateStatics(String id) {
                GroupAndCondition.MY_ID = id;
            }
        });
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return MY_ID;
    }

    public GroupAndCondition(ArrayList<EventCondition<?>> conditions, boolean triggerEveryTime, boolean hasBeenTriggered) {
        super(conditions, triggerEveryTime, hasBeenTriggered);
    }

    public int TestCondition(StateChange state, Context context, Ref<EventTrigger> additionalTriggerInfo) {
        int finalTestResult = 1;
        EventTrigger triggerToReport = null;
        Ref<EventTrigger> innerAdditionalTriggerInfo = new Ref();
        Iterator i$ = this._Conditions.iterator();
        while (i$.hasNext()) {
            EventCondition<?> ec = (EventCondition) i$.next();
            if (finalTestResult != 0) {
                innerAdditionalTriggerInfo.Value = null;
                switch (ec.TestCondition(state, context, innerAdditionalTriggerInfo)) {
                    case 0:
                        finalTestResult = 0;
                        triggerToReport = null;
                        break;
                    case 1:
                        break;
                    case 2:
                        finalTestResult = 2;
                        if (innerAdditionalTriggerInfo.Value == null) {
                            triggerToReport = ec;
                            break;
                        }
                        triggerToReport = innerAdditionalTriggerInfo.Value;
                        break;
                    default:
                        break;
                }
            }
            ec.PeekStateChange(state, context);
        }
        additionalTriggerInfo.Value = triggerToReport;
        return finalTestResult;
    }

    public static GroupAndCondition CreateFrom(String[] parts, int currentPart) {
        ArrayList<EventCondition<?>> conditions = new ArrayList();
        Event.ReadFragmentsIntoLists(0, false, LlamaStorage.SimpleUnescape(parts[currentPart + 1]).split("\\|", -1), conditions, null);
        return new GroupAndCondition(conditions, parts[currentPart + 2].equals("1"), parts[currentPart + 3].equals("1"));
    }

    /* Access modifiers changed, original: protected */
    public GroupAndCondition CreateSelf(ArrayList<EventCondition<?>> conditions, boolean triggerEveryTime, boolean alreadyTriggered) {
        return new GroupAndCondition(conditions, triggerEveryTime, alreadyTriggered);
    }

    /* Access modifiers changed, original: protected */
    public String GetConditionName(Context context) {
        return context.getString(R.string.hrConditionGroupAnd);
    }

    /* Access modifiers changed, original: protected */
    public String GetConditionWordSeparator(Context context) {
        return context.getString(R.string.hrAnd);
    }

    /* Access modifiers changed, original: protected */
    public GroupAndCondition GetThisUpcasted() {
        return this;
    }

    /* Access modifiers changed, original: protected */
    public String GetBracketOpen() {
        return "{";
    }

    /* Access modifiers changed, original: protected */
    public String GetBracketClose() {
        return "}";
    }

    /* Access modifiers changed, original: protected */
    public boolean GetIsAnd() {
        return true;
    }
}
