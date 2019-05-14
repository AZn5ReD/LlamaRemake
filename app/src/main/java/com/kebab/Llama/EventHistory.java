package com.kebab.Llama;

import java.util.Calendar;

public class EventHistory {
    public static final int HISTORY_EVENT_CONFIRMATION_DENIED = 14;
    public static final int HISTORY_EVENT_CONFIRMATION_SHOWN = 11;
    public static final int HISTORY_EVENT_DELAY = 10;
    public static final int HISTORY_EVENT_DELAY_CANCELLED = 13;
    public static final int HISTORY_EVENT_FIRED = 0;
    public static final int HISTORY_EVENT_REPEATING_CANCELLED = 12;
    public static final int HISTORY_EVENT_TRIGGER_PROHIBITED = 20;
    public final int EventHistoryType;
    public String EventName;
    public final Calendar TriggerTime;
    public final String TriggerType;

    public EventHistory(Calendar triggerTime, String eventName, EventTrigger triggerType, int eventHistoryType) {
        this(triggerTime, eventName, triggerType == null ? null : triggerType.getEventTriggerReasonId(), eventHistoryType);
    }

    private EventHistory(Calendar triggerTime, String eventName, String triggerType, int eventHistoryType) {
        this.EventName = eventName;
        this.TriggerTime = triggerTime;
        if (triggerType == null) {
            triggerType = EventFragment.SIMPLE_TRIGGER_NOT_APPLICABLE;
        }
        this.TriggerType = triggerType;
        this.EventHistoryType = eventHistoryType;
    }

    public void ToPsv(StringBuffer sb) {
        sb.append(this.TriggerTime.getTimeInMillis());
        sb.append("|");
        sb.append(this.EventHistoryType);
        sb.append("|");
        sb.append(LlamaStorage.SimpleEscape(this.EventName));
        sb.append("|");
        sb.append(LlamaStorage.SimpleEscape(this.TriggerType));
    }

    public static EventHistory CreateFromPsv(String s) {
        String[] parts = s.split("\\|", -1);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(Long.parseLong(parts[0]));
        return new EventHistory(c, LlamaStorage.SimpleUnescape(parts[2]), LlamaStorage.SimpleUnescape(parts[3]), Integer.parseInt(parts[1]));
    }
}
