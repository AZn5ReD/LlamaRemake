package com.kebab.Llama.EventActions;

import android.app.Activity;
import android.content.Context;
import com.kebab.AppendableCharSequence;
import com.kebab.IterableHelpers;
import com.kebab.Llama.Event;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.R;
import com.kebab.Llama.SimplePackageInfo;
import com.kebab.Selector;
import java.io.IOException;
import java.util.ArrayList;

public class KillAppRestartPackageAction extends KillAppActionBase<KillAppRestartPackageAction> {
    public KillAppRestartPackageAction(ArrayList<SimplePackageInfo> applicationInfo) {
        super(applicationInfo);
    }

    public void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        service.KillApplications(this._ApplicationInfo, false);
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.QUIT_APP_ACTION;
    }

    public static KillAppRestartPackageAction CreateFrom(String[] parts, int currentPart) {
        return new KillAppRestartPackageAction(KillAppActionBase.CreateSimplePackageList(parts, currentPart));
    }

    public void AppendActionDescription(Context context, AppendableCharSequence sb) throws IOException {
        sb.append(String.format(context.getString(R.string.hrKill1DieDieDie), new Object[]{IterableHelpers.ConcatenateString(this._ApplicationInfo, ", ", new Selector<SimplePackageInfo, String>() {
            public String Do(SimplePackageInfo value) {
                return value.getFriendlyName();
            }
        })}));
    }

    /* Access modifiers changed, original: protected */
    public KillAppRestartPackageAction CreateSelf(ArrayList<SimplePackageInfo> info) {
        return new KillAppRestartPackageAction(info);
    }

    /* Access modifiers changed, original: protected */
    public int GetTitleResource() {
        return R.string.hrKillApplication;
    }

    public boolean IsHarmful() {
        return false;
    }
}
