package com.kebab.Llama;

import android.content.Context;
import com.kebab.IterableHelpers;
import com.kebab.Llama.EventConditions.EventCondition;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

public class EventList implements Iterable<Event> {
    HashMap<Integer, ArrayList<Event>> _EventsByTrigger;
    ArrayList<Event> _EventsWithTimers;
    ArrayList<Event> _List;

    public EventList() {
        this._EventsWithTimers = new ArrayList();
        this._EventsByTrigger = new HashMap();
        this._List = new ArrayList();
    }

    public EventList(Collection<Event> events) {
        this._EventsWithTimers = new ArrayList();
        this._EventsByTrigger = new HashMap();
        this._List = new ArrayList(events.size());
        AddEvents(events);
    }

    public Iterator<Event> iterator() {
        return this._List.iterator();
    }

    public Collection<Event> GetEventsForTriggerId(int triggerId, String eventName) {
        if (triggerId == 22) {
            Event e = GetByName(eventName);
            if (e == null) {
                return IterableHelpers.Empty();
            }
            return IterableHelpers.WrapSingle(e);
        }
        ArrayList eventListForTrigger = (ArrayList) this._EventsByTrigger.get(Integer.valueOf(triggerId));
        if (eventListForTrigger == null) {
            return IterableHelpers.Empty();
        }
        return eventListForTrigger;
    }

    public boolean HasEventsForTriggerId(int triggerId, String eventName) {
        return GetEventsForTriggerId(triggerId, eventName).size() > 0;
    }

    public void Add(Event e) {
        this._List.add(e);
        AddTriggersForEvent(e);
    }

    /* Access modifiers changed, original: 0000 */
    public void AddTriggersForEvent(Event e) {
        HashSet<Integer> completedTriggerIds = new HashSet();
        Iterator it = e._Conditions.iterator();
        while (it.hasNext()) {
            for (int triggerId : ((EventCondition) it.next()).getEventTriggers()) {
                if (!completedTriggerIds.contains(Integer.valueOf(triggerId))) {
                    completedTriggerIds.add(Integer.valueOf(triggerId));
                    AddEventForTrigger(triggerId, e);
                }
            }
        }
        if (e.HasTimers() && !completedTriggerIds.contains(Integer.valueOf(2))) {
            Logging.Report("EventList", "Event " + e.Name + " has a delayuntil/repeat", Instances.Service);
            AddEventForTrigger(2, e);
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void AddEventForTrigger(int triggerId, Event e) {
        ArrayList<Event> eventListForTrigger = (ArrayList) this._EventsByTrigger.get(Integer.valueOf(triggerId));
        if (eventListForTrigger == null) {
            eventListForTrigger = new ArrayList();
            this._EventsByTrigger.put(Integer.valueOf(triggerId), eventListForTrigger);
        }
        eventListForTrigger.add(e);
    }

    public void AddEvents(Iterable<Event> es) {
        for (Event e : es) {
            Add(e);
        }
    }

    public void DumpDebug(Context llamaService) {
        StringBuilder sb = new StringBuilder();
        for (Entry<Integer, ArrayList<Event>> kvp : this._EventsByTrigger.entrySet()) {
            sb.append(kvp.getKey()).append("\n");
            Iterator i$ = ((ArrayList) kvp.getValue()).iterator();
            while (i$.hasNext()) {
                sb.append(((Event) i$.next()).Name).append(",");
            }
            sb.append("\n");
        }
        Logging.Report("EventList", sb.toString(), llamaService);
    }

    public Event GetByName(String eventName) {
        for (int i = 0; i < this._List.size(); i++) {
            Event e = (Event) this._List.get(i);
            if (e.Name.equals(eventName)) {
                return e;
            }
        }
        return null;
    }

    public void DeleteByName(String selectedEventName, boolean forceCleanUpForTimers) {
        for (int i = 0; i < this._List.size(); i++) {
            Event e = (Event) this._List.get(i);
            if (e.Name.equals(selectedEventName)) {
                this._List.remove(i);
                DeleteEventFromTriggers(e, forceCleanUpForTimers);
                return;
            }
        }
    }

    public void ReloadTriggersForEvent(Event e, boolean forceCleanUpForTimers) {
        DeleteEventFromTriggers(e, forceCleanUpForTimers);
        AddTriggersForEvent(e);
    }

    public void ReloadTriggersForEventIfItHadTimers(Event e, boolean eventHadTimers) {
        boolean eventNowHasTimers = e.HasTimers();
        if (eventHadTimers) {
            if (!eventNowHasTimers) {
                ReloadTriggersForEvent(e, true);
            }
        } else if (eventNowHasTimers) {
            ReloadTriggersForEvent(e, false);
        }
    }

    private void DeleteEventFromTriggers(Event e, boolean forceCleanUpForTimers) {
        Iterator it = e._Conditions.iterator();
        while (it.hasNext()) {
            for (int triggerId : ((EventCondition) it.next()).getEventTriggers()) {
                DeleteTriggerForEvent(triggerId, e);
            }
        }
        if (forceCleanUpForTimers || e.HasTimers()) {
            DeleteTriggerForEvent(2, e);
        }
    }

    private void DeleteTriggerForEvent(int triggerId, Event e) {
        ArrayList<Event> eventsWithTrigger = (ArrayList) this._EventsByTrigger.get(Integer.valueOf(triggerId));
        if (eventsWithTrigger != null) {
            for (int j = 0; j < eventsWithTrigger.size(); j++) {
                if (((Event) eventsWithTrigger.get(j)).equals(e)) {
                    eventsWithTrigger.remove(j);
                    return;
                }
            }
        }
    }

    public boolean RenameEvent(String oldName, String newName) {
        for (int i = 0; i < this._List.size(); i++) {
            if (((Event) this._List.get(i)).Name.equals(oldName)) {
                ((Event) this._List.get(i)).Name = newName;
                return true;
            }
        }
        return false;
    }

    public void RemoveEventType(int eventType) {
        for (int i = this._List.size() - 1; i >= 0; i--) {
            Event e = (Event) this._List.get(i);
            if (e.Type == eventType) {
                this._List.remove(i);
                DeleteEventFromTriggers(e, false);
            }
        }
    }

    public boolean DeleteByNamePrefix(String namePrefix) {
        for (int i = 0; i < this._List.size(); i++) {
            Event e = (Event) this._List.get(i);
            if (e.Name.startsWith(namePrefix)) {
                this._List.remove(i);
                DeleteEventFromTriggers(e, false);
                return true;
            }
        }
        return false;
    }

    public int size() {
        return this._List.size();
    }

    public void SanityCheck(LlamaService service) {
        HashSet<String> allEventNames = new HashSet();
        Iterator i$ = this._List.iterator();
        while (i$.hasNext()) {
            allEventNames.add(((Event) i$.next()).Name);
        }
        StringBuilder sb = new StringBuilder();
        for (ArrayList<Event> kvp : this._EventsByTrigger.values()) {
            Iterator i$2 = kvp.iterator();
            while (i$2.hasNext()) {
                Event other = (Event) i$2.next();
                if (!allEventNames.contains(other.Name)) {
                    Logging.Report("EventList", "Triggers contain an event '" + other.Name + "' that doesn't actually exist", (Context) service);
                    sb.append("Triggers list contain an event '" + other.Name + "' that doesn't actually exist\n");
                }
            }
        }
        if (sb.length() > 0) {
            service.HandleFriendlyError(sb.toString(), false);
        }
    }
}
