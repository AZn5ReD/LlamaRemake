package com.kebab.Llama.EventConditions;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceActivity;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import com.kebab.ClickablePreferenceEx;
import com.kebab.ClickablePreferenceEx.GotResultHandler;
import com.kebab.DateHelpers;
import com.kebab.Helpers;
import com.kebab.Llama.EventEditActivityDialog.ConditionEdit;
import com.kebab.Llama.EventTrigger;
import com.kebab.Llama.LlamaStorage;
import com.kebab.Llama.StateChange;
import com.kebab.PreferenceEx;
import com.kebab.Ref;
import com.kebab.ResultRegisterableActivity;
import com.kebab.ResultRegisterableActivity.ResultCallback;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;

public abstract class GroupOfEventConditions<T extends GroupOfEventConditions<?>> extends EventCondition<T> {
    public ArrayList<EventCondition<?>> _Conditions;
    boolean _HasBeenTriggered;
    int[] _LazyTriggers;
    boolean _TriggerEveryTime;

    public abstract T CreateSelf(ArrayList<EventCondition<?>> arrayList, boolean z, boolean z2);

    public abstract String GetBracketClose();

    public abstract String GetBracketOpen();

    public abstract String GetConditionName(Context context);

    public abstract String GetConditionWordSeparator(Context context);

    public abstract boolean GetIsAnd();

    public abstract T GetThisUpcasted();

    public abstract int TestCondition(StateChange stateChange, Context context, Ref<EventTrigger> ref);

    public final int[] getEventTriggers() {
        if (this._LazyTriggers == null) {
            HashSet<Integer> triggers = new HashSet();
            Iterator it = this._Conditions.iterator();
            while (it.hasNext()) {
                for (int triggerId : ((EventCondition) it.next()).getEventTriggers()) {
                    triggers.add(Integer.valueOf(triggerId));
                }
            }
            int i = 0;
            int[] result = new int[triggers.size()];
            it = triggers.iterator();
            while (it.hasNext()) {
                result[i] = ((Integer) it.next()).intValue();
                i++;
            }
            this._LazyTriggers = result;
        }
        return this._LazyTriggers;
    }

    public GroupOfEventConditions(ArrayList<EventCondition<?>> conditions, boolean triggerEveryTime, boolean hasBeenTriggered) {
        this._Conditions = conditions;
        this._HasBeenTriggered = hasBeenTriggered;
        this._TriggerEveryTime = triggerEveryTime;
    }

    public final void PeekStateChange(StateChange state, Context context) {
        if (this._HasBeenTriggered) {
            this._HasBeenTriggered = false;
            state.SetEventsNeedSaving();
        }
        Iterator i$ = this._Conditions.iterator();
        while (i$.hasNext()) {
            ((EventCondition) i$.next()).PeekStateChange(state, context);
        }
    }

    public Calendar GetNextEventTime(Calendar timeRoundedToNextMinute) {
        Calendar nextWakeTime = null;
        Iterator i$ = this._Conditions.iterator();
        while (i$.hasNext()) {
            nextWakeTime = DateHelpers.MinCalendarAndNotNull(nextWakeTime, ((EventCondition) i$.next()).GetNextEventTime(timeRoundedToNextMinute));
        }
        return nextWakeTime;
    }

    public final boolean RenameArea(String oldName, String newName) {
        return false;
    }

    public final void AppendConditionDescription(Context context, SpannableStringBuilder sb, StateChange stateChange, int trueColour, int falseColour) throws IOException {
        int result = -1;
        if (stateChange != null) {
            result = TestCondition(stateChange, context, new Ref());
        }
        int start = sb.length();
        sb.append(GetBracketOpen());
        ColouriseSpan(stateChange, result, sb, start, start + 1, trueColour, falseColour);
        boolean needOr = false;
        String spaceOrSpace = " " + GetConditionWordSeparator(context) + " ";
        Iterator i$ = this._Conditions.iterator();
        while (i$.hasNext()) {
            EventCondition<?> ec = (EventCondition) i$.next();
            if (needOr) {
                start = sb.length();
                sb.append(spaceOrSpace);
                ColouriseSpan(stateChange, result, sb, start, start + spaceOrSpace.length(), trueColour, falseColour);
            }
            ec.AppendConditionDescription(context, sb, stateChange, trueColour, falseColour);
            needOr = true;
        }
        start = sb.length();
        sb.append(GetBracketClose());
        ColouriseSpan(stateChange, result, sb, start, start + 1, trueColour, falseColour);
    }

    private static void ColouriseSpan(StateChange stateChange, int testResult, SpannableStringBuilder sb, int start, int end, int trueColour, int falseColour) {
        if (stateChange != null) {
            ForegroundColorSpan spanStyle;
            Object spanStyle2;
            switch (testResult) {
                case 1:
                    spanStyle = new ForegroundColorSpan(trueColour);
                    spanStyle2 = null;
                    break;
                case 2:
                    spanStyle = new ForegroundColorSpan(trueColour);
                    spanStyle2 = new UnderlineSpan();
                    break;
                default:
                    spanStyle = new ForegroundColorSpan(falseColour);
                    spanStyle2 = null;
                    break;
            }
            if (spanStyle != null) {
                sb.setSpan(spanStyle, start, end, 33);
            }
            if (spanStyle2 != null) {
                sb.setSpan(spanStyle2, start, end, 33);
            }
        }
    }

    /* Access modifiers changed, original: protected|final */
    public final int GetPartsConsumption() {
        return 3;
    }

    /* Access modifiers changed, original: protected|final */
    public final void ToPsvInternal(StringBuilder sb) {
        StringBuilder innerSb = new StringBuilder();
        Iterator i$ = this._Conditions.iterator();
        while (i$.hasNext()) {
            EventCondition<?> ec = (EventCondition) i$.next();
            innerSb.setLength(0);
            ec.ToPsv(innerSb);
            sb.append(LlamaStorage.SimpleEscape(innerSb.toString()));
            sb.append(LlamaStorage.SimpleEscapedPipe());
        }
        sb.append("|");
        sb.append(this._TriggerEveryTime ? "1" : "0");
        sb.append("|");
        sb.append(this._HasBeenTriggered ? "1" : "0");
    }

    public final PreferenceEx<T> CreatePreference(PreferenceActivity context) {
        return new ClickablePreferenceEx<T>((ResultRegisterableActivity) context, GetConditionName(context), GetThisUpcasted()) {
            /* Access modifiers changed, original: protected */
            public String GetHumanReadableValue(Context context, T value) {
                SpannableStringBuilder sb = new SpannableStringBuilder();
                try {
                    value.AppendConditionDescription(context, sb, null, 0, 0);
                    Helpers.CapitaliseFirstLetter(sb);
                    return sb.toString();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            /* Access modifiers changed, original: protected */
            public void OnPreferenceClicked(ResultRegisterableActivity host, T existingValue, final GotResultHandler<T> gotResultHandler) {
                host.RegisterActivityResult(ConditionEdit.CreateIntentForConditionEdit(host.GetActivity(), GroupOfEventConditions.this.GetIsAnd(), existingValue._Conditions, existingValue._TriggerEveryTime), new ResultCallback() {
                    public void HandleResult(int resultCode, Intent data, Object extraStateInfo) {
                        if (resultCode == -1) {
                            gotResultHandler.HandleResult(GroupOfEventConditions.this.CreateSelf(ConditionEdit.GetResultForConditionEdit(data), ConditionEdit.GetTriggerEveryTimeResultForConditionEdit(data), false));
                        }
                    }
                }, null);
            }
        };
    }

    public final String GetIsValidError(Context context) {
        return null;
    }
}
