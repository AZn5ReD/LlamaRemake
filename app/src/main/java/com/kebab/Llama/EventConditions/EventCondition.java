package com.kebab.Llama.EventConditions;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.EventTrigger;
import com.kebab.Llama.StateChange;
import com.kebab.Ref;
import java.io.IOException;
import java.util.Calendar;

public abstract class EventCondition<TSelf> extends EventFragment<TSelf> implements EventTrigger {
    public abstract boolean RenameArea(String str, String str2);

    public abstract int TestCondition(StateChange stateChange, Context context, Ref<EventTrigger> ref);

    public Calendar GetNextEventTime(Calendar currentDateTime) {
        return null;
    }

    public final void AppendConditionDescription(Context context, SpannableStringBuilder richTextBuilder) {
        try {
            AppendConditionDescription(context, richTextBuilder, null, 0, 0);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void AppendConditionDescription(Context context, SpannableStringBuilder richTextBuilder, StateChange stateChange, int trueColour, int falseColour) throws IOException {
        int start = richTextBuilder.length();
        Object spanStyle = null;
        Object spanStyle2 = null;
        AppendConditionSimple(context, richTextBuilder);
        if (stateChange != null) {
            switch (TestCondition(stateChange, context, new Ref())) {
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
        }
        int end = richTextBuilder.length();
        if (spanStyle != null) {
            richTextBuilder.setSpan(spanStyle, start, end, 33);
        }
        if (spanStyle2 != null) {
            richTextBuilder.setSpan(spanStyle2, start, end, 33);
        }
    }

    /* Access modifiers changed, original: protected */
    public void AppendConditionSimple(Context context, Appendable sb) throws IOException {
    }

    /* Access modifiers changed, original: protected|final */
    public final boolean IsCondition() {
        return true;
    }

    public void PeekStateChange(StateChange state, Context context) {
    }

    public String getEventTriggerReasonId() {
        return getId();
    }

    public int[] getEventTriggers() {
        return null;
    }
}
