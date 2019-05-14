package com.kebab.Llama;

import android.content.Context;

public class SimpleEventTrigger implements EventTrigger {
    String Id;
    int NameResourceId;

    public SimpleEventTrigger(String id, int nameResourceId) {
        this.Id = id;
        this.NameResourceId = nameResourceId;
    }

    public String getEventTriggerReasonId() {
        return this.Id;
    }

    public String GetName(Context c) {
        if (this.NameResourceId == 0) {
            return c.getString(R.string.hrUnknown);
        }
        return c.getString(this.NameResourceId);
    }
}
