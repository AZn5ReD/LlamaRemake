package com.kebab.Llama.EventActions;

import android.app.Activity;
import android.content.Context;
import com.kebab.AppendableCharSequence;
import com.kebab.Llama.Event;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.EventMeta;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.R;
import java.io.IOException;

public abstract class EventAction<TSelf> extends EventFragment<TSelf> {
    public abstract void AppendActionDescription(Context context, AppendableCharSequence appendableCharSequence) throws IOException;

    public abstract boolean IsHarmful();

    public abstract void PerformAction(LlamaService llamaService, Activity activity, Event event, long j, int i);

    public abstract boolean RenameProfile(String str, String str2);

    /* Access modifiers changed, original: protected */
    public boolean IsCondition() {
        return false;
    }

    public boolean ActionIsProhibited(LlamaService service, int eventRunMode) {
        if (eventRunMode != 2) {
            return false;
        }
        service.HandleFriendlyError(service.getString(R.string.hrTheAction1CannotBeRunFromACustomShortcut, new Object[]{((EventMeta) EventMeta.All.get(getId())).Name}), false);
        return true;
    }
}
