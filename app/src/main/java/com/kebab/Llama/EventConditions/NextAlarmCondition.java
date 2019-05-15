package com.kebab.Llama.EventConditions;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.preference.PreferenceActivity;
import android.provider.Settings.System;
import android.text.SpannableStringBuilder;
import com.kebab.AlertDialogEx.Builder;
import com.kebab.CachedSetting;
import com.kebab.ClickablePreferenceEx;
import com.kebab.ClickablePreferenceEx.GotResultHandler;
import com.kebab.DateHelpers;
import com.kebab.Helpers;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.EventMeta;
import com.kebab.Llama.EventMeta.ConditionStaticInitter4;
import com.kebab.Llama.EventTrigger;
import com.kebab.Llama.LlamaSettings;
import com.kebab.Llama.Logging;
import com.kebab.Llama.R;
import com.kebab.Llama.StateChange;
import com.kebab.PreferenceEx;
import com.kebab.Ref;
import com.kebab.ResultRegisterableActivity;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class NextAlarmCondition extends EventCondition<NextAlarmCondition> {
    static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("ddd H:m");
    public static String MY_ID;
    public static int MY_TRIGGER;
    public static int[] MY_TRIGGERS;
    public static int MY_TRIGGER_OTHER;
    public static int MY_TRIGGER_OTHER2;
    public static int MY_TRIGGER_OTHER3;
    static SimpleDateFormat TIME_FORMAT_12 = new SimpleDateFormat("h:m a");
    static SimpleDateFormat TIME_FORMAT_24 = new SimpleDateFormat("h:m");
    boolean _RequireAlarm;

    static {
        EventMeta.InitCondition(EventFragment.NEXT_ALARM_CONDITION, new ConditionStaticInitter4() {
            public void UpdateStatics(String id, int[] triggers, int trigger, int otherTrigger, int otherTrigger2, int otherTrigger3) {
                NextAlarmCondition.MY_ID = id;
                NextAlarmCondition.MY_TRIGGERS = triggers;
                NextAlarmCondition.MY_TRIGGER = trigger;
                NextAlarmCondition.MY_TRIGGER_OTHER = otherTrigger;
                NextAlarmCondition.MY_TRIGGER_OTHER2 = otherTrigger2;
                NextAlarmCondition.MY_TRIGGER_OTHER3 = otherTrigger3;
            }
        });
        TimeZone tz = TimeZone.getTimeZone("GMT");
        TIME_FORMAT_24.setTimeZone(tz);
        TIME_FORMAT_12.setTimeZone(tz);
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return MY_ID;
    }

    public int[] getEventTriggers() {
        return MY_TRIGGERS;
    }

    public NextAlarmCondition(boolean requireAlarm) {
        this._RequireAlarm = requireAlarm;
    }

    public int TestCondition(StateChange state, Context context, Ref<EventTrigger> ref) {
        long alarmMillis = ((Long) LlamaSettings.LastAlarmTimeMillis.GetValue(context)).longValue();
        if (!this._RequireAlarm) {
            if (state.TriggerType == MY_TRIGGER_OTHER || state.TriggerType == MY_TRIGGER_OTHER2) {
                UpdateAlarmTimeIfNeeded(context, java.lang.System.currentTimeMillis());
            }
            if (alarmMillis != 0) {
                return 0;
            }
            return 1;
        } else if (state.TriggerType == MY_TRIGGER) {
            int i;
            if (state.CurrentMillis == alarmMillis && state.IsExactHmTime) {
                i = 2;
            } else {
                i = 1;
            }
            return i;
        } else {
            if ((state.TriggerType == MY_TRIGGER_OTHER || state.TriggerType == MY_TRIGGER_OTHER2) && UpdateAlarmTimeIfNeeded(context, java.lang.System.currentTimeMillis())) {
                state.SetQueueRtcNeeded();
            }
            if (alarmMillis == 0) {
                return 0;
            }
            return 1;
        }
    }

    public void PeekStateChange(StateChange state, Context context) {
    }

    public Calendar GetNextEventTime(Calendar currentDateTime) {
        return null;
    }

    public static boolean UpdateAlarmTimeIfNeeded(Context context, long currentMillis) {
        boolean cacheTimeInPast;
        String raw = GetNextAlarmRaw(context);
        long alarmMillis = ((Long) LlamaSettings.LastAlarmTimeMillis.GetValue(context)).longValue();
        if (alarmMillis < currentMillis) {
            cacheTimeInPast = true;
        } else {
            cacheTimeInPast = false;
        }
        boolean rawSameAsLast = raw.equals(LlamaSettings.LastAlarmTimeRaw.GetValue(context));
        if (!cacheTimeInPast && rawSameAsLast) {
            return false;
        }
        if (!rawSameAsLast) {
            LlamaSettings.LastAlarmTimeRaw.SetValueAndCommit(context, raw, new CachedSetting[0]);
        }
        if (raw.length() == 0 && alarmMillis == 0) {
            return false;
        }
        alarmMillis = GetNextAlarmMillis(context);
        LlamaSettings.LastAlarmTimeMillis.SetValueAndCommit(context, Long.valueOf(alarmMillis), new CachedSetting[0]);
        Logging.Report("Next alarm is " + DateHelpers.FormatDate(new Date(alarmMillis)), context);
        return true;
    }

    public static String GetNextAlarmRaw(Context context) {
        return System.getString(context.getContentResolver(), "next_alarm_formatted");
    }

    public static long GetNextAlarmMillis(Context context) {
        try {
            String nextAlarm = System.getString(context.getContentResolver(), "next_alarm_formatted");
            if (nextAlarm == null || nextAlarm.length() == 0) {
                return 0;
            }
            Date date;
            String[] parts = nextAlarm.split(" ", -1);
            Integer alarmDayOfWeek = DateHelpers.GetDayOfWeekForShortDayName(parts[0]);
            if (alarmDayOfWeek == null) {
                alarmDayOfWeek = DateHelpers.GetDayOfWeekForShortEnglishDayName(parts[0]);
                if (alarmDayOfWeek == null) {
                    Logging.Report("NextAlarm", "Parsing alarm failed at day: " + nextAlarm, context);
                }
            }
            String timePart = nextAlarm.substring(parts[0].length() + 1);
            try {
                date = TIME_FORMAT_12.parse(timePart);
            } catch (ParseException e) {
                try {
                    date = TIME_FORMAT_24.parse(timePart);
                } catch (ParseException e2) {
                    date = null;
                }
            }
            if (date == null) {
                Logging.Report("NextAlarm", "Parsing alarm failed: " + nextAlarm + ". Got " + alarmDayOfWeek + " '" + timePart + "'", context);
                return 0;
            }
            Calendar now = Calendar.getInstance();
            now.set(13, 0);
            now.set(14, 0);
            int todayDayOfTheWeek = now.get(7);
            HourMinute hmAlarm = new HourMinute(date);
            HourMinute hmNow = new HourMinute(now);
            if (todayDayOfTheWeek == alarmDayOfWeek.intValue()) {
                if (hmAlarm.compareTo(hmNow) > 0) {
                    now.set(11, hmAlarm.Hours);
                    now.set(12, hmAlarm.Minutes);
                    return now.getTimeInMillis();
                }
                now.add(5, 7);
                now.set(11, hmAlarm.Hours);
                now.set(12, hmAlarm.Minutes);
                return now.getTimeInMillis();
            } else if (alarmDayOfWeek.intValue() > todayDayOfTheWeek) {
                now.add(5, alarmDayOfWeek.intValue() - todayDayOfTheWeek);
                now.set(11, hmAlarm.Hours);
                now.set(12, hmAlarm.Minutes);
                return now.getTimeInMillis();
            } else {
                now.add(5, (7 - todayDayOfTheWeek) + alarmDayOfWeek.intValue());
                now.set(11, hmAlarm.Hours);
                now.set(12, hmAlarm.Minutes);
                return now.getTimeInMillis();
            }
        } catch (Exception ex) {
            Logging.Report(ex, context);
            return 0;
        }
    }

    public boolean RenameArea(String oldName, String newName) {
        return false;
    }

    public void AppendConditionSimple(Context context, Appendable sb) throws IOException {
        if (this._RequireAlarm) {
            long alarmMillis = ((Long) LlamaSettings.LastAlarmTimeMillis.GetValue(context)).longValue();
            String dateOrNever = alarmMillis == 0 ? context.getString(R.string.hrNever).toLowerCase() : DateHelpers.FormatDate(new Date(alarmMillis));
            sb.append(context.getString(R.string.hrWhenTheNextAlarmIsDue1, new Object[]{dateOrNever}));
            return;
        }
        sb.append(context.getString(R.string.hrWhenTheresNoAlarmSet));
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 1;
    }

    public static NextAlarmCondition CreateFrom(String[] parts, int currentPart) {
        return new NextAlarmCondition(parts[currentPart + 1].equals("1"));
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(this._RequireAlarm ? "1" : "0");
    }

    public PreferenceEx<NextAlarmCondition> CreatePreference(PreferenceActivity context) {
        final PreferenceActivity preferenceActivity = context;
        return new ClickablePreferenceEx<NextAlarmCondition>((ResultRegisterableActivity) context, context.getString(R.string.hrConditionNextAlarm), this) {
            /* Access modifiers changed, original: protected */
            public String GetHumanReadableValue(Context context, NextAlarmCondition value) {
                SpannableStringBuilder sb = new SpannableStringBuilder();
                value.AppendConditionDescription(context, sb);
                Helpers.CapitaliseFirstLetter(sb);
                return sb.toString();
            }

            /* Access modifiers changed, original: protected */
            public void OnPreferenceClicked(ResultRegisterableActivity host, NextAlarmCondition existingValue, final GotResultHandler<NextAlarmCondition> gotResultHandler) {
                long nextAlarmMillis = NextAlarmCondition.GetNextAlarmMillis(host.GetActivity());
                String string = host.GetActivity().getString(R.string.hrTheNextAlarmIs1AndHeresHowYouUseIt);
                Object[] objArr = new Object[1];
                objArr[0] = nextAlarmMillis == 0 ? host.GetActivity().getString(R.string.hrNever) : DateHelpers.FormatDateWithYear(new Date(nextAlarmMillis));
                new Builder(preferenceActivity).setMessage(String.format(string, objArr)).setPositiveButton(R.string.hrTriggerWhenAlarmIsDue, new OnClickListener() {
                    public void onClick(DialogInterface d, int paramInt) {
                        gotResultHandler.HandleResult(new NextAlarmCondition(true));
                    }
                }).setNegativeButton(R.string.hrTrueWhenNoAlarm, new OnClickListener() {
                    public void onClick(DialogInterface d, int paramInt) {
                        gotResultHandler.HandleResult(new NextAlarmCondition(false));
                    }
                }).show();
            }
        };
    }

    public String GetIsValidError(Context context) {
        return null;
    }
}
