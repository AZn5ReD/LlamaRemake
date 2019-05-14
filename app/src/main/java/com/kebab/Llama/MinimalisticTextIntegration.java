package com.kebab.Llama;

import android.content.Context;

public class MinimalisticTextIntegration {
    static final String MINI_TEXT_AREA_NAMES = "llamaareas";
    static final String MINI_TEXT_PROFILE_NAME = "llamaprofile";

    public static void SetProfileName(Context context, String profileName) {
        OpenIntents.SendToMinimalisticTextAndLlamaWatchers(context, MINI_TEXT_PROFILE_NAME, profileName);
    }

    public static void SetAreaNames(Context context, String formattedAreaNames) {
        if (formattedAreaNames == null) {
            formattedAreaNames = context.getString(R.string.hrUnknownArea);
        }
        OpenIntents.SendToMinimalisticTextAndLlamaWatchers(context, MINI_TEXT_AREA_NAMES, formattedAreaNames);
    }

    public static void SetVariableValue(Context context, String variableName, String variableValue) {
        OpenIntents.SendToMinimalisticTextAndLlamaWatchers(context, variableName, variableValue);
    }
}
