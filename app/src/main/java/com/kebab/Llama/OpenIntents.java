package com.kebab.Llama;

import android.content.Context;
import android.content.Intent;
import com.kebab.Locale.LocaleHelper;

public class OpenIntents {

    public class LlamaIntents {
        public static final String ACTION_AFTER_PROFILE_CHANGE = "com.kebab.llama.AFTER_PROFILE_CHANGE";
        public static final String ACTION_VARIABLE_CHANGED = "com.kebab.llama.VARIABLE_CHANGED";
        public static final String EXTRA_KEY = "com.kebab.llama.extras.KEY";
        public static final String EXTRA_PROFILE_NAME = "com.kebab.llama.extras.PROFILE_NAME";
        public static final String EXTRA_VALUE = "com.kebab.llama.extras.VALUE";
    }

    public static void SendVolumeChanging(Context context, int stream, int volume) {
        Intent notificationIntent = new Intent("org.openintents.audio.action_volume_update");
        notificationIntent.putExtra("org.openintents.audio.extra_stream_type", stream);
        notificationIntent.putExtra("org.openintents.audio.extra_volume_index", volume);
        context.sendBroadcast(notificationIntent);
    }

    public static void SendToMinimalisticTextAndLlamaWatchers(Context context, String name, String value) {
        Intent sendintent = new Intent(LocaleHelper.ACTION_FIRE_SETTING);
        sendintent.setClassName("de.devmil.minimaltext", "de.devmil.minimaltext.locale.LocaleFireReceiver");
        sendintent.putExtra("de.devmil.minimaltext.locale.extras.VAR_NAME", name);
        sendintent.putExtra("de.devmil.minimaltext.locale.extras.VAR_TEXT", value);
        context.sendBroadcast(sendintent);
        Intent sendintent2 = new Intent(LlamaIntents.ACTION_VARIABLE_CHANGED);
        sendintent.putExtra(LlamaIntents.EXTRA_KEY, name);
        sendintent.putExtra(LlamaIntents.EXTRA_VALUE, value);
        context.sendBroadcast(sendintent2);
    }

    public static void SendProfileChange(Context context, String name) {
        Intent i = new Intent(LlamaIntents.ACTION_AFTER_PROFILE_CHANGE);
        i.putExtra(LlamaIntents.EXTRA_PROFILE_NAME, name);
        context.sendBroadcast(i);
    }
}
