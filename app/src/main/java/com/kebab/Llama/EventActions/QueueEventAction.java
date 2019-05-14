package com.kebab.Llama.EventActions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceActivity;
import android.text.SpannableStringBuilder;
import com.kebab.AppendableCharSequence;
import com.kebab.ClickablePreferenceEx;
import com.kebab.ClickablePreferenceEx.GotResultHandler;
import com.kebab.Llama.Constants;
import com.kebab.Llama.Event;
import com.kebab.Llama.EventEditActivity;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.LlamaStorage;
import com.kebab.Llama.R;
import com.kebab.PreferenceEx;
import com.kebab.ResultRegisterableActivity;
import com.kebab.ResultRegisterableActivity.ResultCallback;
import java.io.IOException;
import java.util.Calendar;

public class QueueEventAction extends EventAction<QueueEventAction> {
    static final int MULTIPLIER = 100000;
    Event _EventTemplate;
    int _QueueMinutes;
    int _QueueSeconds;

    public QueueEventAction(Event eventTemplate, int serializeMinsAndSecs) {
        this._EventTemplate = eventTemplate;
        this._QueueMinutes = serializeMinsAndSecs % MULTIPLIER;
        this._QueueSeconds = serializeMinsAndSecs / MULTIPLIER;
    }

    public void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        Event clone = Event.CreateFromPsv(this._EventTemplate.ToPsv());
        clone.Type = 2;
        clone.CancelDelayedIfFailed = true;
        if (!service.EnqueueEventForAfterTestEvents(clone, Calendar.getInstance(), this._QueueMinutes, this._QueueSeconds)) {
            service.HandleFriendlyError(String.format("Failed to queue another event. There is already an event named '%1s'", new Object[]{clone.Name}), false);
        }
    }

    public boolean RenameProfile(String oldName, String newName) {
        return false;
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.QUEUE_EVENT_ACTION;
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 2;
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        int serializeMinsAndSecs = this._QueueMinutes + (this._QueueSeconds * MULTIPLIER);
        sb.append(this._EventTemplate == null ? "" : LlamaStorage.SimpleEscape(this._EventTemplate.ToPsv())).append("|");
        sb.append(serializeMinsAndSecs);
    }

    public static QueueEventAction CreateFrom(String[] parts, int currentPart) {
        String parts1 = parts[currentPart + 1];
        return new QueueEventAction(parts1.length() == 0 ? null : Event.CreateFromPsv(LlamaStorage.SimpleUnescape(parts1)), Integer.parseInt(parts[currentPart + 2]));
    }

    public PreferenceEx<QueueEventAction> CreatePreference(PreferenceActivity context) {
        ResultRegisterableActivity activity = (ResultRegisterableActivity) context;
        final ResultRegisterableActivity resultRegisterableActivity = activity;
        return new ClickablePreferenceEx<QueueEventAction>(activity, context.getString(R.string.hrActionQueueEvent), this) {
            /* Access modifiers changed, original: protected */
            public String GetHumanReadableValue(Context context, QueueEventAction value) {
                if (value._EventTemplate == null) {
                    return "";
                }
                SpannableStringBuilder sb = new SpannableStringBuilder();
                if (value._QueueMinutes > 0) {
                    if (value._QueueSeconds > 0) {
                        sb.append(String.format(context.getString(R.string.hrWaitFor1Minutes2Seconds), new Object[]{Integer.valueOf(value._QueueMinutes), Integer.valueOf(value._QueueSeconds)}));
                    } else {
                        sb.append(String.format(context.getString(R.string.hrWaitFor1Minutes), new Object[]{Integer.valueOf(value._QueueMinutes)}));
                    }
                } else if (value._QueueSeconds > 0) {
                    sb.append(String.format(context.getString(R.string.hrWaitFor1Seconds), new Object[]{Integer.valueOf(value._QueueSeconds)}));
                }
                if (sb.length() > 0) {
                    sb.append(" - ");
                }
                value._EventTemplate.AppendEventConditionDescription(context, sb);
                if (sb.length() > 0) {
                    sb.append(" - ");
                }
                try {
                    value._EventTemplate.AppendEventActionDescription(context, AppendableCharSequence.Wrap(sb), true);
                    return sb.toString();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

            /* Access modifiers changed, original: protected */
            public void OnPreferenceClicked(ResultRegisterableActivity host, QueueEventAction existingValue, final GotResultHandler<QueueEventAction> gotResultHandler) {
                Intent intent = new Intent(resultRegisterableActivity.GetActivity(), EventEditActivity.class);
                intent.putExtra(Constants.EXTRA_QUEUED_EVENT, true);
                intent.putExtra(Constants.EXTRA_EVENT_QUEUE_DELAY, existingValue._QueueMinutes);
                intent.putExtra(Constants.EXTRA_EVENT_QUEUE_DELAY_SECONDS, existingValue._QueueSeconds);
                if (existingValue._EventTemplate == null) {
                    intent.putExtra("Event", new Event(resultRegisterableActivity.GetActivity().getString(R.string.hrQueuedEventDefaultName)));
                } else {
                    intent.putExtra("Event", existingValue._EventTemplate);
                    intent.putExtra(Constants.EXTRA_IS_EDIT, true);
                }
                resultRegisterableActivity.RegisterActivityResult(intent, new ResultCallback() {
                    public void HandleResult(int resultCode, Intent data, Object extraStateInfo) {
                        if (resultCode == -1) {
                            gotResultHandler.HandleResult(new QueueEventAction((Event) data.getParcelableExtra("Event"), data.getIntExtra(Constants.EXTRA_EVENT_QUEUE_DELAY, 0) + (QueueEventAction.MULTIPLIER * data.getIntExtra(Constants.EXTRA_EVENT_QUEUE_DELAY_SECONDS, 0))));
                        }
                    }
                }, null);
            }
        };
    }

    public void AppendActionDescription(Context context, AppendableCharSequence sb) throws IOException {
        if (this._EventTemplate != null) {
            if (this._QueueMinutes > 0) {
                if (this._QueueMinutes > 0) {
                    sb.append(String.format(context.getString(R.string.hrQueueAnEventNamed1After2Minutes3Seconds), new Object[]{this._EventTemplate.Name, Integer.valueOf(this._QueueMinutes), Integer.valueOf(this._QueueSeconds)}));
                    return;
                }
                sb.append(String.format(context.getString(R.string.hrQueueAnEventNamed1After2Minutes), new Object[]{this._EventTemplate.Name, Integer.valueOf(this._QueueMinutes)}));
            } else if (this._QueueMinutes > 0) {
                sb.append(String.format(context.getString(R.string.hrQueueAnEventNamed1After2Seconds), new Object[]{this._EventTemplate.Name, Integer.valueOf(this._QueueSeconds)}));
            } else {
                sb.append(String.format(context.getString(R.string.hrQueueAnEventNamed1), new Object[]{this._EventTemplate.Name}));
            }
        }
    }

    public String GetIsValidError(Context context) {
        if (this._EventTemplate == null || (this._EventTemplate._Conditions.size() == 0 && this._QueueMinutes == 0 && this._QueueSeconds == 0)) {
            return context.getString(R.string.hrQueueEventActionValidation);
        }
        return null;
    }

    public boolean IsHarmful() {
        return true;
    }
}
