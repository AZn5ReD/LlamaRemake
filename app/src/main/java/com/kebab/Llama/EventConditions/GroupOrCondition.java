package com.kebab.Llama.EventConditions;

import android.content.Context;
import com.kebab.Llama.Event;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.EventMeta;
import com.kebab.Llama.EventMeta.ConditionStaticInitterCustomTriggers;
import com.kebab.Llama.EventTrigger;
import com.kebab.Llama.LlamaStorage;
import com.kebab.Llama.Logging;
import com.kebab.Llama.R;
import com.kebab.Llama.StateChange;
import com.kebab.Ref;
import java.util.ArrayList;
import java.util.Iterator;

public class GroupOrCondition extends GroupOfEventConditions<GroupOrCondition> {
    public static String MY_ID;

    static {
        EventMeta.InitCondition(EventFragment.GROUP_OR_CONDITION, new ConditionStaticInitterCustomTriggers() {
            public void UpdateStatics(String id) {
                GroupOrCondition.MY_ID = id;
            }
        });
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return MY_ID;
    }

    public GroupOrCondition(ArrayList<EventCondition<?>> conditions, boolean triggerEveryTime, boolean hasBeenTriggered) {
        super(conditions, triggerEveryTime, hasBeenTriggered);
    }

    public int TestCondition(StateChange state, Context context, Ref<EventTrigger> additionalTriggerInfo) {
        int testResult;
        if (this._Conditions.size() == 0) {
            testResult = 1;
        } else {
            testResult = 0;
        }
        EventTrigger triggerToReport = null;
        Ref<EventTrigger> internalAdditionalTriggerInfo = new Ref();
        Iterator i$ = this._Conditions.iterator();
        while (i$.hasNext()) {
            EventCondition<?> ec = (EventCondition) i$.next();
            switch (testResult) {
                case 0:
                case 1:
                    additionalTriggerInfo.Value = null;
                    int myTestResult = ec.TestCondition(state, context, additionalTriggerInfo);
                    if (myTestResult == 0) {
                        break;
                    }
                    testResult = myTestResult;
                    if (myTestResult != 2) {
                        break;
                    }
                    Logging.Report("ORcondition", "OR can trigger because of trigtype " + state.TriggerType, context);
                    if (internalAdditionalTriggerInfo.Value == null) {
                        triggerToReport = ec;
                        break;
                    }
                    triggerToReport = internalAdditionalTriggerInfo.Value;
                    break;
                case 2:
                    ec.PeekStateChange(state, context);
                    break;
                default:
                    break;
            }
        }
        if (this._HasBeenTriggered) {
            if (testResult == 0) {
                this._HasBeenTriggered = false;
                state.SetEventsNeedSaving();
                Logging.Report("ORcondition", "OR has already trigger, and is now false because of trigtype " + state.TriggerType, context);
            } else if (testResult == 2) {
                if (this._TriggerEveryTime) {
                    Logging.Report("ORcondition", "OR has already triggered, and is triggering again because of trigtype=" + state.TriggerType, context);
                } else {
                    Logging.Report("ORcondition", "OR has already triggered, and is not allowed to trigger again, despite trigtype=" + state.TriggerType, context);
                    testResult = 1;
                }
            }
        } else if (testResult == 2) {
            this._HasBeenTriggered = true;
            state.SetEventsNeedSaving();
            Logging.Report("ORcondition", "OR hasn't already triggered, and is triggering because of trigtype=" + state.TriggerType, context);
        }
        additionalTriggerInfo.Value = triggerToReport;
        return testResult;
    }

    public static GroupOrCondition CreateFrom(String[] parts, int currentPart) {
        ArrayList<EventCondition<?>> conditions = new ArrayList();
        Event.ReadFragmentsIntoLists(0, false, LlamaStorage.SimpleUnescape(parts[currentPart + 1]).split("\\|", -1), conditions, null);
        return new GroupOrCondition(conditions, parts[currentPart + 2].equals("1"), parts[currentPart + 3].equals("1"));
    }

    /* Access modifiers changed, original: protected */
    public GroupOrCondition CreateSelf(ArrayList<EventCondition<?>> conditions, boolean triggerEveryTime, boolean alreadyTriggered) {
        return new GroupOrCondition(conditions, triggerEveryTime, alreadyTriggered);
    }

    /* Access modifiers changed, original: protected */
    public String GetConditionName(Context context) {
        return context.getString(R.string.hrConditionGroupOr);
    }

    /* Access modifiers changed, original: protected */
    public String GetConditionWordSeparator(Context context) {
        return context.getString(R.string.hrOr);
    }

    /* Access modifiers changed, original: protected */
    public GroupOrCondition GetThisUpcasted() {
        return this;
    }

    /* Access modifiers changed, original: protected */
    public String GetBracketOpen() {
        return "[";
    }

    /* Access modifiers changed, original: protected */
    public String GetBracketClose() {
        return "]";
    }

    /* Access modifiers changed, original: protected */
    public boolean GetIsAnd() {
        return false;
    }
}
