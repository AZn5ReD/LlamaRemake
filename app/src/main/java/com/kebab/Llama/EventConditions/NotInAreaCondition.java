package com.kebab.Llama.EventConditions;

import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import com.kebab.Helpers;
import com.kebab.ListPreferenceMultiselect;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.EventMeta;
import com.kebab.Llama.EventMeta.ConditionStaticInitter2;
import com.kebab.Llama.EventTrigger;
import com.kebab.Llama.Instances;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.LlamaStorage;
import com.kebab.Llama.R;
import com.kebab.Llama.StateChange;
import com.kebab.OnGetValueEx;
import com.kebab.PreferenceEx;
import com.kebab.Ref;
import java.io.IOException;
import java.util.Arrays;

public class NotInAreaCondition extends EventCondition<NotInAreaCondition> {
    public static String MY_ID;
    public static int MY_TRIGGER;
    public static int[] MY_TRIGGERS;
    String[] _AreaNames;

    static {
        EventMeta.InitCondition(EventFragment.NOT_IN_AREA_CONDITION, new ConditionStaticInitter2() {
            public void UpdateStatics(String id, int[] triggers, int trigger, int triggerOther) {
                NotInAreaCondition.MY_ID = id;
                NotInAreaCondition.MY_TRIGGERS = triggers;
                NotInAreaCondition.MY_TRIGGER = trigger;
            }
        });
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return MY_ID;
    }

    public int[] getEventTriggers() {
        return MY_TRIGGERS;
    }

    public NotInAreaCondition(String[] areaNames) {
        this._AreaNames = areaNames;
    }

    public int TestCondition(StateChange state, Context context, Ref<EventTrigger> ref) {
        boolean leaveTriggerArea = false;
        for (String area : this._AreaNames) {
            if (state.OtherAreas.contains(area)) {
                return 0;
            }
            if (state.TriggerType == MY_TRIGGER && state.TriggerAreaName.equals(area)) {
                leaveTriggerArea = true;
            }
        }
        return leaveTriggerArea ? 2 : 1;
    }

    public boolean RenameArea(String oldName, String newName) {
        boolean changed = false;
        for (int i = 0; i < this._AreaNames.length; i++) {
            if (this._AreaNames[i].equals(oldName)) {
                this._AreaNames[i] = newName;
                changed = true;
            }
        }
        return changed;
    }

    public void AppendConditionSimple(Context context, Appendable sb) throws IOException {
        String areaNames = Helpers.ConcatenateListOfStrings(Arrays.asList(this._AreaNames), ", ", " " + context.getString(R.string.hrNor) + " ");
        sb.append(String.format(context.getString(R.string.hrWhenNotAt1), new Object[]{areaNames}));
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 1;
    }

    public static NotInAreaCondition CreateFrom(String[] parts, int currentPart) {
        String[] area = LlamaStorage.SimpleUnescape(parts[currentPart + 1]).split("\\|");
        for (int i = 0; i < area.length; i++) {
            area[i] = LlamaStorage.SimpleUnescape(area[i]);
        }
        return new NotInAreaCondition(area);
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        StringBuffer sbInner = new StringBuffer();
        boolean needPipe = false;
        for (String s : this._AreaNames) {
            if (needPipe) {
                sbInner.append("|");
            }
            sbInner.append(LlamaStorage.SimpleEscape(s));
            needPipe = true;
        }
        sb.append(LlamaStorage.SimpleEscape(sbInner.toString()));
    }

    public PreferenceEx<NotInAreaCondition> CreatePreference(PreferenceActivity context) {
        LlamaService.ThreadComplainMustBeWorker();
        String[] areaNames = Instances.Service.GetAreaNames();
        Arrays.sort(areaNames);
        return CreateListPreferenceMultiselect(context, context.getString(R.string.hrNotInArea), areaNames, Arrays.asList(this._AreaNames), new OnGetValueEx<NotInAreaCondition>() {
            public NotInAreaCondition GetValue(Preference preference) {
                return new NotInAreaCondition(((ListPreferenceMultiselect) preference).getValues());
            }
        });
    }

    public String GetIsValidError(Context context) {
        if (this._AreaNames.length == 0) {
            return context.getString(R.string.hrChooseAnAreaToNotBeIn);
        }
        return null;
    }
}
