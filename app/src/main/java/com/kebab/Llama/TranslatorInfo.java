package com.kebab.Llama;

import android.content.Context;

public class TranslatorInfo {
    public String[] LanguageIds;
    public String[] Names;
    public String[] TranslatorNames;

    private TranslatorInfo() {
    }

    public static TranslatorInfo GetInfo(Context context) {
        String[] locales = context.getResources().getStringArray(R.array.localesNames);
        String[] names = new String[locales.length];
        String[] languageIds = new String[locales.length];
        String[] translatorNames = new String[locales.length];
        for (int i = 0; i < locales.length; i++) {
            String l = locales[i];
            String[] parts = l.split("\\|", -1);
            if (parts.length == 1) {
                names[i] = l;
                languageIds[i] = "";
                translatorNames[i] = "";
            } else {
                names[i] = parts[0];
                languageIds[i] = parts[1];
                translatorNames[i] = parts[2];
            }
        }
        TranslatorInfo t = new TranslatorInfo();
        t.Names = names;
        t.TranslatorNames = translatorNames;
        t.LanguageIds = languageIds;
        return t;
    }
}
