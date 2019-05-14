package com.kebab.Llama;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.text.SpannableStringBuilder;
import com.kebab.AlertDialogEx.Builder;
import com.kebab.AppendableCharSequence;
import com.kebab.Helpers;
import com.kebab.Llama.EventActions.EventAction;
import com.kebab.TextEntryDialog;
import com.kebab.TextEntryDialog.ButtonHandler;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

public class SocialLlama {
    public static void ShareEvent(Context context, String description, Iterable<Event> events) {
        String llamaUrl = GetLlamaUrl(description, events);
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("text/plain");
        intent.putExtra("android.intent.extra.TEXT", llamaUrl);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.hrShareLlamaEvent)));
    }

    public static String GetLlamaUrl(String description, Iterable<Event> events) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("http://llama.location.profiles/").append(URLEncoder.encode(description, "UTF-8"));
            for (Event e : events) {
                sb.append("/").append(URLEncoder.encode(e.ToPsv(), "UTF-8"));
            }
            return sb.toString();
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void HandleSharedUrl(Activity activity, String dataString) {
        if (dataString.startsWith("http://llama.location.profiles/")) {
            int errorCount = 0;
            final ArrayList<Event> events = new ArrayList();
            String payload = dataString.substring("http://llama.location.profiles/".length());
            int slashPos = payload.indexOf("/");
            if (slashPos < 0) {
                slashPos = payload.length() + 1;
            }
            String description = payload.substring(0, slashPos);
            for (String encodedEvent : payload.substring(slashPos + 1).split("/")) {
                try {
                    events.add(Event.CreateFromPsv(URLDecoder.decode(encodedEvent, "UTF-8")));
                } catch (UnsupportedEncodingException e) {
                    Logging.Report(e, (Context) activity);
                    errorCount++;
                }
            }
            SpannableStringBuilder sb = new SpannableStringBuilder();
            sb.append(activity.getString(R.string.hrSocialImportWarning));
            if (events.size() > 1) {
                sb.append("\n\n");
                sb.append(description);
            }
            sb.append("\n");
            int index = 1;
            Iterator i$ = events.iterator();
            while (i$.hasNext()) {
                Event e2 = (Event) i$.next();
                sb.append("\n");
                sb.append("------------\n\n");
                sb.append(e2.Name).append(":\n\n");
                e2.AppendEventConditionDescription(activity, sb);
                sb.append(" - ");
                try {
                    e2.AppendEventActionDescription(activity, AppendableCharSequence.Wrap(sb), true);
                    index++;
                } catch (IOException e1) {
                    throw new RuntimeException(e1);
                }
            }
            sb.append("\n\n------------\n\n");
            sb.append(activity.getString(R.string.hrTheChoiceIsYours));
            final Activity activity2 = activity;
            new Builder(activity).setTitle(R.string.hrImportLlamaEvent).setMessage(sb.toString()).setPositiveButton(R.string.hrAddToLlama, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if (SocialLlama.ContainsHarmfulActionsFriendly(events)) {
                        ShowHarmfulConfirmation(activity2, events);
                    } else {
                        SocialLlama.AddEventsToLlama(activity2, events);
                    }
                }

                private void ShowHarmfulConfirmation(final Activity activity, final ArrayList<Event> events) {
                    final String randomStuff = Helpers.GenerateRandomString(5);
                    TextEntryDialog.Show(activity, "Some of these actions may be harmful.\n\nPlease confirm that you want to import these events by typing this:\n\n     " + randomStuff, new ButtonHandler() {
                        public void Do(String result) {
                            if (result != null && result.length() > 0) {
                                if (result.toLowerCase().equals(randomStuff)) {
                                    SocialLlama.AddEventsToLlama(activity, events);
                                } else {
                                    Helpers.ShowSimpleDialogMessage(activity, "The confirmation text did not match.", new OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            AnonymousClass1.this.ShowHarmfulConfirmation(activity, events);
                                        }
                                    });
                                }
                            }
                        }
                    }, 524288);
                }
            }).setNegativeButton(R.string.hrCancel, null).show();
        }
    }

    private static void AddEventsToLlama(Context context, Iterable<Event> events) {
        for (Event e : events) {
            e.GroupName = context.getString(R.string.hrEventImported);
            while (Instances.Service.GetEventByName(e.Name) != null) {
                e.Name += " - " + context.getString(R.string.hrEventImported);
            }
            Instances.Service.AddEvent(e);
        }
    }

    private static boolean ContainsHarmfulActionsFriendly(Iterable<Event> events) {
        for (Event e : events) {
            Iterator i$ = e._Actions.iterator();
            while (i$.hasNext()) {
                if (((EventAction) i$.next()).IsHarmful()) {
                    return true;
                }
            }
        }
        return false;
    }
}
