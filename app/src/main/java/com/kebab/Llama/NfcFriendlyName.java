package com.kebab.Llama;

import java.util.Comparator;

public class NfcFriendlyName {
    public static Comparator<? super NfcFriendlyName> NameComparer = new Comparator<NfcFriendlyName>() {
        public int compare(NfcFriendlyName lhs, NfcFriendlyName rhs) {
            return lhs.Name.compareToIgnoreCase(rhs.Name);
        }
    };
    public String HexString;
    public String Name;

    public NfcFriendlyName(String hexString, String name) {
        this.HexString = hexString;
        this.Name = name;
    }

    public static NfcFriendlyName CreateFromPsv(String psv) {
        String[] parts = psv.split("\\|", -1);
        return new NfcFriendlyName(parts[0], new String(LlamaStorage.SimpleUnescape(parts[1])));
    }

    public String ToPsv() {
        StringBuffer sb = new StringBuffer();
        ToPsv(sb);
        return new String(sb);
    }

    public void ToPsv(StringBuffer sb) {
        sb.append(this.HexString);
        sb.append("|");
        sb.append(LlamaStorage.SimpleEscape(this.Name));
    }
}
